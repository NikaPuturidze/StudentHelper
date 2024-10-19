package com.darkindustry.studenthelper.ui.authenticated

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.firebase.FirestoreListener.Companion.dbStudentStats
import com.darkindustry.studenthelper.logic.firebase.FirestoreListener.Companion.dbUniversityGrades
import com.darkindustry.studenthelper.logic.firebase.FirestoreListener.Companion.dbUniversityLinked
import com.darkindustry.studenthelper.logic.firebase.FirestoreListener.Companion.dbUniversitySchedule
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_TRANSITION_TIME
import com.darkindustry.studenthelper.navigation.AuthenticatedNavGraph
import com.darkindustry.studenthelper.navigation.NavigationRoute
import kotlinx.coroutines.flow.StateFlow

data class BottomNavigationItem(
    val icon: Painter? = null,
    val route: String = "",
    val label: String = ""
) {
    @Composable
    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                icon = painterResource(id = R.drawable.ic_house),
                route = NavigationRoute.Authenticated.Home.route,
                label = "მთავარი"
            ),
            BottomNavigationItem(
                icon = painterResource(id = R.drawable.ic_catalog),
                route = NavigationRoute.Authenticated.Catalog.route,
                label = "კატალოგი"
            ),
            BottomNavigationItem(
                icon = painterResource(id = R.drawable.ic_calendar),
                route = NavigationRoute.Authenticated.Schedule.route,
                label = "ცხრილი"
            ),
            BottomNavigationItem(
                icon = painterResource(id = R.drawable.ic_results),
                route = NavigationRoute.Authenticated.Results.route,
                label = "შედეგები"
            ),
            BottomNavigationItem(
                icon = painterResource(id = R.drawable.ic_user_filled),
                route = NavigationRoute.Authenticated.Profile.route,
                label = "პროფილი"
            ),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationBar(
    dbUsername: StateFlow<String>,
    dbEmail: StateFlow<String>,
    dbPhone: StateFlow<String>,
    dbLessonReminder: StateFlow<Boolean>,
) {
    var navigationSelectedItem by remember { mutableIntStateOf(0) }
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val routesWithAuthenticated = listOf(
        NavigationRoute.Authenticated.Home.route,
        NavigationRoute.Authenticated.Catalog.route,
        NavigationRoute.Authenticated.Schedule.route,
        NavigationRoute.Authenticated.Results.route,
        NavigationRoute.Authenticated.Profile.route,
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = currentRoute in routesWithAuthenticated,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = GLOBAL_TRANSITION_TIME)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = GLOBAL_TRANSITION_TIME)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = GLOBAL_TRANSITION_TIME)
                ) + fadeOut(
                    animationSpec = tween(durationMillis = GLOBAL_TRANSITION_TIME)
                )
            ) {
                NavigationBar(
                    modifier = Modifier.height(64.dp),
                    containerColor = MaterialTheme.colorScheme.onBackground,
                ) {
                    BottomNavigationItem()
                        .bottomNavigationItems()
                        .forEachIndexed { index, navigationItem ->
                            NavigationBarItem(
                                selected = index == navigationSelectedItem,
                                icon = {
                                    navigationItem.icon?.let {
                                        Icon(
                                            painter = it,
                                            contentDescription = "null",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = navigationItem.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                onClick = {
                                    navigationSelectedItem = index
                                    navController.navigate(navigationItem.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                }
            }
        }
    ) { paddingValues ->
        AuthenticatedNavGraph(
            navHostController = navController,
            paddingValues = paddingValues,
            dbUsername = dbUsername.collectAsState().value,
            dbEmail = dbEmail.collectAsState().value,
            dbPhone = dbPhone.collectAsState().value,
            dbLessonReminder = dbLessonReminder.collectAsState().value,
            dbUniversitySchedule = dbUniversitySchedule.collectAsState().value,
            dbUniversityGrades = dbUniversityGrades.collectAsState().value,
            dbUniversityLinked = dbUniversityLinked.collectAsState().value,
            dbStudentStats = dbStudentStats.collectAsState().value
        )
    }
}