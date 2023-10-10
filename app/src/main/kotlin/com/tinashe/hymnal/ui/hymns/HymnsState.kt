package com.tinashe.hymnal.ui.hymns

import hymnal.content.model.Hymn

sealed interface HymnsState {
    data class Success(
        val title: String,
        val hymns: List<Hymn>,
    ): HymnsState

    data class SearchResults(
        val query: String,
        val results: List<Hymn>,
    ): HymnsState

    data object Error : HymnsState
    data object Loading : HymnsState
}