#!/usr/bin/env bash
set -Eeuo pipefail

# EXAMPLE: versionDashes=1-7-0-46 // {major}-{minor}-{patch}-{releaseAutoIncrement}
versionDashes="${1}"

majorMinorPatch="$(echo "${versionDashes}" | cut -d'-' -f1-3)"
releaseAutoIncrement="$(echo "${versionDashes}" | cut -d'-' -f4)"

echo "${majorMinorPatch//-/.}-${releaseAutoIncrement}"
