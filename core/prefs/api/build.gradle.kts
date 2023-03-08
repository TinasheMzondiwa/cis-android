plugins {
    alias(libs.plugins.sgp.base)
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    api(projects.core.prefs.model)
}