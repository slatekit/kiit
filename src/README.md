# Kiit — Build & Quality Guide

All commands are run from this directory (`src/`).

---

## Quality Tooling

| Tool | Purpose | Config |
|------|---------|--------|
| **ktlint** | Code formatting and style | `.editorconfig` |
| **detekt** | Static analysis | `{module}/detekt.yml` |
| **dokka** | KDoc → HTML documentation | No config file — defaults |

---

## ktlint

```bash
# Check for style violations (fails on any violation)
./gradlew :core-codes:ktlintCheck

# Auto-fix all violations that can be fixed automatically
./gradlew :core-codes:ktlintFormat

# Run as part of the full check lifecycle
./gradlew :core-codes:check
```

Reports: `{module}/build/reports/ktlint/`

---

## .editorconfig

Located at `src/.editorconfig`. Applies to all modules under `src/`.

Key rules configured:

| Rule | Setting |
|------|---------|
| Indent | 4 spaces |
| Wildcard imports | disabled |
| Trailing comma on declaration site | disabled |
| Force multiline params when count ≥ | 4 |
| `Example_*.kt` filename rule | disabled (doc-generation convention) |

No Gradle commands — editors and ktlint pick this up automatically.

---

## detekt

```bash
# Run static analysis across all configured source sets
./gradlew :core-codes:detekt

# Run analysis per source set / target
./gradlew :core-codes:detektMetadataCommonMain
./gradlew :core-codes:detektJsMain
./gradlew :core-codes:detektIosArm64Main
./gradlew :core-codes:detektJvmMain          # experimental — includes type resolution

# Generate a full default config to explore all available rules
./gradlew :core-codes:detektGenerateConfig
```

Config: `{module}/detekt.yml`
Reports: `{module}/build/reports/detekt/detekt.html`

---

## dokka

```bash
# Generate HTML documentation
./gradlew :core-codes:dokkaHtml

# Output: {module}/build/dokka/html/index.html
```

> `dokkaJavadoc` is not supported for KMP projects — do not use it.
> The `-javadoc.jar` required for Maven Central is generated automatically by the
> Vanniktech publish plugin when dokka is applied.

---

## Run All Checks

```bash
# ktlint + detekt + tests in one pass
./gradlew :core-codes:check
```
