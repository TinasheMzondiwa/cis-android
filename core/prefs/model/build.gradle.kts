plugins {
    alias(libs.plugins.sgp.base)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
}

android { namespace = "hymnal.prefs.model" }

dependencies {
    api(libs.androidx.annotations)
}