package com.tinashe.hymnal.ui.hymns.hymnals.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.HymnalListItemBinding
import com.tinashe.hymnal.extensions.view.inflate
import com.tinashe.hymnal.ui.hymns.hymnals.HymnalModel

class HymnalViewHolder(
    private val binding: HymnalListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: HymnalModel) {
        binding.apply {
            icon.background.tint(COLORS[absoluteAdapterPosition % COLORS.size])
            iconCheck.isVisible = model.selected
            hymnalTitle.text = model.title
            hymnalLanguage.text = model.language
        }
    }

    private fun Drawable.tint(colorHex: String) {
        DrawableCompat.wrap(this)
        DrawableCompat.setTint(this, Color.parseColor(colorHex))
        DrawableCompat.unwrap<Drawable>(this)
    }

    companion object {
        /**
         * Adventist Identity colors
         *
         * https://identity.adventist.org/global-elements/color/
         */
        private val COLORS =
            arrayListOf("#4b207f", "#5e3929", "#7f264a", "#2f557f", "#e36520", "#448d21", "#3e8391")

        fun create(parent: ViewGroup): HymnalViewHolder = HymnalViewHolder(
            HymnalListItemBinding.bind(parent.inflate(R.layout.hymnal_list_item))
        )
    }
}
