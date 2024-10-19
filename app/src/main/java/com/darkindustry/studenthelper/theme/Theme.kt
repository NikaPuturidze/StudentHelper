package com.darkindustry.studenthelper.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    onPrimary = onPrimary,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    secondary = backgroundLight,
    tertiary = lessDark

)

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = onPrimary,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    secondary = backgroundDark,
    tertiary = lessLight

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun StudentHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    val statusBarColor = if (darkTheme) Color(0xFF000000) else Color(0xFFf2f2f2)

    // Set status bar color
    systemUiController.setStatusBarColor(color = statusBarColor)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = applicationTypography,
        content = content
    )
}