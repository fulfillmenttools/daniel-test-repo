#!/usr/bin/env bash
set -Eeuo pipefail

releaseVersion="${1}"
stage="${2:-alpha}"
bucketPrefix="${3:-gs://ocff-deployment-mobileapps/hubis-app}"

releaseBucketUrl="${bucketPrefix}/releases/${releaseVersion}"

rawLs="$(gcloud storage ls "${releaseBucketUrl}")"
alphaUrl=$(echo "${rawLs}" | grep "/${stage}" || echo "NOT_EXISTS")
if [[ "${alphaUrl}" == "NOT_EXISTS" ]]; then
  echo "no ${stage} version found in ${releaseBucketUrl}"
  echo "DEBUG ls content: ${rawLs}"
  exit 1
fi

echo "${stage}Url to use is: ${alphaUrl}"
