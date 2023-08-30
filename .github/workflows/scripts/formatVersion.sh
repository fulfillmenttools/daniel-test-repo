#!/usr/bin/env bash
set -Eeuo pipefail

input="${1}"

semVer="$(echo "${input}" | cut -d'-' -f1-3)"
suffix="$(echo "${input}" | cut -d'-' -f4)"

echo "${semVer//-/.}-${suffix}"
