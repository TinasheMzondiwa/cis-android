plugins {
    alias(libs.plugins.foundry.root)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false
    alias(libs.plugins.gradle.cache.fix) apply false
    alias(libs.plugins.gradle.retry) apply false
    alias(libs.plugins.sortDependencies) apply false
    alias(libs.plugins.ksp) apply false
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}
