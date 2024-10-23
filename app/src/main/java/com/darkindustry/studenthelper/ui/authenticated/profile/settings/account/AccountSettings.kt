package com.darkindustry.studenthelper.ui.authenticated.profile.settings.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_ELEVATION
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_PADDINGS
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.SettingsItem
import com.darkindustry.studenthelper.navigation.NavigationRoute
import com.darkindustry.studenthelper.ui.AuthenticationState.AuthenticationState
import com.darkindustry.studenthelper.ui.AuthenticationState.setAuthenticationState
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.messaging.FirebaseMessaging

@Composable
fun AccountSettings(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController,
    dbUsername: String,
    dbEmail: String,
    dbPhone: String,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.onBackground)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val message by profileViewModel.message.collectAsState()
    val messageState by profileViewModel.messageState.collectAsState()
    val messageType by profileViewModel.messageType.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AccountSettingsForm(
            profileViewModel = profileViewModel,
            navController = navController,
            dbUsername = dbUsername,
            dbEmail = dbEmail,
            dbPhone = dbPhone
        )
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}

@Composable
fun AccountSettingsForm(
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    dbUsername: String,
    dbEmail: String,
    dbPhone: String,
) {
    CustomHeader(
        title = stringResource(R.string.authenticated_settings_account_header_title),
        leftIcon = R.drawable.ic_arrow_left,
        onLeftClick = {
            navController.popBackStack(
                route = NavigationRoute.Authenticated.Settings.General.route,
                inclusive = false
            )
        })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(GLOBAL_PADDINGS),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AccountInformation(
            dbUsername = dbUsername,
            dbEmail = dbEmail,
            dbPhone = dbPhone,
            onUsernameClick = {
                profileViewModel.checkChangeAvailability(
                    "usernameChangedAt",
                    14,
                    "სახელი"
                ) { isAvailable, _ ->
                    if (isAvailable) {
                        navController.navigate(NavigationRoute.Authenticated.Settings.Account.AccountInformation.EditUsername.route) {
                            launchSingleTop = true
                            popUpTo(NavigationRoute.Authenticated.Settings.Account.AccountInformation.EditUsername.route) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                }
            },
            onEmailClick = {
                profileViewModel.checkChangeAvailability(
                    "emailChangedAt",
                    30,
                    "ელ.ფოსტა"
                ) { isAvailable, _ ->
                    if (isAvailable) {
                        navController.navigate(NavigationRoute.Authenticated.Settings.Account.AccountInformation.VerifyEmail.route) {
                            launchSingleTop = true
                            popUpTo(NavigationRoute.Authenticated.Settings.Account.AccountInformation.VerifyEmail.route) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                }
            },
            onPhoneClick = {

            }

        )
        SecuritySettings(onPasswordClick = {
            navController.navigate(NavigationRoute.Authenticated.Settings.Account.Security.ChangePassword.route) {
                launchSingleTop = true
                popUpTo(NavigationRoute.Authenticated.Settings.Account.Security.ChangePassword.route) {
                    saveState = true
                }
                restoreState = true
            }
        }, onTwoFactorClick = { retrieveFCMToken() })
        AccountManagement(onDeleteAccountClick = { /*TODO*/ }, onLogoutClick = {
            setAuthenticationState(AuthenticationState.Unauthenticated)
            profileViewModel.signOut()
        })
    }
}

@Composable
fun AccountInformation(
    dbUsername: String,
    dbEmail: String,
    dbPhone: String,
    onUsernameClick: () -> Unit,
    onEmailClick: () -> Unit,
    onPhoneClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(R.string.authenticated_settings_account_account_label),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.77f),
            )
        )
    }

    SettingsItem(
        primaryText = stringResource(R.string.authenticated_settings_account_account_item_username),
        secondaryText = dbUsername,
    ) {
        onUsernameClick()
    }
    SettingsItem(
        primaryText = stringResource(R.string.authenticated_settings_account_account_item_email),
        secondaryText = dbEmail,
    ) {
        onEmailClick()
    }
    SettingsItem(
        primaryText = stringResource(R.string.authenticated_settings_account_account_item_phone),
        secondaryText = "მალე",
    ) {
        onPhoneClick()
    }
}

private fun retrieveFCMToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            return@addOnCompleteListener
        }
    }
}

@Composable
fun SecuritySettings(
    onPasswordClick: () -> Unit, onTwoFactorClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(R.string.authenticated_settings_account_security_label),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.77f),
            )
        )
    }

    SettingsItem(
        primaryText = stringResource(R.string.authenticated_settings_account_security_item_password),
    ) {
        onPasswordClick()
    }
    SettingsItem(
        primaryText = stringResource(R.string.authenticated_settings_account_security_item_2fa),
        secondaryText = "მალე",
    ) {
        onTwoFactorClick()
    }
}

@Composable
fun AccountManagement(
    onDeleteAccountClick: () -> Unit, onLogoutClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(R.string.authenticated_settings_account_management_label),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.77f),
            )
        )
    }

    SettingsItem(
        primaryText = stringResource(R.string.authenticated_settings_account_management_item_delete),
        primaryTextColor = Color.Red,
        secondaryText = "მალე",
    ) {
        onDeleteAccountClick()
    }
    SettingsItem(
        primaryText = stringResource(R.string.authenticated_settings_account_management_item_signout),
    ) {
        onLogoutClick()
    }
}