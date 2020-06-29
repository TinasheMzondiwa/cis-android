package com.tinashe.hymnal.ui.hymns.sing.hymn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.databinding.FragmentHymnBinding
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HymnFragment : Fragment() {

    @Inject
    lateinit var prefs: HymnalPrefs

    private var binding: FragmentHymnBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentHymnBinding.inflate(inflater, container, false).also {
            binding = it
            binding?.hymnText?.apply {
                textSize = prefs.getFontSize()
                typeface = ResourcesCompat.getFont(context, prefs.getFontRes())
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hymn: Hymn = arguments?.get(ARG_HYMN) as? Hymn ?: return
        binding?.hymnText?.text = if (hymn.editedContent.isNullOrEmpty()) {
            hymn.content.parseAsHtml()
        } else {
            hymn.editedContent?.parseAsHtml()
        }
    }

    companion object {
        private const val ARG_HYMN = "arg:hymn"

        fun newInstance(hymn: Hymn): HymnFragment = HymnFragment().apply {
            arguments = bundleOf(ARG_HYMN to hymn)
        }
    }
}
