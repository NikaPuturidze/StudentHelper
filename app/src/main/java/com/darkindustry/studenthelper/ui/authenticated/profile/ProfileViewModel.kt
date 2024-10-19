package com.darkindustry.studenthelper.ui.authenticated.profile

import androidx.lifecycle.viewModelScope
import com.darkindustry.studenthelper.logic.data.ApplicationRepository
import com.darkindustry.studenthelper.logic.data.ApplicationViewModel
import com.darkindustry.studenthelper.logic.utils.MessageType
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository,
) : ApplicationViewModel() {

    fun generateAndSaveTokenHttpRequest(
        uniqueId: String,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            applicationRepository.generateAndSaveTokenHttpRequest(uniqueId).fold(
                onSuccess = {
                    onSuccess()
                },
                onFailure = {
                    onFailure()
                }
            )
        }
    }

    fun updateSessionStatus(
        uniqueId: String,
        status: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            applicationRepository.updateSessionStatus(uniqueId = uniqueId, status = status).fold(
                onSuccess = {
                    withContext(Dispatchers.Main) {
                        generateAndSaveTokenHttpRequest(
                            uniqueId,
                            onSuccess = {
                                onSuccess()
                            },
                            onFailure = {
                                onFailure()
                            }
                        )
                    }
                },
                onFailure = { exception ->
                    withContext(Dispatchers.Main) {
                        onFailure()
                    }
                }
            )
        }
    }

    fun sendVerificationCode(
        email: String,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            applicationRepository.sendVerificationCode(email).fold(
                onSuccess = {
                    onSuccess()
                },
                onFailure = { exception ->
                    setMessage("${exception.message}", MessageType.ERROR)
                    onFailure()
                }
            )
        }
    }

    fun verifyCode(
        email: String,
        userEnteredVerificationCode: String,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            applicationRepository.verifyCode(email, userEnteredVerificationCode).fold(
                onSuccess = {
                    setVerificationStatus(true)
                    setInitEmailVerified(true)
                    onSuccess()
                },
                onFailure = { exception ->
                    setMessage("${exception.message}", MessageType.ERROR)
                    onFailure()
                }
            )
        }
    }

    fun checkChangeAvailability(
        timestampFieldName: String,
        thresholdDays: Long,
        changeType: String,
        onSuccess: (Boolean, Int?) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val result =
                applicationRepository.isValueAvailableForChanges(timestampFieldName, thresholdDays)
            val (available, daysLeft) = result.getOrDefault(Pair(true, null))

            if (!available) {
                setMessage(
                    "თქვენ ახლახანს შეცვალეთ $changeType, შეცვლა შესაძლებელი იქნება $daysLeft დღეში.", MessageType.INFO
                )
            }
            onSuccess(available, daysLeft)
        }
    }

    fun validateAndChangePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String,
        onSuccess: (Boolean, String) -> Unit,
    ) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            val errorMessage = when {
                currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmNewPassword.isEmpty() -> {
                    "გთხოვთ გაიმეოროთ ახალი პაროლი შესაბამის ველში."
                }
                currentPassword.isEmpty() && newPassword.isNotEmpty() && confirmNewPassword.isNotEmpty() -> {
                    "გთხოვთ შეიყვანოთ მიმდინარე პაროლი."
                }
                currentPassword.isNotEmpty() && newPassword.isEmpty() && confirmNewPassword.isNotEmpty() -> {
                    "გთხოვთ შეიყვანოთ ახალი პაროლი."
                }
                currentPassword.isEmpty() && newPassword.isEmpty() && confirmNewPassword.isNotEmpty() -> {
                    "გთხოვთ შეიყვანოთ მიმდინარე და ახალი პაროლები."
                }
                currentPassword.isNotEmpty() && newPassword.isEmpty() && confirmNewPassword.isEmpty() -> {
                    "გთხოვთ შეიყვანეთ და გაიმეორეთ ახალი პაროლი."
                }
                currentPassword.isEmpty() && newPassword.isEmpty() && confirmNewPassword.isEmpty() -> {
                    "გთხოვთ შეავსეთ ყველა ველი."
                }
                else -> ""
            }

            onSuccess(false, errorMessage)
            return
        }


        if (newPassword != confirmNewPassword) {
            onSuccess(false, "პაროლები არ ემთხვევა.")
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                onSuccess(true, "პაროლი წარმატებით შეიცვალა.")
                            } else {
                                onSuccess(
                                    false,
                                    "პაროლი შეცვლის დროს დაფიქსირდა შეცდომა, გთხოვთ სცადოთ მოგვიანებით."
                                )
                            }
                        }
                } else {
                    onSuccess(false, "მიმდინარე პაროლი არასწორია..")
                }
            }
        } else {
            onSuccess(false, "მომხმარებელი ვერ მოიძებნა.")
        }
    }

    fun signOut() {
        applicationRepository.signOut()
    }
}
