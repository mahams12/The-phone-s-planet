package com.company.planet.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ScreenSize { Compact, Medium, Expanded }

data class ResponsiveValues(
    val screenSize: ScreenSize,
    val horizontalPadding: Dp,
    val statColumns: Int,
    val phoneColumns: Int,
    val compactHeader: Boolean,
    val useFullScreenSheet: Boolean,
    val statValueSize: Float,
    val logoHeight: Dp
)

@Composable
fun rememberResponsive(): ResponsiveValues {
    val config = LocalConfiguration.current
    val widthDp = config.screenWidthDp

    return remember(widthDp) {
        when {
            widthDp < 360 -> ResponsiveValues(
                screenSize = ScreenSize.Compact,
                horizontalPadding = 12.dp,
                statColumns = 2,
                phoneColumns = 1,
                compactHeader = true,
                useFullScreenSheet = true,
                statValueSize = 17f,
                logoHeight = 52.dp
            )
            widthDp < 600 -> ResponsiveValues(
                screenSize = ScreenSize.Compact,
                horizontalPadding = 16.dp,
                statColumns = 2,
                phoneColumns = 1,
                compactHeader = true,
                useFullScreenSheet = widthDp < 400,
                statValueSize = 19f,
                logoHeight = 56.dp
            )
            widthDp < 840 -> ResponsiveValues(
                screenSize = ScreenSize.Medium,
                horizontalPadding = 20.dp,
                statColumns = 3,
                phoneColumns = 2,
                compactHeader = false,
                useFullScreenSheet = false,
                statValueSize = 21f,
                logoHeight = 60.dp
            )
            else -> ResponsiveValues(
                screenSize = ScreenSize.Expanded,
                horizontalPadding = 20.dp,
                statColumns = 4,
                phoneColumns = if (widthDp < 1100) 3 else 4,
                compactHeader = false,
                useFullScreenSheet = false,
                statValueSize = 21f,
                logoHeight = 64.dp
            )
        }
    }
}
