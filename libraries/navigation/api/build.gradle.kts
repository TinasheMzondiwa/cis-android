plugins {
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
}

dependencies {
    api(projects.libraries.navigation.model)
    api(libs.bundles.circuit)
}