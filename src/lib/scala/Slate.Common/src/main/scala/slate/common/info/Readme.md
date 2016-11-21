# Info

| field | value  | 
|:--|:--|
| **desc** | Get/Set useful diagnostics about the system, language runtime, application and more | 
| **date**| 2016-11-21T16:49:15.687 |
| **version** | 0.9.1  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.info  |
| **source core** | slate.common.info._.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/info](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/info)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Info.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Info.scala) |
| **depends on** |   |

## Import
```scala 
// required 

import slate.common.envs.{Env, Envs}
import slate.common.info._
import slate.common.results.ResultSupportIn



// optional 
import slate.common.Result
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala


    // CASE 1: Get the host info
    val host = Host.local()
    host.log( (name, value) => println( s"${name} : ${value}" ) )
    println()


    // CASE 2: Get the Lang runtime info ( java version, scala version etc )
    val lang = Lang.asScala()
    lang.log( (name, value) => println( s"${name} : ${value}" ) )
    println()


    // CASE 3: Set startup info ( env, config, log, args)
    val startup = new StartInfo(Some(Array[String]("-level=error")), "{@app}-{@env}-{@date}.log", "{@app}.config", Env.QA)
    startup.log( (name, value) => println( s"${name} : ${value}" ) )
    println()


    // CASE 4: Set up info about your application.
    val app = new About(
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
    app.log( (name, value) => println( s"${name} : ${value}" ) )
    

```


## Output

```java
  // HOST INFO
  name : KRPC1
  ip : Windows 10
  origin : local
  arch : amd64
  version : 10.0
  ext1 : C:/Users/kv/AppData/Local/Temp/

  // LANGUAGE INFO
  name : scala
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
  name     : My sample app
  desc     : Sample app using Slate Kit
  group    : product division
  region   : usa.ny
  url      : "http://products.myapp.com"
  contact  : kishore@codehelix.co
  version  : 1.0.1.3
  tags     : api,slate,app
  examples : myapp.exe -env=dev -level=info
```
  