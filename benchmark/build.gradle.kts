import com.android.build.gradle.internal.dsl.ManagedVirtualDevice

plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "app.hymnal.benchmark"
    compileSdk = 33

    defaultConfig {
        minSdk = 23
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
    }

    buildTypes {
        val benchmark by creating {
            isDebuggable = true
            signingConfig = signingConfigs["debug"]
            matchingFallbacks.add("release")
        }
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true

    testOptions {
        managedDevices {
            devices {
                add(
                    ManagedVirtualDevice("pixel6Api31").apply {
                        device = "Pixel 6"
                        apiLevel = 31
                        systemImageSource = "aosp"
                    }
                )
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.profileinstaller)
    implementation(libs.test.androidx.benchmark.macro)
    implementation(libs.test.androidx.ext)
    implementation(libs.test.androidx.uiautomator)
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark"
    }
}