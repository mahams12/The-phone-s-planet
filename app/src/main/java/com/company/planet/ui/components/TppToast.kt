package com.company.planet.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.AccentGradientEnd
import com.company.planet.ui.theme.AccentGradientStart
import com.company.planet.ui.theme.GlowRing
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun TppToast(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(message) {
        if (message != null) {
            delay(2800)
            onDismiss()
        }
    }

    val transition = rememberInfiniteTransition(label = "toastRing")
    val ringProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "toastRingProgress"
    )
    val density = LocalDensity.current

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = message != null,
            enter = fadeIn(tween(280)) + slideInVertically(tween(320)) { it / 2 } + scaleIn(tween(320), initialScale = 0.9f),
            exit = fadeOut(tween(220)) + slideOutVertically(tween(220)) { it / 2 } + scaleOut(tween(220), targetScale = 0.92f)
        ) {
            if (message != null) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .drawBehind {
                            val minDim = minOf(size.width, size.height)
                            val radius = minDim * (0.45f + ringProgress * 0.9f) / 2f
                            val alpha = (1f - ringProgress) * 0.45f
                            drawCircle(
                                color = GlowRing.copy(alpha = alpha),
                                radius = radius,
                                center = Offset(size.width / 2f, size.height / 2f),
                                style = Stroke(width = with(density) { 3.dp.toPx() })
                            )
                        }
                        .background(
                            Brush.horizontalGradient(
                                listOf(AccentGradientStart.copy(alpha = 0.18f), AccentGradientEnd.copy(alpha = 0.18f))
                            ),
                            RoundedCornerShape(1000.dp)
                        )
                        .border(1.5.dp, GlowRing.copy(alpha = 0.7f), RoundedCornerShape(1000.dp))
                        .padding(horizontal = 22.dp, vertical = 13.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Accent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = message,
                            color = TextPrimary,
                            fontFamily = InterFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
