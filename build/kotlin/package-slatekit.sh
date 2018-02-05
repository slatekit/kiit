#!/bin/bash

# Initialize variables
COLOR_ERR='\033[0;31m'
COLOR_OK='\033[0;32m'
COLOR_OFF='\033[0m' 

VERSION=0.9.8
APP_ENV=dev


showError(){
	echo -e "${COLOR_ERR}$1 ${COLOR_OFF}"
}

showHelp() {
	echo -e "\n"

	echo -e "ARGS:\n"
	echo -e "  -ver: the version number"
	echo -e "        ! | string | default ( 0.9.8 ) | 1.0.0\n"
	echo -e "  -env: the environment to package"
	echo -e "        ? optional | string | default ( dev ) | values( 'dev|qa|beta|prod' )\n"
	echo -e "  -log: whether to log details"
	echo -e "        ? optional | bool   | default ( no  ) | values ( 'yes|no' )\n"
	echo -e "\n"

	echo -e "EXAMPLES:\n"
	echo -e "  1. ./package.sh 1.0.0"
	echo -e "  2. ./package.sh 1.0.0 qa"
	echo -e "  3. ./package.sh 1.0.0 qa yes\n"
}


showSettings(){
	echo "VARS:"
	echo "env      : $APP_ENV"
	echo "jar      : $APP_JAR"
	echo "home     : $APP_HOME" 
	echo "build    : $APP_BUILD" 

	echo "srv home : $APP_SERVER_HOME"
	echo "srv src  : $APP_SRC"
	echo "temp     : $APP_TEMP"

	echo "dist     : $APP_DIST"
	echo "rels     : $APP_RELS"
	echo "version  : $APP_VERSION"
}


createDirs(){

	# Main release dirs
	mkdir $APP_TEMP
	mkdir $APP_DIST
	mkdir $APP_RELS
	mkdir $APP_VERSION
	mkdir $APP_VERSION/conf/
	mkdir $APP_SERVER_HOME/lib

	# Directories for version
	mkdir $APP_VERSION
	mkdir $APP_VERSION/bin
	mkdir $APP_VERSION/conf
	mkdir $APP_VERSION/ext
	mkdir $APP_VERSION/lib
	mkdir $APP_VERSION/licenses
	mkdir $APP_VERSION/samples
}


createBuild(){
	cd $APP_SERVER_HOME
	gradle clean
	gradle build 
	gradle copyAllJars

	cd $APP_BUILD
}


createStamp(){
	STAMP_SHA=$(git rev-parse HEAD)
	STAMP_BRANCH=$(git rev-parse --abbrev-ref HEAD)
	STAMP_DATE=$(date +"%Y-%m-%d_%H-%M-%S")
	STAMP_VERSION=$VERSION
	echo "build.commit = $STAMP_SHA"         
	echo "build.date = $STAMP_DATE"       
	echo "build.version = $STAMP_VERSION"
	echo "build.branch = $STAMP_BRANCH"
	echo "build = true"                   >  $APP_VERSION/conf/build.conf
	echo "build.commit = $STAMP_SHA"      >> $APP_VERSION/conf/build.conf
	echo "build.branch = $STAMP_BRANCH"   >> $APP_VERSION/conf/build.conf 
	echo "build.date = $STAMP_DATE"       >> $APP_VERSION/conf/build.conf
	echo "build.version = $STAMP_VERSION" >> $APP_VERSION/conf/build.conf
}


# Validate args
if [ "$#" -ge 4 ]; then
    showError "${COLOR_ERR}Invalid number of parameters"
    showHelp
    exit 
fi


# Validate args
if [ "$#" -eq 0 ]; then
    showError "${COLOR_ERR}Must provide the version number"
    showHelp
    exit 
fi


# Check for help
if [ $1 == "help" ]; then
	showHelp
	exit 
fi


# Check for version
if [ $1 != "" ]; then VERSION=$1; fi 
if [ $2 != "" ]; then APP_ENV=$2; fi


# Set settings
APP_NAME=slatekit
APP_JAR=${APP_NAME}_${VERSION}.jar
APP_HOME=~/git/slatekit
APP_BUILD=${APP_HOME}/build/kotlin

APP_DIST=${APP_HOME}/dist
APP_RELS=${APP_HOME}/dist/${APP_NAME}/releases
APP_VERSION=${APP_HOME}/dist/${APP_NAME}/releases/${VERSION}

APP_SERVER_HOME=${APP_HOME}/src/apps/kotlin
APP_SRC=${APP_SERVER_HOME}/slatekit-sampleapps
APP_TEMP=${APP_SRC}/temp
JAVA_JAR=/Library/Java/JavaVirtualMachines/jdk1.8.0_141.jdk/contents/home/bin


showSettings

# Clean all existing directories
rm -rf $APP_TEMP/*
rm -rf $APP_VERSION/*

createDirs

# Copy all the jars created by gradle into the release specific directory
cp $APP_SRC/lib/slatekit*   $APP_VERSION/bin

# Copy all dependent jars
cp $APP_HOME/lib/ext/json/*.jar       $APP_VERSION/ext/ 
cp $APP_HOME/lib/ext/mysql/*.jar      $APP_VERSION/ext/      
cp $APP_HOME/lib/ext/kotlin/*.jar     $APP_VERSION/lib/    
cp $APP_HOME/lib/ext/spark/2.6/*.jar  $APP_VERSION/lib/

# Copy the sample app resources/conf files 
cp -a $APP_HOME/src/apps/kotlin/slatekit-sampleapps/sampleapp-batch/src/main/resources/.    $APP_VERSION/conf/sampleapp-batch/
cp -a $APP_HOME/src/apps/kotlin/slatekit-sampleapps/sampleapp-server/src/main/resources/.   $APP_VERSION/conf/sampleapp-server/
cp -a $APP_HOME/src/apps/kotlin/slatekit-sampleapps/sampleapp-cli/src/main/resources/.      $APP_VERSION/conf/sampleapp-shell/ 

# Copy the script files to run the sample apps
cp $APP_HOME/scripts/samples/kotlin/*   $APP_VERSION/

# Copy the license files
cp  $APP_HOME/licenses/kotlin/*.txt   $APP_VERSION/licenses/
cp  $APP_HOME/doc/kotlin/LICENSE.txt  $APP_VERSION/LICENSE.txt
cp  $APP_HOME/doc/kotlin/README.txt   $APP_VERSION/README.txt 

# Copy the jar from libs ( this is not auto-copied for some reason )
cp  $APP_SRC/sampleapp-batch/build/libs/sampleapp-batch-${VERSION}.jar   $APP_VERSION/bin/sampleapp-batch.jar
cp  $APP_SRC/sampleapp-cli/build/libs/sampleapp-cli-${VERSION}.jar       $APP_VERSION/bin/sampleapp-cli.jar
cp  $APP_SRC/sampleapp-core/build/libs/sampleapp-core-${VERSION}.jar     $APP_VERSION/bin/sampleapp-core.jar
cp  $APP_SRC/sampleapp-server/build/libs/sampleapp-server-${VERSION}.jar $APP_VERSION/bin/sampleapp-server.jar

# Create a timestamp file
# createStamp

# Zip up the entires release version directory
cd $APP_RELS
zip -r ${APP_NAME}_kotlin_v_${VERSION}.zip $VERSION

cd $APP_BUILD
