package com.tinashe.hymnal.ui.support

import androidx.annotation.Keep

@Keep
data class DonateProduct(
    val sku: String,
    val type: String,
    val price: String
)
