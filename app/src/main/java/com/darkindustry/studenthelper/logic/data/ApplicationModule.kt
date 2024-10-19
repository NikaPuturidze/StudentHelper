package com.darkindustry.studenthelper.logic.data

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return try {
            FirebaseCrashlytics.getInstance().log("AppModule: Providing FirebaseAuth instance")
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("AppModule: Exception in provideFirebaseAuth - ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions {
        return try {
            FirebaseCrashlytics.getInstance().log("AppModule: Providing FirebaseFunctions instance")
            FirebaseFunctions.getInstance()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("AppModule: Exception in provideFirebaseFunctions - ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e
        }
    }


    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return try {
            FirebaseCrashlytics.getInstance()
                .log("FirebaseModule: Providing FirebaseFirestore instance")
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("FirebaseModule: Exception in provideFirebaseFirestore - ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return try {
            FirebaseCrashlytics.getInstance()
                .log("FirebaseModule: Providing FirebaseMessaging instance")
            FirebaseMessaging.getInstance()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("FirebaseModule: Exception in provideFirebaseMessaging - ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseFirebaseDatabase(): FirebaseDatabase {
        return try {
            FirebaseCrashlytics.getInstance()
                .log("FirebaseModule: Providing FirebaseDatabase instance")
            FirebaseDatabase.getInstance()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("FirebaseModule: Exception in provideFirebaseFirebaseDatabase - ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        return try {
            FirebaseCrashlytics.getInstance()
                .log("AppModule: Providing FirebaseCrashlytics instance")
            FirebaseCrashlytics.getInstance()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("AppModule: Exception in provideFirebaseCrashlytics - ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e
        }
    }

    @Provides
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        firebaseFunctions: FirebaseFunctions,
        firebaseDatabase: FirebaseDatabase,
        firebaseMessaging: FirebaseMessaging,
        firebaseCrashlytics: FirebaseCrashlytics
    ): ApplicationRepository {
        return try {
            FirebaseCrashlytics.getInstance().log("AppModule: Providing AuthRepository")
            ApplicationRepository(
                firebaseAuth,
                firebaseFirestore,
                firebaseFunctions,
                firebaseDatabase,
                firebaseMessaging,
                firebaseCrashlytics
            )
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("AppModule: Exception in provideAuthRepository - ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}