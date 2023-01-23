
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

# git tag -a v2.10.1 -m "New release"
# git release v2.10.1 -m "Kiit Release 2.10.1"
# gh release create v2.10.1 --notes "Kiit Release 2.10.1"
# git push --tags
$(git tag -a ${TAG_NAME} -m "New release")
$(git push --tags)
exit 0