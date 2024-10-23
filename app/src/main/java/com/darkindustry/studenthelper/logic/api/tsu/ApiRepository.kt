package com.darkindustry.studenthelper.logic.api.tsu

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.collections.find
import kotlin.text.substringAfter

class ApiRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val httpClient: OkHttpClient = OkHttpClient(),
) {
    internal suspend fun linkUniversity(
        user: String,
        password: String,
        universityName: String,
    ): Result<Boolean> {

        if (callAcademicCard(user = user, password = password).isNotEmpty()) {
            updateIfDifferent(
                fieldPath = "universityData.universityName", data = universityName
            )
            updateIfDifferent(
                fieldPath = "universityData.credentials.username", data = user,
            )
            updateIfDifferent(
                fieldPath = "universityData.credentials.password", data = password,
            )
            return Result.success(true)
        } else {
            return Result.failure(Exception("No academic card data retrieved."))
        }
        println("Trigger")
    }

    internal fun unlinkUniversity() {
        executeBatchDelete(
            fieldPath = "universityData"
        )
    }

    fun updateIfDifferent(
        userRef: DocumentReference = firebaseFirestore.collection("users")
            .document(firebaseAuth.currentUser?.uid ?: ""),
        fieldPath: String,
        data: Any,
    ) {
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val existingValue = documentSnapshot.get(fieldPath)

                    if (existingValue == data) {
                        return@addOnSuccessListener
                    }
                }
                executeBatchUpdate(userRef, fieldPath, data)
            }
            .addOnFailureListener { e ->
                firebaseCrashlytics.recordException(e)
            }
    }

    fun executeBatchUpdate(
        userRef: DocumentReference,
        fieldPath: String,
        newValue: Any,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val batch = firebaseFirestore.batch()
        batch.update(userRef, fieldPath, newValue)
        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                firebaseCrashlytics.recordException(e)
                onFailure()
            }
    }

    private fun executeBatchDelete(
        userRef: DocumentReference = firebaseFirestore.collection("users")
            .document(firebaseAuth.currentUser?.uid ?: ""),
        fieldPath: String
    ) {
        val batch = firebaseFirestore.batch()
        batch.update(userRef, fieldPath, FieldValue.delete())
        batch.commit().addOnFailureListener { e ->
            firebaseCrashlytics.recordException(e)
        }
    }

    private fun provideIdentityToken(username: String, password: String): String? {
        val jsonInputString =
            """{"Username": "$username","Password": "$password","LanguageId": 1}"""
        val cookieName = ".AspNetCore.Identity.Application"

        return try {
            val url = URL("https://uni.tsu.ge/api/Auth/Login")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            connection.outputStream.use { os ->
                val input = jsonInputString.toByteArray()
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val cookies = connection.headerFields["Set-Cookie"]
                if (cookies.isNullOrEmpty()) {
                    FirebaseCrashlytics.getInstance().log("No cookies returned in the response")
                    return null
                }

                val sessionCookie = cookies.find { it.startsWith("$cookieName=") }
                if (sessionCookie.isNullOrEmpty()) {
                    FirebaseCrashlytics.getInstance().log("Session cookie not found")
                    return null
                }

                return sessionCookie.substringAfter("=").substringBefore(";")
            } else {
                FirebaseCrashlytics.getInstance().log("Login failed with response code: $responseCode, message: $responseMessage")
                return null
            }

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log("Exception in provideIdentityToken: ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }.toString()
    }


    suspend fun callAcademicCard(
        user: String,
        password: String
    ): List<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = "\"1\"".toRequestBody("application/json; charset=utf-8".toMediaType())
                val identityToken = provideIdentityToken(user, password)

                if (identityToken.isNullOrEmpty()) {
                    firebaseCrashlytics.log("Failed to retrieve identity token")
                    return@withContext emptyList<Map<String, Any>>()
                }

                val request = Request.Builder().url("https://uni.tsu.ge/api/Card/StudentProfile")
                    .addHeader("Accept", "application/json, text/plain, */*")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cookie", ".AspNetCore.Identity.Application=$identityToken")
                    .addHeader("Origin", "https://uni.tsu.ge")
                    .addHeader("Referer", "https://uni.tsu.ge/card")
                    .addHeader(
                        "User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36"
                    ).post(requestBody).build()

                val response: Response = httpClient.newCall(request).execute()
                val jsonString = response.body?.string()

                if (!response.isSuccessful) {
                    firebaseCrashlytics.log("Failed to fetch student profile, status code: ${response.code}")
                    return@withContext emptyList<Map<String, Any>>()
                }

                if (jsonString.isNullOrEmpty()) {
                    firebaseCrashlytics.log("Empty response body for student profile")
                    return@withContext emptyList<Map<String, Any>>()
                }

                val jsonObject = JSONObject(jsonString)

                val studentProfileArray = jsonObject.optJSONArray("studentProfile")
                val resultList = mutableListOf<Map<String, Any>>()

                if (studentProfileArray != null) {
                    for (i in 0 until studentProfileArray.length()) {
                        val subject = studentProfileArray.getJSONObject(i)
                        val subjectDetails = mutableMapOf<String, Any>()

                        subjectDetails["subjectName"] = subject.optString("subjectName", "Unknown")
                        subjectDetails["semesterName"] = subject.optString("semName", "Unknown")
                        subjectDetails["yearName"] = subject.optString("yearName", "Unknown")
                        subjectDetails["enrollStatus"] = subject.optString("enrollStatus", "Unknown")
                        subjectDetails["sumMark"] = subject.optDouble("sumMark", Double.NaN)
                        subjectDetails["semesterNumber"] = subject.optInt("semesterNumber", -1)
                        subjectDetails["gradeSimbole"] = subject.optString("gradeSimbole", "N/A")

                        resultList.add(subjectDetails)
                    }

                    updateIfDifferent(
                        fieldPath = "universityData.academicCard",
                        data = resultList
                    )
                }

                val studentObject = jsonObject.optJSONObject("student")
                if (studentObject != null) {
                    val gpa = studentObject.optDouble("gpaDanarti", Double.NaN)
                    val sumCredits = studentObject.optInt("sumCredits", 0)
                    val avgMark = studentObject.optDouble("avgMark", Double.NaN)

                    val studentData = mapOf(
                        "gpa" to gpa,
                        "totalCredits" to sumCredits,
                        "avgScore" to avgMark
                    )

                    updateIfDifferent(
                        fieldPath = "universityData.statistics",
                        data = studentData
                    )
                }

                return@withContext resultList

            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                firebaseCrashlytics.log("Exception occurred while fetching student profile: ${e.message}")
                return@withContext emptyList<Map<String, Any>>()
            }
        }
    }
}