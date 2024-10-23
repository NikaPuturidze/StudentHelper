package com.darkindustry.studenthelper.ui.authenticated.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.utils.MessageBox
import com.darkindustry.studenthelper.logic.utils.MessageType
import com.darkindustry.studenthelper.logic.utils.Utils
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomAlertDialog
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.CustomHeader
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_ELEVATION
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_PADDINGS
import com.darkindustry.studenthelper.navigation.NavigationRoute
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController,
    paddingValues: PaddingValues,
    dbUsername: String,
    dbUniversityLinked: String,
) {
    rememberSystemUiController().apply {
        setStatusBarColor(color = MaterialTheme.colorScheme.onBackground)
        setNavigationBarColor(color = MaterialTheme.colorScheme.onBackground)
    }

    val messageState by profileViewModel.messageState.collectAsState()
    val messageType by profileViewModel.messageType.collectAsState()
    val message by profileViewModel.message.collectAsState()

    val context = LocalContext.current
    var showAcceptDialog by remember { mutableStateOf(false) }


    val qrCodeScannerLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        result?.contents?.let { scannedInfo ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                profileViewModel.setUniqueId(Utils.decode(scannedInfo).split(".")[0])
                profileViewModel.setSessionId(Utils.decode(scannedInfo).split(".")[1])
                profileViewModel.updateSessionStatus(
                    uniqueId = Utils.decode(scannedInfo).split(".")[0],
                    status = "scanned",
                    onSuccess = {
                        showAcceptDialog = true
                    },
                    onFailure = {
                        TODO()
                    }
                )
            }
        }
    }

    fun launchQRCodeScanner() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("დაასკანირე QR კოდი ვებ-საიტზე")
            setCameraId(0)
            setBeepEnabled(false)
            setOrientationLocked(true)
        }
        qrCodeScannerLauncher.launch(options)
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            profileViewModel.setMessage(
                "Camera permission is required for scanning QR codes",
                MessageType.ERROR
            )
        }
    }

    if (showAcceptDialog == true) {
        CustomAlertDialog(title = "QR ავტორიზაცია",
            message = "გსურს თუ არა ავტორიზაციის დადასტურება?",
            confirmButtonText = "დადასტურება",
            cancelButtonText = "გაუქმება",
            onConfirm = {
                profileViewModel.updateSessionStatus(
                    uniqueId = profileViewModel.uniqueId.value,
                    status = "success",
                    onSuccess = {
                        profileViewModel.setMessage("წარმატებული ავტორიზაცია.", MessageType.SUCCESS)
                        showAcceptDialog = false
                    },
                    onFailure = {
                        profileViewModel.setMessage(
                            "ავტორიზაცია ვერ მოხდა, გთხოვ სცადე ხელახლა.",
                            MessageType.ERROR
                        )
                        showAcceptDialog = false
                    }
                )
            },
            onCancel = {
                showAcceptDialog = false
            }
        )

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileScreenForm(navController = navController,
            context = context,
            paddingValues = paddingValues,
            dbUsername = dbUsername,
            dbUniversityLinked = dbUniversityLinked,
            launchQRCodeScanner = { launchQRCodeScanner() },
            requestCameraPermissionLauncher = { requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
        )
    }

    MessageBox(
        message = message, messageType = messageType, visible = messageState
    )
}

@Composable
private fun ProfileScreenForm(
    navController: NavHostController,
    context: Context,
    paddingValues: PaddingValues,
    dbUsername: String,
    dbUniversityLinked: String,
    launchQRCodeScanner: () -> Unit,
    requestCameraPermissionLauncher: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CustomHeader(
            title = stringResource(R.string.authenticated_profile_header_title),
            leftIcon = R.drawable.ic_qr,
            onLeftClick = {
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) launchQRCodeScanner() else requestCameraPermissionLauncher()
            },
            rightIcon = R.drawable.ic_settings_outlined,
            onRightClick = {
                navController.navigate(NavigationRoute.Authenticated.Settings.General.route) {
                    launchSingleTop = true

                    restoreState = true
                }
            })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(GLOBAL_PADDINGS),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = GLOBAL_ELEVATION,
                        shape = RoundedCornerShape(12.dp),
                        clip = true
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onBackground)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(GLOBAL_PADDINGS),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_avatar),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(92.dp)
                            .offset(x = -(4.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = dbUsername, style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = dbUniversityLinked,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }
        }
    }
}