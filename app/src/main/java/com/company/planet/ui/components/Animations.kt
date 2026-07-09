package com.company.planet.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape

private val EnterTween = tween<Float>(durationMillis = 420, easing = FastOutSlowInEasing)
private val PressSpring = spring<Float>(dampingRatio = 0.62f, stiffness = 780f)

@Composable
fun Modifier.tppEnterAnimation(index: Int = 0): Modifier {
    val offsetY = remember { Animatable(14f) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index * 45).coerceAtMost(360).toLong())
        offsetY.animateTo(0f, EnterTween)
    }

    return this.graphicsLayer {
        translationY = offsetY.value
    }
}

@Composable
fun Modifier.tppDropIn(): Modifier {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(-14f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(560, easing = FastOutSlowInEasing))
        offsetY.animateTo(0f, tween(560, easing = FastOutSlowInEasing))
    }

    return this.graphicsLayer {
        this.alpha = alpha.value
        translationY = offsetY.value
    }
}

@Composable
fun Modifier.tppPressScale(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = PressSpring,
        label = "pressScale"
    )
    val lift by animateFloatAsState(
        targetValue = if (pressed) 0f else -2f,
        animationSpec = PressSpring,
        label = "pressLift"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            translationY = lift
        }
        .then(Modifier) // keep interaction source attachable from outside
}

fun Modifier.tppCardGlow(
    glowColor: Color,
    highlighted: Boolean
): Modifier = composed {
    if (!highlighted) return@composed this

    val transition = rememberInfiniteTransition(label = "cardGlow")
    val pulse by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    graphicsLayer {
        shadowElevation = 10f * pulse
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ambientShadowColor = glowColor.copy(alpha = 0.45f)
        spotShadowColor = glowColor.copy(alpha = 0.55f)
    }
}

@Composable
fun Modifier.tppChipPress(): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = PressSpring,
        label = "chipScale"
    )
    return this
        .scale(scale)
        .then(Modifier) // interaction wired by clickable in parent
}

private val CardShape = RoundedCornerShape(16.dp)

fun Modifier.tppCard3D(
    shape: Shape = CardShape,
    elevation: Dp = 12.dp,
    lift: Float = -3f
): Modifier = composed {
    this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = Color(0x70000000),
            spotColor = Color(0xA3000000),
            clip = false
        )
        .graphicsLayer {
            translationY = lift
        }
}

fun Modifier.tppCard3DSubtle(
    shape: Shape = CardShape,
    elevation: Dp = 8.dp,
    lift: Float = -2f
): Modifier = composed {
    this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = Color(0x55000000),
            spotColor = Color(0x80000000),
            clip = false
        )
        .graphicsLayer {
            translationY = lift
        }
}
