package com.tinashe.hymnal.ui.hymns.hymnals

import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil

@Keep
data class HymnalModel(
    val code: String,
    val title: String,
    val language: String,
    var selected: Boolean
) {

    fun toHymnal() = hymnal.content.model.Hymnal(code, title, language)

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<HymnalModel>() {
            override fun areItemsTheSame(oldItem: HymnalModel, newItem: HymnalModel): Boolean {
                return oldItem.code == newItem.code
            }

            override fun areContentsTheSame(oldItem: HymnalModel, newItem: HymnalModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
