import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    id("maven-publish")
}

group = "dev.kiit"
version = rootProject.layout.projectDirectory.file("../../version.txt").asFile.readText().trim()

kotlin {
    jvm()

    androidLibrary {
        namespace = "kiit.common.envs"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    js {
        browser()
        nodejs()
        generateTypeScriptDefinitions()
        binaries.library()
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
        }
        commonMain.dependencies {}
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/slatekit/kiit")
            credentials {
                username = System.getenv("KIIT_PUBLISH_ACTOR") ?: ""
                password = System.getenv("KIIT_PUBLISH_TOKEN") ?: ""
            }
        }
    }
    publications.withType<MavenPublication>().configureEach {
        groupId = project.group.toString()
    }
}
