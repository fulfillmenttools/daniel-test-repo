#!/usr/bin/env bash
set -Eeuo pipefail

appVersion="${1}"
stage="${2}"
appBucketFolderName="${3}"
releaseBucketUrl="gs://ocff-deployment-mobileapps/${appBucketFolderName}/releases/${appVersion}"

rawLs="$(gcloud storage ls "${releaseBucketUrl}")"
grepStageUrl=$(echo "${rawLs}" | grep "/${stage}" || echo "NOT_EXISTS")

if [[ "${grepStageUrl}" == "NOT_EXISTS" ]]; then
  echo "App ${appVersion} in ${stage} stage not available."
else
  echo "${stage} stage app found in ${releaseBucketUrl}"
  echo "DEBUG ls content: ${rawLs}"
  exit 1
fi
