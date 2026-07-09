package com.company.planet.data

data class Phone(
    val id: String = "",
    val company: String = "",
    val model: String = "",
    val detail: String = "",
    val imei1: String = "",
    val imei2: String = "",
    val type: String = "JV",
    val pta: String = "Non PTA",
    val storage: String = "",
    val colour: String = "",
    val purchasePrice: Double = 0.0,
    val salePrice: Double = 0.0,
    val toldPrice: Double = 0.0,
    val withdrawn: Double = 0.0,
    val sold: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class PhoneComputed(
    val sold: Boolean,
    val purchase: Double,
    val sale: Double,
    val told: Double,
    val totalMoney: Double,
    val totalProfit: Double,
    val asifProfit: Double,
    val shozabProfit: Double
)

data class PhoneTotals(
    val count: Int = 0,
    val inStock: Int = 0,
    val sold: Int = 0,
    val totalMoney: Double = 0.0,
    val totalProfit: Double = 0.0,
    val asifProfit: Double = 0.0,
    val shozabProfit: Double = 0.0
)

fun computePhone(phone: Phone): PhoneComputed {
    val sold = phone.sold
    val purchase = phone.purchasePrice
    val sale = phone.salePrice
    val told = phone.toldPrice
    val totalMoney = if (sold) sale else 0.0
    val totalProfit = if (sold) maxOf(0.0, sale - purchase) else 0.0

    val asifProfit = when {
        !sold || told <= 0 -> 0.0
        else -> maxOf(0.0, sale - told)
    }
    val shozabProfit = when {
        !sold || told <= 0 -> 0.0
        else -> maxOf(0.0, told - purchase)
    }

    return PhoneComputed(
        sold = sold,
        purchase = purchase,
        sale = sale,
        told = told,
        totalMoney = totalMoney,
        totalProfit = totalProfit,
        asifProfit = asifProfit,
        shozabProfit = shozabProfit
    )
}

fun computeAll(phones: List<Phone>): PhoneTotals {
    var inStock = 0
    var sold = 0
    var totalMoney = 0.0
    var totalProfit = 0.0
    var asifProfit = 0.0
    var shozabProfit = 0.0

    phones.forEach { phone ->
        val c = computePhone(phone)
        if (c.sold) sold++ else inStock++
        totalMoney += c.totalMoney
        totalProfit += c.totalProfit
        asifProfit += c.asifProfit
        shozabProfit += c.shozabProfit
    }

    return PhoneTotals(
        count = phones.size,
        inStock = inStock,
        sold = sold,
        totalMoney = totalMoney,
        totalProfit = totalProfit,
        asifProfit = asifProfit,
        shozabProfit = shozabProfit
    )
}

fun brandColor(company: String): Long {
    return when (company.trim().lowercase()) {
        "apple" -> 0xFFD6D6D6
        "samsung" -> 0xFF4C8DFF
        "oppo" -> 0xFF34C38F
        "vivo" -> 0xFFA78BFA
        "oneplus" -> 0xFFFB7185
        else -> 0xFFF5B14C
    }
}
