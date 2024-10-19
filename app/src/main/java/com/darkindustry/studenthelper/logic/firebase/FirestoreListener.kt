package com.darkindustry.studenthelper.logic.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FirestoreListener {
    companion object {
        private val dbUsernameMutable = MutableStateFlow("")
        val dbUsername: StateFlow<String> get() = dbUsernameMutable

        private val dbEmailMutable = MutableStateFlow("")
        val dbEmail: StateFlow<String> get() = dbEmailMutable

        private val dbPhoneMutable = MutableStateFlow("")
        val dbPhone: StateFlow<String> get() = dbPhoneMutable

        private val universityLinkedMutable = MutableStateFlow("")
        val dbUniversityLinked: StateFlow<String> get() = universityLinkedMutable

        private val dbLessonReminderMutable = MutableStateFlow(false)
        val dbLessonReminder: StateFlow<Boolean> get() = dbLessonReminderMutable

        private val dbUniversityScheduleMutable = MutableStateFlow<Map<String, List<Map<String, String>>>>(emptyMap())
        val dbUniversitySchedule: StateFlow<Map<String, List<Map<String, String>>>> get() = dbUniversityScheduleMutable

        private val dbStudentStatsMutable = MutableStateFlow<Map<String, Any>>(emptyMap())
        val dbStudentStats: StateFlow<Map<String, Any>> get() = dbStudentStatsMutable

        private val dbUniversityGradesMutable = MutableStateFlow<List<Map<String, Any>>>(emptyList())
        val dbUniversityGrades: StateFlow<List<Map<String, Any>>> get() = dbUniversityGradesMutable

        private val dbUniversityUsernameMutable = MutableStateFlow("")
        val dbUniversityUsername: StateFlow<String> get() = dbUniversityUsernameMutable

        private val dbUniversityPasswordMutable = MutableStateFlow("")
        val dbUniversityPassword: StateFlow<String> get() = dbUniversityPasswordMutable

        private fun listenToField(fieldName: String, onUpdate: (Any?) -> Unit) {
            val firebaseAuth = FirebaseAuth.getInstance()
            val firebaseFirestore = FirebaseFirestore.getInstance()

            firebaseAuth.currentUser?.uid?.let { userId ->
                val userRef = firebaseFirestore.collection("users").document(userId)

                userRef.addSnapshotListener { documentSnapshot, e ->
                    if (e != null) {
                        FirebaseCrashlytics.getInstance().log("Firestore error: ${e.message}")
                        return@addSnapshotListener
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val value = documentSnapshot.get(fieldName)
                        onUpdate(value)
                    } else {
                        FirebaseCrashlytics.getInstance().log("Current data: null")
                    }
                }
            }
        }

        private fun listenToUsername() {
            listenToField("username") { value ->
                dbUsernameMutable.value = value as? String ?: ""
            }
        }

        private fun listenToEmail() {
            listenToField("email") { value ->
                dbEmailMutable.value = value as? String ?: ""
            }
        }

        private fun listenToPhone() {
            listenToField("phone") { value ->
                dbPhoneMutable.value = value as? String ?: ""
            }
        }

        private fun listenToUniversityLinked() {
            listenToField("universityData.universityName") { value ->
                universityLinkedMutable.value = value as? String ?: ""
            }
        }

        @Suppress("UNCHECKED_CAST")
        private fun listenToUniversitySchedule() {
            listenToField("universityData.schedule") { value ->
                dbUniversityScheduleMutable.value = value as? Map<String, List<Map<String, String>>> ?: emptyMap()
            }
        }

        @Suppress("UNCHECKED_CAST")
        private fun listenToUniversityCard() {
            listenToField("universityData.academicCard") { value ->
                dbUniversityGradesMutable.value = value as? List<Map<String, Any>> ?: emptyList()
            }
        }

        @Suppress("UNCHECKED_CAST")
        private fun listenToStudentStats() {
            listenToField("universityData.statistics") { value ->
                dbStudentStatsMutable.value = value as? Map<String, Any> ?: emptyMap()
            }
        }

        private fun listenToLessonReminder() {
            listenToField("notifications.dailyLessonReminder") { value ->
                dbLessonReminderMutable.value = value as? Boolean ?: false
            }
        }

        private fun listenToUnviversityUsername() {
            listenToField("universityData.credentials.username") { value ->
                dbUniversityUsernameMutable.value = value as? String ?: ""
            }
        }

        private fun listenToUnviversityPassword(){
            listenToField("universityData.credentials.password") { value ->
                dbUniversityPasswordMutable.value = value as? String ?: ""
            }
        }


        fun listenToAllUserData() {
            listenToUsername()
            listenToEmail()
            listenToPhone()
            listenToUniversityLinked()
            listenToUniversitySchedule()
            listenToUniversityCard()
            listenToStudentStats()
            listenToLessonReminder()
            listenToUnviversityUsername()
            listenToUnviversityPassword()
        }
    }
}
