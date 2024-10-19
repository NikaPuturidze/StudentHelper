package com.darkindustry.studenthelper.ui.authentication.login.passwordRecovery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationButton
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.ApplicationTextField
import com.darkindustry.studenthelper.navigation.NavigationRoute
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun PasswordRecoveryContentMain(
    passwordRecoveryViewModel: PasswordRecoveryViewModel = hiltViewModel(),
    navController: NavController,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val email by passwordRecoveryViewModel.email.collectAsState()

    val messageState by passwordRecoveryViewModel.messageState.collectAsState()
    val messageType by passwordRecoveryViewModel.messageType.collectAsState()
    val message by passwordRecoveryViewModel.message.collectAsState()

    val focusManager = LocalFocusManager.current
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PasswordRecoveryContentMainForm(
                passwordRecoveryViewModel = passwordRecoveryViewModel,
                navController = navController,
                email = email,
                focusManager = focusManager,
                isLoading = { isLoading = it }
            )
        }

        MessageBox(
            message = message, messageType = messageType, visible = messageState,
        )
    }
}

@Composable
private fun PasswordRecoveryContentMainForm(
    passwordRecoveryViewModel: PasswordRecoveryViewModel,
    navController: NavController,
    email: String,
    focusManager: FocusManager,
    isLoading: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(Utils.GLOBAL_PADDINGS),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PopBack(navController)
        Spacer(modifier = Modifier.height(48.dp))
        PasswordRecoveryHeader()
        Spacer(modifier = Modifier.height(32.dp))
        ApplicationTextField(
            topText = stringResource(R.string.password_recovery_enter_email_label),
            value = email,
            onValueChange = passwordRecoveryViewModel::onEmailChange,
            placeholderText = stringResource(R.string.password_recovery_enter_email_placeholder),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_envelope),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    contentDescription = "Email",
                    modifier = Modifier.size(20.dp)
                )
            })
        Spacer(modifier = Modifier.height(24.dp))
        ApplicationButton(
            text = stringResource(R.string.password_recovery_enter_email_button),
            onClick = {
                focusManager.clearFocus()
                isLoading(true)
                passwordRecoveryViewModel.validateAndRecoverPassword(
                    email = email,
                    onSuccess = {
                        passwordRecoveryViewModel.sendVerificationCode(email, onSuccess = {
                            navController.navigate(
                                NavigationRoute.Authentication.PasswordRecovery.VerifyEmail.route.replace(
                                    "{email}",
                                    email
                                )
                            )
                        })
                    },
                    onFailure = {
                        isLoading(false)
                    }
                )
            }
        )
        Spacer(modifier = Modifier.weight(3.5f))
    }
}

@Composable
private fun PopBack(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = "Back to Registration Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.aspectRatio(1f)
            )
        }
    }
}

@Composable
private fun PasswordRecoveryHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.password_recovery_enter_email_header),
            style = MaterialTheme.typography.displaySmall.copy(
                color = MaterialTheme.colorScheme.secondary,
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.password_recovery_enter_email_header_text),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}