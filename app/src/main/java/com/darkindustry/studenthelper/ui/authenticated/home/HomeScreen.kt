package com.darkindustry.studenthelper.ui.authenticated.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.api.tsu.ApiViewModel
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationButton
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.navigation.NavigationRoute
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController,
    paddingValues: PaddingValues,
    dbUniversityGrades: List<Map<String, Any>>,
    dbUniversityLinked: String
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.onBackground)
        setNavigationBarColor(color = MaterialTheme.colorScheme.onBackground)
    }

    val messageState by homeViewModel.messageState.collectAsState()
    val messageType by homeViewModel.messageType.collectAsState()
    val message by homeViewModel.message.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomeScreenForm(
            navController = navController,
            paddingValues = paddingValues,
            dbUniversityGrades = dbUniversityGrades,
            universityLinked = dbUniversityLinked,
        )
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}

@Composable
fun HomeScreenForm(
    navController: NavHostController,
    apiViewModel: ApiViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    dbUniversityGrades: List<Map<String, Any>>,
    universityLinked: String,
) {
    CustomHeader(
        title = stringResource(R.string.authenticated_home_header),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

    }
}