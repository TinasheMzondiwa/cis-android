package com.tinashe.hymnal.ui.hymns.sing

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.ActivitySingBinding
import com.tinashe.hymnal.extensions.activity.applyMaterialTransform
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.ui.collections.add.AddToCollectionFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingHymnsActivity : AppCompatActivity() {

    private val viewModel: SingHymnsViewModel by viewModels()

    private var pagerAdapter: SingFragmentsAdapter? = null
    private lateinit var binding: ActivitySingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        applyMaterialTransform(getString(R.string.transition_shared_element))
        super.onCreate(savedInstanceState)
        binding = ActivitySingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()

        val number = intent.getIntExtra(ARG_SELECTED, 1)

        viewModel.statusLiveData.observeNonNull(this) {
        }
        viewModel.hymnalTitleLiveData.observeNonNull(this) {
            title = it
        }
        viewModel.hymnListLiveData.observeNonNull(this) {
            pagerAdapter = SingFragmentsAdapter(this, it)
            binding.viewPager.apply {
                adapter = pagerAdapter
                setCurrentItem(number - 1, false)
            }
        }

        val collectionId = intent.getIntExtra(ARG_COLLECTION_ID, -1)
        viewModel.loadData(collectionId)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi() {
        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            numberPadView.setOnNumSelectedCallback { number ->
                hideNumPad()
                val position = pagerAdapter?.hymns?.indexOfFirst { it.number == number }
                if (position == null || position < 0) {
                    Snackbar.make(
                        fabNumber,
                        getString(R.string.error_invalid_number, number),
                        Snackbar.LENGTH_LONG
                    ).show()
                    return@setOnNumSelectedCallback
                }
                viewPager.setCurrentItem(position, false)
            }

            fabNumber.setOnClickListener {
                numberPadView.onShown()
                fabNumber.isExpanded = true
                scrimOverLay.isVisible = true
            }
            scrimOverLay.setOnTouchListener { _, _ ->
                if (fabNumber.isExpanded) {
                    hideNumPad()
                }
                true
            }

            bottomAppBar.setOnMenuItemClickListener {
                return@setOnMenuItemClickListener when (it.itemId) {
                    R.id.action_text_format -> {
                        true
                    }
                    R.id.action_edit -> {
                        true
                    }
                    R.id.action_add_to_list -> {
                        val hymnId = pagerAdapter?.hymns?.get(
                            viewPager.currentItem
                        )?.hymnId
                            ?: return@setOnMenuItemClickListener false

                        val fragment = AddToCollectionFragment.newInstance(hymnId)
                        fragment.show(supportFragmentManager, fragment.tag)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun hideNumPad() {
        binding.apply {
            scrimOverLay.isVisible = false
            fabNumber.isExpanded = false
        }
    }

    override fun onBackPressed() {
        if (binding.fabNumber.isExpanded) {
            hideNumPad()
            return
        }
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.hymn_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val ARG_SELECTED = "arg:selected_number"
        private const val ARG_COLLECTION_ID = "arg:collection_id"

        fun singIntent(context: Context, number: Int): Intent =
            Intent(context, SingHymnsActivity::class.java).apply {
                putExtra(ARG_SELECTED, number)
            }

        fun singCollectionIntent(context: Context, id: Int): Intent =
            Intent(context, SingHymnsActivity::class.java).apply {
                putExtra(ARG_COLLECTION_ID, id)
            }
    }
}
