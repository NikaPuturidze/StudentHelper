package com.darkindustry.studenthelper.ui.authenticated.catalog

import com.darkindustry.studenthelper.logic.data.ApplicationRepository
import com.darkindustry.studenthelper.logic.data.ApplicationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository
): ApplicationViewModel(){

}