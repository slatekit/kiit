# kiit-common-envs

Kotlin Multiplatform library for environment management. Provides a typed model for named environments (`loc`, `dev`, `qat`, `stg`, `pro`) and their modes (`Dev`, `Qat`, `Uat`, `Pro`, `Dis`), with parsing, selection, and predicate helpers.

**Targets:** JVM · Android · iOS (x64, arm64, simulatorArm64) · JS (browser + Node.js, TypeScript definitions included)

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
| Xcode | latest stable | iOS targets |
| Node.js | 18+ | JS target, npm publish |
| npm | 9+ | JS publish |

### Environment variables

| Variable | Purpose |
|---|---|
| `KIIT_INSTALL_ACTOR` | GitHub username — read packages from the registry |
| `KIIT_INSTALL_TOKEN` | GitHub PAT with `read:packages` — used by consumers |
| `KIIT_PUBLISH_ACTOR` | GitHub username — publish packages to the registry |
| `KIIT_PUBLISH_TOKEN` | GitHub PAT with `write:packages` — used by CI/CD |

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
