plugins {
    alias(libs.plugins.sgp.base)
    alias(libs.plugins.ksp)
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    kotlin("android")
}

android {
    namespace = "hymnal.content.impl"

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.foundation.android)
    api(projects.core.hymnalContent.api)
    api(projects.core.prefs.api)
    implementation(projects.core.storage)

    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)
    implementation(libs.square.moshi.kotlin)
    ksp(libs.square.moshi.codegen)
    compileOnly(libs.javax.annotation)
    implementation(libs.timber)

    testImplementation(libs.bundles.testing.common)
}