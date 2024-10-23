package com.darkindustry.studenthelper.ui.authenticated.profile.settings

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
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SettingsScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController,
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
        SettingsScreenForm(navController = navController)
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}

@Composable
fun SettingsScreenForm(
    navController: NavHostController,
) {
    CustomHeader(
        title = stringResource(R.string.authenticated_settings_header_title),
        leftIcon =
        R.drawable.ic_arrow_left, onLeftClick = {
            navController.popBackStack(
                route = NavigationRoute.Authenticated.Settings.General.route,
                inclusive = true
            )
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(GLOBAL_PADDINGS),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AccountSettings(
            onAccountClick = {
                navController.navigate(NavigationRoute.Authenticated.Settings.Account.route) {
                    launchSingleTop = true
                    popUpTo(NavigationRoute.Authenticated.Settings.Account.route) {
                        saveState = true
                    }
                    restoreState = true
                }

            },
            onConnectionsClick = {
                navController.navigate(NavigationRoute.Authenticated.Settings.Connections.route) {
                    launchSingleTop = true
                    popUpTo(NavigationRoute.Authenticated.Settings.Connections.route) {
                        saveState = true
                    }
                    restoreState = true
                }
            },
            onNotificationsClick = {
                navController.navigate(NavigationRoute.Authenticated.Settings.Notifications.route) {
                    launchSingleTop = true
                    popUpTo(NavigationRoute.Authenticated.Settings.Notifications.route) {
                        saveState = true
                    }
                    restoreState = true
                }
            }
        )
    }
}

@Composable
fun AccountSettings(
    onAccountClick: () -> Unit, onConnectionsClick: () -> Unit, onNotificationsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(R.string.authenticated_settings_account_label),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f)
            )
        )
    }
    SettingsItem(
        primaryText = stringResource(R.string.authenticated_settings_item_account),
        primaryIconId = R.drawable.ic_user_gear,
    ) {
        onAccountClick()
    }
    SettingsItem(
        primaryText = stringResource(R.string.authenticated_settings_item_notifications),
        primaryIconId = R.drawable.ic_bell,
    ) {
        onNotificationsClick()
    }
    SettingsItem(
        primaryText = stringResource(R.string.authenticated_settings_item_link_university),
        primaryIconId = R.drawable.ic_connection,
    ) {
        onConnectionsClick()
    }
}