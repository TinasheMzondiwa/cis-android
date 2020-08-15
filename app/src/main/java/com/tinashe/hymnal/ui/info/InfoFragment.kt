package com.tinashe.hymnal.ui.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.BuildConfig
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentInfoBinding
import com.tinashe.hymnal.utils.Helper

class InfoFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentInfoBinding.inflate(inflater, container, false).also {
            it.apply {
                buildInfo.setOnClickListener {
                    Helper.launchWebUrl(requireContext(), getString(R.string.app_link))
                }
                tvBuildVersion.text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                tvShareApp.setOnClickListener {
                    ShareCompat.IntentBuilder.from(requireActivity())
                        .setType("text/plain")
                        .setText(
                            getString(
                                R.string.app_share_message,
                                getString(R.string.app_link)
                            )
                        )
                        .startChooser()
                }
                tvFeedback.setOnClickListener {
                    Helper.sendFeedback(requireActivity())
                }
                tvViewSource.setOnClickListener {
                    Helper.launchWebUrl(requireContext(), getString(R.string.app_source))
                }
                tvTwitter.setOnClickListener {
                    Helper.launchWebUrl(requireContext(), getString(R.string.app_twitter))
                }
            }
        }.root
    }
}
