package com.company.planet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.data.Phone
import com.company.planet.data.brandColor
import com.company.planet.data.computePhone
import com.company.planet.ui.components.PlanetMark
import com.company.planet.ui.components.StatusPill
import com.company.planet.ui.components.TppDangerButton
import com.company.planet.ui.components.TppGhostButton
import com.company.planet.ui.components.TppPrimaryButton
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.BgElevated
import com.company.planet.ui.theme.BgPanel
import com.company.planet.ui.theme.Danger
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.JetBrainsMonoFamily
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.LineSoft
import com.company.planet.ui.theme.Muted
import com.company.planet.ui.theme.SpaceGroteskFamily
import com.company.planet.ui.theme.TextPrimary
import com.company.planet.ui.theme.Warn
import com.company.planet.ui.util.formatMoney
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.company.planet.ui.util.formatMoney
import com.company.planet.ui.util.formatMoneyOrDash

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PhonePreviewSheet(
    phone: Phone,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    fullScreen: Boolean = false
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val computed = computePhone(phone)
    val color = Color(brandColor(phone.company))

    val previewContent: @Composable () -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (fullScreen) Modifier.fillMaxHeight() else Modifier)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = if (fullScreen) 16.dp else 24.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    PlanetMark(size = 64.dp, brandColor = color, showMoon = false)
                    Column {
                        Text(
                            text = phone.model.ifBlank { "Untitled model" },
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 3.dp)
                        ) {
                            Text(
                                text = "${phone.company.ifBlank { "—" }} · ${phone.storage.ifBlank { "—" }} · ${phone.colour.ifBlank { "—" }} ·",
                                color = Muted,
                                fontSize = 13.sp
                            )
                            StatusPill(computed.sold)
                        }
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Muted)
                }
            }

            PreviewSection(title = "Device") {
                BoxWithConstraints {
                    val twoCols = maxWidth >= 500.dp
                    if (twoCols) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                                PreviewItem("Type", phone.type.ifBlank { "—" }, Modifier.weight(1f))
                                PreviewItem("PTA Status", phone.pta.ifBlank { "—" }, Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                                PreviewItem("IMEI 1", phone.imei1.ifBlank { "—" }, Modifier.weight(1f))
                                PreviewItem("IMEI 2", phone.imei2.ifBlank { "—" }, Modifier.weight(1f))
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            PreviewItem("Type", phone.type.ifBlank { "—" })
                            PreviewItem("PTA Status", phone.pta.ifBlank { "—" })
                            PreviewItem("IMEI 1", phone.imei1.ifBlank { "—" })
                            PreviewItem("IMEI 2", phone.imei2.ifBlank { "—" })
                        }
                    }
                }
                if (phone.detail.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    PreviewItem("Detail", phone.detail, valueFont = InterFamily, valueWeight = FontWeight.Normal)
                }
            }

            PreviewSection(title = "Sales Ledger") {
                BoxWithConstraints {
                    val wide = maxWidth >= 500.dp
                    val itemWidth = if (wide) 0.48f else 1f
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        maxItemsInEachRow = if (wide) 2 else 1
                    ) {
                        MoneyBox("Purchase Price", formatMoney(computed.purchase), Modifier.fillMaxWidth(itemWidth))
                        MoneyBox("Sale Price", formatMoneyOrDash(computed.sale, computed.sold), Modifier.fillMaxWidth(itemWidth))
                        MoneyBox("Told to Asif", formatMoneyOrDash(computed.told, computed.sold), Modifier.fillMaxWidth(itemWidth))
                        MoneyBox("Total Money", formatMoney(computed.totalMoney), Modifier.fillMaxWidth(itemWidth))
                        MoneyBox(
                            "Total Profit",
                            formatMoneyOrDash(computed.totalProfit, computed.sold),
                            Modifier.fillMaxWidth(itemWidth),
                            valueColor = if (computed.totalProfit >= 0) Accent else Danger
                        )
                        MoneyBox("Money Withdrawn", formatMoney(computed.withdrawn), Modifier.fillMaxWidth(itemWidth), valueColor = Warn)
                        MoneyBox("Remaining Amount", formatMoney(computed.remaining), Modifier.fillMaxWidth(itemWidth))
                        MoneyBox(
                            "Asif's Profit",
                            formatMoneyOrDash(computed.asifProfit, computed.sold),
                            Modifier.fillMaxWidth(itemWidth),
                            valueColor = if (computed.asifProfit >= 0) Accent else Danger
                        )
                        MoneyBox(
                            "Shozab's Profit",
                            formatMoneyOrDash(computed.shozabProfit, computed.sold),
                            Modifier.fillMaxWidth(1f),
                            valueColor = if (computed.shozabProfit >= 0) Accent else Danger
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (fullScreen) Arrangement.spacedBy(8.dp) else Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (fullScreen) {
                    TppDangerButton(text = "Delete", onClick = onDelete, modifier = Modifier.weight(1f))
                    TppGhostButton(text = "Close", onClick = onDismiss, modifier = Modifier.weight(1f))
                    TppPrimaryButton(text = "Edit", onClick = onEdit, modifier = Modifier.weight(1f))
                } else {
                    TppDangerButton(text = "Delete", onClick = onDelete)
                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    TppGhostButton(text = "Close", onClick = onDismiss)
                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    TppPrimaryButton(text = "Edit Phone", onClick = onEdit)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (fullScreen) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgPanel)
            ) {
                previewContent()
            }
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = BgPanel,
            dragHandle = null
        ) {
            previewContent()
        }
    }
}

@Composable
private fun PreviewSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Text(
            text = title.uppercase(),
            color = Muted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun PreviewItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueFont: androidx.compose.ui.text.font.FontFamily = JetBrainsMonoFamily,
    valueWeight: FontWeight = FontWeight.SemiBold
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.dp, color = LineSoft)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(label, color = Muted, fontSize = 13.5.sp)
        Text(
            value,
            color = TextPrimary,
            fontSize = 13.5.sp,
            fontFamily = valueFont,
            fontWeight = valueWeight,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun MoneyBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary
) {
    Column(
        modifier = modifier
            .background(BgElevated, RoundedCornerShape(12.dp))
            .border(1.dp, Line, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = Muted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = value,
            color = valueColor,
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.5.sp
        )
    }
}
