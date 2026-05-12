# kiit-common-envs

Kotlin Multiplatform library for environment management. Provides a typed model for named environments (`loc`, `dev`, `qat`, `stg`, `pro`) and their modes (`Dev`, `Qat`, `Uat`, `Pro`, `Dis`), with parsing, selection, and predicate helpers.

**Targets:** JVM · Android · iOS (x64, arm64, simulatorArm64) · JS (browser + Node.js, TypeScript definitions included)
**iOS distribution:** XCFramework binary via Swift Package Manager (GitHub Releases)

---

## About

| Type | Value |
|---|---|
| Group | `dev.kiit` |
| Artifact | `kiit-common-envs` |
| Version | `3.4.0` |
| Kotlin | 2.1.21 |
| Android minSdk | 24 |
| Android compileSdk | 36 |
| License | Apache 2.0 |

### API

```kotlin
// Sealed class of environment modes
EnvMode.Dev | EnvMode.Qat | EnvMode.Uat | EnvMode.Dis | EnvMode.Pro | EnvMode.Other(name)
EnvMode.parse("qat")              // → EnvMode.Qat

// Single environment
val env = Env.parse("qa1:qat")    // name="qa1", mode=Qat
env.key                           // "qa1:qat"
env.isQat                         // true

// Collection of environments with selection
val envs = Envs.defaults()        // loc, dev, qat, stg, pro
val live = envs.select("pro")
live.isPro                        // true
live.isValid("dev")               // true
live.get("stg")                   // Env?
```

---

## Install

### JVM / Android (Gradle)

Add the GitHub Package Registry to your repositories, then declare the dependency for your target.

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/slatekit/kiit")
            credentials {
                username = System.getenv("KIIT_INSTALL_ACTOR")
                password = System.getenv("KIIT_INSTALL_TOKEN")
            }
        }
    }
}
```

```kotlin
// build.gradle.kts — JVM
dependencies {
    implementation("dev.kiit:kiit-common-envs-jvm:3.4.0")
}

// build.gradle.kts — Android
dependencies {
    implementation("dev.kiit:kiit-common-envs-android:3.4.0")
}

// build.gradle.kts — Kotlin Multiplatform (uses the root metadata artifact)
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("dev.kiit:kiit-common-envs:3.4.0")
        }
    }
}
```

### iOS / Swift Package Manager

The library is distributed as a pre-built **XCFramework** hosted on GitHub Releases. No Kotlin toolchain is needed on the consumer side.

#### Option A — binary target in your own `Package.swift`

Add the binary target directly to your package manifest:

```swift
// Package.swift
// swift-tools-version:5.5
import PackageDescription

let package = Package(
    name: "MyApp",
    platforms: [.iOS(.v13)],
    dependencies: [],
    targets: [
        .binaryTarget(
            name: "KiitCommonEnvs",
            url: "https://github.com/slatekit/kiit/releases/download/v3.4.0/KiitCommonEnvs.xcframework.zip",
            checksum: "<checksum>"   // copy from src/common/envs/Package.swift
        ),
        .target(
            name: "MyApp",
            dependencies: ["KiitCommonEnvs"]
        ),
    ]
)
```

The exact URL and SHA-256 checksum for every release are recorded in `src/common/envs/Package.swift`, which is regenerated automatically during each publish run.

#### Option B — Xcode UI

1. In Xcode, go to **File → Add Package Dependencies…**
2. Paste the release asset URL:
   ```
   https://github.com/slatekit/kiit/releases/download/v3.4.0/KiitCommonEnvs.xcframework.zip
   ```
3. Xcode will resolve the XCFramework and link it to your target.

#### Usage (Swift)

```swift
import KiitCommonEnvs

// Parse an environment string
let env = Env.companion.parse(value: "qa1:qat")
print(env.key)    // "qa1:qat"
print(env.isQat)  // true

