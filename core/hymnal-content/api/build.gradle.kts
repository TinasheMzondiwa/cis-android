plugins {
    alias(libs.plugins.sgp.base)
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "hymnal.content.api"
}

dependencies {
    api(projects.core.hymnalContent.model)
    api(libs.kotlin.coroutines)
}