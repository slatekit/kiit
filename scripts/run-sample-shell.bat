set APP_HOME=.
set APP_CLASSPATH=%APP_HOME%\bin\*;%APP_HOME%\lib\*;%APP_HOME%\ext\*
java -cp "%APP_CLASSPATH%" sampleapp.cli.SampleAppCLI -conf.dir='file://./conf/sampleapp-shell/'