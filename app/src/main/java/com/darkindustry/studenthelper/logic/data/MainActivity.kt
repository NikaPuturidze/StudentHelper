package com.darkindustry.studenthelper.logic.data

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.darkindustry.studenthelper.logic.api.tsu.ApiViewModel
import com.darkindustry.studenthelper.logic.firebase.FirestoreListener
import com.darkindustry.studenthelper.logic.utils.Utils
import com.darkindustry.studenthelper.navigation.AuthenticationNavGraph
import com.darkindustry.studenthelper.theme.StudentHelperTheme
import com.darkindustry.studenthelper.ui.AuthenticationState.AuthenticationState.Authenticated
import com.darkindustry.studenthelper.ui.AuthenticationState.AuthenticationState.Unauthenticated
import com.darkindustry.studenthelper.ui.AuthenticationState.authenticationState
import com.darkindustry.studenthelper.ui.AuthenticationState.setAuthenticationState
import com.darkindustry.studenthelper.ui.authenticated.NavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {

    private val apiViewModel: ApiViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val currentUser = FirebaseAuth.getInstance().currentUser

        setAuthenticationState(
            if (currentUser != null) {
                FirestoreListener.listenToAllUserData()
                Authenticated
            } else {
                Unauthenticated
            }
        )


        setContent {
            StudentHelperTheme {
                val navController = rememberNavController()
                val authenticationState by authenticationState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize().systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (authenticationState) {
                        is Authenticated -> {
                            Authenticated()
                        }

                        is Unauthenticated -> {
                            Unauthenticated(navController)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    @Composable
    fun Authenticated() {
        apiViewModel.linkUniversity(
            user = FirestoreListener.dbUniversityUsername.collectAsState().value,
            password = FirestoreListener.dbUniversityPassword.collectAsState().value,
            universityName = FirestoreListener.dbUniversityLinked.collectAsState().value,
        )

        getFcmTokenForCurrentUid()

        NavigationBar(
            dbUsername = FirestoreListener.dbUsername,
            dbEmail = FirestoreListener.dbEmail,
            dbPhone = FirestoreListener.dbPhone,
            dbLessonReminder = FirestoreListener.dbLessonReminder
        )
    }

    @Composable
    fun Unauthenticated(navController: NavHostController) {
        AuthenticationNavGraph(navHostController = navController)
    }

    private fun getFcmTokenForCurrentUid() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null && token != null) {
                    val uid = currentUser.uid
                    val userRef =
                        FirebaseFirestore.getInstance().collection("users").document(uid)

                    userRef.update("fcmToken", token).addOnSuccessListener {
                    }.addOnFailureListener { e ->
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
            } else {
                FirebaseCrashlytics.getInstance().recordException(task.exception!!)
            }
        }
    }
}
