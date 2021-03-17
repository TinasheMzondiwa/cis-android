package com.tinashe.hymnal.ui.hymns.sing.present

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.databinding.ActivityPresentHymnBinding
import timber.log.Timber

class PresentHymnActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPresentHymnBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresentHymnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExit.setOnClickListener { finish() }

        toggleHideyBar()

        val hymn = intent.getParcelableExtra<Hymn?>(ARG_HYMN)
        if (hymn == null) {
            finish()
            return
        }
        val pagerAdapter = PresentPagerAdapter(this, hymn)
        binding.viewPager.adapter = pagerAdapter
    }

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    private fun toggleHideyBar() {
        val uiOptions: Int = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions
        val isImmersiveModeEnabled = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions
        if (isImmersiveModeEnabled) {
            Timber.i("Turning immersive mode mode off. ")
        } else {
            Timber.i("Turning immersive mode mode on.")
        }
        binding.btnExit.isVisible = isImmersiveModeEnabled
        val color = if (!isImmersiveModeEnabled) {
            ContextCompat.getColor(this, R.color.scrim)
        } else {
            Color.TRANSPARENT
        }
        binding.contentOverLay.setBackgroundColor(color)

        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = newUiOptions
    }

    companion object {
        private const val ARG_HYMN = "arg:hymn"

        fun launchIntent(context: Context, hymn: Hymn): Intent =
            Intent(context, PresentHymnActivity::class.java).apply {
                putExtra(ARG_HYMN, hymn)
            }
    }
}
