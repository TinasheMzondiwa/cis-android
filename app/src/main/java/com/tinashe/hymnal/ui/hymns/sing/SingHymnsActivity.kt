package com.tinashe.hymnal.ui.hymns.sing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.transition.doOnEnd
import androidx.core.transition.doOnStart
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.constants.UiPref
import com.tinashe.hymnal.databinding.ActivitySingBinding
import com.tinashe.hymnal.extensions.activity.applyMaterialTransform
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.ui.collections.add.AddToCollectionFragment
import com.tinashe.hymnal.ui.hymns.hymnals.HymnalListBottomSheetFragment
import com.tinashe.hymnal.ui.hymns.sing.edit.EditHymnActivity
import com.tinashe.hymnal.ui.hymns.sing.player.PlaybackState
import com.tinashe.hymnal.ui.hymns.sing.player.SimpleTunePlayer
import com.tinashe.hymnal.ui.hymns.sing.present.PresentHymnActivity
import com.tinashe.hymnal.ui.hymns.sing.style.TextStyleChanges
import com.tinashe.hymnal.ui.hymns.sing.style.TextStyleFragment
import com.tinashe.hymnal.utils.Helper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SingHymnsActivity : AppCompatActivity(), TextStyleChanges {

    @Inject
    lateinit var prefs: HymnalPrefs

    @Inject
    lateinit var tunePlayer: SimpleTunePlayer

    private val viewModel: SingHymnsViewModel by viewModels()

    private var pagerAdapter: SingFragmentsAdapter? = null
    private lateinit var binding: ActivitySingBinding

    private var currentPosition: Int? = null

    private val themeContainerColor: Int by lazy { ContextCompat.getColor(this, R.color.cis_gold) }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyMaterialTransform(getString(R.string.transition_shared_element))
        super.onCreate(savedInstanceState)
        binding = ActivitySingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()

        val hymnId = intent.getIntExtra(ARG_SELECTED, 1)

        viewModel.statusLiveData.observeNonNull(this) {
        }
        viewModel.hymnalTitleLiveData.observeNonNull(this) {
            title = it
        }
        viewModel.hymnListLiveData.observeNonNull(this) { hymns ->
            pagerAdapter = SingFragmentsAdapter(this, hymns)
            binding.viewPager.apply {
                adapter = pagerAdapter
                val position = currentPosition ?: hymns.indexOfFirst { it.hymnId == hymnId }
                setCurrentItem(position, false)
                currentPosition = null
            }
        }
        tunePlayer.playbackLiveData.observeNonNull(this) {
            invalidateOptionsMenu()
        }

        val collectionId = intent.getIntExtra(ARG_COLLECTION_ID, -1)
        viewModel.loadData(collectionId)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi() {
        lifecycle.addObserver(tunePlayer)

        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tunePlayer.stopMedia()

                    val hymns = pagerAdapter?.hymns
                    if (hymns.isNullOrEmpty()) {
                        return
                    }
                    val hymn = hymns[position]
                    viewModel.hymnViewed(hymn)
                }
            })

            numberPadView.setOnNumSelectedCallback { number ->
                hideNumPad()
                val position = pagerAdapter?.hymns?.indexOfFirst { it.number == number }
                if (position == null || position < 0) {
                    snackbar.show(messageText = getString(R.string.error_invalid_number, number))
                    return@setOnNumSelectedCallback
                }
                viewPager.setCurrentItem(position, false)
            }

            fabNumber.setOnClickListener {
                showNumPad()
            }
            scrimOverLay.setOnTouchListener { _, _ ->
                if (numberPadView.isVisible) {
                    hideNumPad()
                }
                true
            }

            bottomAppBar.setOnMenuItemClickListener {
                return@setOnMenuItemClickListener when (it.itemId) {
                    R.id.action_text_format -> {
                        val fragment = TextStyleFragment.newInstance(
                            prefs.getTextStyleModel(),
                            this@SingHymnsActivity
                        )
                        fragment.show(supportFragmentManager, fragment.tag)
                        true
                    }
                    R.id.action_edit -> {
                        if (pagerAdapter?.hymns?.isNotEmpty() == true) {
                            val hymn = pagerAdapter?.hymns
                                ?.get(viewPager.currentItem)
                                ?: return@setOnMenuItemClickListener false

                            val intent = EditHymnActivity.editIntent(this@SingHymnsActivity, hymn)
                            startActivityForResult(intent, RC_EDIT_HYMN)
                        }
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

    private fun showNumPad() {
        binding.apply {
            numberPadView.onShown()

            val transform = MaterialContainerTransform().apply {
                startView = fabNumber
                endView = numberPadView
                addTarget(endView)
                pathMotion = MaterialArcMotion()
                containerColor = themeContainerColor
                doOnEnd { scrimOverLay.visibility = View.VISIBLE }
            }
            TransitionManager.beginDelayedTransition(coordinator, transform)
            fabNumber.visibility = View.GONE
            numberPadView.visibility = View.VISIBLE
        }
    }

    private fun hideNumPad() {
        binding.apply {
            val transform = MaterialContainerTransform().apply {
                startView = numberPadView
                endView = fabNumber
                addTarget(endView)
                pathMotion = MaterialArcMotion()
                containerColor = themeContainerColor
                doOnStart { scrimOverLay.visibility = View.GONE }
            }
            TransitionManager.beginDelayedTransition(coordinator, transform)
            fabNumber.visibility = View.VISIBLE
            numberPadView.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        if (binding.numberPadView.isVisible) {
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
            R.id.action_tune -> {
                pagerAdapter?.hymns?.get(
                    binding.viewPager.currentItem
                )?.number?.let {
                    tunePlayer.togglePlayTune(it)
                }
                true
            }
            R.id.action_fullscreen -> {
                pagerAdapter?.hymns
                    ?.get(binding.viewPager.currentItem)?.let {
                        val intent = PresentHymnActivity.launchIntent(this, it)
                        startActivity(intent)
                    }
                true
            }
            R.id.actions_hymnals -> {
                val fragment = HymnalListBottomSheetFragment
                    .newInstance {
                        currentPosition = binding.viewPager.currentItem
                        viewModel.switchHymnal(it)
                    }
                fragment.show(supportFragmentManager, fragment.tag)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val mediaItem = menu?.findItem(R.id.action_tune) ?: return super.onPrepareOptionsMenu(menu)
        when (tunePlayer.playbackLiveData.value) {
            PlaybackState.ON_PLAY -> {
                mediaItem.apply {
                    isVisible = true
                    icon = ContextCompat.getDrawable(
                        this@SingHymnsActivity,
                        R.drawable.ic_stop
                    )
                }
            }
            PlaybackState.ON_COMPLETE,
            PlaybackState.ON_STOP,
            null -> {
                val hymns = pagerAdapter?.hymns
                if (hymns.isNullOrEmpty()) {
                    return false
                }
                val number = hymns[binding.viewPager.currentItem].number
                val canPlay = number.let { tunePlayer.canPlayTune(it) }
                mediaItem.apply {
                    isVisible = canPlay
                    icon = ContextCompat.getDrawable(
                        this@SingHymnsActivity, R.drawable.ic_play_circle
                    )
                }
            }
        }
        menu.findItem(R.id.actions_hymnals).isVisible = !intent.hasExtra(ARG_COLLECTION_ID)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun updateTheme(pref: UiPref) {
        prefs.setUiPref(pref)
        Helper.switchToTheme(pref)
    }

    override fun updateTypeFace(fontRes: Int) {
        prefs.setFontRes(fontRes)
        updateHymnUi()
    }

    override fun updateTextSize(size: Float) {
        prefs.setFontSize(size)
        updateHymnUi()
    }

    private fun updateHymnUi() {
        lifecycleScope.launch {
            binding.viewPager.apply {
                val pagePosition = currentItem
                adapter?.apply {
                    notifyItemChanged(pagePosition - 1)
                    notifyItemChanged(pagePosition)
                    notifyItemChanged(pagePosition + 1)

                    setCurrentItem(pagePosition + 2, false)
                    delay(200)
                    setCurrentItem(pagePosition, false)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_EDIT_HYMN && resultCode == Activity.RESULT_OK) {
            currentPosition = binding.viewPager.currentItem

            val collectionId = intent.getIntExtra(ARG_COLLECTION_ID, -1)
            viewModel.loadData(collectionId)
        }
    }

    companion object {
        private const val ARG_SELECTED = "arg:selected_number"
        private const val ARG_COLLECTION_ID = "arg:collection_id"
        private const val RC_EDIT_HYMN = 23

        fun singIntent(context: Context, hymnId: Int): Intent =
            Intent(context, SingHymnsActivity::class.java).apply {
                putExtra(ARG_SELECTED, hymnId)
            }

        fun singCollectionIntent(
            context: Context,
            collectionId: Int,
            hymnId: Int
        ): Intent =
            Intent(context, SingHymnsActivity::class.java).apply {
                putExtra(ARG_COLLECTION_ID, collectionId)
                putExtra(ARG_SELECTED, hymnId)
            }
    }
}
