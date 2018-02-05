----------------------------------------------------------------------------------------
@author : Kishore Reddy
@website: www.slatekit.com 
@date   : July 12, 2016
@release: 0.9.3
@build  : 0.9.3.1
----------------------------------------------------------------------------------------



----------------------------------------------------------------------------------------
SLATE KIT
----------------------------------------------------------------------------------------
This is Slate Kit 0.9.3
Slate Kit is a tool kit, utility library and server backend for mobile and web apps. 
It is written in Kotlin, uses MySql, AWS, and Spark Java.



----------------------------------------------------------------------------------------
LINKS
----------------------------------------------------------------------------------------
1. www.slatekit.com
2. www.codehelix.com 

Overview: www.slatekit.com/overview.html
Architecture: www.slatekit.com/infra.html
Utilities: www.slatekit.com/utils.html
Features: www.slatekit.com/features.html ( coming soon )
Github: https://github.com/code-helix/slatekit



----------------------------------------------------------------------------------------
CONTENTS
----------------------------------------------------------------------------------------
- bin                   : compiled jars for Slate Kit and the sample apps
- conf                  : config files for Slate Kit Shell and sample apps 
- ext                   : external libraries ( json-simple, mysql )
- lib                   : dependent librarites that are core SlateKit ( kotlin + spark )
- licenses              : the licenses for all the libraries used by slatekit
- LICENSE.txt           : the license file for slate kit 
- README.txt            : this 
- run-app-batch.bat     : mac/linux: script to run the Slate Kit Sample Console App 
- run-app-server.bat    : mac/linux: script to run the Slate Kit Server ( Http Web API )
- run-app-shell.bat     : mac/linux: script to run the Slate Kit Sample Shell ( CLI - Command Line Interface )
- run-sample-batch.bat  : windows  : script to run the Slate Kit Sample Console App 
- run-sample-server.bat : windows  : script to run the Slate Kit Server ( Http Web API )
- run-sample-shell.bat  : windows  : script to run the Slate Kit Sample Shell ( CLI - Command Line Interface )



----------------------------------------------------------------------------------------
COMPONENTS
----------------------------------------------------------------------------------------
Slate Kit contains many useful components, utilities and infrastructure abstractions.
In addition to these, Slate Kit contains some new concepts and implementations of those
concepts. These include the following. ( Please refer to website for more details )

1. Slate Kit - Base App 
2. Slate Kit - Protocol Independent APIs
3. Slate Kit - Domain Driven Single-Table ORM 
4. Slate Kit - Server 



----------------------------------------------------------------------------------------
LIBRARIES
----------------------------------------------------------------------------------------
slatekit-sampleapp-batch.jar    : sample console app to showcase the Slate Kit - Base App   
slatekit-sampleapp-cli.jar      : sample cli to showing Slate Kit - Shell component and hosting and using Protocol independent APIs 
slatekit-sampleapp-core.jar     : sample library showing creation of Protocol independent APIs
slatekit-sampleapp-server.jar   : sample server showing Slate Kit - Server and hosting and using Protocol independent APIs 
slatekit-cloud.jar              : Slate Kit abstraction of Message Queues and Files with implementations for AWS SQS, S3 
slatekit-common.jar             : Slate Kit common components and utilities ( used by all other projects ) 
slatekit-core.jar               : Slate Kit core components ( Shell, Base App, Protocol Independent APIs, and more ) 
slatekit-entities.jar           : Slate Kit domain driven single-table ORM        
slatekit-ext.jar                : extensions for mobile/web backend features ( auth, registration, invites and more - not currently available )
slatekit-integration.jar        : Slate Kit integrations between 2 or more Slate Kit components ( to reduce cyclomatic complexity )
slatekit-server.jar             : Slate Kit Web API server that wraps SparkJava ( hosts and manages Protocol independent APIs )
slatekit-shell.jar              : Coming soon 
slatekit-tools.jar              : Coming soon

NOTES:
mysql: mysql-connector-java-5.1.38-bin.jar 
aws  : aws-java-sdk-1.10.55.jar ( not provided - please download and include if using the slate.cloud component )


----------------------------------------------------------------------------------------
REQUIREMENTS
----------------------------------------------------------------------------------------
All the software below is required to run Slate Kit. 
Gradle is used to compile and package Slate Kit.

1. Java	            1.8	
2. Kotlin	        1.1.1
3. Gradle	        3.5+
4. IntelliJ	        latest 
5. MySql Connector	5.7	For JDBC connectios 


Optional
Slate Kit support building Web APIs using SparkJava, Cloud Services ( Files, Queues ) using AWS and databases using MySql. 
The following are needed if you plan on using any of these.

1. Spark 2.1 ( upgrade to 2.5 coming soon )
2. AWS Sdk ( For Java )	latest	Cloud Storage of Files(S3), Queues(SQS)	download
3. MySql 5.7	Database	download



----------------------------------------------------------------------------------------
SAMPLE APP 
----------------------------------------------------------------------------------------
The Sample App Batch shows the Slate Kit Base App.
Refer to APP docs for more info.
http://www.slatekit.com/app.html


The Base App is a powerful base application that includes support for:
- command line args 
- environment selection ( dev, qa, stg, prod )
- configs per environment 
- encryption / decryption 
- host info
- lang runtime info 
- application info and version 
- logging 

1. run script : run-sample-batch.bat
2. the sample app displays startup info 
3. the sample runs some simulated work for 2 seconds and shuts down

