#!/bin/bash

# Creates a release package for slatekit.
VERSION=0.9.6
SLATE_HOME=~/git/slatekit
SLATE_DIST=${SLATE_HOME}/dist/slatekit/kotlin/releases/${VERSION}
SLATE_VERSION=${SLATE_HOME}/dist/slatekit/kotlin/releases/${VERSION}
SLATE_SRC=${SLATE_HOME}/src/lib/kotlin 

mkdir $SLATE_DIST
mkdir $SLATE_DIST/bin
mkdir $SLATE_DIST/conf
mkdir $SLATE_DIST/ext
mkdir $SLATE_DIST/lib
mkdir $SLATE_DIST/licenses
mkdir $SLATE_DIST/samples

# Copy jar files to bin
cp ${SLATE_SRC}/dist/*.jar ${SLATE_VERSION}/bin/

# Copy the sample apps
cp $SLATE_HOME/src/apps/kotlin/slatekit-sampleapps/sampleapp-core/build/libs/slatekit-sampleapp-core.jar      $SLATE_DIST/bin/
cp $SLATE_HOME/src/apps/kotlin/slatekit-sampleapps/sampleapp-batch/build/libs/slatekit-sampleapp-batch.jar    $SLATE_DIST/bin/
cp $SLATE_HOME/src/apps/kotlin/slatekit-sampleapps/sampleapp-cli/build/libs/slatekit-sampleapp-cli.jar        $SLATE_DIST/bin/
cp $SLATE_HOME/src/apps/kotlin/slatekit-sampleapps/sampleapp-server/build/libs/slatekit-sampleapp-server.jar  $SLATE_DIST/bin/

# Copy all dependent jars
cp $SLATE_HOME/lib/ext/json/*.jar       $SLATE_DIST/ext/ 
cp $SLATE_HOME/lib/ext/mysql/*.jar      $SLATE_DIST/ext/      
cp $SLATE_HOME/lib/ext/kotlin/*.jar     $SLATE_DIST/lib/    
cp $SLATE_HOME/lib/ext/spark/2.6/*.jar  $SLATE_DIST/lib/

# Copy the sample app resources/conf files 
cp -a $SLATE_HOME/src/apps/kotlin/slatekit-sampleapps/sampleapp-batch/src/main/resources/.    $SLATE_DIST/conf/sampleapp-batch/
cp -a $SLATE_HOME/src/apps/kotlin/slatekit-sampleapps/sampleapp-server/src/main/resources/.   $SLATE_DIST/conf/sampleapp-server/
cp -a $SLATE_HOME/src/apps/kotlin/slatekit-sampleapps/sampleapp-cli/src/main/resources/.      $SLATE_DIST/conf/sampleapp-shell/ 

# Copy the script files to run the sample apps
cp $SLATE_HOME/scripts/samples/kotlin/*   $SLATE_DIST/

# Copy the license files
cp  $SLATE_HOME/licenses/kotlin/*.txt   $SLATE_DIST/licenses/
cp  $SLATE_HOME/doc/kotlin/LICENSE.txt  $SLATE_DIST/LICENSE.txt
cp  $SLATE_HOME/doc/kotlin/README.txt   $SLATE_DIST/README.txt 

