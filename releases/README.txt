----------------------------------------------------------------------------------------
@author: Kishore Reddy
@website: www.slatekit.com 
@date: Sep 25, 2016
----------------------------------------------------------------------------------------


SLATE KIT
----------------------------------------------------------------------------------------
This is Slate Kit 1.0 
Slate Kit is a tool kit, utility library and server backend for mobile and web apps. 
It is written in Scala and uses Akka-Http for the Server. 


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


LINKS
----------------------------------------------------------------------------------------
1. www.slatekit.com
2. www.codehelix.com 


COMPONENTS
----------------------------------------------------------------------------------------
Slate Kit contains many useful components, utilities and infrastructure abstractions.
In addition to these, Slate Kit contains some new concepts and implementations of those
concepts. These include the following. ( Please refer to website for more details )

1. Slate Kit - Base App 
2. Slate Kit - Protocol Independent APIs
3. Slate Kit - Domain Driven Single-Table ORM 
4. Slate Kit - Server 


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

