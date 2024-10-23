package com.darkindustry.studenthelper.ui.authentication.login.passwordRecovery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationButton
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationTextField
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.navigation.NavigationRoute
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun PasswordRecoveryContentPasswordEnter(
    passwordRecoveryViewModel: PasswordRecoveryViewModel = hiltViewModel(),
    navController: NavController,
    email: String,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val password by passwordRecoveryViewModel.password.collectAsState()
    val confirmPassword by passwordRecoveryViewModel.confirmPassword.collectAsState()
    val showPassword by passwordRecoveryViewModel.passwordVisible.collectAsState()

    val messageState by passwordRecoveryViewModel.messageState.collectAsState()
    val messageType by passwordRecoveryViewModel.messageType.collectAsState()
    val message by passwordRecoveryViewModel.message.collectAsState()

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PasswordRecoveryContentEnterPasswordForm(
                passwordRecoveryViewModel = passwordRecoveryViewModel,
                navController = navController,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                showPassword = showPassword,
                focusManager = focusManager,
                isLoading = { isLoading = it },
                focusRequester = focusRequester
            )
        }

        MessageBox(
            message = message,
            messageType = messageType,
            visible = messageState,
        )
    }
}

@Composable
private fun PasswordRecoveryContentEnterPasswordForm(
    passwordRecoveryViewModel: PasswordRecoveryViewModel,
    navController: NavController,
    email: String,
    password: String,
    confirmPassword: String,
    showPassword: Boolean,
    focusManager: FocusManager,
    focusRequester: FocusRequester,
    isLoading: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(Utils.GLOBAL_PADDINGS),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PopBack(navController)
        Spacer(modifier = Modifier.height(36.dp))
        PasswordRecoveryEnterHeader()
        Spacer(modifier = Modifier.height(32.dp))
        ApplicationTextField(
            topText = stringResource(R.string.authentication_password_recovery_enter_password_new_password_label),
            value = password,
            onValueChange = passwordRecoveryViewModel::onPasswordChange,
            placeholderText = stringResource(R.string.authentication_password_recovery_enter_password_new_password_placeholder),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_lock),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    contentDescription = "Password",
                    modifier = Modifier.size(22.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = passwordRecoveryViewModel::onPasswordVisibilityChanged) {
                    Image(
                        painter = painterResource(
                            id = if (showPassword) {
                                R.drawable.ic_visibility
                            } else {
                                R.drawable.ic_visibility_off
                            }
                        ),
                        contentDescription = if (showPassword) {
                            "Hide password"
                        } else {
                            "Show password"
                        },
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.secondary.copy(
                                alpha = 0.67f
                            )
                        ),
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier.focusRequester(focusRequester)
        )
        Spacer(modifier = Modifier.height(16.dp))
        ApplicationTextField(
            topText = stringResource(R.string.authentication_password_recovery_enter_password_confirm_password_label),
            value = confirmPassword,
            onValueChange = passwordRecoveryViewModel::onConfirmPasswordChange,
            placeholderText = stringResource(R.string.authentication_password_recovery_enter_password_confirm_password_placeholder),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_lock),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    contentDescription = "Password",
                    modifier = Modifier.size(22.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = passwordRecoveryViewModel::onPasswordVisibilityChanged) {
                    Image(
                        painter = painterResource(
                            id = if (showPassword) {
                                R.drawable.ic_visibility
                            } else {
                                R.drawable.ic_visibility_off
                            }
                        ),
                        contentDescription = if (showPassword) {
                            "Hide password"
                        } else {
                            "Show password"
                        },
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.secondary.copy(
                                alpha = 0.67f
                            )
                        ),
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        ApplicationButton(
            text = stringResource(R.string.authentication_password_recovery_enter_password_change_password),
            onClick = {
                focusManager.clearFocus()
                isLoading(true)
                passwordRecoveryViewModel.validateAndChangePassword(
                    password = password,
                    confirmPassword = confirmPassword,
                    onResult = {
                        passwordRecoveryViewModel.changePassword(
                            newPassword = password
                        ) {
                            navController.navigate(
                                NavigationRoute.Authentication.Login.route
                            )
                        }
                    },
                    onError = {
                        isLoading(false)
                    }
                )
            }
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PopBack(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        CustomHeader(
            leftIcon = R.drawable.ic_arrow_left,
            onLeftClick = {
                navController.popBackStack()
            }
        )
    }
}

@Composable
fun PasswordRecoveryEnterHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.authentication_password_recovery_enter_password_header),
            style = MaterialTheme.typography.displaySmall.copy(
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 24.sp,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.authentication_password_recovery_enter_password_header_text),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                fontSize = 16.sp,
            )
        )
    }
}
