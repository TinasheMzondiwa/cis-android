plugins {
    alias(libs.plugins.sgp.base)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
    id("dagger.hilt.android.plugin")
}

slack {
    features { compose() }
}

ksp {
    arg("circuit.codegen.mode", "hilt")
}

dependencies {
    implementation(projects.libraries.navigation.api)
    implementation(projects.libraries.ui)
    implementation(projects.foundation.l10nStrings)

    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)
    ksp(libs.circuit.codegen)
}