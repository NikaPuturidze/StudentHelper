package com.darkindustry.studenthelper.ui.authenticated.profile.settings.account

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
import com.darkindustry.studenthelper.navigation.NavigationRoute
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun AccountEditEmail(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    apiViewModel: ApiViewModel = hiltViewModel(),
    navController: NavHostController,
    dbEmail: String,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.onBackground)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val userEnteredCode by profileViewModel.userEnteredVerificationCodeMutable.collectAsState()
    val isCodeVisible by profileViewModel.codeVisible.collectAsState()

    val message by profileViewModel.message.collectAsState()
    val messageState by profileViewModel.messageState.collectAsState()
    val messageType by profileViewModel.messageType.collectAsState()

    val (isResendEnabled, setResendEnabled) = remember { mutableStateOf(true) }

    var newEmail by remember { mutableStateOf("") }
    var showBackDialog by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val regex = "^[^@]+@[^@]+\\.[^@]+\$".toRegex()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    if (showBackDialog) {
        CustomAlertDialog(title = "შეუნახავი ცვლილებები",
            message = "გასვლის შემთხვევაში, ყოველგვარი ცვლილება დაიკარგება, დარწმუნებული ხარ რომ გასვლა გსურს?",
            confirmButtonText = "გასვლა",
            cancelButtonText = "გაგრძელება",
            onConfirm = {
                navController.popBackStack(
                    route = NavigationRoute.Authenticated.Settings.Account.route,
                    inclusive = false
                )
            },
            onCancel = {
                focusManager.clearFocus()
                showBackDialog = false
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
            isCodeVisible = isCodeVisible,
            isResendEnabled = isResendEnabled,
            newEmail = newEmail,
            initialEmail = dbEmail,
            userEnteredCode = userEnteredCode,
            focusRequester = focusRequester,
            focusManager = focusManager,
            regex = regex,
            setResendEnabled = { setResendEnabled(it) },
            showBackDialog = { showBackDialog = it },
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

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun AccountEditEmailForm(
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    isSaved: Boolean,
    isCodeVisible: Boolean,
    isResendEnabled: Boolean,
    newEmail: String,
    initialEmail: String,
    userEnteredCode: String,
    focusRequester: FocusRequester,
    focusManager: FocusManager,
    regex: Regex,
    setResendEnabled: (Boolean) -> Unit,
    showBackDialog: (Boolean) -> Unit,
    onEmailChange: (String) -> Unit,
) {
    CustomHeader(
        title = stringResource(R.string.authenticated_settings_account_edit_email_header),
        leftIcon = R.drawable.ic_arrow_left,
        onLeftClick = {
            if (isSaved || newEmail == initialEmail) {
                navController.popBackStack(
                    route = NavigationRoute.Authenticated.Settings.Account.route,
                    inclusive = false
                )
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
        var proceed by remember { mutableStateOf(false) }

        ApplicationTextField(
            topText = stringResource(R.string.authenticated_settings_account_edit_email_new_email_label),
            value = newEmail,
            onValueChange = onEmailChange,
            placeholderText = stringResource(R.string.authenticated_settings_account_edit_email_new_email_placeholder),
            enabled = !proceed,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_envelope),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    contentDescription = "Email",
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.focusRequester(focusRequester)
        )
        Spacer(modifier = Modifier.height(16.dp))

        LaunchedEffect(proceed) {
            if (proceed) {
                focusRequester.requestFocus()
            }
        }

        if (proceed) {
            ApplicationTextField(
                topText = stringResource(R.string.authenticated_settings_account_edit_email_new_email_verify_label),
                value = userEnteredCode,
                onValueChange = profileViewModel::onUserEnteredCodeChange,
                placeholderText = stringResource(R.string.authenticated_settings_account_edit_email_new_email_verify_placeholder),
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
                                pushStringAnnotation(
                                    tag = "ახალი კოდი",
                                    annotation = "performAction"
                                )
                                append(stringResource(R.string.authenticated_settings_account_edit_email_new_email_resend_code))
                                pop()
                            }

                            ClickableText(text = annotatedString,
                                style = TextStyle(
                                    color = if (isResendEnabled) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                    }
                                ),
                                onClick = { offset ->
                                    if (isResendEnabled) {
                                        annotatedString.getStringAnnotations(
                                            tag = "ახალი კოდი", start = offset, end = offset
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
                keyboardType = KeyboardType.Number,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .focusRequester(focusRequester)
            )
        }
        if (!proceed) {
            ApplicationButton(
                text = "გაგრძელება",
                onClick = {
                    if (newEmail.isNotEmpty() && regex.matches(
                            newEmail
                        ) && initialEmail != newEmail
                    ) {
                        profileViewModel.checkEmailtoChange(
                            newEmail,
                            onSuccess = {
                                profileViewModel.sendVerificationCode(
                                    newEmail,
                                    onSuccess = { proceed = true }
                                )
                                focusManager.clearFocus()
                            }
                        )
                    } else {
                        profileViewModel.setMessage("შეიყვანეთ ახალი ელ.ფოსტა", MessageType.ERROR)
                    }
                }
            )
        } else {
            ApplicationButton(
                text = "ელ.ფოსტის შეცვლა",
                onClick = {
                    focusManager.clearFocus()
                    profileViewModel.verifyCode(
                        newEmail,
                        userEnteredCode,
                        onSuccess = {
                            profileViewModel.changeCurrentUserEmail(newEmail,
                                onSuccess = {
                                    navController.popBackStack(
                                        route = NavigationRoute.Authenticated.Settings.Account.route,
                                        inclusive = false
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    }
}