// Work with a collection
let envs = Envs.companion.defaults()
let live  = envs.select(name: "pro")
print(live.isPro) // true
```

---

### JS / TypeScript (npm)

Configure the GitHub Package Registry scope in `.npmrc`, then install.

```ini
# .npmrc
@slatekit:registry=https://npm.pkg.github.com
//npm.pkg.github.com/:_authToken=${KIIT_INSTALL_TOKEN}
```

```bash
npm install @slatekit/kiit-common-envs
```

```typescript
import { kiit } from "@slatekit/kiit-common-envs";

const { Env, Envs, EnvMode } = kiit.common.envs;

const envs = Envs.Companion.defaults(null);
const live = envs.select("pro");
console.log(live.isPro);          // true
console.log(live.current.key);    // "pro:pro"
```

---

## Setup

### Prerequisites

| Tool | Minimum version | Required for |
|---|---|---|
| JDK | 11 | JVM + Android build |
| Android SDK | compileSdk 36 | Android target |
| Xcode | latest stable | iOS XCFramework build |
| Node.js | 18+ | JS target, npm publish |
| npm | 9+ | JS publish |
| GitHub CLI (`gh`) | 2.x | iOS publish to GitHub Releases |

### Environment variables

| Variable | Purpose |
|---|---|
| `KIIT_INSTALL_ACTOR` | GitHub username — read packages from the registry |
| `KIIT_INSTALL_TOKEN` | GitHub PAT with `read:packages` — used by consumers |
| `KIIT_PUBLISH_ACTOR` | GitHub username — publish packages to the registry |
| `KIIT_PUBLISH_TOKEN` | GitHub PAT with `write:packages` + `repo` scopes — used by CI/CD (`repo` is needed to create/upload GitHub Release assets for iOS) |

Set these in your shell profile or export them before running Gradle or npm commands:

```bash
export KIIT_PUBLISH_ACTOR=your-github-username
export KIIT_PUBLISH_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx
```

---

## Clean

Remove all build outputs:

```bash
cd src/common/envs
./gradlew clean
```

---

## Build

Build all targets (JVM, Android, iOS, JS):

```bash
cd src/common/envs
./gradlew assemble
```

Build a specific target:

```bash
./gradlew :kiit-common-envs:compileKotlinJvm
./gradlew :kiit-common-envs:bundleAndroidMainAar
./gradlew :kiit-common-envs:compileKotlinIosArm64
./gradlew :kiit-common-envs:compileKotlinJs
```

### iOS XCFramework

Assemble a release XCFramework containing all three iOS slices (device arm64, simulator arm64, simulator x64):

```bash
cd src/common/envs
./gradlew :kiit-common-envs:assembleKiitCommonEnvsReleaseXCFramework
```

Build individual slices only (no XCFramework merge):

```bash
./gradlew :kiit-common-envs:compileKotlinIosArm64         # physical device
./gradlew :kiit-common-envs:compileKotlinIosSimulatorArm64 # Apple Silicon simulator
./gradlew :kiit-common-envs:compileKotlinIosX64            # Intel simulator
```

Output:

```
library/build/XCFrameworks/release/
  KiitCommonEnvs.xcframework/
    ios-arm64/
      KiitCommonEnvs.framework/
    ios-arm64_x86_64-simulator/
      KiitCommonEnvs.framework/
```

The JS production library and TypeScript definitions are written to:

```
library/build/dist/js/productionLibrary/
  kiit-common-envs-kiit-common-envs.js
  kiit-common-envs-kiit-common-envs.d.ts
  package.json
```

---

## Test

Run tests on JVM (fastest, runs on the host machine):

```bash
cd src/common/envs
./gradlew :kiit-common-envs:jvmTest
```

Run tests on JS via Node.js:

```bash
./gradlew :kiit-common-envs:jsNodeTest
```

Run all available tests:

```bash
./gradlew check
```

---

## Publish

All publish tasks require the following environment variables to be set:

```bash
export KIIT_PUBLISH_ACTOR=your-github-username
export KIIT_PUBLISH_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx   # write:packages scope
```

To publish every platform in one command:

```bash
./gradlew :kiit-common-envs:publishAllPublicationsToGitHubPackagesRepository \
          :kiit-common-envs:publishNpmToGitHubPackages
