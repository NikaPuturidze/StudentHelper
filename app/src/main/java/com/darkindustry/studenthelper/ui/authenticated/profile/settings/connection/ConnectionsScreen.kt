package com.darkindustry.studenthelper.ui.authenticated.profile.settings.connection

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.api.tsu.ApiViewModel
import com.darkindustry.studenthelper.logic.firebase.FirestoreListener
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.ui.authenticated.profile.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    apiViewModel: ApiViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.background)
        setNavigationBarColor(color = MaterialTheme.colorScheme.background)
    }

    val message by profileViewModel.message.collectAsState()
    val messageState by profileViewModel.messageState.collectAsState()
    val messageType by profileViewModel.messageType.collectAsState()

    val universityLinked by FirestoreListener.dbUniversityLinked.collectAsState()
    var universityName by remember { mutableStateOf("") }

    var showSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    if (showSheet) {
        BottomSheet(
            onItemClick = { selectedUniversity ->
                universityName = selectedUniversity
                showDialog = true
            },
            onDismiss = {
                showSheet = false
            }
        )
    }


    if (showDialog) {
        showSheet = false
        VisitWebsite("https://uni.tsu.ge"){
            apiViewModel.linkUniversity(
                user = it.first,
                password = it.second,
                universityName = universityName
            )
            showDialog = false
        }
    } else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ConnectionScreenForm(
                navController = navController,
                universityLinked = universityLinked,
                showSheet = { showSheet = it },
                onUnlinkUniversity = {
                    apiViewModel.unlinkUniversity()
                }
            )
        }
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun VisitWebsite(
    url: String,
    apiViewModel: ApiViewModel = hiltViewModel(),
    onresult: (Pair<String, String>) -> Unit
) {

    @Suppress("unused")
    class WebAppInterface(private val apiViewModel: ApiViewModel) {
        @JavascriptInterface
        fun storeFieldValues(textFieldValue: String, passwordFieldValue: String) {
            onresult(Pair(textFieldValue, passwordFieldValue))
        }
    }

    var webView: WebView? = null

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true


                addJavascriptInterface(WebAppInterface(apiViewModel), "AndroidView")

                loadUrl(url)

                webView = this
            }
        },
        update = { webView ->
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.evaluateJavascript(
                        """
                            (function() {
                                function checkFields() {
                                    var button = document.querySelector('button.btn');
                                    var textField = document.querySelector('input[type="text"]');
                                    var passwordField = document.querySelector('input[type="password"]');

                                    if (button) {
                                        console.log("Button found!");
                                        button.addEventListener('click', function() {
                                            logFieldValues();
                                        });
                                    } else {
                                        console.log("Button not found!");
                                    }

                                    if (textField) {
                                        textField.setAttribute('autocomplete', 'off');
                                        console.log("Text field found!");

                                        textField.addEventListener('input', function() {
                                            console.log("Text Field Value: " + textField.value);
                                        });
                                    } else {
                                        console.log("Text field not found!");
                                    }

                                    if (passwordField) {
                                        passwordField.setAttribute('autocomplete', 'off');
                                        console.log("Password field found!");

                                        passwordField.addEventListener('input', function() {
                                            console.log("Password Field Value: " + passwordField.value);
                                        });
                                    } else {
                                        console.log("Password field not found!");
                                    }
                                    
                                    function logFieldValues() {
                                        var textFieldValue = textField ? textField.value : "Text field not found!";
                                        var passwordFieldValue = passwordField ? passwordField.value : "Password field not found!";
                                        
                                        console.log("Text Field Value: " + textFieldValue);
                                        console.log("Password Field Value: " + passwordFieldValue);
                                        console.log("Current URL: " + window.location.href);
                                        AndroidView.storeFieldValues(textFieldValue, passwordFieldValue);
                                        AndroidView.setShowWebView(false);
                                    }
                                }
                                
                                checkFields();
                                window.onpopstate = function(event) {};
                            })();
                            """.trimIndent(),
                        null
                    )
                }
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
        }
    }
}

@Composable
fun ConnectionScreenForm(
    navController: NavHostController,
    universityLinked: String,
    showSheet: (Boolean) -> Unit,
    onUnlinkUniversity: () -> Unit,
) {
    CustomHeader(title = "Link University", left = {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(32.dp)
        )
    },
        onLeftClick = { navController.popBackStack() },
        right = {
            if (universityLinked.isEmpty()) {
                Text(
                    text = "Add",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                    )
                )
            }

        },
        onRightClick = {
            if (universityLinked.isEmpty()) {
                showSheet(true)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        if (universityLinked.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_uni_tsu),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = universityLinked,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        onUnlinkUniversity()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cancel),
                            contentDescription = "Unlink University",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onItemClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }

    val searchFocusRequester = remember { FocusRequester() }

    val universities = listOf(
        Pair("Tbilisi State University (TSU)", R.drawable.ic_uni_tsu),
        Pair("Georgian Technical University (GTU)", R.drawable.ic_uni_gtu),
        Pair("Ilia State University (ISU)", R.drawable.ic_uni_isu)
    )

    val filteredUniversities = universities.filter {
        it.first.contains(searchQuery, ignoreCase = true)
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.onBackground,
        contentColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.fillMaxHeight(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(text = "Search")
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Search Icon"
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.secondary,
                        unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                    ),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
                        .onFocusChanged { isSearchFocused = it.isFocused }
                        .focusRequester(searchFocusRequester),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    ),
                )
            }

            LaunchedEffect(searchQuery, isSearchFocused) {
                if (searchQuery.isNotEmpty() || isSearchFocused) {
                    modalBottomSheetState.expand()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredUniversities.isEmpty()) {
                Text(
                    text = "No universities found.",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 17.sp,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                filteredUniversities.forEach { (title, icon) ->
                    BottomSheetItems(
                        title = title,
                        icon = icon,
                        onClick = onItemClick
                    )
                }
            }
        }
    }
}

@Composable
fun BottomSheetItems(
    title: String,
    icon: Int,
    onClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(title) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 17.sp,
                )
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}