import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.sgp.base)
}

val releaseFile = file("../app/keystore.properties")
val useReleaseKeystore = releaseFile.exists()

android {
    namespace = "com.tinashe.hymnal"

    defaultConfig {
        applicationId = "com.tinashe.christInSong"
        versionCode = 3433
        versionName = libs.versions.app.get()
        vectorDrawables.useSupportLibrary = true

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.incremental" to "true")
            }
        }
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }

    buildFeatures {
        viewBinding = true
    }
}

room {
    schemaLocationDir.set(file("$projectDir/schemas"))
}

dependencies {
    coreLibraryDesugaring(libs.coreLibraryDesugaring)

    implementation(projects.foundation.android)
    implementation(projects.foundation.l10nStrings)

    implementation(libs.android.billing)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.startup)
    implementation(libs.aztec)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.config)
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.google.material)
    implementation(libs.google.hilt.android)
    kapt(libs.google.hilt.compiler)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.android)
    implementation(libs.kotlin.coroutines.playservices)
    implementation(libs.square.moshi.kotlin)
    kapt(libs.square.moshi.codegen)
    compileOnly(libs.javax.annotation)
    implementation(libs.tapTarget)
    implementation(libs.timber)

    testImplementation(libs.bundles.testing.common)
    androidTestImplementation(libs.bundles.testing.android.common)
}
