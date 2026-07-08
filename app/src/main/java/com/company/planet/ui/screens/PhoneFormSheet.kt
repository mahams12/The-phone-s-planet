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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.data.Phone
import com.company.planet.data.computePhone
import com.company.planet.ui.components.TppGhostButton
import com.company.planet.ui.components.TppPrimaryButton
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.BgElevated
import com.company.planet.ui.theme.BgPanel
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.JetBrainsMonoFamily
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.Muted
import com.company.planet.ui.theme.SpaceGroteskFamily
import com.company.planet.ui.theme.TextPrimary
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.company.planet.ui.util.formatMoney

private val knownCompanies = listOf("Apple", "Samsung", "Oppo", "Vivo", "OnePlus", "Other")
private val phoneTypes = listOf("JV", "Factory Unlock", "Bypass")
private val ptaOptions = listOf("Non PTA", "Approved")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PhoneFormSheet(
    editingPhone: Phone?,
    onDismiss: () -> Unit,
    onSave: (Phone) -> Unit,
    fullScreen: Boolean = false
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var company by remember(editingPhone) {
        mutableStateOf(
            when {
                editingPhone == null -> "Apple"
                editingPhone.company in knownCompanies.dropLast(1) -> editingPhone.company
                else -> "Other"
            }
        )
    }
    var customCompany by remember(editingPhone) {
        mutableStateOf(
            if (editingPhone != null && editingPhone.company !in knownCompanies.dropLast(1)) {
                editingPhone.company
            } else ""
        )
    }
    var model by remember(editingPhone) { mutableStateOf(editingPhone?.model.orEmpty()) }
    var storage by remember(editingPhone) { mutableStateOf(editingPhone?.storage.orEmpty()) }
    var colour by remember(editingPhone) { mutableStateOf(editingPhone?.colour.orEmpty()) }
    var type by remember(editingPhone) { mutableStateOf(editingPhone?.type ?: "JV") }
    var pta by remember(editingPhone) { mutableStateOf(editingPhone?.pta ?: "Non PTA") }
    var detail by remember(editingPhone) { mutableStateOf(editingPhone?.detail.orEmpty()) }
    var imei1 by remember(editingPhone) { mutableStateOf(editingPhone?.imei1.orEmpty()) }
    var imei2 by remember(editingPhone) { mutableStateOf(editingPhone?.imei2.orEmpty()) }
    var purchase by remember(editingPhone) {
        mutableStateOf(editingPhone?.purchasePrice?.takeIf { it > 0 }?.toLong()?.toString().orEmpty())
    }
    var sale by remember(editingPhone) {
        mutableStateOf(editingPhone?.salePrice?.takeIf { it > 0 }?.toLong()?.toString().orEmpty())
    }
    var told by remember(editingPhone) {
        mutableStateOf(editingPhone?.toldPrice?.takeIf { it > 0 }?.toLong()?.toString().orEmpty())
    }
    var withdrawn by remember(editingPhone) {
        mutableStateOf(editingPhone?.withdrawn?.takeIf { it > 0 }?.toLong()?.toString().orEmpty())
    }
    var sold by remember(editingPhone) { mutableStateOf(editingPhone?.sold ?: false) }

    val previewPhone = Phone(
        purchasePrice = purchase.toDoubleOrNull() ?: 0.0,
        salePrice = sale.toDoubleOrNull() ?: 0.0,
        toldPrice = told.toDoubleOrNull() ?: 0.0,
        withdrawn = withdrawn.toDoubleOrNull() ?: 0.0,
        sold = sold
    )
    val computed = computePhone(previewPhone)

    val formContent: @Composable () -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (fullScreen) Modifier.fillMaxHeight() else Modifier)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = if (fullScreen) 16.dp else 24.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (editingPhone == null) "Add Phone" else "Edit Phone",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Muted)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val twoColumns = maxWidth >= 500.dp
                if (twoColumns) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        maxItemsInEachRow = 2
                    ) {
                        FormFields(
                            modifier = Modifier.fillMaxWidth(0.48f),
                            company = company,
                            onCompanyChange = { company = it },
                            customCompany = customCompany,
                            onCustomCompanyChange = { customCompany = it },
                            model = model,
                            onModelChange = { model = it },
                            storage = storage,
                            onStorageChange = { storage = it },
                            colour = colour,
                            onColourChange = { colour = it },
                            type = type,
                            onTypeChange = { type = it },
                            pta = pta,
                            onPtaChange = { pta = it },
                            detail = detail,
                            onDetailChange = { detail = it },
                            imei1 = imei1,
                            onImei1Change = { imei1 = it },
                            imei2 = imei2,
                            onImei2Change = { imei2 = it },
                            purchase = purchase,
                            onPurchaseChange = { purchase = it },
                            sale = sale,
                            onSaleChange = { sale = it },
                            told = told,
                            onToldChange = { told = it },
                            withdrawn = withdrawn,
                            onWithdrawnChange = { withdrawn = it },
                            sold = sold,
                            onSoldChange = { sold = it }
                        )
                    }
                } else {
                    FormFields(
                        modifier = Modifier.fillMaxWidth(),
                        company = company,
                        onCompanyChange = { company = it },
                        customCompany = customCompany,
                        onCustomCompanyChange = { customCompany = it },
                        model = model,
                        onModelChange = { model = it },
                        storage = storage,
                        onStorageChange = { storage = it },
                        colour = colour,
                        onColourChange = { colour = it },
                        type = type,
                        onTypeChange = { type = it },
                        pta = pta,
                        onPtaChange = { pta = it },
                        detail = detail,
                        onDetailChange = { detail = it },
                        imei1 = imei1,
                        onImei1Change = { imei1 = it },
                        imei2 = imei2,
                        onImei2Change = { imei2 = it },
                        purchase = purchase,
                        onPurchaseChange = { purchase = it },
                        sale = sale,
                        onSaleChange = { sale = it },
                        told = told,
                        onToldChange = { told = it },
                        withdrawn = withdrawn,
                        onWithdrawnChange = { withdrawn = it },
                        sold = sold,
                        onSoldChange = { sold = it }
                    )
                }
            }

            CalcPreview(computed = computed)

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TppGhostButton(text = "Cancel", onClick = onDismiss)
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                TppPrimaryButton(
                    text = "Save Phone",
                    onClick = {
                        if (model.isBlank()) return@TppPrimaryButton
                        val resolvedCompany = if (company == "Other") {
                            customCompany.trim().ifBlank { "Other" }
                        } else company
                        onSave(
                            Phone(
                                id = editingPhone?.id.orEmpty(),
                                company = resolvedCompany,
                                model = model.trim(),
                                detail = detail.trim(),
                                imei1 = imei1.trim(),
                                imei2 = imei2.trim(),
                                type = type,
                                pta = pta,
                                storage = storage.trim(),
                                colour = colour.trim(),
                                purchasePrice = purchase.toDoubleOrNull() ?: 0.0,
                                salePrice = sale.toDoubleOrNull() ?: 0.0,
                                toldPrice = told.toDoubleOrNull() ?: 0.0,
                                withdrawn = withdrawn.toDoubleOrNull() ?: 0.0,
                                sold = sold,
                                createdAt = editingPhone?.createdAt ?: System.currentTimeMillis()
                            )
                        )
                    }
                )
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
                formContent()
            }
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = BgPanel,
            dragHandle = null
        ) {
            formContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormFields(
    modifier: Modifier,
    company: String,
    onCompanyChange: (String) -> Unit,
    customCompany: String,
    onCustomCompanyChange: (String) -> Unit,
    model: String,
    onModelChange: (String) -> Unit,
    storage: String,
    onStorageChange: (String) -> Unit,
    colour: String,
    onColourChange: (String) -> Unit,
    type: String,
    onTypeChange: (String) -> Unit,
    pta: String,
    onPtaChange: (String) -> Unit,
    detail: String,
    onDetailChange: (String) -> Unit,
    imei1: String,
    onImei1Change: (String) -> Unit,
    imei2: String,
    onImei2Change: (String) -> Unit,
    purchase: String,
    onPurchaseChange: (String) -> Unit,
    sale: String,
    onSaleChange: (String) -> Unit,
    told: String,
    onToldChange: (String) -> Unit,
    withdrawn: String,
    onWithdrawnChange: (String) -> Unit,
    sold: Boolean,
    onSoldChange: (Boolean) -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DropdownField("Phone Company", company, knownCompanies, onCompanyChange)
        if (company == "Other") {
            TppField("Custom Brand Name", customCompany, onCustomCompanyChange, "e.g. Xiaomi")
        }
        TppField("Model", model, onModelChange, "e.g. iPhone 13 Pro")
        TppField("Storage", storage, onStorageChange, "e.g. 128GB")
        TppField("Colour", colour, onColourChange, "e.g. Sierra Blue")
        DropdownField("Type", type, phoneTypes, onTypeChange)
        DropdownField("PTA Status", pta, ptaOptions, onPtaChange)
        TppField("Detail", detail, onDetailChange, "Condition notes, box/accessories, warranty…", multiline = true)
        TppField("IMEI 1", imei1, onImei1Change, "15-digit IMEI", mono = true)
        TppField("IMEI 2", imei2, onImei2Change, "Optional dual-SIM IMEI", mono = true)
        TppField("Purchase Price (Rs)", purchase, onPurchaseChange, "0", mono = true, numeric = true)
        TppField("Sale Price (Rs)", sale, onSaleChange, "0", mono = true, numeric = true)
        TppField("Told to Asif — reported sale (Rs)", told, onToldChange, "0", mono = true, numeric = true)
        TppField("Money Withdrawn (Rs)", withdrawn, onWithdrawnChange, "0", mono = true, numeric = true)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgElevated, RoundedCornerShape(10.dp))
                .border(1.dp, Line, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mark as Sold", color = Muted, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Switch(
                    checked = sold,
                    onCheckedChange = onSoldChange,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = Accent,
                        uncheckedTrackColor = Line
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = BgElevated
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TppField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    multiline: Boolean = false,
    mono: Boolean = false,
    numeric: Boolean = false
) {
    Column {
        Text(label, color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Muted) },
            singleLine = !multiline,
            minLines = if (multiline) 3 else 1,
            shape = RoundedCornerShape(10.dp),
            colors = fieldColors(),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = if (mono) JetBrainsMonoFamily else InterFamily,
                fontSize = 13.5.sp
            )
        )
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Accent,
    unfocusedBorderColor = Line,
    focusedContainerColor = BgElevated,
    unfocusedContainerColor = BgElevated,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = Accent
)

@Composable
private fun CalcPreview(computed: com.company.planet.data.PhoneComputed) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .background(BgElevated, RoundedCornerShape(12.dp))
            .border(1.dp, Line, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CalcRow("Total Money", formatMoney(computed.totalMoney))
        CalcRow("Total Profit", formatMoney(computed.totalProfit))
        CalcRow("Remaining Amount", formatMoney(computed.remaining))
        CalcRow("Asif's Profit", formatMoney(computed.asifProfit))
        CalcRow("Shozab's Profit", formatMoney(computed.shozabProfit))
        CalcRow("Money Withdrawn", formatMoney(computed.withdrawn))
    }
}

@Composable
private fun CalcRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted, fontSize = 12.5.sp)
        Text(value, fontFamily = JetBrainsMonoFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
    }
}
