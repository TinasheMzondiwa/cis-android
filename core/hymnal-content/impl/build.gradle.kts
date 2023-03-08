plugins {
    alias(libs.plugins.sgp.base)
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "hymnal.content.impl"

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }
}

dependencies {
    implementation(projects.foundation.android)
    api(projects.core.hymnalContent.api)
    api(projects.core.prefs.api)
    implementation(projects.core.storage)

    implementation(libs.google.hilt.android)
    kapt(libs.google.hilt.compiler)
    implementation(libs.square.moshi.kotlin)
    kapt(libs.square.moshi.codegen)
    compileOnly(libs.javax.annotation)
    implementation(libs.timber)

    testImplementation(libs.bundles.testing.common)
}