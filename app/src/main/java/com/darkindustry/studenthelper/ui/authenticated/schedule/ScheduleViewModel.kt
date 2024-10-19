package com.darkindustry.studenthelper.ui.authenticated.schedule

import androidx.lifecycle.viewModelScope
import com.darkindustry.studenthelper.logic.data.ApplicationRepository
import com.darkindustry.studenthelper.logic.data.ApplicationViewModel
import com.darkindustry.studenthelper.logic.utils.MessageType
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository
): ApplicationViewModel() {

}