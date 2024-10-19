package com.darkindustry.studenthelper.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import com.darkindustry.studenthelper.ui.authenticated.results.ResultsScreen
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_TRANSITION_TIME
import com.darkindustry.studenthelper.ui.authenticated.catalog.CatalogScreen
import com.darkindustry.studenthelper.ui.authenticated.home.HomeScreen
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileScreen
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileViewModel
import com.darkindustry.studenthelper.ui.authenticated.profile.settings.SettingsScreen
import com.darkindustry.studenthelper.ui.authenticated.profile.settings.account.AccountEditEmail
import com.darkindustry.studenthelper.ui.authenticated.profile.settings.account.AccountEditEmailVerification
import com.darkindustry.studenthelper.ui.authenticated.profile.settings.account.AccountEditPassword
import com.darkindustry.studenthelper.ui.authenticated.profile.settings.account.AccountEditUsername
import com.darkindustry.studenthelper.ui.authenticated.profile.settings.account.AccountSettings
import com.darkindustry.studenthelper.ui.authenticated.profile.settings.connection.ConnectionScreen
import com.darkindustry.studenthelper.ui.authenticated.profile.settings.notifications.NotificationScreen
import com.darkindustry.studenthelper.ui.authenticated.schedule.ScheduleScreen
import com.darkindustry.studenthelper.ui.authentication.login.LoginScreen
import com.darkindustry.studenthelper.ui.authentication.login.passwordRecovery.PasswordRecoveryContentMain
import com.darkindustry.studenthelper.ui.authentication.login.passwordRecovery.PasswordRecoveryContentPasswordEnter
import com.darkindustry.studenthelper.ui.authentication.login.passwordRecovery.PasswordRecoveryVerifyEmail
import com.darkindustry.studenthelper.ui.authentication.registration.RegistrationScreen
import com.darkindustry.studenthelper.ui.authentication.registration.RegistrationVerifyEmail

sealed class NavigationRoute(open val route: String) {
    sealed class Authentication(route: String) : NavigationRoute(route) {
        data object Login : Authentication("auth/login")
        data object Registration : Authentication("auth/register"){
            data object VerifyEmail : Authentication("auth/register/verify_email/{email}/{username}/{password}")
        }

        data object PasswordRecovery : Authentication("auth/recover_password"){
            data object VerifyEmail : Authentication("auth/recover_password/verify_email/{email}")
            data object PasswordEnter : Authentication("auth/recover_password/new_password/{email}")
        }
    }

    sealed class Authenticated(val route: String) {
        data object Home : Authenticated("authenticated/home")
        data object Catalog : Authenticated("authenticated/catalog")
        data object Schedule : Authenticated("authenticated/schedule")
        data object Results : Authenticated("authenticated/subscription")
        data object Profile : Authenticated("authenticated/profile")

        sealed class Settings(route: String) : Authenticated(route) {
            data object General : Settings("authenticated/settings/general")

            data object Account : Settings("authenticated/settings/account") {
                sealed class AccountInformation(val route: String) {
                    data object VerifyEmail : AccountInformation("authenticated/settings/account/verify_email")
                    data object EditUsername : AccountInformation("authenticated/settings/account/edit_username")
                    data object EditEmail : AccountInformation("authenticated/settings/account/edit_email")
                    data object EditPhone : AccountInformation("authenticated/settings/account/edit_phone")
                }

                sealed class Security(val route: String) {
                    data object ChangePassword : Security("authenticated/settings/account/security/edit_password")
                    data object TwoFactor : Security("authenticated/settings/account/security/two_factor")
                }

                sealed class AccountManagement(val route: String) {
                    data object DeleteAccount : AccountManagement("authenticated/settings/account/delete_account")
                    data object SignOut : AccountManagement("authenticated/settings/account/sign_out")
                }
            }

            data object Connections : Settings("authenticated/settings/connections")

            data object Notifications : Settings("authenticated/settings/notifications")

        }
    }
}

val Easing = LinearEasing

