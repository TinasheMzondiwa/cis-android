plugins {
    alias(libs.plugins.sgp.base)
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android { namespace = "hymnal.android" }

dependencies {
    implementation(projects.foundation.l10nStrings)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.core)
    implementation(libs.timber)
}