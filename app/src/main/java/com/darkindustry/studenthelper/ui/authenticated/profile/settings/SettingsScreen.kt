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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
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
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
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
    navController: NavHostController
) {
    CustomHeader(title = "Settings", left = {
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
            .background(color = MaterialTheme.colorScheme.background)
            .padding(GLOBAL_PADDINGS),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AccountSettings(
            onAccountClick = {
                navController.navigate(NavigationRoute.Authenticated.Settings.Account.route)
            },
            onConnectionsClick = {
                navController.navigate(NavigationRoute.Authenticated.Settings.Connections.route)
            },
            onNotificationsClick = {
                navController.navigate(NavigationRoute.Authenticated.Settings.Notifications.route)
            }
        )
    }
}

@Composable
fun AccountSettings(
    onAccountClick: () -> Unit, onConnectionsClick: () -> Unit, onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "Account Settings", style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.77f),
                fontSize = 14.sp,
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.onBackground),
    ) {
        val padding = Modifier.padding(start = 14.dp)

        Spacer(modifier = Modifier.height(2.dp))
        SettingsItem(
            primaryText = "Account", primaryIconId = R.drawable.ic_user_gear, modifier = padding
        ) {
            onAccountClick()
        }
        HorizontalDivider(thickness = 1.2.dp, color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.33f))
        SettingsItem(
            primaryText = "Notifications", primaryIconId = R.drawable.ic_bell, modifier = padding
        ) {
            onNotificationsClick()
        }
        HorizontalDivider(thickness = 1.2.dp, color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.33f))
        SettingsItem(
            primaryText = "Link University Account", primaryIconId = R.drawable.ic_connection, modifier = padding
        ) {
            onConnectionsClick()
        }
    }
}