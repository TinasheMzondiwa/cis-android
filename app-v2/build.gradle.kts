import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val appVersionCode = readPropertyValue(
    filePath = "build_number.properties",
    key = "BUILD_NUMBER",
    defaultValue = "1"
).toInt() + 3444

android {
    namespace = "app.hymnal"

    defaultConfig {
        applicationId = "com.tinashe.christInSong"
        versionCode = appVersionCode
        versionName = libs.versions.app.get()
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        val release by getting {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
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

    buildFeatures {
        buildConfig = true
    }

    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets")
            }
        }
    }
}

foundry {
    features { compose() }
}

ksp {
    arg("circuit.codegen.mode", "hilt")
}

dependencies {
    implementation(projects.core.hymnalContent.impl)
    implementation(projects.features.hymns)
    implementation(projects.features.search)
    implementation(projects.features.singHymn)
    implementation(projects.libraries.ui)
    implementation(projects.foundation.android)
    implementation(projects.foundation.l10nStrings)
    implementation(projects.libraries.models)
    implementation(projects.libraries.navigation.api)
    implementation(projects.services.prefs.impl)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core)
    implementation(libs.androidx.datastore.prefs)
    implementation(libs.material3.adaptive.navigation.suite)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.config)
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)
    ksp(libs.circuit.codegen)


    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.androidx.ext)
    androidTestImplementation(libs.test.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.compose.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

/**
 * Reads a value saved in a [Properties] file
 */
fun Project.readPropertyValue(
    filePath: String,
    key: String,
    defaultValue: String
): String {
    val file = file(filePath)
    return if (file.exists()) {
        val keyProps = Properties().apply {
            load(FileInputStream(file))
        }
        return keyProps.getProperty(key, defaultValue)
    } else {
        defaultValue
    }
}
