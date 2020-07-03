package com.tinashe.hymnal.ui.info

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.BuildConfig
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentInfoBinding
import com.tinashe.hymnal.utils.Helper
import timber.log.Timber

class InfoFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentInfoBinding.inflate(inflater, container, false).also {
            it.apply {
                buildInfo.setOnClickListener { launchWebUrl(getString(R.string.app_link)) }
                tvBuildVersion.text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                tvShareApp.setOnClickListener {
                    ShareCompat.IntentBuilder.from(requireActivity())
                        .setType("text/plain")
                        .setText(getString(R.string.app_share_message, getString(R.string.app_link)))
                        .startChooser()
                }
                tvFeedback.setOnClickListener {
                    Helper.sendFeedback(requireActivity())
                }
                tvViewSource.setOnClickListener { launchWebUrl(getString(R.string.app_source)) }
                tvTwitter.setOnClickListener { launchWebUrl(getString(R.string.app_twitter)) }
            }
        }.root
    }

    private fun launchWebUrl(url: String) {
        val builder = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .enableUrlBarHiding()
            .setStartAnimations(requireContext(), R.anim.slide_up, android.R.anim.fade_out)
            .setExitAnimations(requireContext(), android.R.anim.fade_in, R.anim.slide_down)
        try {
            val intent = builder.build()
            intent.launchUrl(requireContext(), Uri.parse(url))
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }
}
