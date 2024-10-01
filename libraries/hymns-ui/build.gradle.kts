plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.sgp.base)
}

slack {
    features { compose() }
}

dependencies {
    implementation(projects.libraries.ui)
    implementation(libs.kotlinx.collectionsImmutable)
}