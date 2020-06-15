package com.tinashe.hymnal.data.model.json

data class JsonHymnal(
    val key: String,
    val title: String,
    val language: String,
    val hymns: List<JsonHymn>
)
