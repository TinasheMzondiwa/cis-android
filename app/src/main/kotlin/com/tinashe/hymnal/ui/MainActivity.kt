package com.tinashe.hymnal.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.cfg.DonationsConfig
import com.tinashe.hymnal.databinding.ActivityMainBinding
import com.tinashe.hymnal.extensions.activity.applyExitMaterialTransform
import com.tinashe.hymnal.extensions.view.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AppBarBehaviour {

    @Inject
    lateinit var config: DonationsConfig

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        applyExitMaterialTransform()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_hymns,
                R.id.navigation_collections,
                R.id.navigation_support,
                R.id.navigation_info
            )
        )
        binding.apply {
            navView.menu.findItem(R.id.navigation_support).isVisible = config.enabled
            navView.setupWithNavController(navController)
            toolbarLayout.setupWithNavController(
                toolbar,
                navController,
                appBarConfiguration
            )
        }
    }

    override fun setAppBarExpanded(expanded: Boolean) {
        binding.appBarLayout.setExpanded(expanded, true)
    }

    override fun setAppBarTitle(title: String) {
        binding.toolbarLayout.title = title
    }
}
