plugins {
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

foundry {
    features { compose() }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.bundles.compose)
    api(libs.bundles.compose.tooling)
}