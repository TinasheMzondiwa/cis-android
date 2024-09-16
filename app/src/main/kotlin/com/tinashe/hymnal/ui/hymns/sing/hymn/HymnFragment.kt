package com.tinashe.hymnal.ui.hymns.sing.hymn

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentHymnBinding
import dagger.hilt.android.AndroidEntryPoint
import hymnal.content.model.Hymn
import hymnal.prefs.HymnalPrefs
import io.noties.markwon.Markwon
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HymnFragment : Fragment(R.layout.fragment_hymn) {

    @Inject
    lateinit var prefs: HymnalPrefs

    private lateinit var binding: FragmentHymnBinding

    private val markworn: Markwon by lazy {
        Markwon.create(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHymnBinding.bind(view)

        binding.hymnText.apply {
            try {
                textSize = prefs.getFontSize()
                typeface = ResourcesCompat.getFont(context, prefs.getFontRes())
            } catch (ex: Exception) {
                // Some devices failing to resolve resources files
                Timber.e(ex)
            }
        }

        val hymn = BundleCompat.getParcelable(
            requireArguments(),
            ARG_HYMN, 
            Hymn::class.java,
        ) ?: return

        hymn.html?.let { html ->
            binding.hymnText.text = if (hymn.editedContent.isNullOrEmpty()) {
                html.parseAsHtml()
            } else {
                hymn.editedContent?.parseAsHtml()
            }
        }

        hymn.markdown?.let { markdown ->
            markworn.setMarkdown(binding.hymnText, markdown)
        }
    }

    companion object {
        private const val ARG_HYMN = "arg:hymn"

        fun newInstance(hymn: Hymn): HymnFragment = HymnFragment().apply {
            arguments = bundleOf(ARG_HYMN to hymn)
        }
    }
}
