#!/usr/bin/env bash
set -Eeuo pipefail

input="${1}"

onlyVersion="$(echo "${input}" | cut -d'_' -f2)"
semVer="$(echo "${onlyVersion}" | cut -d'-' -f1-3)"
suffix="$(echo "${onlyVersion}" | cut -d'-' -f4)"

echo "${semVer//-/.}-${suffix}"
