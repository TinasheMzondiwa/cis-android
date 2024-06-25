import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sgp.base)
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val releaseFile = file("../app/keystore.properties")
val useReleaseKeystore = releaseFile.exists()

android {
    namespace = "app.hymnal"

    defaultConfig {
        applicationId = "com.tinashe.christInSong"
        versionCode = 3439
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
            manifestPlaceholders["enableReporting"] = true
        }

        val debug by getting {
            manifestPlaceholders["enableReporting"] = false
        }

        val benchmark by creating {
            initWith(release)
            matchingFallbacks.add("release")
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles("benchmark-rules.pro")
        }
    }
}

slack {
    features { compose() }
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.foundation.l10nStrings)


    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.config)
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)


    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.androidx.ext)
    androidTestImplementation(libs.test.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.compose.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}