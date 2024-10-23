package com.darkindustry.studenthelper.logic.utils

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.darkindustry.studenthelper.R
import com.darkindustry.studenthelper.logic.utils.Utils.Companion.GLOBAL_TRANSITION_TIME
import kotlinx.coroutines.delay

enum class MessageType {
    ERROR, INFO, SUCCESS, WARNING
}

@Composable
fun MessageBox(
    message: String,
    messageType: MessageType,
    visible: Boolean
) {
    var isVisible by remember { mutableStateOf(visible) }
    val currentOnTimeout by rememberUpdatedState { isVisible = false }
    val progress = remember { Animatable(1f) }

    val (backgroundColor, image) = when (messageType) {
        MessageType.ERROR -> Color(0xFFff5657) to painterResource(id = R.drawable.ic_octagon_exclamation)
        MessageType.INFO -> MaterialTheme.colorScheme.primary to painterResource(id = R.drawable.ic_info)
        MessageType.SUCCESS -> Color(0xFF41cc4f) to painterResource(id = R.drawable.ic_checkmark)
        MessageType.WARNING -> Color(0xFFf6ab2f) to painterResource(id = R.drawable.ic_triangle_warning)
    }

    LaunchedEffect(visible) {
        isVisible = visible
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            progress.snapTo(1f)
            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 5000,
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }

    Column {
        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis = GLOBAL_TRANSITION_TIME)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(durationMillis = GLOBAL_TRANSITION_TIME)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .padding(horizontal = 16.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .zIndex(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = image,
                        contentDescription = "Error Icon",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier
                            .padding(start = 12.dp, end = 6.dp)
                            .size(32.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = message,
                            color = Color.White,
                            fontSize = 15.sp,
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(5000)
            currentOnTimeout()
        }
    }
}
