package com.company.planet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.company.planet.ui.components.CalcPreviewPanel
import com.company.planet.ui.components.PhoneFormFields
import com.company.planet.ui.components.TppGhostButton
import com.company.planet.ui.components.TppPrimaryButton
import com.company.planet.ui.theme.BgPanel
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.Muted
import com.company.planet.ui.theme.SpaceGroteskFamily
import com.company.planet.ui.theme.TextPrimary
import com.company.planet.ui.theme.Warn
import com.company.planet.ui.theme.WarnDim
import com.company.planet.ui.components.tppCard3D
import com.company.planet.ui.components.tppEnterAnimation
import com.company.planet.ui.components.tppDropIn
import com.company.planet.viewmodel.PhoneViewModel

@Composable
fun AddPhoneScreen(
    editingPhone: Phone?,
    viewModel: PhoneViewModel,
    modifier: Modifier = Modifier
) {
    var clearTick by remember { mutableIntStateOf(0) }
    val resetKey = "${editingPhone?.id ?: "new"}-$clearTick"
    val isEdit = editingPhone != null

    var company by remember(resetKey) {
        mutableStateOf(editingPhone?.company.orEmpty())
    }
    var model by remember(resetKey) { mutableStateOf(editingPhone?.model.orEmpty()) }
    var storage by remember(resetKey) { mutableStateOf(editingPhone?.storage.orEmpty()) }
    var colour by remember(resetKey) { mutableStateOf(editingPhone?.colour.orEmpty()) }
    var type by remember(resetKey) { mutableStateOf(editingPhone?.type ?: "JV") }
    var pta by remember(resetKey) { mutableStateOf(editingPhone?.pta ?: "Non PTA") }
    var detail by remember(resetKey) { mutableStateOf(editingPhone?.detail.orEmpty()) }
    var imei1 by remember(resetKey) { mutableStateOf(editingPhone?.imei1.orEmpty()) }
    var imei2 by remember(resetKey) { mutableStateOf(editingPhone?.imei2.orEmpty()) }
    var purchase by remember(resetKey) {
        mutableStateOf(editingPhone?.purchasePrice?.takeIf { it > 0 }?.toLong()?.toString().orEmpty())
    }
    var sale by remember(resetKey) {
        mutableStateOf(editingPhone?.salePrice?.takeIf { it > 0 }?.toLong()?.toString().orEmpty())
    }
    var told by remember(resetKey) {
        mutableStateOf(editingPhone?.toldPrice?.takeIf { it > 0 }?.toLong()?.toString().orEmpty())
    }
    var sold by remember(resetKey) { mutableStateOf(editingPhone?.sold ?: false) }

    val computed = computePhone(
        Phone(
            purchasePrice = purchase.toDoubleOrNull() ?: 0.0,
            salePrice = sale.toDoubleOrNull() ?: 0.0,
            toldPrice = told.toDoubleOrNull() ?: 0.0,
            sold = sold
        )
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .imePadding()
    ) {
        Column(Modifier.padding(bottom = 18.dp).tppDropIn()) {
            Text(
                if (isEdit) "Edit Phone" else "Add Phone",
                color = TextPrimary,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                if (isEdit) "Updating \"${editingPhone?.model ?: "this phone"}\"." else "Log a new device into the ledger.",
                color = Muted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (isEdit) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(WarnDim, RoundedCornerShape(10.dp))
                    .border(1.dp, Warn.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Editing an existing phone.", color = Warn, fontSize = 13.sp)
                TextButton(onClick = {
                    viewModel.resetForm()
                    clearTick++
                }) {
                    Text("Cancel edit", color = Warn, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .tppEnterAnimation(1)
                .tppCard3D()
                .background(BgPanel, RoundedCornerShape(16.dp))
                .border(1.dp, Line, RoundedCornerShape(16.dp))
                .padding(22.dp)
        ) {
            PhoneFormFields(
                company, { company = it },
                model, { model = it }, storage, { storage = it }, colour, { colour = it },
                type, { type = it }, pta, { pta = it }, detail, { detail = it },
                imei1, { imei1 = it }, imei2, { imei2 = it },
                purchase, { purchase = it }, sale, { sale = it },
                told, { told = it },
                sold, { sold = it }
            )

            CalcPreviewPanel(computed, Modifier.padding(top = 14.dp))

            Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.End) {
                TppGhostButton(text = "Clear", onClick = {
                    viewModel.resetForm()
                    clearTick++
                })
                Spacer(Modifier.padding(horizontal = 5.dp))
                TppPrimaryButton(
                    text = if (isEdit) "Update Phone" else "Save Phone",
                    onClick = {
                        if (model.isBlank()) return@TppPrimaryButton
                        val resolvedCompany = company.trim().ifBlank { "Other" }
                        viewModel.savePhone(
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
                                withdrawn = 0.0,
                                sold = sold,
                                createdAt = editingPhone?.createdAt ?: System.currentTimeMillis()
                            )
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
