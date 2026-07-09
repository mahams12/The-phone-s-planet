package com.company.planet.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.data.PhoneComputed
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.AccentOn
import com.company.planet.ui.theme.BgElevated
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.JetBrainsMonoFamily
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.Muted
import com.company.planet.ui.theme.Muted2
import com.company.planet.ui.theme.TextPrimary
import com.company.planet.ui.theme.TextSecondary
import com.company.planet.ui.util.formatMoney
import kotlinx.coroutines.launch

val knownCompanies = listOf("Apple", "Samsung", "Oppo", "Vivo", "OnePlus")
val phoneTypes = listOf("JV", "Factory Unlock", "Bypass")
val ptaOptions = listOf("Non PTA", "Approved")

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Modifier.scrollOnFocus(): Modifier {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    return this
        .bringIntoViewRequester(bringIntoViewRequester)
        .onFocusChanged { focus ->
            if (focus.isFocused) {
                scope.launch { bringIntoViewRequester.bringIntoView() }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneFormFields(
    company: String,
    onCompanyChange: (String) -> Unit,
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
    sold: Boolean,
    onSoldChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        FormCompanyField("Phone Company", company, knownCompanies, onCompanyChange)
        FormTextField("Model", model, onModelChange, "e.g. Galaxy S24")
        FormTextField("Storage", storage, onStorageChange, "e.g. 128GB")
        FormTextField("Colour", colour, onColourChange, "e.g. Sierra Blue")
        FormDropdown("Type", type, phoneTypes, onTypeChange)
        FormDropdown("PTA Status", pta, ptaOptions, onPtaChange)
        FormTextField("Detail", detail, onDetailChange, "Condition notes, box/accessories, warranty…", multiline = true)
        FormImeiField("IMEI 1", imei1, onImei1Change, "15-digit IMEI")
        FormImeiField("IMEI 2", imei2, onImei2Change, "Optional dual-SIM IMEI")
        FormAmountField("Purchase Price (Rs)", purchase, onPurchaseChange)
        FormAmountField("Sale Price (Rs)", sale, onSaleChange)
        FormAmountField("Reported sale (Rs.)", told, onToldChange)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgElevated, RoundedCornerShape(10.dp))
                .border(1.dp, Line, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mark as Sold", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Switch(
                checked = sold,
                onCheckedChange = onSoldChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AccentOn,
                    checkedTrackColor = Accent,
                    uncheckedThumbColor = TextPrimary,
                    uncheckedTrackColor = Muted2,
                    uncheckedBorderColor = TextSecondary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormCompanyField(
    label: String,
    value: String,
    suggestions: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val filtered = remember(value, suggestions) {
        if (value.isBlank()) suggestions
        else {
            val matches = suggestions.filter { it.contains(value, ignoreCase = true) }
            if (matches.isNotEmpty()) matches else suggestions
        }
    }

    Column {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {
                    onValueChange(it)
                    if (it.isNotBlank()) expanded = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .scrollOnFocus(),
                placeholder = { Text("Type or pick a brand, e.g. Xiaomi", color = Muted) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                shape = RoundedCornerShape(10.dp),
                colors = formFieldColors(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = InterFamily,
                    fontSize = 13.5.sp,
                    color = TextPrimary
                ),
                singleLine = true
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = BgElevated
            ) {
                filtered.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = TextPrimary) },
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
fun FormImeiField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(filterImeiInput(it)) },
            modifier = Modifier.fillMaxWidth().scrollOnFocus(),
            placeholder = { Text(placeholder, color = Muted) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = formFieldColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = JetBrainsMonoFamily,
                fontSize = 13.5.sp,
                color = TextPrimary
            ),
            supportingText = if (value.isNotEmpty() && value.length < 15) {
                { Text("${value.length}/15 digits", color = Muted, fontSize = 11.sp) }
            } else null
        )
    }
}

@Composable
fun FormAmountField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "0",
    step: Long = 500
) {
    fun adjustAmount(delta: Long) {
        val current = value.toLongOrNull() ?: 0L
        val next = (current + delta).coerceAtLeast(0L)
        onValueChange(if (next == 0L) "" else next.toString())
    }

    Column {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedIconButton(
                onClick = { adjustAmount(-step) },
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Line)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
            OutlinedTextField(
                value = value,
                onValueChange = { onValueChange(filterAmountInput(it)) },
                modifier = Modifier.weight(1f).scrollOnFocus(),
                placeholder = { Text(placeholder, color = Muted) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = formFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 13.5.sp,
                    color = TextPrimary
                )
            )
            OutlinedIconButton(
                onClick = { adjustAmount(step) },
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Line)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Accent, modifier = Modifier.size(18.dp))
            }
        }
    }
}

private fun filterImeiInput(input: String): String =
    input.filter { it.isDigit() }.take(15)

private fun filterAmountInput(input: String): String =
    input.filter { it.isDigit() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDropdown(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor().scrollOnFocus(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                shape = RoundedCornerShape(10.dp),
                colors = formFieldColors(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = InterFamily,
                    fontSize = 13.5.sp,
                    color = TextPrimary
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = BgElevated) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = TextPrimary) },
                        onClick = {
                        onValueChange(option)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    multiline: Boolean = false,
    mono: Boolean = false
) {
    Column {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().scrollOnFocus(),
            placeholder = { Text(placeholder, color = Muted) },
            singleLine = !multiline,
            minLines = if (multiline) 3 else 1,
            shape = RoundedCornerShape(10.dp),
            colors = formFieldColors(),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = if (mono) JetBrainsMonoFamily else InterFamily,
                fontSize = 13.5.sp,
                color = TextPrimary
            )
        )
    }
}

@Composable
fun CalcPreviewPanel(computed: PhoneComputed, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .tppCard3DSubtle(shape = RoundedCornerShape(12.dp), elevation = 8.dp)
            .background(BgElevated, RoundedCornerShape(12.dp))
            .border(1.dp, Line, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CalcRow("Total Money", formatMoney(computed.totalMoney))
        CalcRow("Total Profit", formatMoney(computed.totalProfit))
        CalcRow("Asif's Profit", formatMoney(computed.asifProfit))
        CalcRow("Shozab's Profit", formatMoney(computed.shozabProfit))
    }
}

@Composable
private fun CalcRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, fontSize = 12.5.sp)
        Text(value, color = TextPrimary, fontFamily = JetBrainsMonoFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
    }
}

@Composable
fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Accent,
    unfocusedBorderColor = Line,
    focusedContainerColor = BgElevated,
    unfocusedContainerColor = BgElevated,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    disabledTextColor = TextSecondary,
    cursorColor = Accent,
    focusedLabelColor = TextSecondary,
    unfocusedLabelColor = TextSecondary,
    focusedPlaceholderColor = Muted,
    unfocusedPlaceholderColor = Muted
)
