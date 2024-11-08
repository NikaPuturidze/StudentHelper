package com.darkindustry.studenthelper.logic.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darkindustry.studenthelper.R
import kotlinx.coroutines.launch
import java.util.Base64

class Utils {
    companion object {
        val GLOBAL_PADDINGS = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
        val GLOBAL_ELEVATION = 2.dp
        const val GLOBAL_TRANSITION_TIME = 400

        @RequiresApi(Build.VERSION_CODES.O)
        fun decode(encodedString: String): String {
            val decoder = Base64.getDecoder()
            val decodedBytes = decoder.decode(encodedString)

            return String(decodedBytes, Charsets.UTF_8)
        }

        @Suppress("RedundantLambdaArrow")
        @Composable
        fun SettingsItem(
            modifier: Modifier? = Modifier,
            primaryIconId: Int? = null,
            secondaryIconId: Int? = R.drawable.ic_right,
            secondaryIconComposable: (@Composable () -> Unit)? = null,
            primaryText: String? = null,
            primaryTextColor: Color = MaterialTheme.colorScheme.secondary,
            secondaryText: String? = null,
            onClick: () -> Unit,
        ) {
            modifier?.fillMaxWidth()?.let { it ->
                Row(
                    modifier = it
                        .fillMaxWidth()
                        .shadow(
                            elevation = GLOBAL_ELEVATION,
                            shape = RoundedCornerShape(12.dp),
                            clip = true,
                        )
                        .background(
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .height(56.dp)
                        .clickable { onClick() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (primaryIconId != null) {
                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .height(55.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = primaryIconId),
                                contentDescription = primaryText,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    } else {
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    primaryText?.let { it ->
                        Text(
                            text = it, style = MaterialTheme.typography.bodyLarge.copy(
                                color = primaryTextColor.copy(alpha = 0.87f)
                            ),
                            modifier = Modifier
                                .offset(y = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    secondaryText?.let { it ->
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .widthIn(max = 180.dp)
                                .offset(y = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    secondaryIconId?.let { painterResource(it) }?.let { it ->
                        if (secondaryIconComposable != null) secondaryIconComposable() else {
                            Icon(
                                painter = it,
                                contentDescription = "Arrow",
                                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        @Composable
        fun CustomAlertDialog(
            title: String,
            message: String,
            confirmButtonText: String,
            cancelButtonText: String,
            onConfirm: () -> Unit,
            onCancel: () -> Unit,
        ) {
            AlertDialog(
                onDismissRequest = {
                    onCancel()
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                onConfirm()
                            },
                            modifier = Modifier
                                .height(48.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(size = 32.dp)
                        ) {
                            Text(
                                text = confirmButtonText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = {
                                onCancel()
                            },
                            modifier = Modifier
                                .height(48.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onBackground,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(size = 32.dp)
                        ) {
                            Text(
                                text = cancelButtonText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            )
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.onBackground,
            )
        }

        @Composable
        fun CustomHeader(
            modifier: Modifier = Modifier,
            title: String = "",
            leftIcon: Int? = null,
            onLeftClick: suspend () -> Unit = {},
            rightIcon: Int? = null,
            onRightClick: suspend () -> Unit = {},
            textColor: Color = MaterialTheme.colorScheme.secondary,
            enableDivider: Boolean = true,
        ) {
            val coroutineScope = rememberCoroutineScope()

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onBackground)
                    .padding(horizontal = 10.dp)
                    .offset(y = (-2).dp)
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leftIcon != null) {
                    IconButton(
                        onClick = { coroutineScope.launch { onLeftClick() } },
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(
                                if (leftIcon == R.drawable.ic_arrow_left) 28.dp else 26.dp
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = leftIcon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                                modifier = modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    IconButton(
                        onClick = { },
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(
                                when (leftIcon) {
                                    R.drawable.ic_arrow_left -> 28.dp
                                    R.drawable.ic_qr -> 22.dp
                                    else -> 26.dp
                                }
                            )
                        ) {
                            leftIcon?.let {
                                Icon(
                                    painter = painterResource(id = it),
                                    contentDescription = null,
                                    tint = Color.Transparent,
                                    modifier = modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = textColor.copy(alpha = 0.67f),
                    ),
                    modifier = Modifier.offset(y = (2).dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                if (rightIcon != null) {
                    IconButton(
                        onClick = { coroutineScope.launch { onRightClick() } },
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(
                                when (rightIcon) {
                                    R.drawable.ic_settings_outlined -> 28.dp
                                    else -> 26.dp
                                }
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = rightIcon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                                modifier = modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    IconButton(
                        onClick = { },
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(26.dp)
                        ) {
                            rightIcon?.let {
                                Icon(
                                    painter = painterResource(id = it),
                                    contentDescription = null,
                                    tint = Transparent,
                                    modifier = modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
            if (enableDivider) {
                HorizontalDivider(
                    thickness = 0.2.dp,
                    modifier = Modifier.shadow(
                        elevation = GLOBAL_ELEVATION
                    ),
                    color = Transparent,
                )

                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        @Composable
        fun ApplicationButton(
            modifier: Modifier = Modifier,
            text: String,
            onClick: () -> Unit,
            enabled: Boolean = true,
            width: Float? = null,
            containerColor: Color = MaterialTheme.colorScheme.primary,
            contentColor: Color = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor: Color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
            disabledContentColor: Color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
        ) {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            Button(
                onClick = { onClick() },
                modifier = modifier
                    .shadow(
                        elevation = GLOBAL_ELEVATION,
                        shape = RoundedCornerShape(12.dp),
                        clip = true,
                    )
                    .then(if (width != null) Modifier.width(screenWidth * width) else Modifier.fillMaxWidth())
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    disabledContainerColor = disabledContainerColor,
                    contentColor = contentColor,
                    disabledContentColor = disabledContentColor
                ),
                shape = RoundedCornerShape(size = 12.dp),
                enabled = enabled
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        @Composable
        fun ApplicationTextField(
            modifier: Modifier = Modifier,
            value: String,
            onValueChange: (String) -> Unit,
            topText: String = "",
            topTextComposable: (@Composable () -> Unit)? = null,
            placeholderText: String,
            trailingIcon: (@Composable () -> Unit)? = null,
            leadingIcon: (@Composable () -> Unit)? = null,
            visualTransformation: VisualTransformation = VisualTransformation.None,
            keyboardType: KeyboardType = KeyboardType.Text,
            maxLines: Int = 1,
            focusRequester: FocusRequester? = null,
            enabled: Boolean = true,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 3.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (topTextComposable != null) {
                    topTextComposable()
                } else {
                    Text(
                        text = topText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                        )
                    )
                }
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholderText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.67f),
                            textAlign = TextAlign.Center
                        )
                    )
                },
                enabled = enabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.secondary,
                    unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                ),
                trailingIcon = trailingIcon,
                leadingIcon = leadingIcon,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                maxLines = maxLines,
                modifier = modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = GLOBAL_ELEVATION,
                        shape = RoundedCornerShape(12.dp),
                        clip = true
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onBackground)
                    .then(
                        if (focusRequester != null) Modifier.focusRequester(focusRequester)
                        else Modifier
                    ),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = visualTransformation
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}