package com.shverma.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Base typography with fixed font sizes
 */
private val BaseTypography = Typography(
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = TextSecondary
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    )
)

/**
 * Creates responsive typography based on screen size
 */
@Composable
fun createResponsiveTypography(): Typography {
    val screenSizeClass = getScreenSizeClass()

    // Use the same scaling factors as dimensions for consistency
    val scalingFactor = when (screenSizeClass) {
        ScreenSizeClass.COMPACT -> 1.0f
        ScreenSizeClass.MEDIUM -> 1.25f
        ScreenSizeClass.EXPANDED -> 1.5f
    }

    return Typography(
        titleLarge = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = (24 * scalingFactor).sp
        ),
        titleMedium = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = (20 * scalingFactor).sp
        ),
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (16 * scalingFactor).sp
        ),
        bodyMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (14 * scalingFactor).sp,
            color = TextSecondary
        ),
        labelLarge = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scalingFactor).sp,
        )
    )
}

// Default typography for use in places where Composable context is not available
val AppTypography = BaseTypography
