package com.shverma.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * Enum representing different screen size categories
 */
enum class ScreenSizeClass {
    COMPACT,    // Phones in portrait
    MEDIUM,     // Phones in landscape, small tablets
    EXPANDED    // Large tablets, foldables
}

/**
 * A centralized dimension system for consistent UI scaling
 * Base unit is 4.dp, and all other dimensions are multiples of this base unit
 * Dimensions are scaled based on screen size
 */
data class Dimensions(
    // Screen size class
    val screenSizeClass: ScreenSizeClass = ScreenSizeClass.COMPACT,

    // Scaling factors for different screen sizes
    val scalingFactor: Float = when (screenSizeClass) {
        ScreenSizeClass.COMPACT -> 1.0f
        ScreenSizeClass.MEDIUM -> 1.25f
        ScreenSizeClass.EXPANDED -> 1.5f
    },

    // Base unit
    val base: Dp = (4 * scalingFactor).dp,

    // Spacing/Padding
    val spacingXXSmall: Dp = (2 * scalingFactor).dp,  // 0.5x base
    val spacingXSmall: Dp = (4 * scalingFactor).dp,   // 1x base
    val spacingSmall: Dp = (8 * scalingFactor).dp,    // 2x base
    val spacingMedium: Dp = (12 * scalingFactor).dp,  // 3x base
    val spacingRegular: Dp = (16 * scalingFactor).dp, // 4x base
    val spacingLarge: Dp = (20 * scalingFactor).dp,   // 5x base
    val spacingXLarge: Dp = (24 * scalingFactor).dp,  // 6x base
    val spacingXXLarge: Dp = (32 * scalingFactor).dp, // 8x base
    val spacingXXXLarge: Dp = (48 * scalingFactor).dp, // 12x base

    // Component sizes
    val buttonHeight: Dp = (56 * scalingFactor).dp,   // 14x base
    val iconSizeSmall: Dp = (16 * scalingFactor).dp,  // 4x base
    val iconSizeMedium: Dp = (20 * scalingFactor).dp, // 5x base
    val iconSizeLarge: Dp = (24 * scalingFactor).dp,  // 6x base

    // Border radius
    val radiusSmall: Dp = (8 * scalingFactor).dp,     // 2x base
    val radiusMedium: Dp = (12 * scalingFactor).dp,   // 3x base
    val radiusLarge: Dp = (16 * scalingFactor).dp,    // 4x base
    val radiusXLarge: Dp = (24 * scalingFactor).dp,   // 6x base

    // Elevation
    val elevationSmall: Dp = (1 * scalingFactor).dp,
    val elevationMedium: Dp = (2 * scalingFactor).dp,
    val elevationLarge: Dp = (4 * scalingFactor).dp
)

// Default dimensions
val LocalDimensions = compositionLocalOf { Dimensions() }

/**
 * Determines the screen size class based on screen width and height
 */
@Composable
fun getScreenSizeClass(): ScreenSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp

    // Use the smaller dimension to determine size class
    val smallestDimension = min(screenWidthDp, screenHeightDp)

    return when {
        smallestDimension < 600 -> ScreenSizeClass.COMPACT    // Phone
        smallestDimension < 840 -> ScreenSizeClass.MEDIUM     // Small tablet
        else -> ScreenSizeClass.EXPANDED                      // Large tablet
    }
}

/**
 * Helper function to easily access responsive dimensions from composables
 */
@Composable
fun dimensions(): Dimensions {
    val screenSizeClass = getScreenSizeClass()
    return Dimensions(screenSizeClass = screenSizeClass)
}
