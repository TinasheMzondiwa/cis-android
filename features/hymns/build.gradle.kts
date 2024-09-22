plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sgp.base)
    id("dagger.hilt.android.plugin")
}

slack {
    features { compose() }
}

ksp {
    arg("circuit.codegen.mode", "hilt")
}


dependencies {
    implementation(projects.core.hymnalContent.api)
    implementation(projects.foundation.l10nStrings)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation.api)

    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)
    ksp(libs.circuit.codegen)
}