package com.darkindustry.studenthelper.logic.data

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.darkindustry.studenthelper.logic.api.tsu.ApiViewModel
import com.darkindustry.studenthelper.logic.firebase.FirestoreListener
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

                Surface(color = MaterialTheme.colorScheme.background) {
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
