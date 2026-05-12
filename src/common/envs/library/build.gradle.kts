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
    jvm {
        withSourcesJar()
    }

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
        pom {
            name.set("kiit-common-envs")
            description.set("Kiit environment management — Kotlin Multiplatform library")
            url.set("https://github.com/slatekit/kiit")
            licenses {
                license {
                    name.set("Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }
            developers {
                developer {
                    id.set("kishorereddy")
                    name.set("Kishore Reddy")
                    url.set("https://github.com/kishorereddy")
                }
            }
            scm {
                url.set("https://github.com/slatekit/kiit")
                connection.set("scm:git:git://github.com/slatekit/kiit.git")
                developerConnection.set("scm:git:ssh://github.com/slatekit/kiit.git")
            }
        }
    }
}

tasks.register("publishJvmToGitHubPackages") {
    group = "publishing"
    description = "Publishes the JVM Maven publication to GitHub Package Registry"
    dependsOn("publishJvmPublicationToGitHubPackagesRepository")
}

val jsDistDir = layout.buildDirectory.dir("dist/js/productionLibrary")

// Patches the Kotlin-generated package.json with the scoped npm name, description,
// repository, and GitHub Package Registry publishConfig. Runs at execution time so
// it is fully compatible with Gradle's configuration cache.
val patchNpmPackageJson = tasks.register("patchNpmPackageJson") {
    group = "publishing"
    dependsOn("assemble")
    doLast {
        val pkgFile = jsDistDir.get().asFile.resolve("package.json")
        @Suppress("UNCHECKED_CAST")
        val pkg = groovy.json.JsonSlurper().parse(pkgFile) as MutableMap<String, Any>
        pkg["name"] = "@slatekit/kiit-common-envs"
        pkg["description"] = "Kiit environment management — Kotlin Multiplatform library"
        pkg["publishConfig"] = mapOf("registry" to "https://npm.pkg.github.com")
        pkg["repository"] = mapOf(
            "type" to "git",
            "url" to "git+https://github.com/slatekit/kiit.git"
        )
        pkgFile.writeText(groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(pkg)))
    }
}

tasks.register<Exec>("publishNpmToGitHubPackages") {
    group = "publishing"
    description = "Publishes the npm package to GitHub Package Registry"
    dependsOn(patchNpmPackageJson)
    workingDir(jsDistDir)
    commandLine("npm", "publish")

    doFirst {
        val token = System.getenv("KIIT_PUBLISH_TOKEN")
            ?: error("KIIT_PUBLISH_TOKEN must be set to publish to GitHub Package Registry")
        jsDistDir.get().asFile.resolve(".npmrc").writeText(
            "//npm.pkg.github.com/:_authToken=${token}\n"
        )
    }
}
