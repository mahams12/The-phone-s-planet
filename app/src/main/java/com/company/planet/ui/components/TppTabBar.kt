package com.company.planet.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.AccentOn
import com.company.planet.ui.theme.BgElevated
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.Muted
import com.company.planet.viewmodel.AppScreen

@Composable
fun TppTabBar(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val iconOnly = screenWidthDp < 360
    val shortLabels = screenWidthDp < 400

    val tabs = listOf(
        TabItem(AppScreen.DASHBOARD, "Dashboard", "Dash", Icons.Outlined.Dashboard),
        TabItem(AppScreen.ADD, "Add Phone", "Add", Icons.Default.Add),
        TabItem(AppScreen.INVENTORY, "Inventory", "Stock", Icons.Outlined.GridView)
    )

    val selectedIndex = tabs.indexOfFirst { it.screen == currentScreen }.coerceAtLeast(0)
    val barHeight = if (iconOnly) 44.dp else 40.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(BgElevated)
            .border(1.dp, Line, RoundedCornerShape(999.dp))
            .padding(4.dp)
    ) {
        val tabWidth = maxWidth / tabs.size
        val animatedOffset by animateDpAsState(
            targetValue = tabWidth * selectedIndex,
            animationSpec = tween(280),
            label = "tabOffset"
        )

        Box(
            modifier = Modifier
                .offset(x = animatedOffset)
                .width(tabWidth)
                .height(barHeight)
                .clip(RoundedCornerShape(999.dp))
                .background(Accent)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                val selected = currentScreen == tab.screen
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (pressed && !selected) 0.95f else 1f,
                    animationSpec = spring(dampingRatio = 0.62f, stiffness = 780f),
                    label = "tabScale"
                )
                val label = when {
                    iconOnly -> ""
                    shortLabels -> tab.shortLabel
                    else -> tab.label
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(barHeight)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onScreenSelected(tab.screen) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (iconOnly) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = if (selected) AccentOn else Muted,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (selected) AccentOn else Muted,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = label,
                                color = if (selected) AccentOn else Muted,
                                fontFamily = InterFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = if (shortLabels) 11.5.sp else 12.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class TabItem(
    val screen: AppScreen,
    val label: String,
    val shortLabel: String,
    val icon: ImageVector
)
