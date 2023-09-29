#!/usr/bin/env bash
set -Eeuo pipefail

# EXAMPLE: stageDoesntExistsCheck.sh 1-7-0-29 alpha appname

appVersion="${1}"
stage="${2}"
packageAppName="${3}"
releaseBucketUrl="gs://ocff-deployment-mobileapps/${packageAppName}/android/releases/${appVersion}-${stage}"

rawLs="$(gcloud storage ls "${releaseBucketUrl}")"
stageExists=$(echo "${rawLs}" | grep "${appVersion}-${stage}" || echo "NOT_EXISTS")

if [[ "${stageExists}" == "NOT_EXISTS" ]]; then
  echo "No application with version ${appVersion} in ${stage} stage available."
else
  echo "Application in ${stage} stage found in ${releaseBucketUrl}"
  echo "DEBUG - ls content: ${rawLs}"
  exit 1
fi
