# Kiit Framework — Claude Code Context

## About Kiit

Kiit is an open-source Kotlin Multiplatform (KMP) framework developed and maintained by CodeHelix. It is a monorepo of modular libraries organized into categories, designed for sharing code across server (JVM/Kotlin), Android, JavaScript/TypeScript, and iOS projects.

- **Domain**  : kiit.dev
- **GitHub**  : https://github.com/slatekit/kiit
- **Organization**: CodeHelix

---

## Definition Files — Single Source of Truth

The complete registry of categories, libraries, platform targets, migration state, and metadata is defined in the `./definition/` folder at the repo root.

```

./definition/
├── kiit-framework-definition.json    ← root: org, enums, category registry
└── categories/
    ├── core.json
    ├── parse.json
    ├── app.json
    ├── infra.json
    ├── resilience.json
    ├── data.json
    ├── services.json
    ├── vendor.json
    ├── connect.json
    └── internal.json
```

**Do not add, rename, or remove libraries or categories in this document.**
Make all structural changes in the definition files. This document contains
rules and conventions only — the definition files contain the registry.

All skills read from these files. When performing any task involving a specific
library or category, always read the relevant category JSON first.

### Target resolution rule

A library's effective platform targets are resolved as follows:
- If `library.targets` is present → use `library.targets`
- If `library.targets` is absent  → inherit `category.defaultTargets`

Skills and scripts must always apply this rule. Never assume one or the other
is always present.

---

## Current State of the Framework

The framework is currently in a pre-migration state. All libraries exist but do not yet
follow the naming conventions, structure, or build configuration described in this document.

| Concern                | Current State                                                  |
|------------------------|----------------------------------------------------------------|
| Gradle DSL             | Groovy (`build.gradle`)                                        |
| Kotlin version         | 1.9.x                                                          |
| Ktor version           | 2.x.x (tied to Kotlin 1.8.x)                                   |
| Gradle version         | 8.x                                                            |
| Version catalog        | Not in use — versions hardcoded in build files                 |
| Multiplatform (KMP)    | Not configured — JVM only                                      |
| Folder structure       | Does not match desired category structure                      |
| Artifact IDs           | Prefixed with `kiit-{library}` not `kiit-{category}-{library}` |
| Package names          | Rooted at `kiit.*` not `kiit.{category}.{library}*`            |
| Publishing             | GitHub Packages only                                           |
| ktlint                 | Not configured                                                 |
| Android Gradle Plugin  | 7.x                                                            |

The `migration` block in each library's category JSON captures the current artifact ID,
current package name, current folder location, and migration status for every library.

---

## Desired / Future State of the Framework

| Concern                | Desired State                                               |
|------------------------|-------------------------------------------------------------|
| Gradle DSL             | Kotlin (`build.gradle.kts`)                                 |
| Kotlin version         | 2.3.21                                                      |
| Ktor version           | 3.5.0                                                       |
| Gradle version         | 8.13                                                        |
| Version catalog        | `gradle/libs.versions.toml` — single source for versions    |
| Multiplatform (KMP)    | Configured per platform target matrix in definition files   |
| Folder structure       | Matches category structure under `src/{category}/{library}` |
| Artifact IDs           | Follow `kiit-{category}-{library}` convention               |
| Package names          | Rooted at `kiit.*` following naming conventions             |
| Publishing             | Maven Central (stable), GitHub Packages (pre-release)       |
| ktlint                 | Configured and enforced across all modules                  |
| Android Gradle Plugin  | 8.2.0                                                       |

Versions are managed centrally in `gradle/libs.versions.toml`. No module-level
`build.gradle.kts` should hardcode a library version — always reference the catalog.

---

## Naming Conventions

### Maven / Gradle

| Element      | Rule                                                                                   | Example             |
|--------------|----------------------------------------------------------------------------------------|---------------------|
| `groupId`    | Reverse domain, all lowercase, stable forever. Shared by all kiit libraries.           | `dev.kiit`          |
| `artifactId` | `kiit-{category}-{library}`, lowercase, hyphen-separated.                              | `kiit-infra-queues` |
|              | `core` libraries omit the category segment.                                            | `kiit-result`       |
| Version      | Semantic versioning — `MAJOR.MINOR.PATCH`                                              | `1.0.0`             |

**Hard rules:**
- Never include platform suffixes (`-android`, `-jvm`, `-js`) in the base `artifactId`.
  The KMP Gradle plugin appends these automatically on publish.
