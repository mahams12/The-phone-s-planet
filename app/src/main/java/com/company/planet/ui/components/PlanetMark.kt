package com.company.planet.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.BgPanel
import com.company.planet.ui.theme.Muted2

@Composable
fun PlanetMark(
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    brandColor: Color? = null,
    showMoon: Boolean = true,
    showStatusDot: Boolean = false,
    sold: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbit")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val coreColor = brandColor ?: Color(0xFF9AA3AF)
    val ringColor = brandColor ?: Accent

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(size * 0.78f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            coreColor.copy(alpha = 0.9f),
                            coreColor,
                            coreColor.copy(alpha = 0.7f)
                        ),
                        center = Offset(0.32f, 0.28f)
                    )
                )
        )

        Canvas(
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    rotationZ = rotation
                    rotationX = 70f
                }
        ) {
            drawCircle(
                color = ringColor.copy(alpha = 0.55f),
                style = Stroke(width = size.toPx() * 0.033f)
            )
            if (showMoon) {
                drawCircle(
                    color = Accent,
                    radius = size.toPx() * 0.055f,
                    center = Offset(size.toPx() * 0.02f, size.toPx() * 0.5f)
                )
            }
        }

        if (showStatusDot) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(size * 0.2f)
                    .border(2.dp, BgPanel, CircleShape)
                    .clip(CircleShape)
                    .background(if (sold) Accent else Muted2)
            )
        }
    }
}

@Composable
fun EmptyOrb(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(modifier = modifier.size(80.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF3B414C), Color(0xFF1B1F26))
                    )
                )
        )
        Canvas(
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer {
                    rotationZ = rotation
                    rotationX = 70f
                }
        ) {
            drawCircle(
                color = Muted2,
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(8f, 8f)
                    )
                )
            )
        }
    }
}
