package com.darkindustry.studenthelper.ui.authenticated.profile.settings.account

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val message by profileViewModel.message.collectAsState()
    val messageState by profileViewModel.messageState.collectAsState()
    val messageType by profileViewModel.messageType.collectAsState()

    val context = LocalContext.current

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
            context = context,
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
    context: Context,
    dbUsername: String,
    dbEmail: String,
    dbPhone: String,
) {
    CustomHeader(title = "Account", left = {
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
                        navController.navigate(NavigationRoute.Authenticated.Settings.Account.AccountInformation.EditUsername.route)
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
                        navController.navigate(NavigationRoute.Authenticated.Settings.Account.AccountInformation.VerifyEmail.route)
                    }
                }
            },
            onPhoneClick = {

            }

        )
        SecuritySettings(onPasswordClick = {
            navController.navigate(NavigationRoute.Authenticated.Settings.Account.Security.ChangePassword.route)
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
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "Account Information", style = MaterialTheme.typography.titleMedium.copy(
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
            primaryText = "Username", secondaryText = dbUsername, modifier = padding
        ) {
            onUsernameClick()
        }
        HorizontalDivider()
        SettingsItem(
            primaryText = "Email", secondaryText = dbEmail, modifier = padding
        ) {
            onEmailClick()
        }
        HorizontalDivider()
        SettingsItem(
            primaryText = "Phone", secondaryText = "Soon", modifier = padding
        ) {
            onPhoneClick()
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}

private fun retrieveFCMToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            return@addOnCompleteListener
        }
        val token = task.result
        Log.d(TAG, "FCM Registration Token: $token")
    }
}

@Composable
fun SecuritySettings(
    onPasswordClick: () -> Unit, onTwoFactorClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "Security", style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.77f),
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
        val padding = Modifier
            .padding(start = 14.dp)

        Spacer(modifier = Modifier.height(2.dp))
        SettingsItem(
            primaryText = "Password", modifier = padding
        ) {
            onPasswordClick()
        }
        HorizontalDivider()
        SettingsItem(
            primaryText = "Two-Factor Authentication", secondaryText = "Soon", modifier = padding
        ) {
            onTwoFactorClick()
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Composable
fun AccountManagement(
    onDeleteAccountClick: () -> Unit, onLogoutClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "Account Management", style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.77f),
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
        val padding = Modifier
            .padding(start = 14.dp)

        Spacer(modifier = Modifier.height(2.dp))
        SettingsItem(
            primaryText = "Delete Account", primaryTextColor = Color.Red, modifier = padding
        ) {
            onDeleteAccountClick()
        }
        HorizontalDivider()
        SettingsItem(
            primaryText = "Sign Out", modifier = padding
        ) {
            onLogoutClick()
        }

        Spacer(modifier = Modifier.height(2.dp))
    }
}