set VERSION=0.9.4
set SLATE_SOURCE=C:\Dev\github\slatekit
set SLATE_DEST=C:\Dev\github\slatekit\dist\slatekit\kotlin\releases\%VERSION%


mkdir %SLATE_DEST%
mkdir %SLATE_DEST%\bin
mkdir %SLATE_DEST%\conf
mkdir %SLATE_DEST%\ext
mkdir %SLATE_DEST%\lib
mkdir %SLATE_DEST%\licenses
mkdir %SLATE_DEST%\samples


REM ===============================================
REM copy all slatekit jars to releases\{version}\bin
xcopy %SLATE_SOURCE%\src\lib\kotlin\dist\*.jar   %SLATE_DEST%\bin          /s /e /h /y      


REM ===============================================
REM copy all sample app jars to releases\{version}\bin
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-core\build\libs\slatekit-sampleapp-core.jar      %SLATE_DEST%\bin\
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-batch\build\libs\slatekit-sampleapp-batch.jar    %SLATE_DEST%\bin\
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-cli\build\libs\slatekit-sampleapp-cli.jar        %SLATE_DEST%\bin\
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-server\build\libs\slatekit-sampleapp-server.jar  %SLATE_DEST%\bin\


REM ===============================================
REM copy all dependent jars
xcopy %SLATE_SOURCE%\lib\ext\json\*.jar     %SLATE_DEST%\ext          /s /e /h /y      
xcopy %SLATE_SOURCE%\lib\ext\mysql\*.jar    %SLATE_DEST%\ext          /s /e /h /y      
xcopy %SLATE_SOURCE%\lib\ext\kotlin\*.jar   %SLATE_DEST%\lib          /s /e /h /y      
xcopy %SLATE_SOURCE%\lib\ext\spark\2.6\*.jar    %SLATE_DEST%\lib          /s /e /h /y      


REM ===============================================
REM copy the sample app resource files to releases\{version}\conf
xcopy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-batch\src\main\resources    %SLATE_DEST%\conf\sampleapp-batch\       /s /e /h /y   
xcopy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-server\src\main\resources   %SLATE_DEST%\conf\sampleapp-server\      /s /e /h /y    
xcopy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-cli\src\main\resources      %SLATE_DEST%\conf\sampleapp-shell\       /s /e /h /y   


REM ===============================================
REM copy the script files to run the sample apps to releases\{version}\
xcopy %SLATE_SOURCE%\scripts\samples\kotlin\*   %SLATE_DEST%\          /s /e /h /y      


REM ===============================================
REM copy the sample apps to releases\{version}\samples
mkdir %SLATE_DEST%\samples\sampleapp-core
mkdir %SLATE_DEST%\samples\sampleapp-batch
mkdir %SLATE_DEST%\samples\sampleapp-cli
mkdir %SLATE_DEST%\samples\sampleapp-server

copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-core\build.gradle    %SLATE_DEST%\samples\sampleapp-core\ 
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-core\gradlew         %SLATE_DEST%\samples\sampleapp-core\   
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-core\gradlew.bat     %SLATE_DEST%\samples\sampleapp-core\      
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-core\settings.gradle %SLATE_DEST%\samples\sampleapp-core\         
mkdir %SLATE_DEST%\samples\sampleapp-core\src\
xcopy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-core\src            %SLATE_DEST%\samples\sampleapp-core\src\ /s /e /h /y    


copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-batch\build.gradle    %SLATE_DEST%\samples\sampleapp-batch\ 
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-batch\gradlew         %SLATE_DEST%\samples\sampleapp-batch\   
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-batch\gradlew.bat     %SLATE_DEST%\samples\sampleapp-batch\      
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-batch\settings.gradle %SLATE_DEST%\samples\sampleapp-batch\         
mkdir %SLATE_DEST%\samples\sampleapp-batch\src\
xcopy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-batch\src            %SLATE_DEST%\samples\sampleapp-batch\src\ /s /e /h /y    


copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-cli\build.gradle    %SLATE_DEST%\samples\sampleapp-cli\ 
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-cli\gradlew         %SLATE_DEST%\samples\sampleapp-cli\   
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-cli\gradlew.bat     %SLATE_DEST%\samples\sampleapp-cli\      
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-cli\settings.gradle %SLATE_DEST%\samples\sampleapp-cli\         
mkdir %SLATE_DEST%\samples\sampleapp-cli\src\
xcopy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-cli\src            %SLATE_DEST%\samples\sampleapp-cli\src\ /s /e /h /y    


copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-server\build.gradle    %SLATE_DEST%\samples\sampleapp-server\ 
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-server\gradlew         %SLATE_DEST%\samples\sampleapp-server\   
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-server\gradlew.bat     %SLATE_DEST%\samples\sampleapp-server\      
copy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-server\settings.gradle %SLATE_DEST%\samples\sampleapp-server\         
mkdir %SLATE_DEST%\samples\sampleapp-server\src\
xcopy %SLATE_SOURCE%\src\apps\kotlin\slatekit-sampleapps\sampleapp-server\src            %SLATE_DEST%\samples\sampleapp-server\src\ /s /e /h /y    



REM ===============================================
REM copy the license files to releases\{version}\licenses
xcopy %SLATE_SOURCE%\licenses\kotlin   %SLATE_DEST%\licenses\          /s /e /h /y      
copy %SLATE_SOURCE%\doc\kotlin\LICENSE.txt %SLATE_DEST%\LICENSE.txt 
copy %SLATE_SOURCE%\doc\kotlin\README.txt  %SLATE_DEST%\README.txt  

