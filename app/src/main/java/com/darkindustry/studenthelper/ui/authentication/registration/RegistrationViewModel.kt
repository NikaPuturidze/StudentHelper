package com.darkindustry.studenthelper.ui.authentication.registration

import androidx.lifecycle.viewModelScope
import com.darkindustry.studenthelper.logic.data.ApplicationRepository
import com.darkindustry.studenthelper.logic.data.ApplicationViewModel
import com.darkindustry.studenthelper.logic.firebase.FirestoreListener
import com.darkindustry.studenthelper.logic.utils.MessageType
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository
) : ApplicationViewModel() {

    fun validateToRegister(
        email: String,
        password: String,
        username: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val missingFields = mutableListOf<String>()

        if (email.isEmpty()) missingFields.add("ელ.ფოსტა")
        if (password.isEmpty()) missingFields.add("პაროლი")
        if (username.isEmpty()) missingFields.add("მომხმარებელი")

        if (missingFields.isNotEmpty()) {
            val errorMessage = when (missingFields.size) {
                1 -> "შეიყვანეთ ${missingFields.first()}."
                2 -> "შეიყვანეთ ${missingFields.joinToString(" და ")}."
                else -> "შეიყვანეთ ${missingFields.dropLast(1).joinToString(", ")} და ${missingFields.last()}."
            }
            setMessage(errorMessage, MessageType.ERROR)
            onFailure()
            return
        }

        if (!isEmailValid(email)) {
            setMessage("არასწორი ელ.ფოსტის ფორმატი.", MessageType.ERROR)
            onFailure()
            return
        }

        if (username.length < 3){
            setMessage("მომხმარებლის სახელი მინიმუმ 3 სიმბოლსოგან უნდა შედგებოდეს.", MessageType.ERROR)
            onFailure()
            return
        }

        if (password.length <= 8) {
            setMessage("პაროლი მინიმუმ 8 სიმბოლოსგან უნდა შედგებოდეს.", MessageType.ERROR)
            onFailure()
            return
        }

        doesUserExist(email, applicationRepository) { userExist ->
            if (userExist) {
                setMessage("მოცემული ელ.ფოსტა უკვე დარეგისტრირებულია.", MessageType.ERROR)
                onFailure()
            } else {
                onSuccess()
            }
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

    fun registerUser(
        email: String,
        password: String,
        username: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            applicationRepository.registerUser(email, password, username).fold(
                onSuccess = {
                    FirestoreListener.listenToAllUserData()
                    onSuccess()
                },
                onFailure = { exception ->
                    setMessage("${exception.message}", MessageType.ERROR)
                    onFailure()
                }
            )
        }
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
}