package com.darkindustry.studenthelper.ui.authenticated.profile.settings.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.api.tsu.ApiViewModel
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationTextField
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomAlertDialog
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.navigation.NavigationRoute
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.Timestamp

@Composable
fun AccountEditUsername(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    apiViewModel: ApiViewModel = hiltViewModel(),
    navController: NavHostController,
    dbUsername: String,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.onBackground)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val message by profileViewModel.message.collectAsState()
    val messageState by profileViewModel.messageState.collectAsState()
    val messageType by profileViewModel.messageType.collectAsState()

    var newUsername by remember { mutableStateOf(dbUsername) }
    val initialUsername by remember { mutableStateOf(dbUsername) }
    var showBackDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(true) }

    val regex = "^[a-zA-Z0-9_-]*$".toRegex()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    if (showBackDialog) {
        CustomAlertDialog(
            title = "შეუნახავი ცვლილებები",
            message = "გასვლის შემთხვევაში, ყოველგვარი ცვლილება დაიკარგება, დარწმუნებული ხარ რომ გასვლა გსურს?",
            confirmButtonText = "შენახვა და გასვლა",
            cancelButtonText = "გასვლა",
            onConfirm = {
                focusManager.clearFocus()
                apiViewModel.updateUserData(
                    fieldPath = "username",
                    newValue = newUsername,
                    onSuccess = {
                        apiViewModel.updateUserData(
                            fieldPath = "usernameChangedAt",
                            newValue = Timestamp.now(),
                            onSuccess = {
                                showBackDialog = false
                                isSaved = true
                                navController.popBackStack(
                                    route = NavigationRoute.Authenticated.Settings.Account.route,
                                    inclusive = false
                                )
                            }
                        )
                    }
                )
            },
            onCancel = {
                focusManager.clearFocus()
                showBackDialog = false
                navController.popBackStack(
                    route = NavigationRoute.Authenticated.Settings.Account.route,
                    inclusive = false
                )
            }
        )
    }

    if (showConfirmDialog) {
        CustomAlertDialog(
            title = "დარწმუნებული ხარ?",
            message = "მომხმარებლის სახელის ცვლილების შემთხვევაში, მომდევნო ცვლილება შესაძლებელი იქნება მხოლოდ 14 დღეში!",
            confirmButtonText = "დადასტურება",
            cancelButtonText = "გამოსვლა",
            onConfirm = {
                focusManager.clearFocus()
                apiViewModel.updateUserData(
                    fieldPath = "username",
                    newValue = newUsername,
                    onSuccess = {
                        apiViewModel.updateUserData(
                            fieldPath = "usernameChangedAt",
                            newValue = Timestamp.now(),
                            onSuccess = {
                                showBackDialog = false
                                isSaved = true
                                navController.popBackStack(
                                    route = NavigationRoute.Authenticated.Settings.Account.route,
                                    inclusive = false
                                )
                            }
                        )
                    }
                )
            },
            onCancel = {
                focusManager.clearFocus()
                showConfirmDialog = false
                navController.popBackStack(
                    route = NavigationRoute.Authenticated.Settings.Account.route,
                    inclusive = false
                )
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
        AccountEditUsernameForm(
            navController = navController,
            isSaved = isSaved,
            focusRequester = focusRequester,
            newUsername = newUsername,
            initialUsername = initialUsername,
            showBackDialog = { showBackDialog = it },
            showConfirmDialog = { showConfirmDialog = it },
            onUsernameChange = { newValue ->
                if (newValue.length <= 32 && regex.matches(newValue)) {
                    newUsername = newValue
                    isSaved = false
                }
            },
            onClearUsername = {
                newUsername = ""
                isSaved = false
            }
        )
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}

@Composable
fun AccountEditUsernameForm(
    navController: NavHostController,
    isSaved: Boolean,
    focusRequester: FocusRequester,
    newUsername: String,
    initialUsername: String,
    showBackDialog: (Boolean) -> Unit,
    showConfirmDialog: (Boolean) -> Unit,
    onUsernameChange: (String) -> Unit,
    onClearUsername: () -> Unit,
) {
    CustomHeader(
        title = "Username",
        leftIcon = R.drawable.ic_arrow_left,
        onLeftClick = {
            if (isSaved || newUsername == initialUsername) {
                navController.popBackStack()
            } else {
                showBackDialog(true)
            }
        },
        rightIcon = R.drawable.ic_checkmark,
        onRightClick = {
            showConfirmDialog(true)
        }
    )

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
                        text = "Username", style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.77f),
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${newUsername.length}/32",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.77f),
                            fontSize = 12.sp,
                        )
                    )
                }
            },
            value = newUsername,
            onValueChange = onUsernameChange,
            placeholderText = "Your Username",
            trailingIcon = {
                IconButton(onClick = onClearUsername) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            },
            modifier = Modifier.focusRequester(focusRequester)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Username must contain at least 4 characters.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.77f),
                )
            )
        }
    }
}