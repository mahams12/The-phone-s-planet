package com.company.planet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.data.Phone
import com.company.planet.ui.components.AddPhoneButton
import com.company.planet.ui.components.EmptyOrb
import com.company.planet.ui.components.FilterChipsRow
import com.company.planet.ui.components.PhoneCard
import com.company.planet.ui.components.StatCard
import com.company.planet.ui.components.TppLogo
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.Bg
import com.company.planet.ui.theme.BgPanel
import com.company.planet.ui.theme.Danger
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.Muted
import com.company.planet.ui.theme.SpaceGroteskFamily
import com.company.planet.ui.theme.TextPrimary
import com.company.planet.ui.theme.Warn
import com.company.planet.ui.util.formatMoney
import com.company.planet.ui.util.rememberResponsive
import com.company.planet.viewmodel.PhoneViewModel

@Composable
fun HomeScreen(
    viewModel: PhoneViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val responsive = rememberResponsive()
    var deleteTarget by remember { mutableStateOf<Phone?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x0F5EEAD4), Color.Transparent),
                    radius = 900f,
                    center = androidx.compose.ui.geometry.Offset(0.15f, 0f)
                )
            )
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x0DA78BFA), Color.Transparent),
                    radius = 900f,
                    center = androidx.compose.ui.geometry.Offset(1f, 0f)
                )
            )
            .background(Bg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = responsive.horizontalPadding)
                .widthIn(max = 1280.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                TopBar(
                    onAddClick = viewModel::openAddForm,
                    compact = responsive.compactHeader,
                    logoHeight = responsive.logoHeight
                )
                Spacer(modifier = Modifier.height(if (responsive.compactHeader) 16.dp else 22.dp))
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Accent)
                    }
                }
            } else {
                item {
                    StatsGrid(
                        state = state,
                        columns = responsive.statColumns,
                        valueSize = responsive.statValueSize
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    OutlinedTextField(
                        value = state.search,
                        onValueChange = viewModel::setSearch,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search model, brand, IMEI…", color = Muted) },
                        leadingIcon = {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = Muted)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent,
                            unfocusedBorderColor = Line,
                            focusedContainerColor = BgPanel,
                            unfocusedContainerColor = BgPanel,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = Accent
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    FilterChipsRow(
                        companies = state.companies,
                        companyFilter = state.companyFilter,
                        statusFilter = state.statusFilter,
                        onCompanySelected = viewModel::setCompanyFilter,
                        onStatusSelected = viewModel::setStatusFilter
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }

                if (state.filteredPhones.isEmpty()) {
                    item {
                        EmptyState(hasPhones = state.phones.isNotEmpty())
                    }
                } else {
                    val rows = state.filteredPhones.chunked(responsive.phoneColumns)
                    items(rows) { rowPhones ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            rowPhones.forEach { phone ->
                                PhoneCard(
                                    phone = phone,
                                    onClick = { viewModel.openPreview(phone) },
                                    onEdit = { viewModel.openEditForm(phone) },
                                    onDelete = { deleteTarget = phone },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(responsive.phoneColumns - rowPhones.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        if (state.showForm) {
            PhoneFormSheet(
                editingPhone = state.editingPhone,
                onDismiss = viewModel::closeForm,
                onSave = viewModel::savePhone,
                fullScreen = responsive.useFullScreenSheet
            )
        }

        state.previewPhone?.let { phone ->
            PhonePreviewSheet(
                phone = phone,
                onDismiss = viewModel::closePreview,
                onEdit = { viewModel.openEditForm(phone) },
                onDelete = { deleteTarget = phone },
                fullScreen = responsive.useFullScreenSheet
            )
        }

        deleteTarget?.let { phone ->
            AlertDialog(
                onDismissRequest = { deleteTarget = null },
                title = { Text("Delete phone?") },
                text = {
                    Text("Delete \"${phone.model.ifBlank { "this phone" }}\"? This cannot be undone.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deletePhone(phone)
                        deleteTarget = null
                    }) {
                        Text("Delete", color = Danger)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteTarget = null }) {
                        Text("Cancel")
                    }
                },
                containerColor = BgPanel
            )
        }

        state.errorMessage?.let { message ->
            AlertDialog(
                onDismissRequest = viewModel::clearError,
                title = { Text("Error") },
                text = { Text(message) },
                confirmButton = {
                    TextButton(onClick = viewModel::clearError) {
                        Text("OK")
                    }
                },
                containerColor = BgPanel
            )
        }
    }
}

@Composable
private fun StatsGrid(
    state: com.company.planet.viewmodel.PhoneUiState,
    columns: Int,
    valueSize: Float
) {
    val totals = state.totals
    val statItems = listOf(
        Triple("Total Phones", totals.count.toString(), TextPrimary),
        Triple("In Stock", totals.inStock.toString(), Muted),
        Triple("Sold", totals.sold.toString(), Accent),
        Triple("Total Sales Money", formatMoney(totals.totalMoney), Accent),
        Triple(
            "Total Profit",
            formatMoney(totals.totalProfit),
            if (totals.totalProfit >= 0) Accent else Danger
        ),
        Triple("Money Withdrawn", formatMoney(totals.withdrawn), Warn),
        Triple(
            "Remaining Amount",
            formatMoney(totals.remaining),
            if (totals.remaining >= 0) TextPrimary else Danger
        ),
        Triple(
            "Asif's Profit",
            formatMoney(totals.asifProfit),
            if (totals.asifProfit >= 0) Accent else Danger
        ),
        Triple(
            "Shozab's Profit",
            formatMoney(totals.shozabProfit),
            if (totals.shozabProfit >= 0) Accent else Danger
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        statItems.chunked(columns).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { (label, value, color) ->
                    StatCard(
                        label = label,
                        value = value,
                        color = color,
                        valueSize = valueSize,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(columns - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    onAddClick: () -> Unit,
    compact: Boolean,
    logoHeight: androidx.compose.ui.unit.Dp
) {
    if (compact) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgPanel, RoundedCornerShape(16.dp))
                .border(1.dp, Line, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BrandBlock(logoHeight = logoHeight, compact = true)
            AddPhoneButton(onClick = onAddClick, modifier = Modifier.fillMaxWidth())
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgPanel, RoundedCornerShape(16.dp))
                .border(1.dp, Line, RoundedCornerShape(16.dp))
                .padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrandBlock(logoHeight = logoHeight, compact = false)
            AddPhoneButton(onClick = onAddClick)
        }
    }
}

@Composable
private fun BrandBlock(
    logoHeight: androidx.compose.ui.unit.Dp,
    compact: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TppLogo(
            height = logoHeight,
            maxWidth = if (compact) 140.dp else 180.dp
        )
        if (!compact) {
            Column {
                Text(
                    text = "TPP",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Inventory & sales ledger",
                    color = Muted,
                    fontSize = 12.5.sp,
                    letterSpacing = 0.3.sp,
                    fontFamily = InterFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyState(hasPhones: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Line, RoundedCornerShape(16.dp))
            .padding(vertical = 50.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyOrb()
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = if (hasPhones) "Nothing matches" else "No phones yet",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (hasPhones) {
                "Try a different search or filter."
            } else {
                "Add your first phone to start tracking sales and profit."
            },
            color = Muted,
            fontFamily = InterFamily,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
