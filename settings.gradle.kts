pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://a8c-libs.s3.amazonaws.com/android")
    }
}

rootProject.name = "cis-android"
include(
    ":app",
    ":app-v2",
    ":benchmark",
    ":core:hymnal-content:api",
    ":core:hymnal-content:impl",
    ":core:hymnal-content:model",
    ":core:prefs:api",
    ":core:prefs:model",
    ":core:storage",
    ":features:navigation",
    ":foundation:android",
    ":foundation:l10n-strings",
    ":libraries:navigation:api",
    ":libraries:navigation:model",
    ":libraries:ui",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")