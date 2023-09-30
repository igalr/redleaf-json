#!/bin/sh

MODULE=redleaf-json
VERSION=`git describe --tags`
echo $VERSION > src/main/resources/.version
LIFECYCLE=package

if [ -n "$1" ]; then
	LIFECYCLE=$1
fi

echo "{\"version\": \"$VERSION\", \"module\": \"$MODULE\"}" > src/main/resources/.version.$MODULE
mvn clean $LIFECYCLE -Drevision=$VERSION
