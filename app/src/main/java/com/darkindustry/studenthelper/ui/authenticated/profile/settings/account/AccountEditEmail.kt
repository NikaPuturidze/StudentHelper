package com.darkindustry.studenthelper.ui.authenticated.profile.settings.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.api.tsu.ApiViewModel
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.MessageType
import com.darkindustry.studenthelper.logic.utils.Utils
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationButton
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationTextField
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomAlertDialog
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.Timestamp

@Composable
fun AccountEditEmail(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    apiViewModel: ApiViewModel = hiltViewModel(),
    navController: NavHostController,
    dbEmail: String,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val showCodeEnterField by profileViewModel.showCodeEnterField.collectAsState()
    val userEnteredCode by profileViewModel.userEnteredVerificationCodeMutable.collectAsState()
    val isCodeVisible by profileViewModel.codeVisible.collectAsState()

    val message by profileViewModel.message.collectAsState()
    val messageState by profileViewModel.messageState.collectAsState()
    val messageType by profileViewModel.messageType.collectAsState()

    val (isResendEnabled, setResendEnabled) = remember { mutableStateOf(true) }
    val (isEmailVerificationEnabled, setEmailVerificationEnabled) = remember { mutableStateOf(true) }

    var newEmail by remember { mutableStateOf("") }
    var showBackDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val regex = "^[^@]+@[^@]+\\.[^@]+\$".toRegex()

    if (showBackDialog) {
        CustomAlertDialog(title = "Unsaved Changes",
            message = "You have unsaved changes. Are you sure you want to leave without saving them?",
            confirmButtonText = "Exit",
            cancelButtonText = "Cancel",
            onConfirm = {
                navController.popBackStack()
                navController.popBackStack()
            },
            onCancel = {
                focusManager.clearFocus()
                showBackDialog = false
            }
        )
    }

    if (showConfirmDialog) {
        CustomAlertDialog(title = "Confirm changes",
            message = "Are you sure you want to save these changes? You'll be unable to change your email again for the next 30 days",
            confirmButtonText = "Confirm",
            cancelButtonText = "Discard",
            onConfirm = {
                focusManager.clearFocus()
                apiViewModel.updateUserData(fieldPath = "email", newValue = newEmail)
                apiViewModel.updateUserData(
                    fieldPath = "emailChangedAt",
                    newValue = Timestamp.now()
                )
                isSaved = true
                navController.popBackStack()
                navController.popBackStack()
            },
            onCancel = {
                focusManager.clearFocus()
                navController.popBackStack()
                navController.popBackStack()
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
        AccountEditEmailForm(
            profileViewModel = profileViewModel,
            navController = navController,
            isSaved = isSaved,
            showCodeEnterField = showCodeEnterField,
            isCodeVisible = isCodeVisible,
            isEmailVerificationEnabled = isEmailVerificationEnabled,
            isResendEnabled = isResendEnabled,
            newEmail = newEmail,
            initialEmail = dbEmail,
            userEnteredCode = userEnteredCode,
            focusRequester = focusRequester,
            focusManager = focusManager,
            regex = regex,
            setEmailVerificationEnabled = { setEmailVerificationEnabled(it) },
            setResendEnabled = { setResendEnabled(it) },
            showBackDialog = { showBackDialog = it },
            showConfirmDialog = { showConfirmDialog = it },
            onEmailChange = { newValue ->
                newEmail = newValue
                isSaved = false
            }
        )
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}

@Composable
fun AccountEditEmailForm(
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    isSaved: Boolean,
    showCodeEnterField: Boolean,
    isCodeVisible: Boolean,
    isEmailVerificationEnabled: Boolean,
    isResendEnabled: Boolean,
    newEmail: String,
    initialEmail: String,
    userEnteredCode: String,
    focusRequester: FocusRequester,
    focusManager: FocusManager,
    regex: Regex,
    setEmailVerificationEnabled: (Boolean) -> Unit,
    setResendEnabled: (Boolean) -> Unit,
    showBackDialog: (Boolean) -> Unit,
    showConfirmDialog: (Boolean) -> Unit,
    onEmailChange: (String) -> Unit,
) {
    CustomHeader(title = "Email", left = {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(32.dp)
        )
    }, onLeftClick = {
        if (isSaved || newEmail == initialEmail) {
            navController.popBackStack()
            navController.popBackStack()
        } else {
            showBackDialog(true)
        }
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Utils.GLOBAL_PADDINGS),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ApplicationTextField(
            topTextComposable = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Email", style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.77f),
                        )
                    )
                }
            },
            value = newEmail,
            onValueChange = onEmailChange,
            placeholderText = "Enter new email",
            trailingIcon = {
                Row(
                    modifier = Modifier.padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    val annotatedString = buildAnnotatedString {
                        pushStringAnnotation(tag = "ACTION", annotation = "performAction")
                        append("Verify email")
                        pop()
                    }

                    @Suppress("DEPRECATION") ClickableText(text = annotatedString,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = if (isEmailVerificationEnabled && newEmail.isNotEmpty() && regex.matches(
                                    newEmail
                                ) && initialEmail != newEmail
                            ) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            }
                        ),
                        onClick = { offset ->
                            if (isEmailVerificationEnabled && newEmail.isNotEmpty() && regex.matches(
                                    newEmail
                                ) && initialEmail != newEmail
                            ) {
                                annotatedString.getStringAnnotations(
                                    tag = "ACTION", start = offset, end = offset
                                ).firstOrNull()?.let { annotation ->
                                    if (annotation.item == "performAction") {
                                        focusManager.clearFocus()
                                        profileViewModel.sendVerificationCode(
                                            newEmail,
                                            onSuccess = {
                                                profileViewModel.setShowCodeEnterField(true)
                                                setEmailVerificationEnabled(false)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .padding(bottom = 12.dp)
        )

        if (showCodeEnterField) {
            ApplicationTextField(
                topTextComposable = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Verification Code",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.77f),
                            )
                        )
                    }
                },
                value = userEnteredCode,
                onValueChange = { if (it.length <= 6) profileViewModel.onUserEnteredCodeChange(it) },
                placeholderText = "Enter Code",
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = profileViewModel::onCodeVisibilityChanged) {
                            Image(
                                painter = painterResource(
                                    id = if (isCodeVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                                ),
                                contentDescription = if (isCodeVisible) "Hide password" else "Show password",
                                colorFilter = ColorFilter.tint(
                                    MaterialTheme.colorScheme.secondary.copy(
                                        alpha = 0.33f
                                    )
                                ),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(2.dp))

                        Row(
                            modifier = Modifier.padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            val annotatedString = buildAnnotatedString {
                                pushStringAnnotation(tag = "ACTION", annotation = "performAction")
                                append("Resend Code")
                                pop()
                            }

                            @Suppress("DEPRECATION") ClickableText(text = annotatedString,
                                style = TextStyle(
                                    fontSize = 18.sp, color = if (isResendEnabled) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                    }
                                ),
                                onClick = { offset ->
                                    if (isResendEnabled) {
                                        annotatedString.getStringAnnotations(
                                            tag = "ACTION", start = offset, end = offset
                                        ).firstOrNull()?.let { annotation ->
                                            if (annotation.item == "performAction") {
                                                focusManager.clearFocus()
                                                profileViewModel.sendVerificationCode(
                                                    newEmail,
                                                    onSuccess = {
                                                        setResendEnabled(false)
                                                        profileViewModel.setMessage(
                                                            "ვერიფიკაციის კოდი გაიგზავნა!",
                                                            MessageType.SUCCESS
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 12.dp)

            )
        }

        ApplicationButton(
            onClick = {
                showConfirmDialog(true)
            },
            text = "Change Email",
            enabled = userEnteredCode.length == 6 && newEmail.isNotEmpty() && regex.matches(
                newEmail
            ) && initialEmail != newEmail
        )
    }
}
