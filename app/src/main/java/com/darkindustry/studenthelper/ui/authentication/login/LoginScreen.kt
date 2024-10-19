package com.darkindustry.studenthelper.ui.authentication.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.navigation.NavigationRoute
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationButton
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationTextField
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_ELEVATION
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    navController: NavController
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val showPassword by loginViewModel.passwordVisible.collectAsState()
    val messageState by loginViewModel.messageState.collectAsState()
    val messageType by loginViewModel.messageType.collectAsState()
    val message by loginViewModel.message.collectAsState()

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginMainForm(
            loginViewModel = loginViewModel,
            navController = navController,
            email = email,
            password = password,
            showPassword = showPassword,
            focusManager = focusManager
        )
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}

@Composable
private fun LoginMainForm(
    loginViewModel: LoginViewModel,
    navController: NavController,
    email: String,
    password: String,
    showPassword: Boolean,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(Utils.GLOBAL_PADDINGS),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        LoginHeader()
        Spacer(modifier = Modifier.weight(0.4f))
        ApplicationTextField(
            topText = stringResource(id = R.string.login_email_label),
            value = email,
            onValueChange = loginViewModel::onEmailChange,
            placeholderText = stringResource(id = R.string.login_email_placeholder),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_envelope),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    contentDescription = "Email",
                    modifier = Modifier.size(20.dp)
                )
            }
        )
        Spacer(modifier = Modifier.weight(0.1f))
        ApplicationTextField(
            topText = stringResource(id = R.string.login_password_label),
            value = password,
            onValueChange = loginViewModel::onPasswordChange,
            placeholderText = stringResource(id = R.string.login_password_placeholder),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_lock),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    contentDescription = "Password",
                    modifier = Modifier.size(22.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = loginViewModel::onPasswordVisibilityChanged) {
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
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f)),
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
        Spacer(modifier = Modifier.weight(0.15f))
        LoginForgotPasswordLink {
            navController.navigate(NavigationRoute.Authentication.PasswordRecovery.route)
        }
        Spacer(modifier = Modifier.weight(0.15f))
        ApplicationButton(
            text = stringResource(id = R.string.login_login_button),
            onClick = {
                focusManager.clearFocus()
                loginViewModel.authenticateUser(
                    email = email, password = password
                )
            },
        )
        Spacer(modifier = Modifier.weight(0.4f))
        SocialLoginHeader()
        Spacer(modifier = Modifier.weight(0.2f))
        SocialLoginOptions()
        Spacer(modifier = Modifier.weight(1f))
        RegisterPrompt { navController.navigate(NavigationRoute.Authentication.Registration.route) }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun LoginHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.login_header),
            style = MaterialTheme.typography.displayMedium.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )
    }
}

@Composable
private fun LoginForgotPasswordLink(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        ClickableText(text = buildAnnotatedString { append(stringResource(id = R.string.login_forgot_password)) },
            onClick = { onClick() },
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f)
            )
        )
    }
}

@Composable
private fun SocialLoginHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.login_social_header),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                textAlign = TextAlign.Center
            ),
        )
    }
}

@Composable
private fun SocialLoginOptions() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        SocialLoginButton(
            iconResId = R.drawable.ic_google, iconTint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(32.dp))

        SocialLoginButton(
            iconResId = R.drawable.ic_facebook, iconTint = Color(0xFF1877f2)
        )
    }
}

@Composable
private fun SocialLoginButton(iconResId: Int, iconTint: Color) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .shadow(elevation = GLOBAL_ELEVATION, shape = CircleShape, clip = true)
            .background(color = MaterialTheme.colorScheme.onBackground, shape = CircleShape)
            .clip(shape = CircleShape)
            .padding(12.dp)
            .wrapContentSize()
            .clickable(
                onClick = { }, interactionSource = interactionSource, indication = null
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun RegisterPrompt(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ClickableText(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                    letterSpacing = MaterialTheme.typography.bodyLarge.letterSpacing,
                )
            ) {
                append(stringResource(R.string.login_register_prompt))
            }
            addStyle(
                style = SpanStyle(color = MaterialTheme.colorScheme.primary),
                start = length - "შექმენი".length,
                end = length
            )
        }, onClick = { onClick() })
    }
}