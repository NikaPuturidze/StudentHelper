package com.darkindustry.studenthelper.logic.data

import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkindustry.studenthelper.logic.utils.MessageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

open class ApplicationViewModel @Inject constructor() : ViewModel() {
    // State Flow Properties

    private val emailMutable = MutableStateFlow("")
    val email: StateFlow<String> get() = emailMutable

    private val passwordMutable = MutableStateFlow("")
    val password: StateFlow<String> get() = passwordMutable

    private val confirmPasswordMutable = MutableStateFlow("")
    val confirmPassword: StateFlow<String> get() = confirmPasswordMutable

    var currentPasswordMutable = MutableStateFlow("")
    val currentPassword: StateFlow<String> get() = currentPasswordMutable

    val newPasswordMutable = MutableStateFlow("")
    val newPassword: StateFlow<String> get() = newPasswordMutable

    val confirmNewPasswordMutable = MutableStateFlow("")
    val confirmNewPassword: StateFlow<String> get() = confirmNewPasswordMutable

    private val passwordChangeVisibilityMutable = MutableStateFlow(false)
    val passwordChangeVisibility: StateFlow<Boolean> get() = passwordChangeVisibilityMutable

    private val isEmailAvailableMutable = MutableStateFlow(false)

    private val messageMutable = MutableStateFlow("")
    val message: StateFlow<String> get() = messageMutable

    private val messageStateMutable = MutableStateFlow(false)
    val messageState: StateFlow<Boolean> get() = messageStateMutable

    private val messageTypeMutable = MutableStateFlow(MessageType.INFO)
    val messageType: StateFlow<MessageType> get() = messageTypeMutable

    val userEnteredVerificationCodeMutable = MutableStateFlow("")
    val userEnteredVerificationCode: StateFlow<String> get() = userEnteredVerificationCodeMutable

    private val passwordVisibleMutable = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> get() = passwordVisibleMutable

    private val codeVisibleMutable = MutableStateFlow(false)
    val codeVisible: StateFlow<Boolean> get() = codeVisibleMutable

    private val showCodeEnterFieldMutable = MutableStateFlow(false)
    val showCodeEnterField: StateFlow<Boolean> get() = showCodeEnterFieldMutable

    private val verificationStatusMutable = MutableStateFlow(false)
    val verificationStatus: StateFlow<Boolean> get() = verificationStatusMutable

    private val usernameMutable = MutableStateFlow("")
    val username: StateFlow<String> get() = usernameMutable

    private val sessionIdMutable = MutableStateFlow("")
    private val uniqueIdMutable = MutableStateFlow("")
    val uniqueId: StateFlow<String> get() = uniqueIdMutable

    private val initEmailVerifiedMutable = MutableStateFlow(false)
    val initEmailVerified: StateFlow<Boolean> get() = initEmailVerifiedMutable

    private val lastUsernameChangeMutable = MutableStateFlow(false)
    val valueChangedAt: StateFlow<Boolean> get() = lastUsernameChangeMutable

    // Methods for State Management
    fun setSessionId(sessionId: String) {
        sessionIdMutable.value = sessionId
    }

    fun setUniqueId(uniqueId: String) {
        uniqueIdMutable.value = uniqueId
    }

    fun setVerificationStatus(verificationState: Boolean) {
        verificationStatusMutable.value = verificationState
    }

    fun setEmailAvailable(emailAvailable: Boolean) {
        isEmailAvailableMutable.value = emailAvailable
    }

    fun setInitEmailVerified(emailVerified: Boolean) {
        initEmailVerifiedMutable.value = emailVerified
    }

    fun setShowCodeEnterField(showCodeEnterField: Boolean) {
        showCodeEnterFieldMutable.value = showCodeEnterField
    }

    fun onEmailClear() {
        emailMutable.value = ""
    }

    fun onEmailChange(newEmail: String) {
        emailMutable.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        passwordMutable.value = newPassword
    }

    fun onOldPasswordChange(newPassword: String) {
        currentPasswordMutable.value = newPassword
    }

    fun onNewPasswordChange(newPassword: String) {
        newPasswordMutable.value = newPassword
    }

    fun onConfirmNewPasswordChange(newPassword: String) {
        confirmNewPasswordMutable.value = newPassword
    }

    fun onConfirmPasswordChange(newPassword: String) {
        confirmPasswordMutable.value = newPassword
    }

    fun onUsernameChange(newUsername: String) {
        usernameMutable.value = newUsername
    }

    fun onUsernameClear() {
        usernameMutable.value = ""
    }

    fun onPasswordVisibilityChanged() {
        passwordVisibleMutable.value = !passwordVisibleMutable.value
    }

    fun onPasswordChangeVisibilityChanged() {
        passwordChangeVisibilityMutable.value = !passwordChangeVisibilityMutable.value
    }

    fun onCodeVisibilityChanged() {
        codeVisibleMutable.value = !codeVisibleMutable.value
    }

    fun onUserEnteredCodeChange(newCode: String) {
        userEnteredVerificationCodeMutable.value = newCode
    }

    // Error Handling
    fun setMessage(message: String, messageType: MessageType) {
        messageTypeMutable.value = messageType
        viewModelScope.launch {
            messageMutable.value = message
            delay(120)
            messageStateMutable.value = true
            delay(3000)
            messageStateMutable.value = false
            delay(120)
            messageMutable.value = ""
        }
    }

    // User Validation
    protected fun doesUserExist(
        email: String,
        applicationRepository: ApplicationRepository,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            applicationRepository.doesUserExistByEmail(email).fold(
                onSuccess = { isAvailable ->
                    isEmailAvailableMutable.value = isAvailable
                    onResult(isAvailable)
                },
                onFailure = {
                    isEmailAvailableMutable.value = false
                    onResult(false)
                }
            )
        }
    }

    // Validation Helpers
    protected fun isEmailValid(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }
}