```

### JVM

Publishes `kiit-common-envs-jvm-{version}.jar` and `kiit-common-envs-jvm-{version}-sources.jar` to GitHub Package Registry as a Maven artifact.

```bash
cd src/common/envs
./gradlew :kiit-common-envs:publishJvmToGitHubPackages
```

| Published artifact | Description |
|---|---|
| `dev.kiit:kiit-common-envs-jvm:{version}` | JVM classes JAR |
| `dev.kiit:kiit-common-envs-jvm:{version}:sources` | Sources JAR |

### Android

Publishes `kiit-common-envs-android-{version}.aar` and a sources JAR to GitHub Package Registry as a Maven artifact.

```bash
cd src/common/envs
./gradlew :kiit-common-envs:publishAndroidToGitHubPackages
```

| Published artifact | Description |
|---|---|
| `dev.kiit:kiit-common-envs-android:{version}` | Android AAR |
| `dev.kiit:kiit-common-envs-android:{version}:sources` | Sources JAR |

### JS

Patches the generated `package.json` with the scoped name and registry, then publishes the npm package (including the `.d.ts` TypeScript definitions) to GitHub Package Registry.

```bash
cd src/common/envs
./gradlew :kiit-common-envs:publishNpmToGitHubPackages
```

| Published artifact | Description |
|---|---|
| `@slatekit/kiit-common-envs` | npm package with JS + TypeScript definitions |

The `.npmrc` auth file is written into the dist directory at publish time and is not committed to source control.

### iOS

The iOS publish pipeline runs four steps automatically:

1. **Assemble** — builds the XCFramework from all three iOS slices
2. **Zip** — produces `KiitCommonEnvs.xcframework.zip` in `library/build/spm/`
3. **Checksum** — computes the SHA-256 required by SPM's `binaryTarget`
4. **Upload** — creates the GitHub Release tag (if absent) and uploads the zip as a release asset

Requires `KIIT_PUBLISH_TOKEN` with both `write:packages` and `repo` scopes, and the [GitHub CLI](https://cli.github.com) (`gh`) on your `PATH`.

```bash
cd src/common/envs
export KIIT_PUBLISH_ACTOR=your-github-username
export KIIT_PUBLISH_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx   # write:packages + repo scopes

./gradlew :kiit-common-envs:publishIosToGitHubPackages
```

Individual steps (useful for debugging or partial runs):

```bash
# 1. Build and zip only
./gradlew :kiit-common-envs:zipXCFramework

# 2. Compute checksum only (depends on zip)
./gradlew :kiit-common-envs:computeXCFrameworkChecksum

# 3. Generate Package.swift only (depends on checksum)
./gradlew :kiit-common-envs:generatePackageSwift
```

#### Build outputs

```
library/build/spm/
  KiitCommonEnvs.xcframework.zip          # uploaded to GitHub Releases
  KiitCommonEnvs.xcframework.zip.sha256   # hex digest, consumed by generatePackageSwift
  Package.swift                           # reference copy

src/common/envs/
  Package.swift                           # committed to git — source of truth for consumers
```

`Package.swift` is regenerated on every publish run with the correct release URL and checksum for that version. Commit this file so consumers can always find the right values.

#### Published artifact

| Artifact | Location |
|---|---|
| `KiitCommonEnvs.xcframework.zip` | GitHub Release `v{version}` asset |
| `Package.swift` | `src/common/envs/Package.swift` in this repo |

#### Required GitHub PAT scopes

| Scope | Reason |
|---|---|
| `write:packages` | Publish Maven / npm packages |
| `repo` | Create GitHub Releases and upload release assets |
