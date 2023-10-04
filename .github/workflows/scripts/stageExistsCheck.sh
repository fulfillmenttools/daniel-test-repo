#!/usr/bin/env bash
set -Eeuo pipefail

# EXAMPLE: stageExistsCheck.sh 1-7-0-29 alpha appname

appVersion="${1}"
stage="${2}"
packageAppName="${3}"
releaseBucketUrl="gs://ocff-deployment-mobileapps/${packageAppName}/android/releases"

rawLs="$(gcloud storage ls "${releaseBucketUrl}")"
stageExists=$(echo "${rawLs}" | grep "${appVersion}-${stage}" || echo "NOT_EXISTS")

if [[ "${stageExists}" == "NOT_EXISTS" ]]; then
  echo "No application ${appVersion}-${stage} found in ${releaseBucketUrl}."
  exit 1
else
  echo "Application ${appVersion}-${stage} found in ${releaseBucketUrl}."
fi
