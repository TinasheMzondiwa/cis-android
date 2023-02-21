plugins {
    alias(libs.plugins.sgp.base)
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "hymnal.l10n"
    buildFeatures { androidResources = true }
}

dependencies { coreLibraryDesugaring(libs.coreLibraryDesugaring) }