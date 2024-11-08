package com.darkindustry.studenthelper.ui.authentication.registration

import android.view.ViewTreeObserver
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationButton
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationTextField
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.ui.AuthenticationState.AuthenticationState
import com.darkindustry.studenthelper.ui.AuthenticationState.setAuthenticationState
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun RegistrationVerifyEmail(
    registrationViewModel: RegistrationViewModel = hiltViewModel(),
    navController: NavHostController,
    email: String,
    username: String,
    password: String,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val view = LocalView.current
    var keyboardHeight by remember { mutableIntStateOf(0) }

    LocalDensity.current.density
    val keyboardHeightDp by rememberUpdatedState(with(LocalDensity.current) { keyboardHeight.toDp() })

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

    val userEnteredVerificationCode by registrationViewModel.userEnteredVerificationCode.collectAsState()
    val codeVisible by registrationViewModel.codeVisible.collectAsState()
    val (resendCode, setResendCode) = remember { mutableStateOf(true) }

    val messageState by registrationViewModel.messageState.collectAsState()
    val messageType by registrationViewModel.messageType.collectAsState()
    val message by registrationViewModel.message.collectAsState()

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var hasFocused by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasFocused) {
            focusRequester.requestFocus()
            hasFocused = true
        }
    }

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
            RegistrationVerifyEmailForm(
                registrationViewModel = registrationViewModel,
                navController = navController,
                email = email,
                password = password,
                username = username,
                userEnteredVerificationCode = userEnteredVerificationCode,
                codeVisible = codeVisible,
                resendCode = resendCode,
                keyboardHeightDp = keyboardHeightDp,
                setResendCode = setResendCode,
                focusManager = focusManager,
                focusRequester = focusRequester,
                isLoading = { isLoading = it },
            )
        }

        MessageBox(
            message = message,
            messageType = messageType,
            visible = messageState
        )
    }
}


@Composable
private fun RegistrationVerifyEmailForm(
    registrationViewModel: RegistrationViewModel,
    navController: NavHostController,
    email: String,
    userEnteredVerificationCode: String,
    codeVisible: Boolean,
    resendCode: Boolean,
    keyboardHeightDp: Dp,
    setResendCode: (Boolean) -> Unit,
    focusManager: FocusManager,
    focusRequester: FocusRequester,
    isLoading: (Boolean) -> Unit,
    password: String,
    username: String,
) {
    val bottomPadding = if (keyboardHeightDp > 0.dp) {
        maxOf(100.dp, keyboardHeightDp - 40.dp)
    } else {
        12.dp
    }

    val annotatedString = buildAnnotatedString {
        pushStringAnnotation(tag = "ახალი კოდი", annotation = "performAction")
        withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
            append(stringResource(R.string.authentication_registration_verify_email_new_code))
        }
        pop()
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(Utils.GLOBAL_PADDINGS),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PopBack(navController)
        Spacer(modifier = Modifier.height(48.dp))
        EmailVerificationHeader()
        Spacer(modifier = Modifier.height(12.dp))
        EmailVerificationHeaderText(email)
        Spacer(modifier = Modifier.height(12.dp))
        ApplicationTextField(
            value = userEnteredVerificationCode,
            onValueChange = registrationViewModel::onUserEnteredCodeChange,
            placeholderText = stringResource(R.string.authentication_registration_verify_email_code_placeholder),
            trailingIcon = {
                Row(
                    modifier = Modifier.padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = registrationViewModel::onCodeVisibilityChanged) {
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
                                        registrationViewModel.sendVerificationCode(
                                            email,
                                            onSuccess = {
                                                setResendCode(false)
                                            },
                                            onFailure = {
                                                setResendCode(true)
                                            }
                                        )
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
            text = stringResource(R.string.authentication_registration_verify_email_next_button),
            onClick = {
                focusManager.clearFocus()
                isLoading(true)
                registrationViewModel.verifyCode(email, userEnteredVerificationCode,
                    onSuccess = {
                        registrationViewModel.registerUser(
                            email = email,
                            password = password,
                            username = username,
                            onSuccess = {
                                setAuthenticationState(AuthenticationState.Authenticated)
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
        Spacer(modifier = Modifier.height(bottomPadding))
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
private fun EmailVerificationHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.authentication_registration_verify_email_header),
            style = MaterialTheme.typography.displayMedium.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )
    }
}

@Composable
private fun EmailVerificationHeaderText(
    email: String,
) {
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
                    append("$email-ზე")
                }
            },
            textAlign = TextAlign.Center
        )
    }
}