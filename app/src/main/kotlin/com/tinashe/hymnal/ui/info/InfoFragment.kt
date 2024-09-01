package com.tinashe.hymnal.ui.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.BuildConfig
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentInfoBinding
import hymnal.android.context.launchWebUrl
import hymnal.android.context.sendFeedback
import hymnal.l10n.R as L10nR

class InfoFragment : Fragment(R.layout.fragment_info) {

    private lateinit var binding: FragmentInfoBinding

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoBinding.bind(view)

        binding.apply {
            buildInfo.setOnClickListener {
                requireActivity().launchWebUrl(getString(L10nR.string.app_link))
            }
            tvBuildVersion.text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            tvShareApp.setOnClickListener {
                ShareCompat.IntentBuilder(requireActivity())
                    .setType("text/plain")
                    .setText(
                        getString(
                            L10nR.string.app_share_message,
                            getString(L10nR.string.app_link)
                        )
                    )
                    .startChooser()
            }
            tvFeedback.setOnClickListener {
                requireContext().sendFeedback(BuildConfig.VERSION_NAME)
            }
            tvViewSource.setOnClickListener {
                requireContext().launchWebUrl(getString(L10nR.string.app_source))
            }
            tvTwitter.setOnClickListener {
                requireContext().launchWebUrl(getString(L10nR.string.app_twitter))
            }
            tvReview.setOnClickListener {
                requireContext().launchWebUrl( getString(L10nR.string.app_link))
            }
            tvAppInfo.text = getString(L10nR.string.app_info).parseAsHtml()
        }
    }
}
