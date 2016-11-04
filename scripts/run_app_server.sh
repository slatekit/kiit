#!/bin/bash

echo "Server App"
APP_HOME="."
APP_CLASSPATH="${APP_HOME}/bin/*:${APP_HOME}/lib/*:${APP_HOME}/ext/*"
echo $APP_CLASSPATH
java -cp "${APP_CLASSPATH}" sampleapp.server.SampleAppServer -conf.dir='file://./conf/sampleapp-server/'