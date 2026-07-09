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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.data.Phone
import com.company.planet.ui.components.EmptyOrb
import com.company.planet.ui.components.FilterChipsRow
import com.company.planet.ui.components.PhoneCard
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.BgPanel
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.Muted
import com.company.planet.ui.theme.TextPrimary
import com.company.planet.ui.theme.TextSecondary
import com.company.planet.ui.util.rememberResponsive
import com.company.planet.ui.components.tppCard3D
import com.company.planet.ui.components.tppCard3DSubtle
import com.company.planet.ui.components.tppDropIn
import com.company.planet.ui.components.tppEnterAnimation
import com.company.planet.viewmodel.PhoneUiState
import com.company.planet.viewmodel.PhoneViewModel

@Composable
fun InventoryScreen(
    state: PhoneUiState,
    viewModel: PhoneViewModel,
    onDeleteRequest: (Phone) -> Unit,
    modifier: Modifier = Modifier
) {
    val responsive = rememberResponsive()

    LaunchedEffect(state.highlightId) {
        if (state.highlightId != null) {
            kotlinx.coroutines.delay(1500)
            viewModel.clearHighlight()
        }
    }

    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp)
                .tppDropIn()
        ) {
            Text("Inventory", color = TextPrimary, fontFamily = InterFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text("Every phone you've logged, searchable and filterable.", color = TextSecondary, fontFamily = InterFamily, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }

        OutlinedTextField(
            value = state.search,
            onValueChange = viewModel::setSearch,
            modifier = Modifier
                .fillMaxWidth()
                .tppCard3DSubtle(shape = RoundedCornerShape(10.dp), elevation = 6.dp),
            placeholder = { Text("Search model, brand, IMEI…", color = Muted) },
            leadingIcon = { Icon(Icons.Outlined.Search, null, tint = Muted) },
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

        Spacer(Modifier.height(10.dp))

        FilterChipsRow(
            companies = state.companies,
            companyFilter = state.companyFilter,
            statusFilter = state.statusFilter,
            onCompanySelected = viewModel::setCompanyFilter,
            onStatusSelected = viewModel::setStatusFilter
        )

        Spacer(Modifier.height(18.dp))

        if (state.filteredPhones.isEmpty()) {
            EmptyInventory(hasPhones = state.phones.isNotEmpty())
        } else {
            val rows = state.filteredPhones.chunked(responsive.phoneColumns)
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                rows.forEachIndexed { rowIndex, rowPhones ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        rowPhones.forEachIndexed { colIndex, phone ->
                            val cardIndex = rowIndex * responsive.phoneColumns + colIndex
                            PhoneCard(
                                phone = phone,
                                highlighted = state.highlightId == phone.id,
                                index = cardIndex,
                                onClick = { viewModel.openPreview(phone) },
                                onEdit = { viewModel.openEditForm(phone) },
                                onDelete = { onDeleteRequest(phone) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(responsive.phoneColumns - rowPhones.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyInventory(hasPhones: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .tppEnterAnimation(0)
            .tppCard3D()
            .background(BgPanel, RoundedCornerShape(16.dp))
            .border(1.dp, Line, RoundedCornerShape(16.dp))
            .padding(vertical = 50.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyOrb()
        Spacer(Modifier.height(18.dp))
        Text(
            if (hasPhones) "Nothing matches" else "No phones yet",
            color = TextPrimary,
            fontFamily = InterFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            if (hasPhones) "Try a different search or filter." else "Add your first phone to start tracking sales and profit.",
            color = Muted,
            fontFamily = InterFamily,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
