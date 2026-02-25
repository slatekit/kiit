import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    id("maven-publish")
}

group = "dev.kiit"
version = "1.0.0"

kotlin {
    jvm()
    androidLibrary {
        namespace = "org.jetbrains.kotlinx.multiplatform.library.template"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    JvmTarget.JVM_11
                )
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            // The URL format is https://maven.pkg.github.com
            url = uri("https://maven.pkg.github.com/slatekit/kiit")

            credentials {
                // Use environment variables for credentials
                username = System.getenv("KIIT_PUBLISH_ACTOR") ?: ""
                password = System.getenv("KIIT_PUBLISH_TOKEN") ?: ""
            }
        }
    }
    publications {
        // Register a publication for the Kotlin component
        register<MavenPublication>("gpr") {
            from(components["kotlin"])
            groupId = project.group.toString()
            artifactId = "kiit-common-env"
            version = project.version.toString()
        }
    }
}

//mavenPublishing {
//    publishToMavenCentral()
//
//    signAllPublications()
//
//    coordinates(group.toString(), "library", version.toString())
//
//    pom {
//        name = "My library"
//        description = "A library."
//        inceptionYear = "2024"
//        url = "https://github.com/kotlin/multiplatform-library-template/"
//        licenses {
//            license {
//                name = "XXX"
//                url = "YYY"
//                distribution = "ZZZ"
//            }
//        }
//        developers {
//            developer {
//                id = "XXX"
//                name = "YYY"
//                url = "ZZZ"
//            }
//        }
//        scm {
//            url = "XXX"
//            connection = "YYY"
//            developerConnection = "ZZZ"
//        }
//    }
//}
