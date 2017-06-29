#!/bin/bash

echo "Shell API"
APP_HOME="."
APP_CLASSPATH="${APP_HOME}/bin/*:${APP_HOME}/lib/*:${APP_HOME}/ext/*"
echo $APP_CLASSPATH
java -cp "${APP_CLASSPATH}" sampleapp.cli.SampleAppCLI -conf.dir='file://./conf/sampleapp-shell/'