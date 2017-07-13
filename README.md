# SLATE KIT
Slate Kit is a tool kit, utility library and server backend for web and mobile apps. 

It is written in **Kotlin**, uses MySql, AWS, and Spark Java.

It is partially available in Scala.

- **author**: Kishore Reddy
- **website**: www.slatekit.com 
- **date**: July 12, 2017


# LINKS
1. www.slatekit.com
2. www.codehelix.com 


# GITTER
[![Join the chat at https://gitter.im/code-helix/slatekit](https://badges.gitter.im/code-helix/slatekit.svg)](https://gitter.im/code-helix/slatekit?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


# COMPONENTS
Slate Kit contains many useful components, utilities and infrastructure abstractions.
In addition to these, Slate Kit contains some new concepts and implementations of those
concepts. These include the following. ( Please refer to website for more details )

1. Slate Kit - Base App 
2. Slate Kit - Protocol Independent APIs
3. Slate Kit - Domain Driven Single-Table ORM 
4. Slate Kit - API Server
5. Slate Kit - CLI 



# MODULES
- sampleapp-batch.jar    : sample console app to showcase the Slate Kit - Base App   
- sampleapp-cli.jar      : sample cli to showing Slate Kit - Shell component and hosting and using Protocol independent APIs 
- sampleapp-core.jar     : sample library showing creation of Protocol independent APIs
- sampleapp-server.jar   : sample server showing Slate Kit - Server and hosting and using Protocol independent APIs 
- slatekit-cloud.jar        : Slate Kit abstraction of Message Queues and Files with implementations for AWS SQS, S3 
- slatekit-common.jar       : Slate Kit common components and utilities ( used by all other projects ) 
- slatekit-core.jar         : Slate Kit core components ( Shell, Base App, Protocol Independent APIs, and more ) 
- slatekit-entities.jar     : Slate Kit domain driven single-table ORM        
- slatekit-ext.jar          : extensions for mobile/web backend features ( auth, registration, invites and more - not currently available )
- slatekit-integration.jar  : Slate Kit integrations between 2 or more Slate Kit components ( to reduce cyclomatic complexity )
- slatekit-server.jar       : Slate Kit Web API server that wraps Akka-Http ( hosts and manages Protocol independent APIs )
- slatekit-shell.jar        : Slate Kit CLI interface ( includes a CLI access to code-generator and more - Work in progress ) 
- slatekit-tools.jar        : Slate Kit tools as the Code Generator ( Work in progress ) 

## NOTES:
- mysql: mysql-connector-java-5.1.38-bin.jar 
- aws  : aws-java-sdk-1.10.55.jar ( not provided - please download and include if using the slate.cloud component )



# REQUIREMENTS
All the software below is required to run Scala and Slate Kit. Scala is dependent on Java. 
Using Sbt ( the Scala build tool ) will making building and packaging your apps easier.

1. Java	            1.8	
2. Kotlin	          1.1.2
3. Gradle	          latest
4. IntelliJ	        latest 
5. MySql Connector	5.7	For JDBC connectios 


## Optional
Slate Kit support building Web APIs using Spark Java, Cloud Services ( Files, Queues ) using AWS and databases using MySql. 
The following are needed if you plan on using any of these.

1. Spark	2.1 
2. AWS Sdk ( For Java )	latest	Cloud Storage of Files(S3), Queues(SQS)	download
3. MySql 5.7	Database	download


