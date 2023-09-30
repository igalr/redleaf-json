#!/bin/sh

DT_URL="http://localapi.dependencytrack.org:8080"

eval `cat ./sbom-params.sh`

MODULE="redleaf-ishell"
VERSION=`git describe --tags`
mvn clean -Drevision=$VERSION
mvn org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom -Drevision=$VERSION

B64=`base64 target/sbom.xml | tr -d "\n\r "`

s=`echo $VERSION | awk -F"-" '{print $3 $4}'`

if [ "$s" = "" ]; then
  payload="{
    \"autoCreate\": true,
    \"projectName\": \"$MODULE\",
    \"projectVersion\": \"$VERSION\",
    \"bom\": \"$B64\"
  }"
else
  payload="{
    \"project\": \"$DT_PROJECT\",
    \"bom\": \"$B64\"
  }"

  curl -v -X "PATCH" "$DT_URL/api/v1/project/$DT_PROJECT" \
     -H 'Content-Type: application/json' \
     -H "X-API-Key: $DT_APIKEY" \
     -d "{ \"version\": \"$VERSION\" }"

fi

echo $payload > payload.json

curl -v -X "PUT" "$DT_URL/api/v1/bom" \
     -H 'Content-Type: application/json' \
     -H "X-API-Key: $DT_APIKEY" \
     -d @payload.json

rm payload.json