- Never use uppercase, underscores, or dots in an `artifactId`.
- All libraries in all categories share the same `groupId`: `dev.kiit`.

### Kotlin Package Names

| Rule                                                                                   | Example                 |
|----------------------------------------------------------------------------------------|-------------------------|
| Mirror the `artifactId`: drop `kiit-`, replace hyphens with dots                       | `kiit.infra.queues`     |
| All lowercase, dot-separated, no hyphens                                               | `kiit.data.repo`        |
| Do NOT use `dev.` prefix — `dev.kiit` is a Maven registry concern, not a namespace     | `kiit.result` ✓         |
| Vendor libraries use dots for sub-segments                                             | `kiit.vendor.aws.sqs`   |
| Core libraries use a single segment after `kiit`                                       | `kiit.result`           |

### npm / JavaScript

| Rule                                                                   | Example              |
|------------------------------------------------------------------------|----------------------|
| Scoped under `@kiit`                                                   | `@kiit/result`       |
| Drop the `kiit-` prefix from the artifact ID, keep hyphens             | `@kiit/infra-queues` |
| Only publish multiplatform libraries (core, infra, app-envs, app-info) | —                    |

### iOS / Swift Package Manager

| Rule                                                                    | Example            |
|-------------------------------------------------------------------------|--------------------|
| `baseName` is PascalCase concatenation of all artifact ID segments      | `KiitInfraQueues`  |
| No hyphens, no dots — must be a valid Swift identifier                  | `KiitResult` ✓     |
| `baseName` must match exactly across all iOS slices                     | —                  |
| `isStatic = true` unless there is a specific reason for dynamic linking | —                  |
| Only multiplatform libraries produce an XCFramework                     | —                  |

### Naming Example — `infra / queues`

| Dimension      | Value                  |
|----------------|------------------------|
| `groupId`      | `dev.kiit`             |
| `artifactId`   | `kiit-infra-queues`    |
| Kotlin package | `kiit.infra.queues`    |
| npm            | `@kiit/infra-queues`   |
| iOS `baseName` | `KiitInfraQueues`      |

### Naming Example — `core / result`

| Dimension      | Value           |
|----------------|-----------------|
| `groupId`      | `dev.kiit`      |
| `artifactId`   | `kiit-result`   |
| Kotlin package | `kiit.result`   |
| npm            | `@kiit/result`  |
| iOS `baseName` | `KiitResult`    |

---

## Category Overview

Full category definitions, library lists, platform targets, and migration metadata
are in `.kiit/definition/categories/{category}.json`. The summary below is for
orientation only.

| Category     | Artifact prefix            | Default targets           | Published |
|--------------|----------------------------|---------------------------|-----------|
| `core`       | `kiit-{library}`           | all platforms             | Yes       |
| `parse`      | `kiit-parse-{library}`     | all platforms             | Yes       |
| `app`        | `kiit-app-{library}`       | mixed (see category file) | Yes       |
| `infra`      | `kiit-infra-{library}`     | all platforms             | Yes       |
| `resilience` | `kiit-resilience-{lib}`    | all platforms             | Yes       |
| `data`       | `kiit-data-{library}`      | JVM · Android             | Yes       |
| `services`   | `kiit-services-{library}`  | JVM only                  | Yes       |
| `vendor`     | `kiit-vendor-{library}`    | JVM only                  | Yes       |
| `connect`    | `kiit-connect-{library}`   | JVM only                  | Yes       |
| `internal`   | `kiit-internal-{library}`  | JVM only                  | No        |

### Core membership test

A library belongs in `core` if **both** conditions are true:
1. Other kiit libraries depend on it internally (not just consumers).
2. It only depends on other kiit `core` dependencies or ideally none at all.
3. It can NOT depend on any kiit dependencies in other categories.

If a library fails these conditions, it belongs in another category.

---

## Dependency Direction Rules

Dependencies must only flow in this direction — never in reverse, never circular:

```
core       → (no kiit deps)
parse      → core
app        → core, parse
infra      → core
resilience → core
data       → core, parse
services   → core, app, infra, data, resilience
vendor     → core, infra
connect    → (any two or more of the above)
internal   → (anything — never published)
```

