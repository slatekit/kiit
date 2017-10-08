# Slate Kit
A Kotlin Mobile Back-end, Server Framework and Utility Library

![macOS](https://img.shields.io/badge/os-macOS-green.svg?style=flat)
![Linux](https://img.shields.io/badge/os-linux-green.svg?style=flat)
![Apache 2](https://img.shields.io/badge/license-Apache2-blue.svg?style=flat)
[![Join the chat at https://gitter.im/code-helix/slatekit](https://badges.gitter.im/code-helix/slatekit.svg)](https://gitter.im/code-helix/slatekit?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

- **author**: Kishore Reddy
- **website**: www.slatekit.com 
- **company**: www.codehelix.co


# Components
Slate Kit contains many useful architecture components, utilities and applications features. The slatekit-common has 0 dependencies adn contains most of the common utilities and components used throughout all the other projects. 

docs | source | desc | download
------------ | ------------ | ------------- | -------------
[slatekit-common](http://www.slatekit.com/utils.html)                  | [src](src/lib/kotlin/slatekit-common)      | common utilities | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-common/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-common/_latestVersion)
[slatekit-meta](http://www.slatekit.com/utils.html)                    | [src](src/lib/kotlin/slatekit-meta)        | meta/reflection utils | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-meta/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-meta/_latestVersion)
[slatekit-core](http://www.slatekit.com/infra.html)                    | [src](src/lib/kotlin/slatekit-core)        | architecture components | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-core/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-core/_latestVersion)
[slatekit-entities](http://www.slatekit.com/kotlin-core-orm.html)      | [src](src/lib/kotlin/slatekit-entities)    | database entities/orm | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-entities/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-entities/_latestVersion)
[slatekit-apis](http://www.slatekit.com/kotlin-core-apis.html)     | [src](src/lib/kotlin/slatekit-apis)        | api container | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-apis/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-apis/_latestVersion)
[slatekit-integration](https://www.slatekit.com)                       | [src](src/lib/kotlin/slatekit-integration) | integration components | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-integration/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-integration/_latestVersion)
[slatekit-cloud](http://www.slatekit.com/infra.html)               | [src](src/lib/kotlin/slatekit-cloud)       | clouder servers ( AWS ) | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-cloud/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-cloud/_latestVersion)
[slatekit-server](http://www.slatekit.com/kotlin-core-server.html) | [src](src/lib/kotlin/slatekit-server)      | Http Server ( using Spark ) | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-server/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-server/_latestVersion)


# Overview
Visit www.slatekit.com/overview.html


# Setup
Visit www.slatekit.com/setup.html for setup guide and dependencies. 

component | version | required | notes |  
------------ | ------------ | ------------- | -------------
Java   | 1.8   | required  | 
Kotlin | 1.1.2 | required  | 
Gradle | 3.5   | optional  | for building source code 
MySql  | 5.7   | optional  | used by the entities / orm module
Spark  | 2.6   | optional  | used by the api server 
AWS    | 1.11  | optional  | used by Files Messages components 


# Modules
- slatekit-common.jar       : Slate Kit common components and utilities ( used by all other projects ) 
- slatekit-meta.jar         : Slate Kit reflection support
- slatekit-core.jar         : Slate Kit core architecture components ( App, CLI, Protocol Independent APIs, and more ) 
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
- aws  : aws-java-sdk-1.11.100.jar ( not provided - please download and include if using the slate.cloud component )

