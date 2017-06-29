#!/bin/bash
echo "Console App"
APP_HOME="."
APP_CLASSPATH="${APP_HOME}/bin/*:${APP_HOME}/lib/*:${APP_HOME}/ext/*"
echo $APP_CLASSPATH
# java -cp "${APP_CLASSPATH}" sampleapp.batch.SampleAppBatch -conf.dir='file://./conf/sampleapp-batch/'
java -cp "${APP_CLASSPATH}" sampleapp.batch.SampleAppBatch -conf.dir='file://./conf/sampleapp-batch/'