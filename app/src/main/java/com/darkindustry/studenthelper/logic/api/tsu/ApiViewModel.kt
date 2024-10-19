package com.darkindustry.studenthelper.logic.api.tsu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
) : ViewModel() {
    fun linkUniversity(
        user: String,
        password: String,
        universityName: String,
        onFailure: () -> Unit = {},
    ) {
        viewModelScope.launch {
            apiRepository.linkUniversity(
                user = user,
                password = password,
                universityName = universityName
            ).onFailure { onFailure() }
        }
    }

    fun unlinkUniversity() {
        viewModelScope.launch {
            apiRepository.unlinkUniversity()
        }
    }

    fun updateUserData(
        userRef: DocumentReference = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid ?: ""),
        fieldPath: String, newValue: Any,
    ) {
        apiRepository.executeBatchUpdate(userRef, fieldPath, newValue)
    }
}