**Hard rules:**
- `core` libraries must have zero kiit dependencies.
- `infra` must never depend on `vendor` — never couple abstraction to implementation.
- `vendor` must never depend on `services` or `connect`.
- Circular dependencies between any two libraries are never permitted.

---

## Build Infrastructure

### Version catalog

All dependency and plugin versions are managed in `gradle/libs.versions.toml`.
No `build.gradle.kts` file should hardcode a version string — always use catalog references.

```toml
[versions]
kotlin = "2.3.21"
ktor   = "3.5.0"
gradle = "8.13"
agp    = "8.2.0"

[libraries]
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }

[plugins]
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
```

### Gradle wrapper

The Gradle version is set in `gradle/wrapper/gradle-wrapper.properties`.
Update via: `./gradlew wrapper --gradle-version {version}`

---

## Documentation Markers

Example source files use XML-style comment markers to tag sections for README generation.
The tag format is:

```kotlin
//<doc:examples>
// ... code ...
//</doc:examples>
```

Valid tags (defined in `kiit-framework-definition.json` under `docTags`):

| Tag                  | Purpose                                                    |
|----------------------|------------------------------------------------------------|
| `import_required`    | Imports the consumer must add to use the library           |
| `import_examples`    | Additional imports used only within the example code       |
| `examples`           | Primary usage examples                                     |
| `examples_support`   | Helper and support functions used by the examples          |
| `output`             | Expected console output, wrapped in Hugo highlight block   |

Example files currently reference `slatekit.*` package names. The `kiit-docs-generate-readme`
skill must translate these to `kiit.*` equivalents using `migration.currentPackage` →
`library.package` mapping from the category JSON before writing the README.

---

## Contribution Tiers

| Tier      | Repo                              | groupId        | Maintained by  | Published to          |
|-----------|-----------------------------------|----------------|----------------|-----------------------|
| Official  | `github.com/slatekit/kiit`        | `dev.kiit`     | CodeHelix      | Maven Central         |
| Extension | `github.com/slatekit/kiit-ext`    | `dev.kiit.ext` | Contributor    | Maven Central / GH    |
| Community | Author's own repo                 | Author's own   | Author         | Author's choice       |

---

## Open-Source Publishing Requirements

Every artifact published to Maven Central must have:

- [ ] Sources JAR (`-sources.jar`)
- [ ] Dokka JAR (`-javadoc.jar`)
- [ ] GPG signature on all artifacts
- [ ] POM with: `name`, `description`, `url`, `licenses`, `developers`, `scm`
- [ ] Semantic version tag on the git commit

Pre-release artifacts (alpha, beta, RC) → GitHub Packages only.
Stable releases → Maven Central.

---

## API Stability Annotations

```kotlin
@RequiresOptIn(
    message = "This kiit API is experimental. It may change without notice.",
    level = RequiresOptIn.Level.WARNING
)
annotation class ExperimentalKiitApi

@RequiresOptIn(
    message = "This is an internal kiit API. Do not use in consumer code.",
    level = RequiresOptIn.Level.ERROR
)
annotation class InternalKiitApi
```

| Category     | Default stability |
|--------------|-------------------|
| `core`       | stable            |
| `parse`      | stable            |
| `app`        | experimental      |
| `infra`      | stable            |
| `resilience` | experimental      |
| `data`       | stable            |
| `services`   | experimental      |
| `vendor`     | stable            |
| `connect`    | experimental      |

---

## Platform-Specific Notes

### iOS / SPM
- XCFramework `baseName` must be identical across `iosArm64`, `iosSimulatorArm64`, and `iosX64`.
- Kotlin packages are stripped at the Swift boundary — class names must be unique within a framework.
- Use `@ObjCName` to control Swift-visible names where the default is awkward.
- `suspend` functions require either the `KMP-NativeCoroutines` library or the `skie` plugin.

### JS / TypeScript
- Always use the IR compiler: `js(IR)`.
- Always declare `binaries.library()` — never `binaries.executable()` for library modules.
- Annotate all public API declarations with `@JsExport`.
- Call `generateTypeScriptDefinitions()` to emit `.d.ts` files.

### JVM / Java Interop
- Kiit is Kotlin-first. Java compatibility is a courtesy, not a requirement.
- Use `@JvmStatic` on companion object members.
- Use `@JvmOverloads` on functions with default parameters.
- Use `@JvmName` to resolve accessor name conflicts.
- Avoid exposing `suspend` functions at the Java API boundary without a wrapper.
