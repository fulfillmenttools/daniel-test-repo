#!/usr/bin/env bash
set -Eeuo pipefail

appVersion="${1}"
stage="${2}"
appBucketFolderName="${3}"
releaseBucketUrl="gs://ocff-deployment-mobileapps/${appBucketFolderName}/releases/${appVersion}"

rawLs="$(gcloud storage ls "${releaseBucketUrl}")"
alphaUrl=$(echo "${rawLs}" | grep "/${stage}" || echo "NOT_EXISTS")

if [[ "${alphaUrl}" == "NOT_EXISTS" ]]; then
  echo "No application in ${stage} stage found in ${releaseBucketUrl}"
  echo "DEBUG - ls content: ${rawLs}"
  exit 1
else
  echo "Application with version ${appVersion} in ${stage} stage available."
fi
