package com.darkindustry.studenthelper.ui.authentication.login.passwordRecovery

import androidx.lifecycle.viewModelScope
import com.darkindustry.studenthelper.logic.data.ApplicationRepository
import com.darkindustry.studenthelper.logic.data.ApplicationViewModel
import com.darkindustry.studenthelper.logic.utils.MessageType
import com.darkindustry.studenthelper.logic.utils.Utils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PasswordRecoveryViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository
) : ApplicationViewModel() {
    fun validateAndRecoverPassword(
        email: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        if (email.isEmpty()) {
            setMessage("შეიყვანეთ ელ.ფოსტა", MessageType.ERROR)
            onFailure()
            return
        }

        if (!isEmailValid(email)) {
            setMessage("არასწორი ელ.ფოსტის ფორმატი.", MessageType.ERROR)
            onFailure()
            return
        }

        onSuccess()
    }

    fun validateAndChangePassword(
        password: String,
        confirmPassword: String,
        onResult: () -> Unit,
        onError: () -> Unit
    ) {
        if (password.isEmpty() || password.isEmpty()) {
            val missingFields = mutableListOf<String>()
            if (password.isEmpty()) missingFields.add("Password")
            if (confirmPassword.isEmpty()) missingFields.add("Confirm Password")

            val errorMessage = if (missingFields.size == 1) {
                "Please enter your ${missingFields.first()}."
            } else {
                "Please fill in the following fields: ${missingFields.joinToString(", ")}."
            }

            onError()
            setMessage(errorMessage, MessageType.ERROR)
            return
        }

        if (password.length <= 8) {
            onError()
            setMessage("Password must be at least 8 characters", MessageType.ERROR)
            return
        }

        if (password != confirmPassword) {
            onError()
            setMessage("Passwords do not match", MessageType.ERROR)
            return
        }
        onResult()
    }

    fun sendVerificationCode(
        email: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit = {}
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

    fun changePassword(
        newPassword: String,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            applicationRepository.changePassword(newPassword).fold(
                onSuccess = {
                    setMessage("პაროლი წარმატებით შეიცვალა.", MessageType.SUCCESS)
                    onSuccess()
                },
                onFailure = {
                    setMessage("${it.message}", MessageType.ERROR)
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
}