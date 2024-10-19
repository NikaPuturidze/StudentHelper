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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.MessageType
import com.darkindustry.studenthelper.logic.utils.Utils
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
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val warningAboutEmailChangeText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f))) {
            append("Email change are available only once in ")
        }
        withStyle(style = SpanStyle(color = Color.Red.copy(alpha = 0.67f))) {
            append("30 DAYS.")
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

    if (verificationStatus) {
        navController.navigate(
            NavigationRoute.Authenticated.Settings.Account.AccountInformation.EditEmail.route.also {
                profileViewModel.setVerificationStatus(false)
            }
        )
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
            setResendCode = setResendCode
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
) {
    var codeSend by remember { mutableStateOf(false) }

    CustomHeader(title = "Email", left = {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(32.dp)
        )
    }, onLeftClick = {
        navController.popBackStack()
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
                        }
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
            text = "Verify Email",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Please verify your current email address, $currentEmail, before making changes.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onClick, modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .height(48.dp), colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ), shape = RoundedCornerShape(size = 12.dp)
        ) {
            Text(
                text = "Send Verification Code", style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 20.sp
                )
            )
        }

        Text(
            text = warningAboutEmailChangeText,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.errorContainer,
                fontSize = 14.sp,
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
    onVerifyClick: () -> Unit,
) {
    val view = LocalView.current
    var keyboardHeight by remember { mutableIntStateOf(0) }

    LocalDensity.current.density
    val keyboardHeightDp by rememberUpdatedState(with(LocalDensity.current) { keyboardHeight.toDp() })

    val bottomPadding = if (keyboardHeightDp > 0.dp) {
        maxOf(12.dp, keyboardHeightDp - 12.dp)
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
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Verify your email", style = MaterialTheme.typography.displaySmall.copy(
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 28.sp,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = buildAnnotatedString {
                append("Enter the 6 digit code we sent to\n ")
                append(currentEmail)
            }, style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.secondary, fontSize = 20.sp
            ), textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = userEnteredVerificationCode,
            textStyle = TextStyle(fontSize = 17.sp),
            onValueChange = profileViewModel::onUserEnteredCodeChange,
            placeholder = {
                Text(
                    text = "Code",
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                    fontSize = 18.sp,
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.secondary,
                unfocusedTextColor = MaterialTheme.colorScheme.secondary,
            ),
            visualTransformation = if (codeVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                                MaterialTheme.colorScheme.secondary.copy(
                                    alpha = 0.33f
                                )
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))

                    val annotatedString = buildAnnotatedString {
                        pushStringAnnotation(tag = "ACTION", annotation = "performAction")
                        append("Resend code")
                        pop()
                    }

                    ClickableText(text = annotatedString, style = TextStyle(
                        fontSize = 18.sp, color = if (resendCode) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        }
                    ), onClick = { offset ->
                        if (resendCode) {
                            annotatedString.getStringAnnotations(
                                tag = "ACTION", start = offset, end = offset
                            ).firstOrNull()?.let { annotation ->
                                if (annotation.item == "performAction") {
                                    onCodeResend()
                                    setResendCode(false)
                                }
                            }
                        }
                    })
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.onBackground),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onVerifyClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(size = 8.dp)
        ) {
            Text(
                text = "Continue", style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary, fontSize = 20.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(bottomPadding))
    }
}