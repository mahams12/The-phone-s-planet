package com.company.planet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.data.Phone
import com.company.planet.data.PhoneTotals
import com.company.planet.data.brandColor
import com.company.planet.data.computePhone
import com.company.planet.ui.components.StatCard
import com.company.planet.ui.components.StatusPill
import com.company.planet.ui.components.tppCard3D
import com.company.planet.ui.components.tppDropIn
import com.company.planet.ui.components.tppEnterAnimation
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.BgElevated
import com.company.planet.ui.theme.BgPanel
import com.company.planet.ui.theme.Danger
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.JetBrainsMonoFamily
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.TextPrimary
import com.company.planet.ui.theme.TextSecondary
import com.company.planet.ui.theme.Warn
import com.company.planet.ui.util.formatMoney
import com.company.planet.ui.util.rememberResponsive
import com.company.planet.viewmodel.PhoneViewModel

@Composable
fun DashboardScreen(
    phones: List<Phone>,
    totals: PhoneTotals,
    viewModel: PhoneViewModel,
    modifier: Modifier = Modifier
) {
    val responsive = rememberResponsive()

    Column(modifier = modifier) {
        ScreenHead(title = "Dashboard", subtitle = "Your business at a glance.")

        StatsGrid(totals, responsive.statColumns, responsive.statValueSize)
        Spacer(Modifier.height(14.dp))

        BoxWithConstraints {
            if (maxWidth >= 860.dp) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    ProfitSplitPanel(totals, Modifier.weight(1.1f), enterIndex = 4)
                    BrandPanel(phones, Modifier.weight(1f), enterIndex = 5)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ProfitSplitPanel(totals, Modifier.fillMaxWidth(), enterIndex = 4)
                    BrandPanel(phones, Modifier.fillMaxWidth(), enterIndex = 5)
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        RecentPanel(phones, onPhoneClick = viewModel::openPreview, enterIndex = 6)
    }
}

@Composable
private fun ScreenHead(title: String, subtitle: String) {
    Column(Modifier.padding(bottom = 18.dp).tppDropIn()) {
        Text(title, color = TextPrimary, fontFamily = InterFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(subtitle, color = TextSecondary, fontFamily = InterFamily, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun StatsGrid(totals: PhoneTotals, columns: Int, valueSize: Float) {
    data class Stat(
        val label: String,
        val value: String,
        val color: Color,
        val subtitle: String? = null
    )

    val items = listOf(
        Stat("Inventory", totals.count.toString(), TextPrimary, "${totals.inStock} in stock"),
        Stat("Sold", totals.sold.toString(), Accent),
        Stat("Total Profit", formatMoney(totals.totalProfit), if (totals.totalProfit >= 0) Accent else Danger),
        Stat("Shozab's Profit", formatMoney(totals.shozabProfit), Warn)
    )

    var itemIndex = 0
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(columns).forEach { row ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { stat ->
                    StatCard(
                        label = stat.label,
                        value = stat.value,
                        color = stat.color,
                        subtitle = stat.subtitle,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        valueSize = valueSize,
                        index = itemIndex++
                    )
                }
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun DashboardPanel(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    enterIndex: Int = 0,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .tppEnterAnimation(enterIndex)
            .tppCard3D()
            .background(BgPanel, RoundedCornerShape(16.dp))
            .border(1.dp, Line, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(title, color = TextPrimary, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp)
        Text(subtitle, color = TextSecondary, fontFamily = InterFamily, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
        content()
    }
}

@Composable
private fun ProfitSplitPanel(totals: PhoneTotals, modifier: Modifier = Modifier, enterIndex: Int = 0) {
    DashboardPanel("Profit Split", "Partner share from sold phones", modifier, enterIndex) {
        if (totals.sold == 0) {
            Text("No sales recorded yet.", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            return@DashboardPanel
        }

        val a = maxOf(totals.asifProfit, 0.0)
        val s = maxOf(totals.shozabProfit, 0.0)
        val total = a + s
        val aPct = if (total > 0) (a / total).toFloat() else 0.5f
        val sPct = if (total > 0) (s / total).toFloat() else 0.5f

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(BgElevated)
                .border(1.dp, Line, RoundedCornerShape(999.dp))
        ) {
            Box(Modifier.fillMaxHeight().weight(aPct).background(Accent))
            Box(Modifier.fillMaxHeight().weight(sPct).background(Warn))
        }

        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            LegendItem("Asif", Accent, formatMoney(totals.asifProfit))
            LegendItem("Shozab", Warn, formatMoney(totals.shozabProfit))
        }

        Spacer(Modifier.height(12.dp))
        CashFlowItem("Sales revenue", formatMoney(totals.totalMoney), Accent)
    }
}

@Composable
private fun CashFlowItem(label: String, amount: String, color: Color) {
    Column {
        Text(label, color = TextSecondary, fontSize = 11.5.sp, fontFamily = InterFamily)
        Text(amount, color = color, fontFamily = JetBrainsMonoFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun LegendItem(name: String, color: Color, amount: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Box(Modifier.size(9.dp).background(color, RoundedCornerShape(999.dp)))
        Text(name, fontSize = 12.5.sp, color = TextSecondary, fontFamily = InterFamily)
        Text(amount, color = TextPrimary, fontFamily = JetBrainsMonoFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
    }
}

@Composable
private fun BrandPanel(phones: List<Phone>, modifier: Modifier = Modifier, enterIndex: Int = 0) {
    DashboardPanel("Inventory by Brand", "Phones grouped by company", modifier, enterIndex) {
        val counts = phones.groupingBy { it.company.ifBlank { "Other" } }.eachCount().entries.sortedByDescending { it.value }
        if (counts.isEmpty()) {
            Text("No phones logged yet.", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            return@DashboardPanel
        }
        val max = counts.maxOf { it.value }
        counts.forEach { (name, count) ->
            val color = Color(brandColor(name))
            Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(name, color = TextSecondary, fontSize = 12.5.sp, fontFamily = InterFamily, modifier = Modifier.width(78.dp))
                Box(Modifier.weight(1f).height(9.dp).background(BgElevated, RoundedCornerShape(999.dp))) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(count / max.toFloat()).background(color, RoundedCornerShape(999.dp)))
                }
                Text(
                    count.toString(),
                    color = TextPrimary,
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 12.5.sp,
                    modifier = Modifier.width(28.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun RecentPanel(phones: List<Phone>, onPhoneClick: (Phone) -> Unit, enterIndex: Int = 0) {
    DashboardPanel("Recent Additions", "Last phones added to the ledger", Modifier.fillMaxWidth(), enterIndex) {
        val recent = phones.sortedByDescending { it.createdAt }.take(5)
        if (recent.isEmpty()) {
            Text("Nothing added yet — head to Add Phone.", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            return@DashboardPanel
        }
        recent.forEachIndexed { index, phone ->
            val c = computePhone(phone)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .tppEnterAnimation(index + 1)
                    .clickable { onPhoneClick(phone) }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(30.dp).background(Color(brandColor(phone.company)).copy(alpha = 0.3f), RoundedCornerShape(999.dp)))
                Column(Modifier.weight(1f)) {
                    Text(
                        phone.model.ifBlank { "Untitled model" },
                        color = TextPrimary,
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        maxLines = 1
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${phone.company.ifBlank { "—" }} ·", color = TextSecondary, fontSize = 11.5.sp, fontFamily = InterFamily)
                        StatusPill(c.sold)
                    }
                }
                Text(
                    if (c.sold) formatMoney(c.totalProfit) else "—",
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    color = when {
                        !c.sold -> TextSecondary
                        c.totalProfit >= 0 -> Accent
                        else -> Danger
                    }
                )
            }
            if (index < recent.lastIndex) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(Line.copy(alpha = 0.5f)))
            }
        }
    }
}
