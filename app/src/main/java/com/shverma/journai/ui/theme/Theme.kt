package com.shverma.journai.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = YellowAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = OnDarkPrimary,
    onSecondary = OnDarkPrimary,
    onTertiary = OnDarkPrimary,
    onBackground = OnDarkSurface,
    onSurface = OnDarkSurface
)

private val LightColorScheme = lightColorScheme(
    primary = BrownPrimary,
    secondary = LightPeachSecondary,
    tertiary = YellowAccent,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = OnPrimary,
    onSecondary = OnPrimary,
    onTertiary = OnPrimary,
    onBackground = OnSurface,
    onSurface = OnSurface
)


@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    // Get responsive dimensions based on screen size
    val screenSizeClass = getScreenSizeClass()
    val responsiveDimensions = Dimensions(screenSizeClass = screenSizeClass)

    // Get responsive typography based on screen size
    val responsiveTypography = createResponsiveTypography()

    CompositionLocalProvider(
        LocalDimensions provides responsiveDimensions
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = responsiveTypography,
            content = content
        )
    }
}
