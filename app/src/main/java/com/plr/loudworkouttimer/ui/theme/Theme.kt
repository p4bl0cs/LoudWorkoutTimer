package com.plr.loudworkouttimer.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.plr.loudworkouttimer.CustomColorScheme

private val DarkColorScheme = darkColorScheme(
    background = HardNavyBlue,
    primary = HardMidnightBlue,
    secondary = HardBlueGray,
    tertiary = LiteSmoke,
    surface = HardNavyBlue
)

private val LightColorScheme = lightColorScheme(
    background = LiteDark,
    primary = LiteSmoke,
    primaryContainer = LiteLime,
    secondary = LiteYellow,
    secondaryContainer = LitePurple,
    tertiary = HardDarkBlue,
    surface = LiteDarkBlue,
    error = LiteRed


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
fun LoudWorkoutTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    var colorScheme = LightColorScheme

    /*if (darkTheme) {
        colorScheme = DarkColorScheme
    }*/

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    val appTypography = Typography(
        labelLarge = TextStyle(
            color = colorScheme.primary,
            fontSize = 30.sp
        )
    )

    CustomColorScheme.getInstance(
        colorScheme.primary,
        colorScheme.secondary,
        colorScheme.tertiary,
        colorScheme.background,
        colorScheme.surface,
        colorScheme.error,
        colorScheme.primaryContainer)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography,
        content = content,
    )
}