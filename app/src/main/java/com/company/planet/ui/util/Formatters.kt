package com.company.planet.ui.util

import java.text.NumberFormat
import java.util.Locale

private val pkLocale = Locale.forLanguageTag("en-PK")
private val moneyFormat = NumberFormat.getNumberInstance(pkLocale).apply {
    maximumFractionDigits = 0
}

fun formatMoney(amount: Double): String = "Rs ${moneyFormat.format(amount)}"

fun formatMoneyOrDash(amount: Double, sold: Boolean): String =
    if (sold) formatMoney(amount) else "—"
