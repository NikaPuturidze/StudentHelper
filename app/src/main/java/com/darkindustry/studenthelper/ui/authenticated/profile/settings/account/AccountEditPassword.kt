package com.darkindustry.studenthelper.ui.authenticated.profile.settings.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.MessageType
import com.darkindustry.studenthelper.logic.utils.Utils
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationButton
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationTextField
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomAlertDialog
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AccountEditPassword(
    profileViewModel: ProfileViewModel = hiltViewModel(), navController: NavHostController,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.onBackground)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val currentPassword by profileViewModel.currentPassword.collectAsState()
    val newPassword by profileViewModel.newPassword.collectAsState()
    val confirmNewPassword by profileViewModel.confirmNewPassword.collectAsState()
    val showPassword by profileViewModel.passwordChangeVisibility.collectAsState()
    var showBackDialog by remember { mutableStateOf(false) }
    var passwordChanged by remember { mutableStateOf(false) }

    val message by profileViewModel.message.collectAsState()
    val messageType by profileViewModel.messageType.collectAsState()
    val messageState by profileViewModel.messageState.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    if (showBackDialog) {
        CustomAlertDialog(
            title = "შეუნახავი ცვლილებები",
            message = "გასვლის შემთხვევაში, ყოველგვარი ცვლილება დაიკარგება, დარწმუნებული ხარ რომ გასვლა გსურს?",
            confirmButtonText = "გასვლა",
            cancelButtonText = "გაგრძელება",
            onConfirm = {
                focusManager.clearFocus()
                navController.popBackStack()
            },
            onCancel = {
                focusManager.clearFocus()
                showBackDialog = false
            })
    }

    Column(
        Modifier.fillMaxHeight()
    ) {
        CustomHeader(
            title = stringResource(R.string.authenticated_settings_account_edit_password_header),
            leftIcon = R.drawable.ic_arrow_left,
            onLeftClick = {
                if (currentPassword.isEmpty() && newPassword.isEmpty() && confirmNewPassword.isEmpty() || passwordChanged) {
                    navController.popBackStack()
                } else {
                    showBackDialog = true
                }
            })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(Utils.GLOBAL_PADDINGS),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ApplicationTextField(
                topText = stringResource(R.string.authenticated_settings_account_edit_password_current_password_label),
                value = currentPassword,
                onValueChange = profileViewModel::onOldPasswordChange,
                placeholderText = stringResource(R.string.authenticated_settings_account_edit_password_current_password_placeholder),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_lock),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                        contentDescription = "Password",
                        modifier = Modifier.size(22.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = profileViewModel::onPasswordChangeVisibilityChanged) {
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

            ApplicationTextField(
                topText = stringResource(R.string.authenticated_settings_account_edit_password_new_password_label),
                value = newPassword,
                onValueChange = profileViewModel::onNewPasswordChange,
                placeholderText = stringResource(R.string.authenticated_settings_account_edit_password_new_password_placeholder),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_lock),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                        contentDescription = "Password",
                        modifier = Modifier.size(22.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = profileViewModel::onPasswordChangeVisibilityChanged) {
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

            ApplicationTextField(
                topText = stringResource(R.string.authenticated_settings_account_edit_password_confirm_new_password_label),
                value = confirmNewPassword,
                onValueChange = profileViewModel::onConfirmNewPasswordChange,
                placeholderText = stringResource(R.string.authenticated_settings_account_edit_password_confirm_new_password_placeholder),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_lock),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                        contentDescription = "Password",
                        modifier = Modifier.size(22.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = profileViewModel::onPasswordChangeVisibilityChanged) {
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
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .padding(bottom = 12.dp)
            )

            ApplicationButton(
                text = stringResource(R.string.authenticated_settings_account_edit_password_change_password_button),
                onClick = {
                    focusManager.clearFocus()
                    profileViewModel.validateAndChangePassword(
                        currentPassword = currentPassword,
                        newPassword = newPassword,
                        confirmNewPassword = confirmNewPassword
                    ) { success, message ->
                        if (success) {
                            passwordChanged = true
                            profileViewModel.setMessage(message, MessageType.SUCCESS)
                            profileViewModel.currentPasswordMutable.value = ""
                            profileViewModel.newPasswordMutable.value = ""
                            profileViewModel.confirmNewPasswordMutable.value = ""
                        } else {
                            profileViewModel.setMessage(message, MessageType.ERROR)
                        }
                    }
                })
        }
    }
    MessageBox(message = message, messageType = messageType, visible = messageState)
}