# kiit-codes — Build & Publish Guide

All Gradle commands below are run from the **repository root** (`kiit/`), not from this folder.

---

## Install

### Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| JDK  | 17+     | `java -version` to verify |
| Android SDK | any | Required for `androidTarget` compilation |
| GPG  | 2.x     | `gpg --version`; must have the dev.kiit secret key imported |

Import the signing key if not already present:
```bash
gpg --import dev.kiit.seckey.asc
# Verify
gpg --list-secret-keys --keyid-format LONG
```

---

## Setup

Choose **one** of the two approaches below. Both are equivalent — pick whichever fits your workflow.

### Option A — `~/.gradle/gradle.properties` (recommended for local dev)

Add the following to `~/.gradle/gradle.properties` (create the file if it does not exist):

```properties
# Maven Central credentials (portal token — not your account password)
mavenCentralUsername=<portal-token-username>
mavenCentralPassword=<portal-token-password>

# GPG signing via system keyring
signing.gnupg.keyName=<full-key-id>
signing.gnupg.passphrase=<passphrase>
```

With this in place, publishing commands need no extra flags:
```bash
./gradlew :core-codes:publishAndReleaseToMavenCentral
```

### Option B — Shell environment variables (recommended for CI / scripted runs)

Export these variables in your shell profile (e.g. `~/.zshrc`) or in your CI secrets:

| Shell variable      | Gradle property             |
|---------------------|-----------------------------|
| `KIIT_MAVEN_USER`   | `mavenCentralUsername`      |
| `KIIT_MAVEN_PSWD`   | `mavenCentralPassword`      |
| `KIIT_MAVEN_GPGNAME`| `signing.gnupg.keyName`     |
| `KIIT_MAVEN_GPGPASS`| `signing.gnupg.passphrase`  |

Pass them as `-P` flags because dots in the property names are not valid bash variable names:

```bash
./gradlew :core-codes:publishAndReleaseToMavenCentral \
    -Psigning.gnupg.keyName=$KIIT_MAVEN_GPGNAME \
    -Psigning.gnupg.passphrase=$KIIT_MAVEN_GPGPASS \
    -PmavenCentralUsername=$KIIT_MAVEN_USER \
    -PmavenCentralPassword=$KIIT_MAVEN_PSWD
```

---

## CI — GitHub Actions

CI runners are ephemeral — there is no persistent GPG keyring. The secret key must be imported at the start of every run.

### 1. Repository secrets

Add these five secrets under `Settings → Secrets and variables → Actions`:

| Secret name           | Value |
|-----------------------|-------|
| `KIIT_GPG_SECRET_KEY` | Base64-encoded GPG secret key (see below) |
| `KIIT_MAVEN_GPGNAME`  | GPG key ID |
| `KIIT_MAVEN_GPGPASS`  | GPG passphrase |
| `KIIT_MAVEN_USER`     | Maven Central portal token username |
| `KIIT_MAVEN_PSWD`     | Maven Central portal token password |

Encode your secret key for the `KIIT_GPG_SECRET_KEY` secret (run locally):
```bash
gpg --armor --export-secret-keys <your-key-id> | base64 | pbcopy
```

### 2. Workflow

```yaml
name: Publish kiit-codes

on:
  workflow_dispatch:
  push:
    tags: [ 'codes-v*' ]

jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Import GPG key
        run: |
          echo "${{ secrets.KIIT_GPG_SECRET_KEY }}" | base64 --decode | gpg --import
          gpg --list-secret-keys --keyid-format LONG

      - name: Publish to Maven Central
        run: |
          ./gradlew :core-codes:publishAndReleaseToMavenCentral \
            -Psigning.gnupg.keyName=${{ secrets.KIIT_MAVEN_GPGNAME }} \
            -Psigning.gnupg.passphrase=${{ secrets.KIIT_MAVEN_GPGPASS }} \
            -PmavenCentralUsername=${{ secrets.KIIT_MAVEN_USER }} \
            -PmavenCentralPassword=${{ secrets.KIIT_MAVEN_PSWD }}
```

