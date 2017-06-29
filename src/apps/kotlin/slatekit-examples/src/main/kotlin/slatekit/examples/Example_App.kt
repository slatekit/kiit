/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slatekit.examples

//<doc:import_required>
import slatekit.core.app.AppProcess
import slatekit.core.common.AppContext

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.encrypt.Encryptor
import slatekit.common.info.About
import slatekit.common.log.LoggerConsole
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.app.AppRunner
import slatekit.entities.core.Entities

//</doc:import_examples>


class Example_App : Cmd("auth") {


  //<doc:setup>
  class SampleApp(cctx:AppContext) : AppProcess(cctx) {

    /**
     * initialize app and metadata
     */
    override fun onInit(): Unit 
    {
    }


    /**
     * executes the app
      *
      * @return
     */
    override fun onExecute():Result<Any>
    {
      // NOTE(s):
      // The app contains the context which stores multiple services setup above in init method.
      // The args and conf are available as members in the base class itself.

      // 1. Get the selected environment
      println( ctx.env.toString() )

      // 2. Get the command line args and show the raw inputs supplied
      println ( ctx.arg.raw )

      // 3. Get the setting from base config ( common config that all other configs inherit from )
      println ( conf.getString("app.api") )

      // 4. Get value from inherited config ( env.qa.conf ) that inherits
      // from the common config ( env.conf )
      println ( conf.getString("app.api") )
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
      println ( ctx.enc?.let { enc -> enc.encrypt("hello world") })

      // 10. Execute your work here.
      info("app executing now")

      // simulate work
      Thread.sleep(1000)

      info("app completed")

      return ok()
    }


    /**
     * called when app is done
     */
    override fun onEnd(): Unit
    {
      info("app shutting down")
    }


    override fun collectSummaryExtra(): List<Pair<String,String>>?
    {
      return listOf(
        Pair( ctx.app.about.name, " extra 1  = extra summary data1" ),
        Pair( ctx.app.about.name, " extra 2  = extra summary data2")
      )
    }
  }
  //</doc:setup>



  override fun executeInternal(args: Array<String>?) : Result<Any>
  {
    //<doc:examples>
    // 1. Load the config "env.conf" from resources
    val conf = Config("env.conf")

    // 2. Build the context
    val ctx = AppContext (
      arg  = Args.default(),
      env  = conf.env(),
      cfg  = conf,
      log  = LoggerConsole(),
      ent  = Entities(),
      dbs  = null,
      enc  = Encryptor("wejklhviuxywehjk", "3214maslkdf03292"),
      inf  = About(
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
    return AppRunner.run(SampleApp(ctx))
    //</doc:examples>
  }

  /*
  //<doc:output>
```java
  Info : app executing now
  Info : app completed
  Info : app shutting down
  Info : ===============================================================
  Info : SUMMARY :
  Info : ===============================================================
  Info : api          = sampleapp
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
  Info : duration      = slatekit.common.TimeSpan@3ec27ce4
  Info : status        = ended
  Info : errors        = 0
  Info : error         = n/a
  Info : host.api     = KREDDY
  Info : host.ip       = Windows 7
  Info : host.origin   = local
  Info : host.version  = 6.1
  Info : lang.api     = scala
  Info : lang.version  = 2.11.7
  Info : lang.java     = local
  Info : lang.home     = C:/Tools/Java/jdk1.7.0_79/jre
  Info : sampleapp: extra 1  = extra summary data1
  Info : sampleapp: extra 2  = extra summary data2
  Info : ===============================================================
```
  //</doc:output>
  */
}
