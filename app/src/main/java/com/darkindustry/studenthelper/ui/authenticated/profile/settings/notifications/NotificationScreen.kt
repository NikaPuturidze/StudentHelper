package com.darkindustry.studenthelper.ui.authenticated.profile.settings.notifications

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.api.tsu.ApiViewModel
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_PADDINGS
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.SettingsItem
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun NotificationScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController,
    dbLessonReminder: Boolean
){
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val messageState by profileViewModel.messageState.collectAsState()
    val messageType by profileViewModel.messageType.collectAsState()
    val message by profileViewModel.message.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        NotificationScreenForm(
            navController = navController,
            dbLessonReminder = dbLessonReminder
        )
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}

@Composable
fun NotificationScreenForm(
    apiViewModel: ApiViewModel = hiltViewModel(),
    navController: NavHostController,
    dbLessonReminder: Boolean,
){
    CustomHeader(title = "Notifications", left = {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(32.dp)
        ) },
        onLeftClick = {
            navController.popBackStack()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(GLOBAL_PADDINGS),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "General", style = MaterialTheme.typography.titleMedium.copy(
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
            val padding = Modifier.padding(start = 16.dp)

            Spacer(modifier = Modifier.height(2.dp))
            SettingsItem(
                primaryText = "Daily Lesson Reminder",
                secondaryIconComposable = {
                    Box(
                        modifier = Modifier
                            .scale(0.8f)
                    ) {
                        Switch(
                            checked = dbLessonReminder,
                            onCheckedChange = {
                                apiViewModel.updateUserData(
                                    fieldPath = "notifications.dailyLessonReminder",
                                    newValue = it
                                )
                            }
                        )
                    }
                }, modifier = padding
            ) {

            }
        }
    }
}