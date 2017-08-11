# Slate Kit
A Kotlin Mobile Back-end, Server Framework and Utility Library

![macOS](https://img.shields.io/badge/os-macOS-green.svg?style=flat)
![Linux](https://img.shields.io/badge/os-linux-green.svg?style=flat)
![Apache 2](https://img.shields.io/badge/license-Apache2-blue.svg?style=flat)

- **author**: Kishore Reddy
- **website**: www.slatekit.com 


# Links
1. www.slatekit.com
2. www.codehelix.com 


# Components
Slate Kit contains many useful architecture components, utilities and applications features. The slatekit-common has 0 dependencies adn contains most of the common utilities and components used throughout all the other projects. 


docs | source | desc | download
------------ | ------------ | ------------- | -------------
[slatekit-common](http://www.slatekit.com/utils.html)                  | [src](src/lib/kotlin/slatekit-common)      | common utilities | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-common/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-common/_latestVersion)
[slatekit-meta](http://www.slatekit.com/utils.html)                    | [src](src/lib/kotlin/slatekit-meta)        | meta/reflection utils | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-meta/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-meta/_latestVersion)
[slatekit-core](http://www.slatekit.com/infra.html)                    | [src](src/lib/kotlin/slatekit-core)        | architecture components | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-core/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-core/_latestVersion)
[slatekit-entities](http://www.slatekit.com/kotlin-core-orm.html)      | [src](src/lib/kotlin/slatekit-entities)    | database entities/orm | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-entities/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-entities/_latestVersion)
[slatekit-apis.jar](http://www.slatekit.com/kotlin-core-apis.html)     | [src](src/lib/kotlin/slatekit-apis)        | api container | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-apis/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-apis/_latestVersion)
[slatekit-integration](https://www.slatekit.com)                       | [src](src/lib/kotlin/slatekit-integration) | integration components | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-integration/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-integration/_latestVersion)
[slatekit-cloud.jar](http://www.slatekit.com/infra.html)               | [src](src/lib/kotlin/slatekit-cloud)       | clouder servers ( AWS ) | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-cloud/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-cloud/_latestVersion)
[slatekit-server.jar](http://www.slatekit.com/kotlin-core-server.html) | [src](src/lib/kotlin/slatekit-server)      | Http Server ( using Spark ) | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-server/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-server/_latestVersion)
                      


# Gitter
[![Join the chat at https://gitter.im/code-helix/slatekit](https://badges.gitter.im/code-helix/slatekit.svg)](https://gitter.im/code-helix/slatekit?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


# Modules
- slatekit-common.jar       : Slate Kit common components and utilities ( used by all other projects ) 
- slatekit-meta.jar         : Slate Kit reflection support
- slatekit-core.jar         : Slate Kit core components ( Shell, Base App, Protocol Independent APIs, and more ) 
- slatekit-entities.jar     : Slate Kit domain driven single-table ORM        
- slatekit-apis.jar         : Slate Kit API server core
- slatekit-cloud.jar        : Slate Kit abstraction of Message Queues and Files with implementations for AWS SQS, S3 
- slatekit-ext.jar          : extensions for mobile/web backend features ( auth, registration, invites and more - not currently available )
- slatekit-integration.jar  : Slate Kit integrations between 2 or more Slate Kit components ( to reduce cyclomatic complexity )
- slatekit-server.jar       : Slate Kit Web API server that wraps Akka-Http ( hosts and manages Protocol independent APIs )
- slatekit-shell.jar        : Slate Kit CLI interface ( includes a CLI access to code-generator and more - Work in progress ) 
- slatekit-tools.jar        : Slate Kit tools as the Code Generator ( Work in progress ) 

## Notes:
- mysql: mysql-connector-java-5.1.38-bin.jar 
- aws  : aws-java-sdk-1.10.55.jar ( not provided - please download and include if using the slate.cloud component )


# Requirements
All the software below is required to run Scala and Slate Kit. Scala is dependent on Java. 
Using Sbt ( the Scala build tool ) will making building and packaging your apps easier.

1. Java	            1.8	
2. Kotlin	          1.1.2


## Optional
Slate Kit support building Web APIs using Spark Java, Cloud Services ( Files, Queues ) using AWS and databases using MySql. 
The following are needed if you plan on using any of these.

1. Spark	2.1 
2. Json-simple
3. AWS Sdk ( For Java )	latest	Cloud Storage of Files(S3), Queues(SQS)	download
4. MySql 5.7	Database	download


