package com.darkindustry.studenthelper.ui.authentication.registration

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.navigation.NavigationRoute
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationButton
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationTextField
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_ELEVATION
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun RegistrationScreen(
    registrationViewModel: RegistrationViewModel = hiltViewModel(),
    navController: NavController,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val email by registrationViewModel.email.collectAsState()
    val password by registrationViewModel.password.collectAsState()
    val username by registrationViewModel.username.collectAsState()
    val showPassword by registrationViewModel.passwordVisible.collectAsState()
    val messageState by registrationViewModel.messageState.collectAsState()
    val messageType by registrationViewModel.messageType.collectAsState()
    val message by registrationViewModel.message.collectAsState()

    val focusManager = LocalFocusManager.current
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RegistrationContentMainForm(
                registrationViewModel = registrationViewModel,
                navController = navController,
                email = email,
                password = password,
                username = username,
                showPassword = showPassword,
                focusManager = focusManager,
                isLoading = { isLoading = it }
            )
        }

        MessageBox(
            message = message, messageType = messageType, visible = messageState
        )
    }
}

@Composable
fun RegistrationContentMainForm(
    registrationViewModel: RegistrationViewModel,
    navController: NavController,
    email: String,
    password: String,
    username: String,
    showPassword: Boolean,
    focusManager: FocusManager,
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
        Spacer(modifier = Modifier.weight(1f))
        RegistrationHeader()
        Spacer(modifier = Modifier.weight(0.4f))
        ApplicationTextField(
            topText = stringResource(id = R.string.registration_email_label),
            value = email,
            onValueChange = registrationViewModel::onEmailChange,
            placeholderText = stringResource(id = R.string.registration_email_placeholder),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_envelope),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    contentDescription = "Email",
                    modifier = Modifier.size(20.dp)
                )
            })
        Spacer(modifier = Modifier.weight(0.1f))
        ApplicationTextField(
            topText = stringResource(id = R.string.registration_username_label),
            value = username,
            onValueChange = registrationViewModel::onUsernameChange,
            placeholderText = stringResource(id = R.string.registration_username_placeholder),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_envelope),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    contentDescription = "Email",
                    modifier = Modifier.size(20.dp)
                )
            })
        Spacer(modifier = Modifier.weight(0.1f))
        ApplicationTextField(
            topText = stringResource(id = R.string.registration_password_label),
            value = password,
            onValueChange = registrationViewModel::onPasswordChange,
            placeholderText = stringResource(id = R.string.registration_password_placeholder),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_lock),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    contentDescription = "Password",
                    modifier = Modifier.size(22.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = registrationViewModel::onPasswordVisibilityChanged) {
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
        Spacer(modifier = Modifier.weight(0.3f))
        ApplicationButton(
            text = stringResource(R.string.registration_register_button),
            onClick = {
                focusManager.clearFocus()
                isLoading(true)
                registrationViewModel.validateToRegister(
                    email = email,
                    password = password,
                    username = username,
                    onSuccess = {
                        registrationViewModel.sendVerificationCode(
                            email,
                            onSuccess = {
                                navController.navigate(
                                    NavigationRoute.Authentication.Registration.VerifyEmail.route
                                        .replace("{email}", email)
                                        .replace("{username}", username)
                                        .replace("{password}", password)
                                )
                            },
                            onFailure = {
                                isLoading(false)
                            }
                        )
                    },
                    onFailure = {
                        isLoading(false)
                    }
                )
            }
        )
        Spacer(modifier = Modifier.weight(0.4f))
        RegistrationSocialHeader()
        Spacer(modifier = Modifier.weight(0.2f))
        SocialRegistrationOptions()
        Spacer(modifier = Modifier.weight(1f))
        LoginPrompt { navController.popBackStack()}
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun RegistrationHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.registration_header),
            style = MaterialTheme.typography.displayMedium.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )
    }
}

@Composable
private fun RegistrationSocialHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.registration_social_header),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                textAlign = TextAlign.Center
            ),
        )
    }
}

@Composable
private fun SocialRegistrationOptions() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        SocialRegistrationButton(
            iconResId = R.drawable.ic_google, iconTint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(32.dp))

        SocialRegistrationButton(
            iconResId = R.drawable.ic_facebook, iconTint = Color(0xFF1877f2)
        )
    }
}

@Composable
private fun SocialRegistrationButton(iconResId: Int, iconTint: Color) {
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
private fun LoginPrompt(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ClickableText(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.67f),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                    letterSpacing = MaterialTheme.typography.bodyLarge.letterSpacing,
                )
            ) {
                append(stringResource(R.string.registration_login_prompt))
            }
        }, onClick = { onClick() })
    }
}