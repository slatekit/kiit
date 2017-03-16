set VERSION=1.4.1
set SLATE_SOURCE=C:\Dev\github\blend-server
set SLATE_DEST=C:\Dev\github\blend-server\dist\slatekit\releases\%VERSION%

mkdir %SLATE_SOURCE%\build\lib\%VERSION%
mkdir %SLATE_DEST%
mkdir %SLATE_DEST%\bin
mkdir %SLATE_DEST%\conf
mkdir %SLATE_DEST%\ext
mkdir %SLATE_DEST%\lib
mkdir %SLATE_DEST%\licenses
mkdir %SLATE_DEST%\samples

REM ===============================================
REM copy all slatekit jars to releases\{version}\bin
copy %SLATE_SOURCE%\src\lib\scala\Slate.Api\target\scala-2.11\slate-api_2.11-%VERSION%.jar        			%SLATE_DEST%\bin\slate-api_2.11-%VERSION%.jar         /y   
copy %SLATE_SOURCE%\src\lib\scala\Slate.Cloud\target\scala-2.11\slate-cloud_2.11-%VERSION%.jar        		%SLATE_DEST%\bin\slate-cloud_2.11-%VERSION%.jar       /y   
copy %SLATE_SOURCE%\src\lib\scala\Slate.Common\target\scala-2.11\slate-common_2.11-%VERSION%.jar       		%SLATE_DEST%\bin\slate-common_2.11-%VERSION%.jar      /y    
copy %SLATE_SOURCE%\src\lib\scala\Slate.Core\target\scala-2.11\slate-core_2.11-%VERSION%.jar         		%SLATE_DEST%\bin\slate-core_2.11-%VERSION%.jar        /y  
copy %SLATE_SOURCE%\src\lib\scala\Slate.Entities\target\scala-2.11\slate-entities_2.11-%VERSION%.jar     	%SLATE_DEST%\bin\slate-entities_2.11-%VERSION%.jar    /y        
copy %SLATE_SOURCE%\src\lib\scala\Slate.Integration\target\scala-2.11\slate-integration_2.11-%VERSION%.jar  %SLATE_DEST%\bin\slate-integration_2.11-%VERSION%.jar /y             
copy %SLATE_SOURCE%\src\lib\scala\Slate.Server\target\scala-2.11\slate-server_2.11-%VERSION%.jar       		%SLATE_DEST%\bin\slate-server_2.11-%VERSION%.jar      /y         
copy %SLATE_SOURCE%\src\lib\scala\Slate.Shell\target\scala-2.11\slate-shell_2.11-%VERSION%.jar         		%SLATE_DEST%\bin\slate-shell_2.11-%VERSION%.jar       /y       
copy %SLATE_SOURCE%\src\lib\scala\Slate.Tools\target\scala-2.11\slate-tools_2.11-%VERSION%.jar         		%SLATE_DEST%\bin\slate-tools_2.11-%VERSION%.jar       /y       

REM ===============================================
REM copy all scala, akka libs to releases\{version}\lib
copy %SLATE_SOURCE%\lib\ext\akka\2.4.10\akka-actor_2.11-2.4.10.jar                        %SLATE_DEST%\lib\akka-actor_2.11-2.4.10.jar                                                             
copy %SLATE_SOURCE%\lib\ext\akka\2.4.10\akka-http-core_2.11-2.4.10.jar                    %SLATE_DEST%\lib\akka-http-core_2.11-2.4.10.jar                                                         
copy %SLATE_SOURCE%\lib\ext\akka\2.4.10\akka-http-experimental_2.11-2.4.10.jar            %SLATE_DEST%\lib\akka-http-experimental_2.11-2.4.10.jar                                                 
copy %SLATE_SOURCE%\lib\ext\akka\2.4.10\akka-http-spray-json-experimental_2.11-2.4.10.jar %SLATE_DEST%\lib\akka-http-spray-json-experimental_2.11-2.4.10.jar                                      
copy %SLATE_SOURCE%\lib\ext\akka\2.4.10\akka-parsing_2.11-2.4.10.jar                      %SLATE_DEST%\lib\akka-parsing_2.11-2.4.10.jar                                                           
copy %SLATE_SOURCE%\lib\ext\akka\2.4.10\akka-stream_2.11-2.4.10.jar                       %SLATE_DEST%\lib\akka-stream_2.11-2.4.10.jar                                                            
copy %SLATE_SOURCE%\lib\ext\akka\2.4.10\reactive-streams-1.0.0.jar                        %SLATE_DEST%\lib\reactive-streams-1.0.0.jar                                                             
copy %SLATE_SOURCE%\lib\ext\scala\scala-java8-compat_2.11-0.7.0.jar                       %SLATE_DEST%\lib\scala-java8-compat_2.11-0.7.0.jar                                                      
copy %SLATE_SOURCE%\lib\ext\scala\scala-library.jar                                       %SLATE_DEST%\lib\scala-library.jar                                                                      
copy %SLATE_SOURCE%\lib\ext\scala\scala-parser-combinators_2.11-1.0.4.jar                 %SLATE_DEST%\lib\scala-parser-combinators_2.11-1.0.4.jar                                                
copy %SLATE_SOURCE%\lib\ext\scala\scala-reflect.jar                                       %SLATE_DEST%\lib\scala-reflect.jar                                                                      
copy %SLATE_SOURCE%\lib\ext\akka\2.4.10\spray-json_2.11-1.3.2.jar                         %SLATE_DEST%\lib\spray-json_2.11-1.3.2.jar                                                              
copy %SLATE_SOURCE%\lib\ext\akka\2.4.10\ssl-config-akka_2.11-0.2.1.jar                    %SLATE_DEST%\lib\ssl-config-akka_2.11-0.2.1.jar                                                         
copy %SLATE_SOURCE%\lib\ext\akka\2.4.10\ssl-config-core_2.11-0.2.1.jar                    %SLATE_DEST%\lib\ssl-config-core_2.11-0.2.1.jar                                                         


