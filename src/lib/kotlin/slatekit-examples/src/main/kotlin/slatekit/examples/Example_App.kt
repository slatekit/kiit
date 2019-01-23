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
import slatekit.core.app.AppOptions
import slatekit.core.app.AppProcess
import slatekit.core.app.AppRunner

//</doc:import_required>

//<doc:import_examples>
import slatekit.common.Result
import slatekit.common.ResultEx
import slatekit.common.Success
import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
import slatekit.common.conf.Config
import slatekit.common.encrypt.B64Java8
import slatekit.common.info.About
import slatekit.common.log.LoggerConsole
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.LogsDefault
import slatekit.common.results.ResultFuncs.success
import slatekit.common.toResultEx
import slatekit.core.cmds.Cmd
import slatekit.core.common.AppContext
import slatekit.entities.core.Entities

//</doc:import_examples>

//<doc:setup>
// Step 1: Extend your application from AppProcess which
// provides life-cycle methods, and much of the boiler-plate code.
//
// NOTE: Ensure your environment files are setup:
// Refer to sample app for more info.
// - resources/env.conf
// - resources/env.local.conf
// - resources/env.dev.conf
//
// CONTEXT:
// The AppProcess must have an AppContext ( see docs online and example below for more info)
// which is a container to store core dependencies such as
// - selected environment
// - config settings
// - logger
// - encryptor
// - info about app
//
// There are different ways you can build up the context
// 1. Manually      ( explictly supply the components - see below )
// 2. Automatically ( using helper functions to that check command line args )
class SampleApp(ctx: AppContext) : AppProcess(ctx) {

    /**
     * Options for the application that you can override
     * E.g. Just shown for example, not needed to override.
     * By default, the app process prints a summary of your app ( see example at bottom of this file )
     * at the end of the app process, but you can customize here
     * if you want the summary to be shown at the beginning of life-cycle.
     */
    override val options = AppOptions(
            printSummaryBeforeExec = false,
            printSummaryOnShutdown = true
    )


    /**
     * Life-cycle init hook: for your app to perform any initialization
     */
    override fun onInit(): Unit {
        println("app initialized")
    }


    /**
     * Life-cycle execution hook: for your app to perform the main logic
     *
     * @return
     */
    override fun onExecute(): ResultEx<Any> {
        // The AppContext ( ctx ) is required for the AppProcess and will be
        // available for derived classes to access its components.

        // 1. Get the selected environment name/mode ( local.dev )
        println(ctx.env.name)
        println(ctx.env.mode)
        println(ctx.env.toString())

        // 2. Get the command line args and show the raw inputs supplied
        println(ctx.arg.raw)

        // 3. Get the setting from base config ( common config that all other configs inherit from )
        println(conf.getString("app.api"))

        // 4. Get value from inherited config ( env.qa.conf ) that inherits
        // from the common config ( env.conf )
        println(conf.getString("app.api"))
        println(conf.dbCon())

        // 5. Get and use logger
        ctx.logs.getLogger().info("default logger ")

        // 6. Get app info ( showing just 1 property )
        println(ctx.app.about.name)

        // 7. Get the host computer info
        println(ctx.app.host)

        // 8. Get the java runtime info
        println(ctx.app.lang)

        // 9. Get the encryptor to encrypt/decrypt
        println(ctx.enc?.let { enc -> enc.encrypt("hello world") })

        // 10. Execute your work here.
        info("app executing now")

        // simulate work
        Thread.sleep(1000)

        info("app completed")

        return Success("")
    }


    /**
     * Life-cycle end hook: called when app is shutting down
     */
    override fun onEnd(): Unit {
        info("app shutting down")
    }


    /**
     * template method: allows you to build up info to show in the summary
     * displayed at the end of the application
     */
    override fun collectSummaryExtra(): List<Pair<String, String>>? {
        return listOf(
                Pair(ctx.app.about.name, " extra 1  = extra summary data1"),
                Pair(ctx.app.about.name, " extra 2  = extra summary data2")
        )
    }
}
//</doc:setup>


class Example_App : Cmd("app") {

