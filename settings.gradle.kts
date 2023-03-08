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
    ":benchmark",
    ":core:hymnal-content:api",
    ":core:hymnal-content:model",
    ":core:storage",
    ":core:ui",
    ":foundation:android",
    ":foundation:l10n-strings",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")