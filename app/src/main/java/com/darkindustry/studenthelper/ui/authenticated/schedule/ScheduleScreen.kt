package com.darkindustry.studenthelper.ui.authenticated.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationButton
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_ELEVATION
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_PADDINGS
import com.darkindustry.studenthelper.navigation.NavigationRoute
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ScheduleScreen(
    scheduleViewModel: ScheduleViewModel = hiltViewModel(),
    navController: NavHostController,
    paddingValues: PaddingValues,
    dbUniversitySchedule: Map<String, List<Map<String, String>>>?,
    dbUniversityLinked: String
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
        setNavigationBarColor(color = MaterialTheme.colorScheme.onBackground)
    }

    val daysOfWeek =
        listOf("ორშაბათი", "სამშაბათი", "ოთხშაბათი", "ხუთშაბათი", "პარასკევი", "შაბათი", "კვირა")
    val sortedDays = daysOfWeek.filter { dbUniversitySchedule?.containsKey(it) == true }

    val messageState by scheduleViewModel.messageState.collectAsState()
    val messageType by scheduleViewModel.messageType.collectAsState()
    val message by scheduleViewModel.message.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScheduleScreenForm(
            navController = navController,
            universityLinked = dbUniversityLinked,
            dbUniversitySchedule = dbUniversitySchedule,
            sortedDays = sortedDays
        )
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}

@Composable
fun ScheduleScreenForm(
    navController: NavHostController,
    universityLinked: String,
    dbUniversitySchedule: Map<String, List<Map<String, String>>>?,
    sortedDays: List<String>
) {
    CustomHeader(
        title = "ცხრილი",
    )

    if (universityLinked.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "To view your schedule, please link your university account first.",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            ApplicationButton(text = "Link University", width = 0.5f, onClick = {
                navController.navigate(NavigationRoute.Authenticated.Settings.Connections.route)
            })
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(GLOBAL_PADDINGS),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            sortedDays.forEach { day ->
                val subjects = dbUniversitySchedule?.get(day) ?: emptyList()
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = day, style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White, fontSize = 20.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    subjects.forEach { subject ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .shadow(
                                    elevation = GLOBAL_ELEVATION,
                                    shape = RoundedCornerShape(12.dp),
                                    clip = true
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(color = MaterialTheme.colorScheme.onBackground)
                                    .padding(GLOBAL_PADDINGS)
                                    .fillMaxWidth()
                            ) {
                                Text(text = buildAnnotatedString {
                                    withStyle(
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.secondary.copy(
                                                alpha = 0.9f
                                            ), fontSize = 14.sp
                                        ).toSpanStyle()
                                    ) {
                                        append("${subject["subjectType"]}: ")
                                    }
                                    withStyle(
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.secondary.copy(
                                                alpha = 0.8f
                                            ), fontSize = 14.sp
                                        ).toSpanStyle()
                                    ) {
                                        append("${subject["startTime"]} - ${subject["endTime"]}")
                                    }
                                })

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "${subject["subjectName"]}",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(text = buildAnnotatedString {
                                    withStyle(
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.secondary.copy(
                                                alpha = 0.9f
                                            ), fontSize = 14.sp
                                        ).toSpanStyle()
                                    ) {
                                        append("აუდიტორია: ")
                                    }
                                    withStyle(
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.secondary.copy(
                                                alpha = 0.8f
                                            ), fontSize = 14.sp
                                        ).toSpanStyle()
                                    ) {
                                        append("${subject["auditorium"]}")
                                    }
                                })

                                Text(text = buildAnnotatedString {
                                    withStyle(
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.secondary.copy(
                                                alpha = 0.9f
                                            ), fontSize = 14.sp
                                        ).toSpanStyle()
                                    ) {
                                        append("პედაგოგი: ")
                                    }
                                    withStyle(
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.secondary.copy(
                                                alpha = 0.8f
                                            ), fontSize = 14.sp
                                        ).toSpanStyle()
                                    ) {
                                        append("${subject["fullName"]}")
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}