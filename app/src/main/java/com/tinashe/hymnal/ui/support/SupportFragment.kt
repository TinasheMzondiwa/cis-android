package com.tinashe.hymnal.ui.support

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tinashe.hymnal.BuildConfig
import com.tinashe.hymnal.R
import com.tinashe.hymnal.extensions.arch.observeNonNull
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SupportFragment : Fragment(R.layout.fragment_support) {

    private val viewModel: SupportViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.inAppProductsLiveData.observeNonNull(viewLifecycleOwner) {
            Timber.d("IN-APP: ${it.size}")
        }
        viewModel.subscriptionsLiveData.observeNonNull(viewLifecycleOwner) {
            Timber.d("SUBS: ${it.size}")
        }

        viewModel.loadData(requireActivity())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.support_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_help -> {
                ShareCompat.IntentBuilder.from(requireActivity())
                    .setType("message/rfc822")
                    .addEmailTo(getString(R.string.app_email))
                    .setSubject("${getString(R.string.app_full_name)} v${BuildConfig.VERSION_NAME}")
                    .setChooserTitle(R.string.send_with)
                    .startChooser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
