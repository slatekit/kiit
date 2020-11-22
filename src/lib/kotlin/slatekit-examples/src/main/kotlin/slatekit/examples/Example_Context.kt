/**
<slate_header>
author: Kishore Reddy
url: www.github.com/code-helix/slatekit
copyright: 2015 Kishore Reddy
license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
desc: A tool-kit, utility library and server-backend
usage: Please refer to license on github for more info.
</slate_header>
 */


package slatekit.examples

//<doc:import_required>
import slatekit.app.AppUtils
import slatekit.context.AppContext
//</doc:import_required>

//<doc:import_examples>
import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
import slatekit.common.conf.Config
import slatekit.common.utils.B64Java8
import slatekit.common.crypto.Encryptor
import slatekit.common.info.*
import slatekit.common.log.LogsDefault
import slatekit.context.Context
import slatekit.common.envs.Envs
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.results.*

//</doc:import_examples>


class Example_Context : Command("cmd") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:examples>

        // OVERVIEW:
        // The AppContext is a container for common dependencies
        // across different components in an application.
        // The main design goal of the context is to contain
        // many of the dependencies in 1 object so they can be
        // easily passed around where needed and made accessible.
        //
        // INCLUDED:
        // 1.  args: parsed command line arguments
        // 2.  env : the selected environment ( dev, qa, uat, prod )
        // 3.  conf: the config settings
        // 4.  log : the global logger ( you can easily have local loggers )
        // 5.  inf : info about the application (name, desc, group, etc)
        // 6.  ent : the entities which are mapped ORM entities ( optional )
        // 7.  sys : the system info including host and language
        // 8.  dirs: the standardized runtime folders for the app
        // 9.  enc : the encryption service to handle encryption/decryption
        // 10. app : the application info/about
        //
        // NOTES:
        // 1. Many of these are OPTIONAL
        // 2. It is implemented as a data class
        // 3. The Context is an interface defined in slatekit.context.Context
        // 4. The AppContext is an implementation defined in slatekit.core.common.AppContext
        // 5. To customize the context for different components, you
        //    either extend the Context, and/or copy the AppContext
        //    with modifications
        // CASE 1: Build a simple context with minimal info that includes:
        val ctx = AppContext.simple("demoapp")

        // CASE 2: Build a simple context with minimal info that includes:
        // - default arguments ( command line )
        // - dev environment
        // - Config() representing conf settings from "env.conf"
        // - default logger ( console )
        // - entities ( registrations for orm )
        val ctx2 = AppContext(
                args = Args.empty(),
                envs = Envs.defaults(),
                conf = Config(),
                logs = LogsDefault,
                info = Info(
                        About(
                                area = "department1",
                                name = "sample-app-1",
                                desc = "Sample application 1",
                                company = "Company 1",
                                region = "New York",
                                url = "http://company1.com/dep1/sampleapp-1",
                                contact = "dept1@company1.com",
                                version = "1.0.1",
                                tags = "sample app slatekit",
                                examples = ""
                        ),
                        Build.empty,
                        Sys.build()
                )
        )

        // CASE 3: Typically your application will want to derive the
        // context from either the command line args and or the config
        // There is a builder method takes command line arguments and
        // other inputs and constructs the context. This example shows
        // only providing the arguments to build the context
        //
        // NOTE: This checks for "-env" arg and loads the corresponding
        // inherited config environment (refer to config in utils for more info )
        // but basically, this loads the env.dev.conf with fallback to env.conf
        // 1. "env.dev.conf" ( environment specific )
        // 2. "env.conf"     ( common / base line   )

        // CASE 4 : This example shows providing the args schema for parsing the args
        // refer to Args in utils for more info.
        // NOTE: There are additional parameters on the build function ( callbacks )
        // to allow you to get the context and modify it before it is returned.
        val ctx3 = AppUtils.context(
                envs = Envs.defaults(),
                args = Args.parse("-env=dev -log -log.level=debug").getOrElse { Args.empty() },
                enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                schema = ArgsSchema()
                        .text("env", "the environment to run in", "", false, "dev", "dev", "dev1|qa1|stg1|pro")
                        .text("region", "the region linked to app", "", false, "us", "us", "us|europe|india|*")
                        .text("config.loc", "location of config files", "", false, "jar", "jar", "jar|conf")
                        .text("log.level", "the log level for logging", "", false, "info", "info", "debug|info|warn|error"),
                about = About("app id", "sample app", "app desc"),
                logs = LogsDefault
        )
        showContext(ctx3)


        // CASE 4: Access common info
        println(ctx.args.line       )
        println(ctx.args.action     ) // e.g. "action" if using "service.action -key=1"
        println(ctx.args.get("env") )
        println(ctx.args.getStringOrNull("log.level") )
        println(ctx.args.getStringOrElse("log.level", "warn") )


        println("name:" + ctx.envs.name   ) // "loc" ( representing local )
        println("env :" + ctx.envs.env    ) // "dev" EnvMode: ( Dev | Qat | Uat | Pro )
        println("key :" + ctx.envs.key    ) // "loc:dev"  {name}:{env}
        println("curr:" + ctx.envs.current) // current Env object
        println("all :" + ctx.envs.all    ) // list of all Env environments

        //ctx.conf.getInt("paging.batchSize")
        //ctx.conf.getIntOrNull("paging.batchSize")
        //ctx.conf.getIntOrElse("paging.batchSize", 4)

        val logger1 = ctx.logs.getLogger()
        val logger2 = ctx.logs.getLogger(name = "service1")
        val logger3 = ctx.logs.getLogger(Example_Jobs::class.java)
        logger1.level
        logger1.name
        logger1.debug("Debug message")
        logger1.info ("Info message")
        logger1.warn ("Warn message")
        logger1.error("Error message")
        logger1.fatal("Fatal message")

        println(ctx.enc?.encrypt("raw text"))
        println(ctx.enc?.decrypt(""))

        ctx.info.about.area
        ctx.info.about.name
        ctx.info.about.desc
        ctx.info.about.company
        ctx.info.about.version

        ctx.info.build.date
        ctx.info.build.branch
        ctx.info.build.commit
        ctx.info.build.version

        //</doc:examples>
        return Success("")
    }


    fun showContext(ctx: Context) {
        println("args: " + ctx.args)
        println("env : " + ctx.envs)
        println("conf: " + ctx.conf)
        println("logs: " + ctx.logs)
        println("dirs: " + ctx.dirs)
        println("app : " + ctx.info.about)
        println("host: " + ctx.info.system.host)
    }

}

