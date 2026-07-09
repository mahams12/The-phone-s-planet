package com.company.planet.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

private val GlowShape = RoundedCornerShape(1000.dp)
private val PressSpring = spring<Float>(dampingRatio = 0.62f, stiffness = 780f)

data class GlowButtonStyle(
    val background: Brush,
    val borderColor: Color,
    val glowColor: Color
)

@Composable
fun Modifier.tppGlowPress(
    interactionSource: MutableInteractionSource,
    enabled: Boolean = true,
    pulseRing: Boolean = true,
    ringColor: Color,
    glowColor: Color,
    onClick: () -> Unit
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val lift by animateFloatAsState(
        targetValue = if (!enabled) 0f else if (pressed) -6f else -1f,
        animationSpec = PressSpring,
        label = "glowLift"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "glowAlpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = PressSpring,
        label = "glowScale"
    )

    val transition = rememberInfiniteTransition(label = "ringPulse")
    val ringProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringProgress"
    )

    var size by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    return this
        .onSizeChanged { size = it }
        .graphicsLayer {
            translationY = lift
            scaleX = scale
            scaleY = scale
            shadowElevation = if (pressed) 14f else 8f
            shape = GlowShape
            ambientShadowColor = glowColor.copy(alpha = 0.45f)
            spotShadowColor = glowColor.copy(alpha = 0.55f)
        }
        .drawBehind {
            if (!enabled || size == IntSize.Zero) return@drawBehind

            val minDim = minOf(size.width, size.height).toFloat()
            val radius = minDim * (0.5f + ringProgress * 1.4f) / 2f
            val alpha = (1f - ringProgress) * if (pressed) 0f else 0.5f
            if (pulseRing && alpha > 0.01f) {
                drawCircle(
                    color = ringColor.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(size.width / 2f, size.height / 2f),
                    style = Stroke(width = with(density) { 3.dp.toPx() })
                )
            }

            if (glowAlpha > 0f) {
                drawRoundRect(
                    color = glowColor.copy(alpha = glowAlpha * 0.7f),
                    cornerRadius = CornerRadius(size.height / 2f, size.height / 2f),
                    style = Stroke(width = with(density) { 5.dp.toPx() })
                )
            }
        }
        .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}

@Composable
fun TppGlowButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pulseRing: Boolean = true,
    style: GlowButtonStyle,
    shape: Shape = GlowShape,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 42.dp)
                .clip(shape)
                .background(style.background)
                .border(1.dp, style.borderColor.copy(alpha = 0.4f), shape)
                .tppGlowPress(
                    interactionSource = interactionSource,
                    enabled = enabled,
                    pulseRing = pulseRing,
                    ringColor = style.glowColor,
                    glowColor = style.glowColor,
                    onClick = onClick
                )
                .padding(horizontal = 18.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
