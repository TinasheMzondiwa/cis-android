plugins {
    alias(libs.plugins.sgp.base)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "hymnal.content.api"
}

dependencies {
    api(projects.core.hymnalContent.model)
    api(libs.kotlin.coroutines)
}