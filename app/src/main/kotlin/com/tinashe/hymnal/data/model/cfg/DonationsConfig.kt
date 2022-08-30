package com.tinashe.hymnal.data.model.cfg

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DonationsConfig(val enabled: Boolean = false)
