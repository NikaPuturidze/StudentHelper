package com.darkindustry.studenthelper.ui.authentication.login

import androidx.lifecycle.viewModelScope
import com.darkindustry.studenthelper.logic.data.ApplicationRepository
import com.darkindustry.studenthelper.logic.data.ApplicationViewModel
import com.darkindustry.studenthelper.logic.firebase.FirestoreListener
import com.darkindustry.studenthelper.ui.AuthenticationState.AuthenticationState
import com.darkindustry.studenthelper.ui.AuthenticationState.setAuthenticationState
import com.darkindustry.studenthelper.logic.utils.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository
) : ApplicationViewModel() {
    fun authenticateUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            applicationRepository.authenticateUser(email, password,).fold(
                onSuccess = {
                    withContext(Dispatchers.Main) {
                        FirestoreListener.listenToAllUserData()
                        setAuthenticationState(AuthenticationState.Authenticated)
                    }
                },
                onFailure = { exception ->
                    withContext(Dispatchers.Main) {
                        setMessage("${exception.message}", MessageType.ERROR)
                    }
                }
            )
        }
    }
}