package com.darkindustry.studenthelper.ui.authenticated.profile.settings.account

import android.view.ViewTreeObserver
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.MessageType
import com.darkindustry.studenthelper.logic.utils.Utils
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationButton
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationTextField
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_TRANSITION_TIME
import com.darkindustry.studenthelper.navigation.NavigationRoute
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AccountEditEmailVerification(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController,
    dbEmail: String,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.onBackground)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val warningAboutEmailChangeText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f))) {
            append(stringResource(R.string.authenticated_settings_account_edit_email_verification_warning) + " ")
        }
        withStyle(style = SpanStyle(color = Color.Red.copy(alpha = 0.67f))) {
            append(stringResource(R.string.authenticated_settings_account_edit_email_verification_warning2))
        }
    }

    val message by profileViewModel.message.collectAsState()
    val messageState by profileViewModel.messageState.collectAsState()
    val messageType by profileViewModel.messageType.collectAsState()

    val userEnteredVerificationCode by profileViewModel.userEnteredVerificationCode.collectAsState()
    val verificationStatus by profileViewModel.verificationStatus.collectAsState()
    val codeVisible by profileViewModel.codeVisible.collectAsState()

    val (resendCode, setResendCode) = remember { mutableStateOf(true) }
    val currentEmail by remember { mutableStateOf(dbEmail) }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    if (verificationStatus) {
        navController.navigate(NavigationRoute.Authenticated.Settings.Account.AccountInformation.EditEmail.route) {
            launchSingleTop = true
            popUpTo(NavigationRoute.Authenticated.Settings.Account.AccountInformation.EditEmail.route) {
                saveState = true
            }
            restoreState = true
        }.also {
            profileViewModel.setVerificationStatus(false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AccountEditEmailVerificationForm(
            profileViewModel = profileViewModel,
            navController = navController,
            codeVisible = codeVisible,
            resendCode = resendCode,
            currentEmail = currentEmail,
            userEnteredVerificationCode = userEnteredVerificationCode,
            warningAboutEmailChangeText = warningAboutEmailChangeText,
            setResendCode = setResendCode,
            focusManager = focusManager,
            focusRequester = focusRequester,
        )
    }


    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}

@Composable
fun AccountEditEmailVerificationForm(
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    codeVisible: Boolean,
    resendCode: Boolean,
    currentEmail: String,
    userEnteredVerificationCode: String,
    warningAboutEmailChangeText: AnnotatedString,
    setResendCode: (Boolean) -> Unit,
    focusManager: FocusManager,
    focusRequester: FocusRequester,
) {
    var codeSend by remember { mutableStateOf(false) }

    CustomHeader(
        title = stringResource(R.string.authenticated_settings_account_edit_email_verification_header),
        leftIcon = R.drawable.ic_arrow_left,
        onLeftClick = {
            navController.popBackStack(
                route = NavigationRoute.Authenticated.Settings.Account.route,
                inclusive = false
            )
        })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Utils.GLOBAL_PADDINGS),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedContent(
            targetState = codeSend,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME,
                        easing = LinearEasing
                    )
                ) togetherWith fadeOut(
                    animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME,
                        easing = LinearEasing
                    )
                )
            }, label = "ProfileScreens"
        ) { targetState ->
            when (targetState) {
                false -> {
                    ContentVerifyEmail(
                        currentEmail, warningAboutEmailChangeText = warningAboutEmailChangeText,
                        onClick = {
                            profileViewModel.sendVerificationCode(
                                currentEmail,
                                onSuccess = {
                                    codeSend = true
                                }
                            )
                        },
                    )
                }

                true -> {
                    ContentEnterCode(
                        profileViewModel = profileViewModel,
                        currentEmail = currentEmail,
                        userEnteredVerificationCode = userEnteredVerificationCode,
                        codeVisible = codeVisible,
                        resendCode = resendCode,
                        setResendCode = setResendCode,
                        onCodeResend = {
                            profileViewModel.sendVerificationCode(
                                currentEmail,
                                onSuccess = {
                                    setResendCode(false)
                                    profileViewModel.setMessage(
                                        "ვერიფიკაციის კოდი გაიგზავნა!",
                                        MessageType.SUCCESS
                                    )
                                }
                            )
                        },
                        onVerifyClick = {
                            profileViewModel.verifyCode(
                                email = currentEmail,
                                userEnteredVerificationCode = userEnteredVerificationCode
                            )
                        },
                        codeSend = codeSend,
                        focusRequester = focusRequester,
                        focusManager = focusManager
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentVerifyEmail(
    currentEmail: String,
    onClick: () -> Unit,
    warningAboutEmailChangeText: AnnotatedString,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.authenticated_settings_account_edit_email_verification_header_label),
            style = MaterialTheme.typography.displaySmall.copy(
                color = MaterialTheme.colorScheme.secondary,
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(
                R.string.authenticated_settings_account_edit_email_verification_header_text,
                currentEmail
            ),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        ApplicationButton(
            text = stringResource(R.string.authenticated_settings_account_edit_email_verification_email_next_button),
            onClick = {
                onClick()
            },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = warningAboutEmailChangeText,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.errorContainer,
                textAlign = TextAlign.Center
            ),
        )

        Spacer(modifier = Modifier.weight(2f))
    }
}

@Composable
private fun ContentEnterCode(
    profileViewModel: ProfileViewModel,
    userEnteredVerificationCode: String,
    codeVisible: Boolean,
    resendCode: Boolean,
    setResendCode: (Boolean) -> Unit,
    currentEmail: String,
    onCodeResend: () -> Unit,
    focusRequester: FocusRequester,
    focusManager: FocusManager,
    onVerifyClick: () -> Unit,
    codeSend: Boolean,
) {
    LaunchedEffect(codeSend) {
        if (codeSend) {
            focusRequester.requestFocus()
        }
    }

    val view = LocalView.current
    var keyboardHeight by remember { mutableIntStateOf(0) }

    LocalDensity.current.density
    val keyboardHeightDp by rememberUpdatedState(with(LocalDensity.current) { keyboardHeight.toDp() })

    val bottomPadding = if (keyboardHeightDp > 0.dp) {
        maxOf(100.dp, keyboardHeightDp - 40.dp)
    } else {
        12.dp
    }

    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val insets = ViewCompat.getRootWindowInsets(view) ?: return@OnGlobalLayoutListener
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            keyboardHeight = imeInsets.bottom
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.authenticated_settings_account_edit_email_verification_verify_email_header),
                style = MaterialTheme.typography.displaySmall.copy(color = MaterialTheme.colorScheme.secondary)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                            letterSpacing = MaterialTheme.typography.bodyLarge.letterSpacing,
                        )
                    ) {
                        append(stringResource(R.string.authentication_registration_verify_email_header_text))
                    }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                            letterSpacing = MaterialTheme.typography.bodyLarge.letterSpacing,
                        )
                    ) {
                        append("$currentEmail-ზე")
                    }
                },
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ApplicationTextField(
            value = userEnteredVerificationCode,
            onValueChange = profileViewModel::onUserEnteredCodeChange,
            placeholderText = stringResource(R.string.authenticated_settings_account_edit_email_verification_verify_email_code_placeholder),
            trailingIcon = {
                Row(
                    modifier = Modifier.padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = profileViewModel::onCodeVisibilityChanged) {
                        Image(
                            painter = painterResource(
                                id = if (codeVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                            ),
                            contentDescription = if (codeVisible) "Hide password" else "Show password",
                            colorFilter = ColorFilter.tint(
                                MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))

                    val annotatedString = buildAnnotatedString {
                        pushStringAnnotation(tag = "ახალი კოდი", annotation = "performAction")
                        append(stringResource(R.string.authenticated_settings_account_edit_email_verification_email_new_code))
                        pop()
                    }

                    ClickableText(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (resendCode) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.33f)
                            }
                        ),
                        onClick = { offset ->
                            if (resendCode) {
                                annotatedString.getStringAnnotations(
                                    tag = "ახალი კოდი", start = offset, end = offset
                                ).firstOrNull()?.let { annotation ->
                                    if (annotation.item == "performAction") {
                                        onCodeResend()
                                        setResendCode(false)
                                    }
                                }
                            }
                        }
                    )
                }
            },
            visualTransformation = if (codeVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Number,
            modifier = Modifier.focusRequester(focusRequester)
        )

        Spacer(modifier = Modifier.weight(1f))

        ApplicationButton(
            text = stringResource(R.string.authentication_password_recovery_verify_email_next_button),
            onClick = {
                focusManager.clearFocus()
                onVerifyClick()
            }
        )

        Spacer(modifier = Modifier.height(bottomPadding))
    }
}