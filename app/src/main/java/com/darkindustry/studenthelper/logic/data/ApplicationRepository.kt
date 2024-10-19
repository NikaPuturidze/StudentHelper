package com.darkindustry.studenthelper.logic.data

import android.os.Build
import android.os.NetworkOnMainThreadException
import android.util.Log
import androidx.annotation.RequiresExtension
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.MalformedURLException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Date
import java.util.Properties
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.net.ssl.SSLHandshakeException
import kotlin.Result
import kotlin.random.Random

class ApplicationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseFunctions: FirebaseFunctions,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseMessaging: FirebaseMessaging,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val httpClient: OkHttpClient = OkHttpClient(),
) {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun handleException(e: Exception): Exception {
        Log.e("ExceptionHandler", "Caught exception: ${e::class.simpleName}: ${e.message}")
        return when (e) {
            is FirebaseTooManyRequestsException -> Exception("მოთხოვნების ლიმიტი ამოიწურა. გთხოვთ სცადოთ მოგვიანებით.")
            is MalformedURLException -> Exception("გთხოვთ, გადაამოწმეთ URL მისამართი.")
            is SocketTimeoutException -> Exception("გადაამოწმეთ ინტერნეტ კავშირი და სცადეთ ხელახლა.")
            is InterruptedIOException -> Exception("IO ოპერაცია შეწყვეტილი იყო, სცადეთ ხელახლა ან დაუკავშირდით ტექნიკურ ჯგუფს.")
            is SSLHandshakeException -> Exception("SSL ხელშეკრულების შეცდომა. გთხოვთ, გადაამოწმეთ თქვენი უსაფრთხოება.")
            is IllegalStateException -> Exception("შეცდომა პროგრამის მდგომარეობაში. გთხოვთ სცადოთ ხელახლა.")
            is FirebaseException -> Exception("Firebase-ზე შეცდომა მოხდა. გთხოვთ სცადოთ მოგვიანებით.")
            is UnknownHostException -> Exception("სერვერზე შეცდომა, გთხოვთ სცადეთ მოგვიანებით.")
            is NoRouteToHostException -> Exception("შესაბამისი მოთხოვნა ვერ მოიძებნა. გთხოვთ, გადაამოწმეთ ინტერნეტ კავშირი.")
            is TimeoutException -> Exception("გადაამოწმეთ ინტერნეტ კავშირი და სცადეთ ხელახლა.")
            is ConnectException -> (Exception("ვერ მოხერხდა დაკავშირება სერვერთან. გადამოწმეთ ინტერნეტ კავშირი."))
            is IOException -> Exception("IO შეცდომა, გთხოვთ სცადოთ ხელახლა, ან დაუკავშირდით ტექნიკურ ჯგუფს.")
            is NetworkOnMainThreadException -> Exception("NetworkOnMainThreadException შეცდომა, დაუკავშირდით ტექნიკურ ჯგუფს.")
            else -> Exception("უცნობი შეცდომა, გთხოვთ სცადოთ ხელახლა, ან დაუკავშირდით ტექნიკურ ჯგუფს.1")
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun authenticateUser(
        email: String,
        password: String,
    ): Result<Boolean> {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        if (!emailRegex.matches(email)) {
            return Result.failure(Exception("არასწორი პაროლი ან ელ.ფოსტა."))
        }

        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: FirebaseAuthInvalidUserException) {
            firebaseCrashlytics.recordException(e)
            Result.failure(Exception("ეს მომხმარებელი ვერ მოიძებნა. გთხოვთ დარეგისტრირდეთ."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            firebaseCrashlytics.recordException(e)
            Result.failure(Exception("არასწორი პაროლი ან ელ.ფოსტა. გთხოვთ სცადოთ ხელახლა."))
        } catch (e: FirebaseAuthActionCodeException) {
            firebaseCrashlytics.recordException(e)
            Result.failure(Exception("მოქმედება ვერ განხორციელდა. გთხოვთ სცადეთ ხელახლა."))
        } catch (e: Exception) {
            firebaseCrashlytics.recordException(e)
            Result.failure(handleException(e))
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun registerUser(email: String, password: String, username: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid ?: throw Exception("მომხმარებელი ვერ მოიძება, სცადეთ ხელახლა ან დაუკავშირდით ტექნიკურ ჯგუფს.")

                firebaseAuth.signInWithEmailAndPassword(email, password).await().user

                try {
                    val fcmToken = firebaseMessaging.token.await()
                    firebaseFirestore.collection("users").document(userId).update("fcmToken", fcmToken).await()
                    firebaseFirestore.collection("users").document(userId).update("username", username).await()
                } catch (e: Exception) {
                    firebaseCrashlytics.recordException(e)
                    throw Exception("ვერ მოხდა FCM ტოკენის განახლება, დაუკავშირდით ტექნიკურ ჯგუფს.")
                }

                Result.success(true)
            } catch (e: FirebaseAuthUserCollisionException) {
                firebaseCrashlytics.recordException(e)
                Result.failure(Exception("მოცემული ელ.ფოსტა უკვე დარეგისტრირებულია."))
            } catch (e: FirebaseAuthWeakPasswordException) {
                firebaseCrashlytics.recordException(e)
                Result.failure(Exception("პაროლი მინიმუმ 8 სიმბოლოსგან უნდა შედგებოდეს."))
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                Result.failure(handleException(e))
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun sendVerificationCode(email: String): Result<Boolean> {
        if (email.isBlank()) {
            return Result.failure(Exception("შეყვანეთ თქვენი ელ.ფოსტა შესაბამის ველში."))
        }

        val sanitizedEmail = email.replace(Regex("[.#$\\[\\]]"), "_")

        val code = Random.nextInt(100000, 999999).toString()

        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.host", "tbs01-cpanel-15.cld9.cloud")
            put("mail.smtp.port", "465")
            put("mail.smtp.ssl.enable", "true")
        }

        val session = Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(
                    "noreply@studenthelper.darkindustry.net",
                    "nCoTYG*,x~F1"
                )
            }
        })

        val mailOptions = MimeMessage(session).apply {
            setFrom(InternetAddress("noreply@studenthelper.darkindustry.net"))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
            subject = "ვერიფიკაციის კოდი $code"
            setContent(getTemplate(code), "text/html")
        }

        return withContext(Dispatchers.IO) {
            try {
                val expirationTime = System.currentTimeMillis() + 15 * 60 * 1000
                val dbRef = firebaseDatabase.getReference("verificationCodes/$sanitizedEmail")
                dbRef.setValue(mapOf("code" to code, "expiresAt" to expirationTime)).await()

                Transport.send(mailOptions)
                Result.success(true)
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                Result.failure(handleException(e))
            }
        }
    }

    fun getTemplate(code: String): String {

        return "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "        <title>Verification Code</title>\n" +
                "        <style>\n" +
                "            * {\n" +
                "                margin: 0;\n" +
                "                padding: 0;\n" +
                "                box-sizing: border-box;\n" +
                "            }\n" +
                "            body {\n" +
                "                font-family: Arial, sans-serif;\n" +
                "                background-color: #f4f4f4;\n" +
                "                display: flex;\n" +
                "                justify-content: center;\n" +
                "                align-items: center;\n" +
                "                height: 100vh;\n" +
                "            }\n" +
                "            .email-content {\n" +
                "                background-color: #ffffff;\n" +
                "                padding: 20px;\n" +
                "                margin: 10px auto;\n" +
                "                border-radius: 8px;\n" +
                "                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);\n" +
                "                width: 600px;\n" +
                "                display: flex;\n" +
                "                flex-direction: column;\n" +
                "                align-items: center;\n" +
                "                gap: 20px;\n" +
                "                border: 2px solid black;\n" +
                "            }\n" +
                "            .codebox {\n" +
                "                width: 250px;\n" +
                "                height: 70px;\n" +
                "                border: 2px solid black;\n" +
                "                border-radius: 8px;\n" +
                "                display: flex;\n" +
                "                justify-content: center;\n" +
                "                align-items: center;\n" +
                "            }\n" +
                "            a {\n" +
                "                text-decoration: none;\n" +
                "            }\n" +
                "            .codebox p {\n" +
                "                font-size: 22px;\n" +
                "            }\n" +
                "            h1 {\n" +
                "                color: #010715;\n" +
                "            }\n" +
                "            p {\n" +
                "                font-size: 16px;\n" +
                "                color: #010715;\n" +
                "            }\n" +
                "        </style>\n" +
                "    </head>\n" +
                "    <body style=\"margin: 0; padding: 0; background-color: #f4f4f4; font-family: sans-serif;\">\n" +
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "        <tr>\n" +
                "            <td style=\"padding: 10px 0 30px 0;\">\n" +
                "                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: 1px solid #cccccc; border-collapse: collapse; border-radius: 10px; overflow: hidden;\">\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" bgcolor=\"#021238\" style=\"padding: 40px 0 30px 0; color: #ffffff; font-size: 24px; font-weight: bold;\">\n" +
                "                            <img src=\"https://console.darkindustry.net/api/EmailStyle/logo3.png\" alt=\"Logo\" width=\"100\" height=\"100\" style=\"display: block;\" />\n" +
                "                            <h1 style=\"color: #ffffff; margin: 15px 0;\">StudentHelper</h1>\n" +
                "                            <p style=\"color: #ffffff; font-size: 18px; margin: 0;\">Email Verification code:</p>\n" +
                "                            <div style=\"border: 2px solid #ffffff; border-radius: 8px; margin: 25px auto; width: 350px; height: 60px; line-height: 60px; text-align: center;\">\n" +
                "                                <span style=\"font-size: 30px; font-weight: bold; letter-spacing: 10px; color: #ffffff;\">${code}</span>\n" +
                "                            </div>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#ffffff\" style=\"padding: 20px 30px 20px 30px;\">\n" +
                "                            <h1 style=\"color: #021238; font-size: 25px; margin: 0; text-align: center;\">If this wasn't you</h1>\n" +
                "                            <p style=\"margin: 10px 0; color: #021238; text-align: center; font-size: 13px;\">This email was sent because someone is trying to register an account using your email.</p>\n" +
                "                            <p style=\"margin: 10px 0; color: #021238; text-align: center; font-size: 13px;\">If you are not trying to register in our app just ignore this message and delete it.</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#ffffff\" style=\"padding: 10px 30px 30px 30px;\">\n" +
                "                            <h3 style=\"color: #021238; margin: 0;\">Cheers,</h3>\n" +
                "                            <p style=\"margin: 10px 0; color: #021238;\">StudentHelper's Team</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#021238\" style=\"padding: 20px 30px 20px 30px; text-align: center;\">\n" +
                "                            <p style=\"margin: 0; color: #ffffff;\">DarkIndustry.net &copy; 2024. All Rights Reserved.</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "    </body>\n" +
                "    </html>"
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun verifyCode(email: String, code: String): Result<Boolean> {
        val sanitizedEmail = email.replace(Regex("[.#$\\[\\]]"), "_")
        val ref: DatabaseReference =
            firebaseDatabase.getReference("verificationCodes/$sanitizedEmail")

        return try {
            val snapshot = ref.get().await()
            if (snapshot.exists()) {
                val data = snapshot.getValue<Map<String, Any>>()
                val storedCode = data?.get("code") as? String
                val expiresAt = data?.get("expiresAt") as? Long
                val currentTime = System.currentTimeMillis()

                if (storedCode != null && expiresAt != null) {
                    if (currentTime <= expiresAt && storedCode == code) {
                        ref.removeValue().await()
                        Result.success(true)
                    } else {
                        Result.failure(Exception("არასწორი ან ვადაგასული კოდი. გთხოვთ სცადეთ ხელახლა."))
                    }
                } else {
                    Result.failure(Exception("ვერ მოხდა კოდის ვერიფიცირება, გთხოვ სცადეთ ხელახლა."))
                }
            } else {
                Result.failure(Exception("შეცდომა, კოდი ვერ მოიძებნა, გთხოვთ დაუკავშირდით ტექნიკურ ჯგუფს."))
            }
        } catch (e: Exception) {
            firebaseCrashlytics.recordException(e)
            Result.failure(handleException(e))
        }
    }


    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun doesUserExistByEmail(email: String): Result<Boolean> {
        val query = firebaseFirestore.collection("users").whereEqualTo("email", email).limit(1).get()
        return try {
            val result = query.await()
            Result.success(result.isEmpty.not())
        } catch (e: Exception) {
            firebaseCrashlytics.recordException(e)
            Result.failure(Exception("ვერ მოხდა მომხმარებლის არსებობის განსაზღვრა, დაუკავშირდით ტექნიკურ ჯგუფს."))
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun changePassword(newPassword: String): Result<Boolean> {
        return try {
            val userId = firebaseAuth.currentUser ?: throw Exception("მომხმარებელი ვერ მოიძება, სცადეთ ხელახლა ან დაუკავშირდით ტექნიკურ ჯგუფს.")
            userId.updatePassword(newPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(task.exception ?: Exception("შეცდომა პაროლის შეცვლისას. გთხოვთ სცადოთ ხელახლა."))
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            firebaseCrashlytics.recordException(e)
            Result.failure(handleException(e))
        }
    }

    suspend fun generateAndSaveTokenHttpRequest(uniqueId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            val userId = firebaseAuth.currentUser?.uid ?: throw Exception("მომხმარებელი ვერ მოიძება, სცადეთ ხელახლა ან დაუკავშირდით ტექნიკურ ჯგუფს.")
            val url = "https://us-central1-studenthelper-dark.cloudfunctions.net/generateAndSaveToken"

            val requestBody = FormBody.Builder().add("userId", userId).add("uniqueId", uniqueId).build()
            val request = Request.Builder().url(url).post(requestBody).build()

            return@withContext try {
                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    firebaseCrashlytics.log("HTTP error ${response.code}")
                    Result.failure(Exception("HTTP error ${response.code}"))
                }
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                Result.failure(e)
            }
        }

    suspend fun updateSessionStatus(uniqueId: String, status: String): Result<Boolean> {
        return try {
            val data = hashMapOf("uniqueId" to uniqueId, "status" to status)
            val result =
                firebaseFunctions.getHttpsCallable("updateSessionStatus").call(data).await()

            val resultData = result.data as? Map<*, *>
            val success = resultData?.get("success") as? Boolean == true
            val message = resultData?.get("message") as? String

            if (success) {
                Result.success(true)
            } else {
                firebaseCrashlytics.log("Failed to update session status: $message")
                Result.failure(Exception(message ?: "Failed to update session status"))
            }
        } catch (e: Exception) {
            firebaseCrashlytics.recordException(e)
            Result.failure(e)
        }
    }

    suspend fun isValueAvailableForChanges(
        timestampFieldName: String, thresholdDays: Long,
    ): Result<Pair<Boolean, Int?>> {
        return try {
            val userDoc = firebaseAuth.currentUser?.uid?.let {
                firebaseFirestore.collection("users").document(it).get().await()
            }
            val lastChange = userDoc?.getDate(timestampFieldName)

            if (lastChange != null) {
                val currentTime = Date()
                val timeDifference = currentTime.time - lastChange.time
                val daysDifference = TimeUnit.MILLISECONDS.toDays(timeDifference)

                if (daysDifference >= thresholdDays) {
                    Result.success(Pair(true, null))
                } else {
                    Result.success(Pair(false, (thresholdDays - daysDifference).toInt()))
                }
            } else {
                firebaseCrashlytics.log("No last change timestamp found")
                Result.success(Pair(true, null))
            }
        } catch (e: Exception) {
            firebaseCrashlytics.recordException(e)
            Result.failure(e)
        }
    }

    fun signOut(): Result<Boolean> {
        return try {
            firebaseAuth.signOut()
            Result.success(true)
        } catch (e: Exception) {
            firebaseCrashlytics.recordException(e)
            Result.failure(e)
        }
    }
}