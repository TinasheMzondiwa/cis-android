plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.hilt)
}

foundry {
    features { compose() }
}

ksp {
    arg("circuit.codegen.mode", "hilt")
}


dependencies {
    implementation(projects.core.prefs.api)
    implementation(projects.core.hymnalContent.api)
    implementation(projects.foundation.l10nStrings)
    implementation(projects.libraries.fonts)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation.api)

    implementation(libs.androidx.annotations)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.hilt.android)
    implementation(libs.markwon.core)
    ksp(libs.google.hilt.compiler)
    ksp(libs.circuit.codegen)
}