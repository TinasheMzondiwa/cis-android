plugins {
    alias(libs.plugins.sgp.base)
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "hymnal.content.model"
}

dependencies {}