package com.tinashe.hymnal.ui.hymns.sing.present

import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import hymnal.content.model.Hymn

class PresentPagerAdapter(
    fragment: FragmentActivity,
    hymn: Hymn
) : FragmentStateAdapter(fragment) {

    private val hymnVerses: List<String>

    init {
        val verses = ((hymn.editedContent ?: hymn.content).parseAsHtml()).split("\n\n")
        val chorus = verses.find { it.contains("CHORUS", true) }
        val parsed = arrayListOf<String>()
        verses.forEachIndexed { index, verse ->
            if (verse.isEmpty()) return@forEachIndexed
            parsed.add(verse)
            if (index > 2 && !chorus.isNullOrEmpty() && verse != chorus) {
                parsed.add(chorus)
            }
        }
        hymnVerses = parsed
    }

    override fun getItemCount(): Int = hymnVerses.size

    override fun createFragment(position: Int): Fragment =
        HymnVerseFragment.newInstance(hymnVerses[position])
}
