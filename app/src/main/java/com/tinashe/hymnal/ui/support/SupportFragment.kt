package com.tinashe.hymnal.ui.support

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentSupportBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.extensions.view.inflateView
import com.tinashe.hymnal.extensions.view.viewBinding
import com.tinashe.hymnal.utils.Helper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportFragment : Fragment(R.layout.fragment_support) {

    private val viewModel: SupportViewModel by viewModels()
    private val binding by viewBinding(FragmentSupportBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvPolicy.setOnClickListener {
                Helper.launchWebUrl(requireContext(), getString(R.string.app_privacy_policy))
            }
            tvTerms.setOnClickListener {
                Helper.launchWebUrl(requireContext(), getString(R.string.app_terms))
            }
        }

        viewModel.purchaseResultLiveData.observeNonNull(viewLifecycleOwner) { pair ->
            binding.apply {
                chipGroupInApp.clearCheck()
                chipGroupSubs.clearCheck()
            }
            val message = pair.second?.let {
                getString(it)
            } ?: return@observeNonNull
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
        viewModel.deepLinkLiveData.observeNonNull(viewLifecycleOwner) {
            binding.apply {
                chipGroupInApp.clearCheck()
                chipGroupSubs.clearCheck()
            }
            Helper.launchWebUrl(requireContext(), it)
        }
        viewModel.inAppProductsLiveData.observeNonNull(viewLifecycleOwner) { products ->
            if (products.isEmpty()) {
                binding.tvOneTimeDonation.setText(R.string.error_un_available)
                return@observeNonNull
            } else {
                binding.tvOneTimeDonation.setText(R.string.one_tine_donation)
            }
            binding.chipGroupInApp.apply {
                removeAllViews()
                products.forEach { product ->
                    val chip: Chip = inflateView(
                        R.layout.chip_amount,
                        this,
                        false
                    ) as Chip
                    chip.apply {
                        id = product.sku.hashCode()
                        text = product.price
                    }
                    addView(chip)
                }

                setOnCheckedChangeListener { _: ChipGroup?, checkedId: Int ->
                    if (checkedId != -1) {
                        products.find { it.sku.hashCode() == checkedId }?.let {
                            viewModel.initiatePurchase(it, requireActivity())
                        }
                    }
                }
            }
        }
        viewModel.subscriptionsLiveData.observeNonNull(viewLifecycleOwner) { subs ->
            if (subs.isEmpty()) {
                binding.tvMonthlyDonation.setText(R.string.blank)
                return@observeNonNull
            } else {
                binding.tvMonthlyDonation.setText(R.string.monthly_donations)
            }
            binding.chipGroupSubs.apply {
                removeAllViews()
                subs.forEach { product ->
                    val chip: Chip = inflateView(
                        R.layout.chip_amount,
                        this,
                        false
                    ) as Chip
                    chip.apply {
                        id = product.sku.hashCode()
                        text = getString(R.string.subscription_period, product.price)
                    }
                    addView(chip)
                }

                setOnCheckedChangeListener { _: ChipGroup?, checkedId: Int ->
                    if (checkedId != -1) {
                        subs.find { it.sku.hashCode() == checkedId }?.let {
                            viewModel.initiatePurchase(it, requireActivity())
                        }
                    }
                }
            }
        }

        viewModel.loadData(requireActivity())
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.support_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_account_settings -> {
                Helper.launchWebUrl(requireContext(), getString(R.string.subscriptions_url))
                true
            }
            R.id.action_help -> {
                Helper.sendFeedback(requireActivity())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.viewResumed()
    }
}