### 3. GPG pinentry on Linux (if signing hangs)

GPG 2.1+ on Linux defaults to a GUI pinentry dialog which blocks in CI. If the passphrase is silently rejected, add this before the import step:

```bash
echo "pinentry-mode loopback" >> ~/.gnupg/gpg.conf
echo "allow-loopback-pinentry" >> ~/.gnupg/gpg-agent.conf
gpgconf --kill gpg-agent
```

Ubuntu runners usually work without this because Gradle passes `--batch --pinentry-mode loopback` automatically when shelling out to `gpg`.

---

## Build

```bash
# Stop the Gradle daemon (useful after changing env vars or upgrading Gradle)
./gradlew --stop

# Clean build outputs
./gradlew :core-codes:clean

# Compile all targets (JVM, Android, JS, iOS)
./gradlew :core-codes:build

# Compile only — no tests
./gradlew :core-codes:assemble
```

---

## Test (local)

```bash
# JVM tests (fastest — runs on the local JVM)
./gradlew :core-codes:jvmTest

# All platform tests
./gradlew :core-codes:allTests

# Publish to Maven Local (~/.m2) for integration testing against other modules
./gradlew :core-codes:publishToMavenLocal
```

Maven Local artifacts are saved to:
```
~/.m2/repository/dev/kiit/kiit-codes/
```

---

## Publish

### Publish to Maven Local

No credentials required.

```bash
./gradlew :core-codes:publishToMavenLocal
```

### Publish to Maven Central — Option A (gradle.properties)

Requires `~/.gradle/gradle.properties` populated per the Setup section above.

```bash
./gradlew :core-codes:publishAndReleaseToMavenCentral
```

### Publish to Maven Central — Option B (env vars)

```bash
./gradlew :core-codes:publishAndReleaseToMavenCentral \
    -Psigning.gnupg.keyName=$KIIT_MAVEN_GPGNAME \
    -Psigning.gnupg.passphrase=$KIIT_MAVEN_GPGPASS \
    -PmavenCentralUsername=$KIIT_MAVEN_USER \
    -PmavenCentralPassword=$KIIT_MAVEN_PSWD
```

### Sign artifacts only (dry-run check)

```bash
./gradlew :core-codes:signKotlinMultiplatformPublication
```

---

## FAQ

### "Cannot perform signing task — has no configured signatory"

The signing plugin could not find the key. Check in order:

1. Verify the key is in the GPG keyring:
   ```bash
   gpg --list-secret-keys --keyid-format LONG
   ```
2. Confirm `signing.gnupg.keyName` matches the full key ID shown above.
3. If using `~/.gradle/gradle.properties`, make sure the file is saved and the Gradle daemon is restarted (`./gradlew --stop`).
4. Do **not** use `signAllPublications()` inside `mavenPublishing {}` — it uses `providers.gradleProperty` internally which does not reliably read `~/.gradle/gradle.properties` (Gradle issue #23572). Use the `signing {}` block directly with `useGpgCmd()`.

### "GPG prompts for the wrong key's passphrase"

`signing.gnupg.keyName` is pointing at the wrong key ID. Run `gpg --list-secret-keys --keyid-format LONG`, find the `dev.kiit` key, and update the property to its full fingerprint.

### "Could not read PGP secret key"

Do not use in-memory key signing (`useInMemoryPgpKeys`). The stripped base64 format is fragile and Bouncycastle can silently produce a null signatory. Use `useGpgCmd()` instead.

### Maven Central portal returns 401

The `mavenCentralUsername` and `mavenCentralPassword` are **portal token** credentials, not your account login. Generate a token at [central.sonatype.com](https://central.sonatype.com) under Account → Generate User Token.

### How do I bump the version?

Edit the `coordinates(...)` block in `build.gradle.kts`:
```kotlin
coordinates(
    groupId    = "dev.kiit",
    artifactId = "kiit-codes",
    version    = "0.1.2"   // ← bump here
)
```
