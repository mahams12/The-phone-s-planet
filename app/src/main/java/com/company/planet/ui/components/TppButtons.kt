package com.company.planet.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.AccentGradientEnd
import com.company.planet.ui.theme.AccentGradientStart
import com.company.planet.ui.theme.AccentOn
import com.company.planet.ui.theme.BgElevated
import com.company.planet.ui.theme.Danger
import com.company.planet.ui.theme.DangerDim
import com.company.planet.ui.theme.GlowRing
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.TextPrimary

private val PrimaryGlowStyle = GlowButtonStyle(
    background = Brush.horizontalGradient(listOf(AccentGradientStart, AccentGradientEnd)),
    borderColor = GlowRing,
    glowColor = GlowRing
)

private val GhostGlowStyle = GlowButtonStyle(
    background = Brush.linearGradient(listOf(BgElevated, BgElevated)),
    borderColor = Accent,
    glowColor = Accent
)

private val DangerGlowStyle = GlowButtonStyle(
    background = Brush.linearGradient(listOf(DangerDim, DangerDim)),
    borderColor = Danger,
    glowColor = Danger
)

@Composable
fun TppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    pulseRing: Boolean = true
) {
    TppGlowButton(
        onClick = onClick,
        modifier = modifier,
        pulseRing = pulseRing,
        style = PrimaryGlowStyle
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentOn)
        }
        Text(text, fontFamily = InterFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentOn)
    }
}

@Composable
fun TppGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TppGlowButton(
        onClick = onClick,
        modifier = modifier,
        pulseRing = false,
        style = GhostGlowStyle
    ) {
        Text(text, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
    }
}

@Composable
fun TppDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TppGlowButton(
        onClick = onClick,
        modifier = modifier,
        pulseRing = false,
        style = DangerGlowStyle
    ) {
        Text(text, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Danger)
    }
}

@Composable
fun AddPhoneButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TppPrimaryButton(
        text = "Add Phone",
        onClick = onClick,
        modifier = modifier,
        icon = Icons.Default.Add
    )
}