    override fun executeInternal(args: Array<String>?): ResultEx<Any> {
        //<doc:examples>
        // NOTE: The application uses an AppContext ( see docs for more info )
        // which contains many core dependencies available in a single container
        // that can be easily passed around if needed.
        // There are different ways you can build up the context:
        // 1. Manually      ( explictly supply the components - see below )
        // 2. Automatically ( using helper functions to that check command line args )


        // APPROACH 1: Manually / Explicitly build up the AppContext
        // Load the config "env.conf" from resources
        val conf = Config("env.conf")
        val ctx = AppContext(
                arg = Args.default(),
                env = conf.env(),
                cfg = conf,
                logs = LogsDefault,
                ent = Entities(),
                dbs = null,
                enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                inf = About(
                        id = "slatekit.examples",
                        name = "Slate Sample App",
                        desc = "Sample to show the base application with manually built context",
                        company = "slatekit",
                        version = "0.9.1",
                        contact = "kishore@abc.co",
                        region = "",
                        group = "",
                        url = "",
                        tags = "",
                        examples = ""
                ),
                state = Success(true, msg ="manually built").toResultEx()
        )
        // Now run the app with context info with
        // the help of the AppRunner which will call the life-cycle events.
        AppRunner.run( SampleApp( ctx ) )


        // APPROACH 2: Automatically build the AppContext using the AppRunner.build function
        // that will check the command line args for selected environment and other info
        // 1. args      : command line arguments
        // 2. enc       : the encryptor to handle encryption and decryption of args/settings etc.
        // 3. schema    : the schema representing allowed command line arguments
        // 4. converter : a callback to convert/modify the application 1 last time before it is
        //                finally supplied to your SampleApp constructor.
        // NOTES:
        // - Ensure your config files are available e.g. resources/env.conf
        // - Env : By default, the first supported environment is used which is local "env.local"
        // - Conf: By default, the config file associated w/ the environment is loaded "env.local.conf"
        // - You can store info about the your app in your config file and that can be loaded.
        val res = AppRunner.run (

                    SampleApp (

                        AppRunner.build (

                            args      = args,
                            enc       = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                            schema    =  ArgsSchema()
                                        .text("env"      , "the environment ", false, "dev"  , "dev"  , "loc|dev|qa1" )
                                        .text("log.level", "the log level"   , false, "info" , "info" , "debug|info"),
                            converter = { context -> context.copy( inf = context.inf.copy(
                                                desc = "Sample app to show the base application using auto-built context",
                                                url = "http://apps.companyabc.com/wiki")
                                        )}
                        )
                    )
        )
        return res
        //</doc:examples>
    }

    /*
    //<doc:output>
```bat
 Info  : app executing now
 Info  : app completed
 Info  : app shutting down
 Info  : ===============================================================
 Info  : SUMMARY :
 Info  : ===============================================================
 Info  : name              = Slate Sample App
 Info  : desc              = Sample to show the base application
 Info  : version           = 0.9.1
 Info  : tags              =
 Info  : group             =
 Info  : region            =
 Info  : contact           = kishore@abc.co
 Info  : url               =
 Info  : args              =
 Info  : env               = dev
 Info  : config            = env.conf
 Info  : log               = local:dev
 Info  : started           = 2017-07-11T11:54:13.132-04:00[America/New_York]
 Info  : ended             = 2017-07-11T11:54:18.408-04:00[America/New_York]
 Info  : duration          = PT5.276S
 Info  : status            = ended
 Info  : errors            = 0
 Info  : error             = n/a
 Info  : host.name         = KRPC1
 Info  : host.ip           =
 Info  : host.origin       = Windows 10
 Info  : host.version      = 10.0
 Info  : lang.name         = kotlin
 Info  : lang.version      = 1.8.0_91
 Info  : lang.vendor       = Oracle Corporation
 Info  : lang.java         = local
 Info  : lang.home         = C:/Tools/Java/jdk1.8.0_91/jre
 Info  : Slate Sample App =  extra 1  = extra summary data1
 Info  : Slate Sample App =  extra 2  = extra summary data2
 Info  : ===============================================================

```
    //</doc:output>
    */
}
