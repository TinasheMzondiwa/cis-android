package com.tinashe.hymnal.data.model.constants

enum class Hymnals(val key: String) {

    ENGLISH("english"),
    TSWANA("tswana"),
    SOTHO("sotho"),
    CHICHEWA("chichewa"),
    TONGA("tonga"),
    SHONA("shona"),
    VENDA("venda"),
    SWAHILI("swahili"),
    NDEBELE("ndebele"),
    XHOSA("xhosa");

    companion object {
        private val map = values().associateBy(Hymnals::key)

        fun fromString(key: String) = map[key]
    }
}
