package com.darkindustry.studenthelper.ui.authenticated.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.ui.authenticated.home.HomeScreenForm
import com.darkindustry.studenthelper.ui.authenticated.home.HomeViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun CatalogScreen(
    catalogViewModel: CatalogViewModel = hiltViewModel(),
    navController: NavHostController,
    paddingValues: PaddingValues
){
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.onBackground)
        setNavigationBarColor(color = MaterialTheme.colorScheme.onBackground)
    }

    val messageState by catalogViewModel.messageState.collectAsState()
    val messageType by catalogViewModel.messageType.collectAsState()
    val message by catalogViewModel.message.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Catalog Test")
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}