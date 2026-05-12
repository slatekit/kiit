import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.security.MessageDigest

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    id("maven-publish")
}

group = "dev.kiit"
version = rootProject.layout.projectDirectory.file("../../version.txt").asFile.readText().trim()

val xcfName = "KiitCommonEnvs"
val xcf = XCFramework(xcfName)

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

    iosX64 {
        binaries.framework {
            baseName = xcfName
            isStatic = true
            xcf.add(this)
        }
    }
    iosArm64 {
        binaries.framework {
            baseName = xcfName
            isStatic = true
            xcf.add(this)
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
            isStatic = true
            xcf.add(this)
        }
    }

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

// ---------------------------------------------------------------------------
// Maven / GitHub Packages publishing (JVM, Android)
// ---------------------------------------------------------------------------

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

tasks.register("publishAndroidToGitHubPackages") {
    group = "publishing"
    description = "Publishes the Android AAR publication to GitHub Package Registry"
    dependsOn("publishAndroidPublicationToGitHubPackagesRepository")
}

// ---------------------------------------------------------------------------
// npm / GitHub Packages publishing (JS)
// ---------------------------------------------------------------------------

val jsDistDir = layout.buildDirectory.dir("dist/js/productionLibrary")

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

// ---------------------------------------------------------------------------
// SPM / GitHub Releases publishing (iOS XCFramework)
// ---------------------------------------------------------------------------

val xcfOutputDir  = layout.buildDirectory.dir("XCFrameworks/release")
val spmOutputDir  = layout.buildDirectory.dir("spm")
val xcfZipName    = "${xcfName}.xcframework.zip"
val assembleXCFTask = "assemble${xcfName}ReleaseXCFramework"

// 1. Zip the assembled XCFramework.
val zipXCFramework = tasks.register<Zip>("zipXCFramework") {
    group = "publishing"
    description = "Zips the $xcfName XCFramework for SPM distribution"
    dependsOn(assembleXCFTask)
    from(xcfOutputDir) {
        include("${xcfName}.xcframework/**")
    }
    archiveFileName.set(xcfZipName)
    destinationDirectory.set(spmOutputDir)
}

// 2. Compute the SHA-256 checksum required by SPM binaryTarget.
val computeXCFrameworkChecksum = tasks.register("computeXCFrameworkChecksum") {
    group = "publishing"
    description = "Computes SHA-256 checksum of the zipped XCFramework"
    dependsOn(zipXCFramework)
    doLast {
        val zipFile = spmOutputDir.get().asFile.resolve(xcfZipName)
        val digest   = MessageDigest.getInstance("SHA-256")
        val checksum = digest.digest(zipFile.readBytes()).joinToString("") { "%02x".format(it) }
        spmOutputDir.get().asFile.resolve("${xcfName}.xcframework.zip.sha256").writeText(checksum)
        println("XCFramework SHA-256: $checksum")
    }
}

// 3. Generate Package.swift referencing the GitHub Release asset.
val generatePackageSwift = tasks.register("generatePackageSwift") {
    group = "publishing"
    description = "Generates Package.swift for SPM binary distribution"
    dependsOn(computeXCFrameworkChecksum)
    doLast {
        val ver      = project.version.toString()
        val checksum = spmOutputDir.get().asFile
            .resolve("${xcfName}.xcframework.zip.sha256")
            .readText().trim()
        val url = "https://github.com/slatekit/kiit/releases/download/v${ver}/${xcfZipName}"

        val content = """
            // swift-tools-version:5.5
            import PackageDescription

            let package = Package(
                name: "$xcfName",
                platforms: [.iOS(.v13)],
                products: [
                    .library(name: "$xcfName", targets: ["$xcfName"]),
                ],
                targets: [
                    .binaryTarget(
                        name: "$xcfName",
                        url: "$url",
                        checksum: "$checksum"
                    ),
                ]
            )
        """.trimIndent()

        // Write to build/spm/ for inspection, and to the module project root for git.
        spmOutputDir.get().asFile.resolve("Package.swift").writeText(content)
        projectDir.parentFile.resolve("Package.swift").writeText(content)
        println("Generated Package.swift → ${projectDir.parentFile.resolve("Package.swift")}")
        println("  url:      $url")
        println("  checksum: $checksum")
    }
}

// 4. Upload the zip to GitHub Releases (creates the release tag if absent).
tasks.register("publishIosToGitHubPackages") {
    group = "publishing"
    description = "Uploads the XCFramework zip to a GitHub Release for SPM consumption"
    dependsOn(generatePackageSwift)
    doLast {
        val token   = System.getenv("KIIT_PUBLISH_TOKEN")
            ?: error("KIIT_PUBLISH_TOKEN must be set to publish to GitHub Package Registry")
        val ver     = project.version.toString()
        val tag     = "v${ver}"
        val zipPath = spmOutputDir.get().asFile.resolve(xcfZipName).absolutePath

        // Create the GitHub Release for this tag if it does not yet exist.
        exec {
            environment("GITHUB_TOKEN", token)
            commandLine(
                "sh", "-c",
                "gh release view $tag --repo slatekit/kiit > /dev/null 2>&1 || " +
                "gh release create $tag --repo slatekit/kiit " +
                "--title \"Release $tag\" --notes \"KMP $tag\""
            )
        }

        // Upload (or replace) the XCFramework zip asset.
        exec {
            environment("GITHUB_TOKEN", token)
            commandLine(
                "gh", "release", "upload", tag, zipPath,
                "--repo", "slatekit/kiit", "--clobber"
            )
        }

        println("Uploaded $xcfZipName to GitHub Release $tag")
        println("Add to your Xcode project / Package.swift:")
        println("  https://github.com/slatekit/kiit/releases/download/$tag/$xcfZipName")
    }
}
