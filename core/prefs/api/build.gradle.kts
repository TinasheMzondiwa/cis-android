plugins {
    alias(libs.plugins.sgp.base)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

dependencies {
    api(projects.core.prefs.model)
    api(libs.kotlin.coroutines)
}