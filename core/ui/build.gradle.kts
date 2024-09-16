plugins {
    alias(libs.plugins.sgp.base)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

slack {
    features { compose() }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.bundles.compose)
    api(libs.bundles.compose.tooling)
}