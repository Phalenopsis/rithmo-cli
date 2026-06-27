#!/usr/bin/env bash

set -e

cd "$(git rev-parse --show-toplevel)"

TAG_VERSION="$1"

if [ -z "$TAG_VERSION" ]; then
echo "Usage: check-release.sh <version>"
exit 1
fi

echo "🔎 Validating release $TAG_VERSION..."

POM_VERSION=$(mvn help:evaluate \
-Dexpression=project.version \
-q \
-DforceStdout)

#
# No SNAPSHOT releases
#
if [[ "$POM_VERSION" == *-SNAPSHOT ]]; then
echo "❌ pom.xml contains a SNAPSHOT version ($POM_VERSION)."
exit 1
fi

#
# Tag == pom.xml
#
if [[ "$TAG_VERSION" != "$POM_VERSION" ]]; then
echo "❌ Tag version ($TAG_VERSION) does not match pom.xml version ($POM_VERSION)."
exit 1
fi

#
# Changelog contains released version
#
if ! grep -q "^## \[$TAG_VERSION\]" CHANGELOG.md; then
echo "❌ CHANGELOG.md does not contain section [$TAG_VERSION]."
exit 1
fi

#
# Changelog no longer contains snapshot section
#
if grep -q "^## \[$TAG_VERSION-SNAPSHOT\]" CHANGELOG.md; then
echo "❌ CHANGELOG.md still contains [$TAG_VERSION-SNAPSHOT]."
exit 1
fi

echo "✅ Release metadata validated."