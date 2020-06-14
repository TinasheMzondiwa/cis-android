package com.tinashe.hymnal.data.model

import androidx.room.Relation

data class HymnalHymns(
    val code: String,
    val title: String,
    val language: String,
    @Relation(parentColumn = "code", entityColumn = "book", entity = Hymn::class)
    val hymns: List<Hymn>
)