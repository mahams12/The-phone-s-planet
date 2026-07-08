package com.company.planet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.AccentDim
import com.company.planet.ui.theme.BgPanel
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.JetBrainsMonoFamily
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.Muted
import com.company.planet.ui.theme.SpaceGroteskFamily
import com.company.planet.ui.theme.TextPrimary
import com.company.planet.viewmodel.StatusFilter

@Composable
fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    valueSize: Float = 21f
) {
    Column(
        modifier = modifier
            .background(BgPanel, RoundedCornerShape(16.dp))
            .border(1.dp, Line, RoundedCornerShape(16.dp))
            .drawBehind {
                drawLine(
                    color = color.copy(alpha = 0.6f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = Muted,
            fontFamily = InterFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.5.sp,
            letterSpacing = 0.6.sp,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            color = color,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = valueSize.sp,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TppFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) AccentDim else BgPanel
    val borderColor = if (selected) Accent else Line
    val textColor = if (selected) Accent else Muted

    Text(
        text = label,
        modifier = modifier
            .background(bg, RoundedCornerShape(999.dp))
            .border(1.dp, borderColor, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        color = textColor,
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipsRow(
    companies: List<String>,
    companyFilter: String,
    statusFilter: StatusFilter,
    onCompanySelected: (String) -> Unit,
    onStatusSelected: (StatusFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TppFilterChip("All Brands", companyFilter == "all", onClick = { onCompanySelected("all") })
            companies.forEach { company ->
                TppFilterChip(company, companyFilter == company, onClick = { onCompanySelected(company) })
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TppFilterChip("All", statusFilter == StatusFilter.ALL, onClick = { onStatusSelected(StatusFilter.ALL) })
            TppFilterChip("In Stock", statusFilter == StatusFilter.STOCK, onClick = { onStatusSelected(StatusFilter.STOCK) })
            TppFilterChip("Sold", statusFilter == StatusFilter.SOLD, onClick = { onStatusSelected(StatusFilter.SOLD) })
        }
    }
}

@Composable
fun StatusPill(sold: Boolean, modifier: Modifier = Modifier) {
    val bg = if (sold) AccentDim else Color(0x248B93A1)
    val color = if (sold) Accent else Muted
    Text(
        text = if (sold) "SOLD" else "IN STOCK",
        modifier = modifier
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp),
        color = color,
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.5.sp,
        letterSpacing = 0.3.sp
    )
}

@Composable
fun CardRow(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Muted, fontSize = 12.5.sp, fontFamily = InterFamily)
        Text(
            value,
            color = valueColor,
            fontSize = 12.5.sp,
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.Medium
        )
    }
}
