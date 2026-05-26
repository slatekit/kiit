pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "kiit"

// Sub-projects are added here as libraries are migrated.
// Pattern:
//   include(":core-result")
//   project(":core-result").projectDir = file("core/result")

// core
include(":core-codes")
project(":core-codes").projectDir = file("core/codes")
