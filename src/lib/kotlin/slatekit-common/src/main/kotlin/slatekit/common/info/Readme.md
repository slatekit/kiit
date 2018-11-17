---
layout: start_page_mods_utils
title: module Info
permalink: /kotlin-mod-info
---

# Info

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Get/Set useful diagnostics about the system, language runtime, application and more | 
| **date**| 2018-11-16 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.info  |
| **source core** | slatekit.common.info._.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Info.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Info.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.envs.Qa
import slatekit.common.info.About
import slatekit.common.info.Host
import slatekit.common.info.Lang
import slatekit.common.info.StartInfo


// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.ResultEx
import slatekit.common.Success


```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


    // CASE 1: Get the host info
    val host = Host.local()
    host.log( { name, value -> println( "${name} : ${value}" ) } )
    println()


    // CASE 2: Get the Lang runtime info ( java version, scala version etc )
    val lang = Lang.kotlin()
    lang.log( { name, value -> println( "${name} : ${value}" ) } )
    println()


    // CASE 3: Set startup info ( env, config, log, args)
    val startup = StartInfo("-level=error", "{@app}-{@env}-{@date}.log", "{@app}.config", Qa.name)
    startup.log( { name, value -> println( "${name} : ${value}" ) } )
    println()


    // CASE 4: Set up info about your application.
    val app = About(
      id = "slatekit.examples",
      name = "My sample app",
      desc = "Sample app using Slate Kit",
      company = "slatekit",
      region = "usa.ny",
      version = "1.0.1.3",
      tags = "api,slate,app",
      group = "product division",
      url = "http://products.myapp.com",
      contact = "kishore@codehelix.co",
      examples = "myapp.exe -env=dev -level=info"
    )
    app.log( { name, value -> println( "${name} : ${value}" ) } )
    

```


## Output

```java
  // HOST INFO
  api : KRPC1
  ip : Windows 10
  origin : local
  arch : amd64
  version : 10.0
  ext1 : C:/Users/kv/AppData/Local/Temp/

  // LANGUAGE INFO
  api : scala
  home : C:/Tools/Java/jdk1.8.0_91/jre
  versionNum : 2.11.7
  version : 1.8.0_91
  origin : local
  ext1 :

  // STARTUP INFO
  args : Some([Ljava.lang.String;11c20519)
  log : {app}-{env}-{date}.log
  config : {app}.config
  env : qa

  // APP INFO
  api     : My sample app
  desc     : Sample app using Slate Kit
  group    : product division
  region   : usa.ny
  url      : "http://products.myapp.com"
  contact  : kishore@codehelix.co
  version  : 1.0.1.3
  tags     : api,slate,app
  examples : myapp.exe -env=dev -level=info
```
  