package com.darkindustry.studenthelper.ui.authenticated.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_ELEVATION
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_PADDINGS
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.util.Locale

@Composable
fun ResultsScreen(
    resultsViewModel: ResultsViewModel = hiltViewModel(),
    navController: NavHostController,
    paddingValues: PaddingValues,
    dbUniversityLinked: String,
    dbUniversityGrades: List<Map<String, Any>>,
    dbStudentStats: Map<String, Any>,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.onBackground)
        setNavigationBarColor(color = MaterialTheme.colorScheme.onBackground)
    }

    val messageState by resultsViewModel.messageState.collectAsState()
    val messageType by resultsViewModel.messageType.collectAsState()
    val message by resultsViewModel.message.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ResultsScreenForm(
            navController = navController,
            universityLinked = dbUniversityLinked,
            dbUniversityGrades = dbUniversityGrades,
            dbStudentStats = dbStudentStats
        )
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}


@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
fun ResultsScreenForm(
    navController: NavHostController,
    universityLinked: String,
    dbUniversityGrades: List<Map<String, Any>>,
    dbStudentStats: Map<String, Any>,
) {
    val totalCredits = dbStudentStats["totalCredits"]
    val avgScore = String.format(Locale.US, "%.2f", dbStudentStats["avgScore"])
    val gpa = String.format(Locale.US, "%.2f", dbStudentStats["gpa"])

    CustomHeader(
        title = "შედეგები",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(GLOBAL_PADDINGS),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = GLOBAL_ELEVATION,
                    shape = RoundedCornerShape(12.dp),
                    clip = true
                )
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.onBackground)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(GLOBAL_PADDINGS),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                            ).toSpanStyle()
                        ) {
                            append("დაგროვილი კრედიტები: ")
                        }
                        withStyle(
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                            ).toSpanStyle()
                        ) {
                            append("$totalCredits")
                        }
                    })

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(text = buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                            ).toSpanStyle()
                        ) {
                            append("საშუალო ქულა: ")
                        }
                        withStyle(
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                            ).toSpanStyle()
                        ) {
                            append(avgScore)
                        }
                    })

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(text = buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                            ).toSpanStyle()
                        ) {
                            append("GPA: ")
                        }
                        withStyle(
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                            ).toSpanStyle()
                        ) {
                            append(gpa)
                        }
                    })
                }
            }
        }

        val semesters =
            dbUniversityGrades.groupBy { "${it["yearName"]} ${it["semesterName"]} სემესტრი" }.keys.toList()

        var selectedSemester by remember { mutableStateOf(semesters.lastOrNull() ?: "") }
        var expanded by remember { mutableStateOf(false) }

        val subjectsForSelectedSemester = dbUniversityGrades.filter {
            "${it["yearName"]} ${it["semesterName"]} სემესტრი" == selectedSemester
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                shape = RoundedCornerShape(12.dp),
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = selectedSemester, style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center
                    )
                )
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            semesters.forEach { semester ->
                DropdownMenuItem(text = { Text(text = semester) }, onClick = {
                    selectedSemester = semester
                    expanded = false
                })
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            subjectsForSelectedSemester.forEach { subject ->
                item {
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
                                .background(
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                .padding(GLOBAL_PADDINGS)
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "${subject["subjectName"]}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = buildAnnotatedString {
                                    withStyle(
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.secondary.copy(
                                                alpha = 0.9f
                                            ),
                                        ).toSpanStyle()
                                    ) {
                                        append("სტატუსი: ")
                                    }

                                    val statusColor = when (subject["enrollStatus"]) {
                                        "აღიარებული" -> Color(0xFF00FF00)
                                        "ჩაჭრილი" -> Color(0xFFFF0000)
                                        "განუსაზღვრელი" -> Color(0xFFFFD700)
                                        else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                    }

                                    withStyle(
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = statusColor
                                        ).toSpanStyle()
                                    ) {
                                        append("${subject["enrollStatus"]}")
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
                                        append("ქულა: ")
                                    }
                                    withStyle(
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.secondary.copy(
                                                alpha = 0.8f
                                            ), fontSize = 14.sp
                                        ).toSpanStyle()
                                    ) {
                                        append("${subject["totalScore"] ?: "0"}")
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