
MAJOR=$1
MINOR=$2
PATCH=$3
BUILD=$4
TAG=$5
BRANCH=srv_${MAJOR}_${MINOR}_${PATCH}_${TAG}
VERSION=${MAJOR}.${MINOR}.${PATCH}.${BUILD}
FULL_TAG=srv_${MAJOR}_${MINOR}_${PATCH}_${BUILD}_${TAG}
echo ${BRANCH}
echo ${VERSION}

$(git tag ${FULL_TAG})
$(git push --tags)
exit 0