---
layout: start_page_mods_infra
title: module App
permalink: /mod-app
---

# App

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A base application with support for command line args, environment selection, configs, encryption, logging, diagnostics and more | 
| **date**| 2017-03-12T23:33:49.165 |
| **version** | 1.4.0  |
| **jar** | slate.core.jar  |
| **namespace** | slate.core.app  |
| **source core** | slate.core.app.AppProcess.scala  |
| **source folder** | [/src/lib/scala/Slate.Core/src/main/scala/slate/core/app](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Core/src/main/scala/slate/core/app)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_App.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_App.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 
import slate.common.args.Args
import slate.common.encrypt.Encryptor
import slate.common.info.About
import slate.common.logging.LoggerConsole
import slate.core.app.{AppProcess, AppRunner}
import slate.core.common.{Conf, AppContext}
import slate.entities.core.Entities



// optional 
import slate.common.Result
import slate.common.results.ResultSupportIn
import slate.core.cmds.Cmd


```

## Setup
```scala


  class SampleApp(cctx:Option[AppContext]) extends AppProcess(cctx)
  {

    /**
     * initialize app and metadata
     */
    override def onInit(): Unit =
    {
    }


    /**
     * executes the app
      *
      * @return
     */
    override def onExecute():Result[Any] =
    {
      // NOTE(s):
      // The app contains the context which stores multiple services setup above in init method.
      // The args and conf are available as members in the base class itself.

      // 1. Get the selected environment
      println( ctx.env.toString )

      // 2. Get the command line args and show the raw inputs supplied
      println ( ctx.arg.raw )

      // 3. Get the setting from base config ( common config that all other configs inherit from )
      println ( conf.getString("app.name") )

      // 4. Get value from inherited config ( env.qa.conf ) that inherits
      // from the common config ( env.conf )
      println ( conf.getString("app.name") )
      println ( conf.dbCon() )

      // 5. Get and use logger
      ctx.log.info("default logger ")

      // 6. Get app info ( showing just 1 property )
      println( ctx.app.about.name )

      // 7. Get the host computer info
      println ( ctx.app.host )

      // 8. Get the scala language runtime
      println ( ctx.app.lang )

      // 9. Get the encryptor to encrypt/decrypt
      println ( ctx.enc.fold("no encryption")( enc => enc.encrypt("hello world")))

      // 10. Execute your work here.
      info("app executing now")

      // simulate work
      Thread.sleep(1000)

      info("app completed")

      ok()
    }


    /**
     * called when app is done
     */
    override def onEnd(): Unit =
    {
      info("app shutting down")
    }


    override def collectSummaryExtra(): Option[List[(String,String)]] =
    {
      Some(List[(String,String)](
        ( ctx.app.about.name, " extra 1  = extra summary data1" ),
        ( ctx.app.about.name, " extra 2  = extra summary data2")
      ))
    }
  }
  

```

## Usage
```scala


    // 1. Load the config "env.conf" from resources
    val conf = new Conf(Some("env.conf"))

    // 2. Build the context
    val ctx = new AppContext (
      arg  = Args(),
      env  = conf.env().get,
      cfg  = conf,
      log  = new LoggerConsole(),
      ent  = new Entities(None),
      dbs  = None,
      enc  = Some(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")),
      inf  = new About(
        id = "slatekit.examples",
        name = "Slate Sample App",
        desc = "Sample to show the base application",
        company = "slatekit",
        version = "0.9.1",
        contact = "kishore@abc.co",
        region = "",
        group = "",
        url = "",
        tags = "",
        examples = ""
      )
    )

    // 3. Now run the app with context info
    AppRunner.run(new SampleApp(Some(ctx)))
    

```


## Output

```java
  Info : app executing now
  Info : app completed
  Info : app shutting down
  Info : ===============================================================
  Info : SUMMARY :
  Info : ===============================================================
  Info : name          = sampleapp
  Info : desc          = sample app to show the appprocess base class, template methods, and functionality
  Info : version       = 1.0.0.3
  Info : tags          = feature_01
  Info : args          = Some([Ljava.lang.String;@9304022)
  Info : env           = qa
  Info : config        = qa.config
  Info : log           = sampleapp-qa-2016-4-13-3-1-14.log
  Info : region        = usa.ny
  Info : started       = 2016-4-13 3:1:14
  Info : ended         = 2016-4-13 3:1:15
  Info : duration      = slate.common.TimeSpan@3ec27ce4
  Info : status        = ended
  Info : errors        = 0
  Info : error         = n/a
  Info : host.name     = KREDDY
  Info : host.ip       = Windows 7
  Info : host.origin   = local
  Info : host.version  = 6.1
  Info : lang.name     = scala
  Info : lang.version  = 2.11.7
  Info : lang.java     = local
  Info : lang.home     = C:/Tools/Java/jdk1.7.0_79/jre
  Info : sampleapp: extra 1  = extra summary data1
  Info : sampleapp: extra 2  = extra summary data2
  Info : ===============================================================
```
  