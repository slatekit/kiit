----------------------------------------------------------------------------------------
@author : Kishore Reddy
@website: www.slatekit.com 
@date   : Nov 3, 2016
@release: 1.1.0
@build  : 1.1.0.2
----------------------------------------------------------------------------------------



----------------------------------------------------------------------------------------
SLATE KIT
----------------------------------------------------------------------------------------
This is Slate Kit 1.1 
Slate Kit is a tool kit, utility library and server backend for mobile and web apps. 
It is written in Scala and uses Akka-Http for the Server. 



----------------------------------------------------------------------------------------
CONTENTS
----------------------------------------------------------------------------------------
- bin                   : compiled jars for Slate Kit and the sample apps
- conf                  : config files for Slate Kit Shell and sample apps 
- ext                   : external libraries ( apache http, mysql, joda, typesafe config )
- lib                   : scala libraries and akka-http libraries
- LICENSE.txt           : the license file for slate kit 
- README.txt            : this 
- run-sample-batch.bat  : script to run the Slate Kit Sample Console App 
- run-sample-server.bat : script to run the Slate Kit Server ( Http Web API )
- run-sample-shell.bat  : script to run the Slate Kit Sample Shell ( CLI - Command Line Interface )
- run-slate-shell.bat   : script to run the Slate Kit Shell ( work in progress ) 



----------------------------------------------------------------------------------------
LINKS
----------------------------------------------------------------------------------------
1. www.slatekit.com
2. www.codehelix.com 



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
sampleapp-batch.jar    : sample console app to showcase the Slate Kit - Base App   
sampleapp-cli.jar      : sample cli to showing Slate Kit - Shell component and hosting and using Protocol independent APIs 
sampleapp-core.jar     : sample library showing creation of Protocol independent APIs
sampleapp-server.jar   : sample server showing Slate Kit - Server and hosting and using Protocol independent APIs 
slate-cloud.jar        : Slate Kit abstraction of Message Queues and Files with implementations for AWS SQS, S3 
slate-common.jar       : Slate Kit common components and utilities ( used by all other projects ) 
slate-core.jar         : Slate Kit core components ( Shell, Base App, Protocol Independent APIs, and more ) 
slate-entities.jar     : Slate Kit domain driven single-table ORM        
slate-ext.jar          : extensions for mobile/web backend features ( auth, registration, invites and more - not currently available )
slate-integration.jar  : Slate Kit integrations between 2 or more Slate Kit components ( to reduce cyclomatic complexity )
slate-server.jar       : Slate Kit Web API server that wraps Akka-Http ( hosts and manages Protocol independent APIs )
slate-shell.jar        : Slate Kit CLI interface ( includes a CLI access to code-generator and more - Work in progress ) 
slate-tools.jar        : Slate Kit tools as the Code Generator ( Work in progress ) 

NOTES:
mysql: mysql-connector-java-5.1.38-bin.jar 
aws  : aws-java-sdk-1.10.55.jar ( not provided - please download and include if using the slate.cloud component )


----------------------------------------------------------------------------------------
REQUIREMENTS
----------------------------------------------------------------------------------------
All the software below is required to run Scala and Slate Kit. Scala is dependent on Java. 
Using Sbt ( the Scala build tool ) will making building and packaging your apps easier.

1. Java	            1.8	
2. Scala	        2.17
3. Sbt	            latest
4. IntelliJ	        latest 
5. MySql Connector	5.7	For JDBC connectios 


Optional
Slate Kit support building Web APIs using Akka-Http, Cloud Services ( Files, Queues ) using AWS and databases using MySql. 
The following are needed if you plan on using any of these.

1. Akka-Http	2.4.10 
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
Info  : version          = 1.1.0
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
Info  : lang.name        = scala
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
:> sys.version.scala 
:> app.info.lang
:> app.info.host
:> app.info.app
:> sampleapp.users ?
:> sampleapp.users.total
:> sampleapp.users.create  -email="batman@gotham.com" -first="bruce" -last="wayne" -isMale=true -age=32 -phone="123456789" -country="us" 
:> sampleapp.users.create  -email="superman@metropolis.com" -first="clark" -last="kent" -isMale=true -age=32 -phone="987654321" -country="us" 
:> sampleapp.users.create  -email="wonderwoman@metropolis.com" -first="diana" -last="price" -isMale=false -age=32 -phone="111111111" -country="br" 
:> sampleapp.users.getById -id=1
:> sampleapp.users.first   
:> sampleapp.users.last
:> sampleapp.users.recent -count=2
:> sampleapp.users.oldest -count=2
:> exit



----------------------------------------------------------------------------------------
WEB API SERVER
----------------------------------------------------------------------------------------
The Sample App Server shows Slate Kit protocol independent APIs running in the Slate Kit Server
Refer to API docs for more info on protocol independent APIs. 
http://www.slatekit.com/apis-detail.html

NOTES:
- The Web API Server runs on Akka-Http and hosts the APIs located in the SampleApp.Core project.
- The APIs are protocol independent and can also run the CLI ( above ).
- In order to test the server do the following:

1. run script : run-sample-server.bat
2. open postman ( chrome extension to send/test http requests )
3. ensure you have setup a header with api-key => 54B1817194C1450B886404C6BEA81673
4. ensure for "post" requests you have set the body = raw ( json/application )
5. ensure for "post" requests you have at least an empty json object "{ }" for calling endpoints/methods that take 0 params
6. use any of the urls below for testing

verb,  header,       url                                              body ( json )
post   see step 3.   http://localhost:5000/api/sys/version/java       { }      
post   see step 3.   http://localhost:5000/api/sys/version/scala      { }          
get    see step 3.   http://localhost:5000/api/app/info/lang          { }                 
get    see step 3.   http://localhost:5000/api/app/info/host          { }          
get    see step 3.   http://localhost:5000/api/app/info/app           { }        
post   see step 3.   http://localhost:5000/api/sampleapp/users/total  { }
post   see step 3.   http://localhost:5000/api/sampleapp/users/create { "email" : "batman@gotham.com", "first" : "bruce", "last" : "wayne", "isMale" : true, "age" : 32, "phone" : "123456789", "country" : "us" }
post   see step 3.   http://localhost:5000/api/sampleapp/users/create { "email" : "superman@metropolis.com", "first" : "clark", "last" : "kent", "isMale" : true, "age" : 32, "phone" : "987654321", "country" : "us" }
post   see step 3.   http://localhost:5000/api/sampleapp/users/create { "email" : "wonderwoman@metropolis.com", "first" : "diana", "last" : "price", "isMale" : false, "age" : 32, "phone" : "111111111", "country" : "us" }
post   see step 3.   http://localhost:5000/api/sampleapp/users/first  { }
post   see step 3.   http://localhost:5000/api/sampleapp/users/last   { }
post   see step 3.   http://localhost:5000/api/sampleapp/users/recent { "count" : 2 }
post   see step 3.   http://localhost:5000/api/sampleapp/users/oldest { "count" : 2 }