OUTPUT:
Info  : ===============================================================
Info  : SUMMARY :
Info  : ===============================================================
Info  : name             = Sample App - Console
Info  : desc             = Sample console application to show the Slate Kit base app
Info  : version          = 1.4.1
Info  : tags             = slate,shell,cli
Info  : group            = Samples
Info  : region           = ny
Info  : contact          = kishore@codehelix.co
Info  : url              = http://sampleapp.slatekit.com
Info  : args             = Some([Ljava.lang.String;@2424686b)
Info  : env              = loc
Info  : config           = env.loc.conf
Info  : log              = console
Info  : started          = 2016-10-01T01:18:29.753
Info  : ended            = 2016-10-01T01:18:31.182
Info  : duration         = slate.common.TimeSpan@6ea94d6a
Info  : status           = ended
Info  : errors           = 0
Info  : error            = n/a
Info  : host.name        = KRPC1
Info  : host.ip          = Windows 10
Info  : host.origin      = local
Info  : host.version     = 10.0
Info  : lang.name        = kotlin
Info  : lang.version     = 1.8.0_91
Info  : lang.versionNum  = 2.11.8
Info  : lang.java        = local
Info  : lang.home        = C:/Tools/Java/jdk1.8.0_91/jre
Info  : region = n/a
Info  : ===============================================================



----------------------------------------------------------------------------------------
SAMPLE CLI 
----------------------------------------------------------------------------------------
The Sample App Shell shows Slate Kit protocol independent APIs running in a command line shell.
Refer to API docs for more info on protocol independent APIs. 
http://www.slatekit.com/apis-detail.html

1. run script : run-sample-shell.bat
2. type ? to list all the api areas 
3. type sys ? to list all the apis in the "sys" area 
4. type sys.version? to list all the actions in the "sys.version" api 
5. you can test any of the api commands below. 
6. type "exit" to shutdown the sample app shell and return the command line 

EXAMPLES 
:> ?
:> sys.version.java
:> sys.app.lang
:> sys.app.host
:> sys.app.app
:> app.users ?
:> app.users.total
:> app.users.createWith  -email="batman@gotham.com" -first="bruce" -last="wayne" -isMale=true -age=32 -phone="123456789" -country="us" 
:> app.users.createWith  -email="superman@metropolis.com" -first="clark" -last="kent" -isMale=true -age=32 -phone="987654321" -country="us" 
:> app.users.createWith  -email="wonderwoman@metropolis.com" -first="diana" -last="price" -isMale=false -age=32 -phone="111111111" -country="br" 
:> app.users.getById -id=1
:> app.users.getAll
:> app.users.updatePhone -id=1 -phone=1112223334
:> app.users.first          
:> app.users.getById -id=2   
:> app.users.last           
:> app.users.recent -count=2
:> app.users.oldest -count=2
:> app.users.deleteById -id=2     
:> app.users.total
:> app.users.getAll          


:> exit



----------------------------------------------------------------------------------------
WEB API SERVER
----------------------------------------------------------------------------------------
The Sample App Server shows Slate Kit protocol independent APIs running in the Slate Kit Server
Refer to API docs for more info on protocol independent APIs. 
http://www.slatekit.com/apis-detail.html

NOTES:
- The Web API Server runs SparkJava and hosts the APIs located in the SampleApp.Core project.
- The APIs are protocol independent and can also run the CLI ( above ).
- In order to test the server do the following:

1. run script : run-sample-server.bat
2. open postman ( chrome extension to send/test http requests )
3. ensure you have setup a header with api-key => 54B1817194C1450B886404C6BEA81673
4. ensure for "post" requests you have set the body = raw ( json/application )
5. ensure for "post" requests you have at least an empty json object "{ }" for calling endpoints/methods that take 0 params
6. use any of the urls below for testing


Use POSTMAN for easy testing of the APIs below:

HEADERS:
api-key : 54B1817194C1450B886404C6BEA81673
Authorization : 54B1817194C1450B886404C6BEA81673

VERB,  HEADERS,      URL                                                BODY ( json )
get    see above   http://localhost:5000/api/sys/version/java         { }
post   see above   http://localhost:5000/api/sys/app/lang             { }
post   see above   http://localhost:5000/api/sys/app/host             { }
post   see above   http://localhost:5000/api/sys/app/about            { }
get    see above   http://localhost:5000/api/app/movies/total         { }
get    see above   http://localhost:5000/api/app/movies/getAll        { }
get    see above   http://localhost:5000/api/app/users/total          { }
post   see above   http://localhost:5000/api/app/users/create         { "email" : "batman@gotham.com", "first" : "bruce", "last" : "wayne", "isMale" : true, "age" : 32, "phone" : "123456789", "country" : "us" }
post   see above   http://localhost:5000/api/app/users/create         { "email" : "superman@metropolis.com", "first" : "clark", "last" : "kent", "isMale" : true, "age" : 32, "phone" : "987654321", "country" : "us" }
post   see above   http://localhost:5000/api/app/users/create         { "email" : "wonderwoman@themyscira.com", "first" : "diana", "last" : "price", "isMale" : false, "age" : 32, "phone" : "111111111", "country" : "us" }
get    see above   http://localhost:5000/api/app/users/getById?id=2
get    see above   http://localhost:5000/api/app/users/getAll         { }
put    see above   http://localhost:5000/api/app/users/updatePhone    { "id" : 1, "phone": "1112223334" }
get    see above   http://localhost:5000/api/app/users/first          { }
get    see above   http://localhost:5000/api/app/users/last           { }
get    see above   http://localhost:5000/api/app/users/recent?count=2
get    see above   http://localhost:5000/api/app/users/oldest?count=2
delete see above   http://localhost:5000/api/app/users/deleteById     { "id" : 2 }
get    see above   http://localhost:5000/api/app/users/total          { }
get    see above   http://localhost:5000/api/app/users/getAll         { }

LIMITATIONS:
1. File upload ( WIP - Work in progress )
2. Default parameter values for api actions/methods
3. API classes are currently singletons - ( support creation mode: singleton | 1 instance per request )