REM ===============================================
REM copy all the binaries to the releases\{version}\bin directory
copy %SLATE_SOURCE%\lib\ext\java\commons-codec-1.9.jar                %SLATE_DEST%\ext\commons-codec-1.9.jar
copy %SLATE_SOURCE%\lib\ext\java\commons-logging-1.2.jar              %SLATE_DEST%\ext\commons-logging-1.2.jar
copy %SLATE_SOURCE%\lib\ext\java\config-1.3.0.jar                     %SLATE_DEST%\ext\config-1.3.0.jar
copy %SLATE_SOURCE%\lib\ext\java\httpclient-4.5.1.jar                 %SLATE_DEST%\ext\httpclient-4.5.1.jar
copy %SLATE_SOURCE%\lib\ext\java\httpcore-4.4.3.jar                   %SLATE_DEST%\ext\httpcore-4.4.3.jar
copy %SLATE_SOURCE%\lib\ext\java\jackson-annotations-2.7.0.jar        %SLATE_DEST%\ext\jackson-annotations-2.7.0.jar
copy %SLATE_SOURCE%\lib\ext\java\jackson-core-2.7.6.jar               %SLATE_DEST%\ext\jackson-core-2.7.6.jar
copy %SLATE_SOURCE%\lib\ext\java\jackson-databind-2.7.6.jar           %SLATE_DEST%\ext\jackson-databind-2.7.6.jar
copy %SLATE_SOURCE%\lib\ext\java\joda-time-2.8.1.jar                  %SLATE_DEST%\ext\joda-time-2.8.1.jar
copy %SLATE_SOURCE%\lib\ext\java\json_simple-1.1.jar                  %SLATE_DEST%\ext\json_simple-1.1.jar
copy %SLATE_SOURCE%\lib\ext\mysql\mysql-connector-java-5.1.38-bin.jar %SLATE_DEST%\ext\mysql-connector-java-5.1.38-bin.jar


REM ===============================================
REM copy the sample app resource files to releases\{version}\conf
xcopy %SLATE_SOURCE%\src\apps\scala\Slate.SampleApp\SampleApp.Batch\src\main\resources    %SLATE_DEST%\conf\sampleapp-batch\       /s /e /h /y   
xcopy %SLATE_SOURCE%\src\apps\scala\Slate.SampleApp\SampleApp.Server\src\main\resources   %SLATE_DEST%\conf\sampleapp-server\      /s /e /h /y    
xcopy %SLATE_SOURCE%\src\apps\scala\Slate.SampleApp\SampleApp.CLI\src\main\resources      %SLATE_DEST%\conf\sampleapp-shell\       /s /e /h /y  
xcopy %SLATE_SOURCE%\src\lib\scala\Slate.Shell\src\main\resources                         %SLATE_DEST%\conf\slate-shell\           /s /e /h /y      


REM ===============================================
REM copy the license files to releases\{version}\licenses
xcopy %SLATE_SOURCE%\licenses   %SLATE_DEST%\licenses\          /s /e /h /y      


REM ===============================================
REM copy the script files to run the sample apps to releases\{version}\
xcopy %SLATE_SOURCE%\scripts\samples   %SLATE_DEST%\          /s /e /h /y      