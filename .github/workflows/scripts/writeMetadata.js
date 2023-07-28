const fs = require("fs")

const {VERSION_CODE, VERSION_NAME, BUILD_DATE, METADATA_FILE} = process.env

if (!VERSION_CODE) {
  throw new Error("VERSION_CODE not given!")
}
if (!VERSION_NAME) {
  throw new Error("VERSION_NAME not given!")
}
if (!BUILD_DATE) {
  throw new Error("BUILD_DATE not given!")
}
if (!METADATA_FILE) {
  throw new Error("METADATA_FILE not given!")
}

let metadataFileContent = JSON.stringify(
  {
    versionCode: VERSION_CODE,
    appVersion: VERSION_NAME,
    buildDate: BUILD_DATE
  }
);
fs.writeFileSync(
  METADATA_FILE,
  metadataFileContent
)

console.log(`Written content ${metadataFileContent} to file ${METADATA_FILE}`)
