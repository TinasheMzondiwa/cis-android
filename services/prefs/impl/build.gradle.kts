plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sgp.base)
    alias(libs.plugins.hilt)
}


dependencies {
    api(projects.core.prefs.api)
    implementation(projects.foundation.android)
    implementation(projects.libraries.fonts)
    implementation(projects.libraries.models)

    implementation(libs.androidx.datastore.prefs)
    implementation(libs.androidx.preference)
    implementation(libs.google.hilt.android)
    implementation(libs.timber)
    ksp(libs.google.hilt.compiler)

}