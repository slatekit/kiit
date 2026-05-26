plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

kotlin {
    jvm()

    androidTarget {
        publishLibraryVariants("release")
    }

    js(IR) {
        browser()
        nodejs()
        binaries.library()
        generateTypeScriptDefinitions()
    }

    listOf(iosArm64(), iosSimulatorArm64(), iosX64()).forEach {
        it.binaries.framework {
            baseName = "KiitCodes"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // No kiit dependencies — core library
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace  = "kiit.codes"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

mavenPublishing {
    coordinates(
        groupId    = "dev.kiit",
        artifactId = "kiit-codes",
        version    = "0.1.0-alpha"
    )
    pom {
        name        = "kiit-codes"
        description = "Typed status and error codes used by kiit-result for structured success and failure classification"
        url         = "https://kiit.dev"
        licenses {
            license {
                name = "Apache-2.0"
                url  = "https://www.apache.org/licenses/LICENSE-2.0"
            }
        }
        developers {
            developer {
                id   = "codehelix"
                name = "CodeHelix"
                url  = "https://kiit.dev"
            }
        }
        scm {
            url                 = "https://github.com/slatekit/kiit"
            connection          = "scm:git:git://github.com/slatekit/kiit.git"
            developerConnection = "scm:git:ssh://git@github.com/slatekit/kiit.git"
        }
    }
}
