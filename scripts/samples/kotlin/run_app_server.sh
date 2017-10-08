#!/bin/bash

echo "Server App"
APP_HOME="."
APP_CLASSPATH="${APP_HOME}/bin/*:${APP_HOME}/lib/*:${APP_HOME}/ext/*"
echo $APP_CLASSPATH
java -cp "${APP_CLASSPATH}" slatekit.sampleapp.server.SampleAppServerKt -conf.dir='conf/sampleapp-server/'