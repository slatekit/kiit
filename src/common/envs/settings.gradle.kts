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

rootProject.name = "kiit-common-envs"
include(":kiit-common-envs")
project(":kiit-common-envs").projectDir = file("library")
