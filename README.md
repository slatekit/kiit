
![Kotlin](https://img.shields.io/badge/kotlin-1.3-orange.svg)
![Build](https://travis-ci.org/code-helix/slatekit.svg?branch=master)
![macOS](https://img.shields.io/badge/os-linux-green.svg?style=flat)
![Apache 2](https://img.shields.io/badge/license-Apache2-blue.svg?style=flat)
[![Join the chat at https://gitter.im/code-helix/slatekit](https://badges.gitter.im/code-helix/slatekit.svg)](https://gitter.im/code-helix/slatekit?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Follow us on twitter](https://img.shields.io/badge/twitter-kishore__reddy-green.svg)](https://twitter.com/kishore_reddy)

![image](media/slatekit-banner.png)

# About
**Slate Kit** is a collection of architecture components and libraries for full-stack Kotlin development.

It is comprised of 3 core feature categories.

1. Common utilities ( for both server / client )
2. Server API Framework ( called Universal APIs )
3. Architecture Components ( micro-orm, cli, workers, and more)


# Links
Some important links / pages for more info.

type | link | note
------------ | ------------ | -------------
home | www.slatekit.com | landing page
overview | http://www.slatekit.com/overview.html | goals / uses / philosophy
setup    | www.slatekit.com/setup.html | setup kotlin / slatekit
starting | http://www.slatekit.com/components.html | key concepts / steps
releases | http://www.slatekit.com/releases.html | relase history / notes
utilities| http://www.slatekit.com/utils.html    | utilities for client/server
modules  | http://www.slatekit.com/infra.html    | architecture components
learn    | http://www.slatekit.com/kotlin101.html | kotlin 101
standards| http://www.slatekit.com/kotlin-standards.html | coding standards


# Modules
Slate Kit contains many useful architecture components, utilities and applications features. The slatekit-common has 0 dependencies adn contains most of the common utilities and components used throughout all the other projects. 

docs | source | desc | download
------------ | ------------ | ------------- | -------------
[slatekit-common](http://www.slatekit.com/utils.html)                  | [src](src/lib/kotlin/slatekit-common)      | common utilities | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-common/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-common/_latestVersion)
[slatekit-meta](http://www.slatekit.com/utils.html)                    | [src](src/lib/kotlin/slatekit-meta)        | meta/reflection utils | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-meta/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-meta/_latestVersion)
[slatekit-core](http://www.slatekit.com/infra.html)                    | [src](src/lib/kotlin/slatekit-core)        | architecture components | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-core/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-core/_latestVersion)
[slatekit-entities](http://www.slatekit.com/kotlin-core-orm.html)      | [src](src/lib/kotlin/slatekit-entities)    | database entities/orm | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-entities/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-entities/_latestVersion)
[slatekit-apis](http://www.slatekit.com/kotlin-core-apis.html)         | [src](src/lib/kotlin/slatekit-apis)        | api container | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-apis/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-apis/_latestVersion)
[slatekit-workers](http://www.slatekit.com/kotlin-core-workers.html)    | [src](src/lib/kotlin/slatekit-workers)     | Background workers | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-workers/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-workers/_latestVersion)
[slatekit-integration](https://www.slatekit.com)                       | [src](src/lib/kotlin/slatekit-integration) | integration components | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-integration/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-integration/_latestVersion)
[slatekit-cloud](http://www.slatekit.com/infra.html)                   | [src](src/lib/kotlin/slatekit-cloud)       | clouder services ( AWS ) | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-cloud/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-cloud/_latestVersion)
[slatekit-server](http://www.slatekit.com/kotlin-core-server.html)     | [src](src/lib/kotlin/slatekit-server)      | Http Server ( using Spark ) | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-server/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-server/_latestVersion)


# Setup
You can use maven/gradle to reference Slate Kit libraries. Refer to [Setup](http://www.slatekit.com/kotlin-setup.html) for more info. Make sure you add the maven url **http://dl.bintray.com/codehelixinc/slatekit**.

```groovy
buildscript {
    ext.kotlin_version = '1.3.0'

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}

apply plugin: 'java'
apply plugin: 'kotlin'

repositories {
    mavenCentral()
    maven {
        url  "http://dl.bintray.com/codehelixinc/slatekit"
    }
}

dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    compile "org.jetbrains.kotlin:kotlin-stdlib-jre8:$kotlin_version"
	
    // Reference the slate kit binaries here
    compile 'com.slatekit:slatekit-common:0.9.9'
    compile 'com.slatekit:slatekit-meta:0.9.9'
}
```

# Author
- **author**: Kishore Reddy
- **website**: www.slatekit.com
- **company**: www.codehelix.co


# Like Slate Kit ? :heart:
- Support Slate Kit by clicking the :star: button on the upper right of this page. :v:
- Buy [Kishore](https://patreon.com/kishorepreddy-placeholder) a coffee to work nights/weekends! ( coming soon )
- Contribute to continued development https://opencollective.com/kishorepreddy-placeholder ( coming soon )
