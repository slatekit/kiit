# App

| field | value  | 
|:--|:--|
| **desc** | A base application with support for command line args, environment selection, configs, encryption, logging, diagnostics and more | 
| **date**| 2016-11-21T17:15:23.214 |
| **version** | 0.9.1  |
| **jar** | slate.core.jar  |
| **namespace** | slate.core.app  |
| **source core** | slate.core.app.AppProcess.scala  |
| **source folder** | [/src/lib/scala/Slate.Core/src/main/scala/slate/core/app](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Core/src/main/scala/slate/core/app)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_App.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_App.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 
import slate.common.app.AppMeta
import slate.common.encrypt.Encryptor
import slate.common.info.{Lang, Host, About}
import slate.common.logging.LoggerConsole
import slate.core.app.{AppProcess, AppRunner}
import slate.core.common.{AppContext, Conf}
import slate.entities.core.Entities
import slate.common.databases.DbLookup._



// optional 
import slate.common.{Result}
import slate.common.results.ResultSupportIn
import slate.core.cmds.Cmd
import scala.collection.mutable.ListBuffer


```

## Setup
```scala


  class SampleApp extends AppProcess
  {

    /**
     * initialize app and metadata
     */
    override def onInit(): Unit =
    {
      options.printSummaryBeforeExec = false
      options.printSummaryOnShutdown = true

      // The base init can take care of some things such as:
      // 1. loading the common "resources/env.conf" config file as "confBase"
      // 2. loading the selected environment "env" from either
      //    the command line first ( -env="dev1:dev" ) or "env.conf"
      // 3. setting up "conf" inherited config from "env.dev.conf" + "env.conf"

      // Initialize the context with common app info
      // The context contains all the various services that are common
      // to most apps, you can then make this available to other parts of your application.

      ctx = new AppContext (
        env  = conf.env().get,
        cfg  = conf,
        log  = new LoggerConsole(getLogLevel()),
        ent  = new Entities(Option(dbs())),
        dbs  = Some(dbs()),
        enc  = Some(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")),
        inf  = aboutApp()
      )
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
      println ( args.raw )

      // 3. Get the setting from base config ( common config that all other configs inherit from )
      println ( confBase.getString("app.name") )

      // 4. Get value from inherited config ( env.qa.conf ) that inherits
      // from the common config ( env.conf )
      println ( conf.getString("app.name") )
      println ( conf.getBool("log.enabled") )
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
    override def onShutdown(): Unit =
    {
      info("app shutting down")
    }


    override def collectSummaryExtra(): Option[List[(String,String)]] =
    {
      Some(List[(String,String)](
        ( meta.about.name, " extra 1  = extra summary data1" ),
        ( meta.about.name, " extra 2  = extra summary data2")
      ))
    }


    override def aboutApp():About = {
      new About(
        id = "slatekit.examples",
        name = "Slate Sample App",
        desc = "Sample to show the base application",
        company = "slatekit",
        version = "0.9.1",
        contact = "kishore@codehelix.co"
      )
    }
  }
  

```

## Usage
```scala


    AppRunner.run(new SampleApp(), Some(Array("-env='qa1.qa'", "-log.level=info")))
    

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
  