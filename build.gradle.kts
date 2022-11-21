plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    apply(from = "$rootDir/ktlint.gradle")
}
