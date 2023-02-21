package com.tinashe.hymnal.ui.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.BuildConfig
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentInfoBinding
import com.tinashe.hymnal.utils.Helper
import hymnal.l10n.R as L10nR

class InfoFragment : Fragment(R.layout.fragment_info) {

    private lateinit var binding: FragmentInfoBinding

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoBinding.bind(view)

        binding.apply {
            buildInfo.setOnClickListener {
                Helper.launchWebUrl(requireContext(), getString(L10nR.string.app_link))
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
                Helper.sendFeedback(requireActivity())
            }
            tvViewSource.setOnClickListener {
                Helper.launchWebUrl(requireContext(), getString(L10nR.string.app_source))
            }
            tvTwitter.setOnClickListener {
                Helper.launchWebUrl(requireContext(), getString(L10nR.string.app_twitter))
            }
            tvReview.setOnClickListener {
                Helper.launchWebUrl(requireContext(), getString(L10nR.string.app_link))
            }
        }
    }
}
