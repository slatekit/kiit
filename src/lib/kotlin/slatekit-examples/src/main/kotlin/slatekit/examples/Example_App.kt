/**
 * <slate_header>
 * author: Kishore Reddy
 * url: www.github.com/code-helix/slatekit
 * copyright: 2015 Kishore Reddy
 * license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
 * desc: A tool-kit, utility library and server-backend
 * usage: Please refer to license on github for more info.
 * </slate_header>
 */

package slatekit.examples

//<doc:import_required>
import kotlinx.coroutines.runBlocking
import slatekit.common.Context
import slatekit.app.AppOptions
import slatekit.app.App
import slatekit.app.AppRunner

//</doc:import_required>

//<doc:import_examples>
import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
import slatekit.common.conf.Config
import slatekit.common.utils.B64Java8
import slatekit.common.info.About
import slatekit.common.encrypt.Encryptor
import slatekit.common.info.Build
import slatekit.common.info.Sys
import slatekit.common.log.LogsDefault
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.common.envs.Envs
import slatekit.common.info.Info
import slatekit.db.Db
import slatekit.entities.Entities
import slatekit.integration.common.AppEntContext
import slatekit.providers.logs.logback.LogbackLogs
import slatekit.results.Success
import slatekit.results.Try

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
class SampleApp(ctx: Context) : App<Context>(ctx, AppOptions(
        printSummaryBeforeExec = false,
        printSummaryOnShutdown = true
)) {

    /**
     * Life-cycle init hook: for your app to perform any initialization
     */
    override suspend fun init(): Try<Boolean> {
        println("app initialized")
        return Success(true)
    }


    /**
     * Life-cycle execution hook: for your app to perform the main logic
     *
     * @return
     */
    override suspend fun exec(): Try<Any> {
        // The AppContext ( ctx ) is required for the AppProcess and will be
        // available for derived classes to access its components.

        // 1. Get the selected environment name/mode ( local.dev )
        println(ctx.envs.name)
        println(ctx.envs.mode)
        println(ctx.envs.toString())

        // 2. Get the command line args and show the raw inputs supplied
        println(ctx.args.raw)

        // 3. Get the setting from base config ( common config that all other configs inherit from )
        println(ctx.conf.getString("app.api"))

        // 4. Get value from inherited config ( env.qa.conf ) that inherits
        // from the common config ( env.conf )
        println(ctx.conf.getString("app.api"))
        println(ctx.conf.dbCon())

        // 5. Get and use logger
        ctx.logs.getLogger().info("default logger ")

        // 6. Get app info ( showing just 1 property )
        println(ctx.info.about.name)

        // 7. Get the host computer info
        println(ctx.info.system.host)

        // 8. Get the java runtime info
        println(ctx.info.system.lang)

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
    override suspend fun end(): Try<Boolean> {
        info("app shutting down")
        return Success(true)
    }


//    /**
//     * template method: allows you to build up info to show in the summary
//     * displayed at the end of the application
//     */
//    override fun collectSummaryExtra(): List<Pair<String, String>>? {
//        return listOf(
//                Pair(ctx.app.name, " extra 1  = extra summary data1"),
//                Pair(ctx.app.name, " extra 2  = extra summary data2")
//        )
//    }
}
//</doc:setup>


class Example_App : Command("app") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:examples>
        // NOTE: The application uses an AppContext ( see docs for more info )
        // which contains many core dependencies available in a single container
        // that can be easily passed around if needed.
        // There are different ways you can build up the context:
        // 1. Manually      ( explictly supply the components - see below )
        // 2. Automatically ( using helper functions to that check command line args )


        // APPROACH 1: Manually / Explicitly build up the AppContext
        // Load the config "env.conf" from resources
        val conf = Config.of("env.conf")
        val ctx = AppEntContext(
                args = Args.default(),
                envs = Envs.defaults().select(conf.env().name),
                conf = conf,
                logs = LogsDefault,
                ent = Entities({ con -> Db(con) }),
                enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                info = Info(
                        About(
                                area = "slatekit",
                                name = "sample-app",
                                desc = "Sample to show the base application with manually built context",
                                company = "slatekit",
                                version = "0.9.1",
                                contact = "kishore@abc.co",
                                region = "",
                                url = "",
                                tags = "",
                                examples = ""
                        ),
                        Build.empty,
                        Sys.build()
                )
        )
        // Now run the app with context info with
        // the help of the AppRunner which will call the life-cycle events.
        runBlocking {
            AppRunner.run(SampleApp(ctx))
        }


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
        val res = runBlocking {
            AppRunner.run(
                    rawArgs = request.args.raw.toTypedArray(),
                    schema = ArgsSchema(),
                    enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                    logs = LogbackLogs(),
                    about = About.none,
                    builder = { ctx -> SampleApp(ctx) }
            )
        }
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
