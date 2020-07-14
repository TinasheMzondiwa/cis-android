package com.tinashe.hymnal.data.model.constants

enum class HymnalSort {
    TITLE,
    LANGUAGE,
    OFFLINE;

    companion object {
        private val map = values().associateBy(HymnalSort::name)

        fun fromString(name: String) = map[name]
    }
}
