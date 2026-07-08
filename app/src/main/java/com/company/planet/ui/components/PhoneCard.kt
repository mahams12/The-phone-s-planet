package com.company.planet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.data.Phone
import com.company.planet.data.brandColor
import com.company.planet.data.computePhone
import com.company.planet.ui.theme.BgElevated
import com.company.planet.ui.theme.BgPanel
import com.company.planet.ui.theme.Danger
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.JetBrainsMonoFamily
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.Muted
import com.company.planet.ui.theme.Muted2
import com.company.planet.ui.theme.SpaceGroteskFamily
import com.company.planet.ui.util.formatMoney

@Composable
fun PhoneCard(
    phone: Phone,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val computed = computePhone(phone)
    val color = Color(brandColor(phone.company))

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BgPanel)
            .border(1.dp, Line, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                PlanetMark(
                    size = 44.dp,
                    brandColor = color,
                    showMoon = false,
                    showStatusDot = true,
                    sold = computed.sold
                )
                Column {
                    Text(
                        text = phone.model.ifBlank { "Untitled model" },
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.5.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${phone.company.ifBlank { "—" }} · ${phone.storage.ifBlank { "—" }} · ${phone.colour.ifBlank { "—" }}",
                        color = Muted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BgElevated)
                        .border(1.dp, Line, RoundedCornerShape(8.dp))
                        .clickable(onClick = onEdit)
                        .padding(7.dp)
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        tint = Muted,
                        modifier = Modifier.height(14.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BgElevated)
                        .border(1.dp, Line, RoundedCornerShape(8.dp))
                        .clickable(onClick = onDelete)
                        .padding(7.dp)
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = Danger,
                        modifier = Modifier.height(14.dp)
                    )
                }
            }
        }

        PhoneCardRow(label = "Status", value = "") {
            StatusPill(computed.sold)
        }
        PhoneCardRow(label = "Purchase", value = formatMoney(computed.purchase))
        PhoneCardRow(
            label = "Sale",
            value = if (computed.sold) formatMoney(computed.sale) else "—"
        )

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Line, RoundedCornerShape(1.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TOTAL PROFIT",
                color = Muted,
                fontSize = 11.sp,
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = when {
                    !computed.sold -> "Not sold yet"
                    computed.totalProfit >= 0 -> formatMoney(computed.totalProfit)
                    else -> formatMoney(computed.totalProfit)
                },
                color = when {
                    !computed.sold -> Muted2
                    computed.totalProfit >= 0 -> Color(0xFF5EEAD4)
                    else -> Danger
                },
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun PhoneCardRow(
    label: String,
    value: String,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Muted, fontSize = 12.5.sp, fontFamily = InterFamily)
        if (trailing != null) {
            trailing()
        } else {
            Text(
                value,
                color = Color(0xFFE9EBEE),
                fontSize = 12.5.sp,
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
