package com.tinashe.hymnal.ui.hymns.sing.present

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.tinashe.hymnal.databinding.ActivityPresentHymnBinding
import com.tinashe.hymnal.extensions.view.viewBinding
import hymnal.android.intent.getParcelableCompat
import hymnal.content.model.Hymn

class PresentHymnActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityPresentHymnBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnExit.setOnClickListener { finish() }

        enableImmersiveMode()

        val hymn = intent.getParcelableCompat(ARG_HYMN, Hymn::class.java)
        if (hymn == null) {
            finish()
            return
        }
        val pagerAdapter = PresentPagerAdapter(this, hymn)
        binding.viewPager.adapter = pagerAdapter
    }

    private fun enableImmersiveMode() {
        with(WindowCompat.getInsetsController(window, window.decorView)) {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    companion object {
        private const val ARG_HYMN = "arg:hymn"

        fun launchIntent(context: Context, hymn: Hymn): Intent =
            Intent(context, PresentHymnActivity::class.java).apply {
                putExtra(ARG_HYMN, hymn)
            }
    }
}
