// Root build file — shared plugin declarations only.
// No sub-project configuration here; each library has its own build.gradle.kts.

plugins {
    alias(libs.plugins.kotlinMultiplatform)        apply false
    alias(libs.plugins.androidLibrary)             apply false
    alias(libs.plugins.vanniktech.mavenPublish)    apply false
    alias(libs.plugins.ktlint)                     apply false
}
