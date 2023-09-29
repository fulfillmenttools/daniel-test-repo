#!/usr/bin/env bash
set -Eeuo pipefail

# EXAMPLE: stageDoesntExistsCheck.sh 1-7-0-29 alpha appname

appVersion="${1}"
stage="${2}"
packageAppName="${3}"
releaseBucketUrl="gs://ocff-deployment-mobileapps/${packageAppName}/android/releases/${packageAppName}-${stage}"

if [ -d "$releaseBucketUrl" ]; then
  echo "Application in ${stage} stage found in ${releaseBucketUrl}"
  exit 1
else
  echo "No application with version ${appVersion} in ${stage} stage available."
fi