@Composable
fun AuthenticationNavGraph(navHostController: NavHostController) {
    NavHost(
        navController = navHostController, startDestination = NavigationRoute.Authentication.Login.route
    ) {
        composable(
            route = NavigationRoute.Authentication.Login.route,
        ){
            LoginScreen(navController = navHostController)
        }

        composable(route = NavigationRoute.Authentication.Registration.route) {
            RegistrationScreen(navController = navHostController)
        }

        composable(
            route = NavigationRoute.Authentication.Registration.VerifyEmail.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            val username = backStackEntry.arguments?.getString("username")
            val password = backStackEntry.arguments?.getString("password")
            if (email != null && username != null && password != null) {
                RegistrationVerifyEmail(navController = navHostController,
                    email = email,
                    username = username,
                    password = password
                )
            }
        }

        composable(
            route = NavigationRoute.Authentication.PasswordRecovery.route,
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
        ) {
            PasswordRecoveryContentMain(navController = navHostController)
        }
        composable(
            route = NavigationRoute.Authentication.PasswordRecovery.VerifyEmail.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing)
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("email")?.let {
                PasswordRecoveryVerifyEmail(navController = navHostController, email = it)
            }
        }

        composable(
            route = NavigationRoute.Authentication.PasswordRecovery.PasswordEnter.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing)
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("email")?.let {
                PasswordRecoveryContentPasswordEnter(navController = navHostController, email = it)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthenticatedNavGraph(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navHostController: NavHostController,
    paddingValues: PaddingValues,
    dbUsername: String,
    dbEmail: String,
    dbPhone: String,
    dbLessonReminder: Boolean,
    dbUniversitySchedule: Map<String, List<Map<String, String>>>?,
    dbUniversityGrades: List<Map<String, Any>>,
    dbStudentStats: Map<String, Any>,
    dbUniversityLinked: String
) {

    NavHost(
        navController = navHostController, startDestination = NavigationRoute.Authenticated.Profile.route
    ) {
        // Navigation Bar
        composable(NavigationRoute.Authenticated.Home.route) {
            HomeScreen(
                navController = navHostController,
                paddingValues = paddingValues,
                dbUniversityGrades = dbUniversityGrades,
                dbUniversityLinked = dbUniversityLinked
            )
        }

        composable(NavigationRoute.Authenticated.Catalog.route) {
            CatalogScreen(
                navController = navHostController,
                paddingValues = paddingValues
            )
        }

        composable(NavigationRoute.Authenticated.Schedule.route) {
            ScheduleScreen(
                navController = navHostController,
                paddingValues = paddingValues,
                dbUniversitySchedule = dbUniversitySchedule,
                dbUniversityLinked = dbUniversityLinked
            )
        }

        composable(NavigationRoute.Authenticated.Results.route) {
            ResultsScreen(
                navController = navHostController,
                paddingValues = paddingValues,
                dbUniversityLinked = dbUniversityLinked,
                dbUniversityGrades = dbUniversityGrades,
                dbStudentStats = dbStudentStats
            )
        }

        // Profile and its subitems
        composable(
            NavigationRoute.Authenticated.Profile.route,
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing)
                )
            },
        ) {
            ProfileScreen(
                navController = navHostController,
                paddingValues = paddingValues,
                dbUsername = dbUsername,
                dbUniversityLinked = dbUniversityLinked
            )
        }

        composable(
            NavigationRoute.Authenticated.Settings.General.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
        ) {
            SettingsScreen(
                navController = navHostController
            )
        }

        composable(
            NavigationRoute.Authenticated.Settings.Account.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
        ) {
            AccountSettings(
                navController = navHostController,
                dbUsername = dbUsername,
                dbEmail = dbEmail,
                dbPhone = dbPhone
            )
        }

        composable(
            NavigationRoute.Authenticated.Settings.Connections.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
        ) {
           ConnectionScreen(
               navController = navHostController
           )
        }

        composable(
            NavigationRoute.Authenticated.Settings.Notifications.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
        ) {
            NotificationScreen(
                navController = navHostController,
                dbLessonReminder = dbLessonReminder
            )
        }

        composable(
            NavigationRoute.Authenticated.Settings.Account.AccountInformation.EditUsername.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
        ){
            AccountEditUsername(
                navController = navHostController,
                dbUsername = dbUsername,
            )
        }

        composable(
            NavigationRoute.Authenticated.Settings.Account.AccountInformation.EditEmail.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
        ){
            AccountEditEmail(
                navController = navHostController,
                dbEmail = dbEmail,
            )
        }

        composable(
            NavigationRoute.Authenticated.Settings.Account.AccountInformation.VerifyEmail.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
        ){
            AccountEditEmailVerification(
                navController = navHostController,
                dbEmail = dbEmail,
            )
        }

        composable(
            NavigationRoute.Authenticated.Settings.Account.Security.ChangePassword.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, animationSpec = tween(
                        durationMillis = GLOBAL_TRANSITION_TIME, easing = Easing
                    )
                )
            },
        ){
            AccountEditPassword(
                navController = navHostController,
            )
        }

    }
}

