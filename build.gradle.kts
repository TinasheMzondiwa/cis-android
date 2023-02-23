plugins {
    alias(libs.plugins.sgp.root)
    alias(libs.plugins.sgp.base)
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false
    alias(libs.plugins.gradle.cache.fix) apply false
    alias(libs.plugins.gradle.retry) apply false
    alias(libs.plugins.sortDependencies) apply false
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}
