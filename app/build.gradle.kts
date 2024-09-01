import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sgp.base)
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

val releaseFile = file("../app/keystore.properties")
val useReleaseKeystore = releaseFile.exists()

android {
    namespace = "com.tinashe.hymnal"

    defaultConfig {
        applicationId = "sda.vellore.hymnal"
        versionCode = 3442
        versionName = libs.versions.app.get()
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        if (useReleaseKeystore) {
            val keyProps = Properties().apply {
                load(FileInputStream(releaseFile))
            }

            create("release") {
                storeFile = file(keyProps.getProperty("release.keystore"))
                storePassword = keyProps.getProperty("release.keystore.password")
                keyAlias = keyProps.getProperty("key.alias")
                keyPassword = keyProps.getProperty("key.password")
            }
        }
    }

    buildTypes {
        val release by getting {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
            if (useReleaseKeystore) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.foundation.android)
    implementation(projects.foundation.l10nStrings)
    implementation(projects.core.hymnalContent.impl)
    implementation(projects.core.prefs.api)

    implementation(libs.android.billing)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.datastore.prefs)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.startup)
    implementation(libs.aztec)
    implementation(libs.google.material)
    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)
    implementation(libs.tapTarget)
    implementation(libs.timber)
    implementation(libs.markwon.core)

    testImplementation(libs.bundles.testing.common)
    androidTestImplementation(libs.bundles.testing.android.common)
}
