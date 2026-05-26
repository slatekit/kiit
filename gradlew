#!/bin/sh
# Delegates to the Gradle build rooted at src/.
exec "$(dirname "$0")/src/gradlew" -p "$(dirname "$0")/src" "$@"
