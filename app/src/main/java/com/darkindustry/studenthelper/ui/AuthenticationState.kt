package com.darkindustry.studenthelper.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthenticationState {
    sealed class AuthenticationState {
        data object Authenticated : AuthenticationState()
        data object Unauthenticated : AuthenticationState()
    }

    private val authenticationStateMutable = MutableStateFlow<AuthenticationState>(AuthenticationState.Unauthenticated)
    internal val authenticationState: StateFlow<AuthenticationState> get() = authenticationStateMutable.asStateFlow()

    internal fun setAuthenticationState(authenticationState: AuthenticationState) {
        when (authenticationState) {
            is AuthenticationState.Authenticated -> {
                authenticationStateMutable.value = AuthenticationState.Authenticated
            }

            is AuthenticationState.Unauthenticated -> {
                authenticationStateMutable.value = AuthenticationState.Unauthenticated
            }
        }
    }
}