/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */


package slatekit.examples

//<doc:import_required>
import slatekit.common.Context
import slatekit.core.common.AppContext
//</doc:import_required>

//<doc:import_examples>
import slatekit.common.Result
import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
import slatekit.common.conf.Config
import slatekit.common.envs.Dev
import slatekit.common.envs.Env
import slatekit.common.info.About
import slatekit.common.info.Host
import slatekit.common.info.Lang
import slatekit.common.log.LoggerConsole
import slatekit.common.results.BAD_REQUEST
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.app.AppRunner
import slatekit.entities.core.Entities
import slatekit.core.cmds.Cmd

//</doc:import_examples>


class Example_Context : Cmd("cmd") {

    override fun executeInternal(args: Array<String>?): Result<Any> {
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
        // 7.  host: the computer host running the app
        // 8.  lang: the version info of java/scala running the app
        // 9.  dbs : a list of available database connections
        // 10. dirs: the standardized runtime folders for the app
        // 11. enc : the encryption service to handle encryption/decryption
        //
        // NOTES:
        // 1. Many of these are OPTIONAL
        // 2. It is implemented as a data class
        // 3. The Context is an interface defined in slatekit.common.Context
        // 4. The AppContext is an implementation defined in slatekit.core.common.AppContext
        // 5. To customize the context for different components, you
        //    either extend the Context, and/or copy the AppContext
        //    with modifications

        // CASE 1: Build info about the app ( to be reused for the examples below )
        val info = About(
            id = "sample-app-1",
            name = "Sample App-1",
            desc = "Sample application 1",
            company = "Company 1",
            group = "Department 1",
            region = "New York",
            url = "http://company1.com/dep1/sampleapp-1",
            contact = "dept1@company1.com",
            version = "1.0.1",
            tags = "sample app slatekit scala",
            examples = ""
        )

        // CASE 2: Build a simple context with minimal info that includes:
        // - default arguments ( command line )
        // - dev environment
        // - Config() representing conf settings from "env.conf"
        // - default logger ( console )
        // - entities ( registrations for orm )
        val ctx1 = AppContext(
            arg = Args.default(),
            env = Env("dev", Dev, "ny", "dev environment"),
            cfg = Config(),
            log = LoggerConsole(),
            ent = Entities(),
            inf = info,
            host = Host.local(),
            lang = Lang.kotlin()
        )

        // CASE 3: Typically your application will want to derive the
        // context from either the command line args and or the config
        // There is a builder method takes command line arguments and
        // other inputs and constructs the context. This example shows
        // only providing the arguments to build the context

        // CASE 3A: This checks for "-env" arg and loads the corresponding
        // inherited config environment (refer to config in utils for more info )
        // but basically, this loads the env.dev.conf with fallback to env.conf
        // 1. "env.dev.conf" ( environment specific )
        // 2. "env.conf"     ( common / base line   )
        val ctx2 = AppRunner.build(arrayOf("-env=dev -log -log.level=debug"))
        showContext(ctx2)

        // CASE 3B: This example shows providing the args schema for parsing the args
        // refer to Args in utils for more info.
        // NOTE: There are additional parameters on the build function ( callbacks )
        // to allow you to get the context and modify it before it is returned.
        val schema = ArgsSchema()
            .text("env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
            .text("region", "the region linked to app", false, "us", "us", "us|europe|india|*")
            .text("config.loc", "location of config files", false, "jar", "jar", "jar|conf")
            .text("log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")
        val ctx3 = AppRunner.build(arrayOf("-env=dev -log -log.level=debug"), schema)
        showContext(ctx3)

        // CASE 3C: You can also build an error context representing an invalid context
        val ctx4 = AppContext.err(BAD_REQUEST, "Bad context, invalid inputs supplied")
        showContext(ctx4)

        //</doc:examples>
        return ok()
    }


    fun showContext(ctx: AppContext): Unit {
        println("args: " + ctx.arg)
        println("env : " + ctx.env)
        println("conf: " + ctx.cfg)
        println("log : " + ctx.log)
        println("inf : " + ctx.inf)
        println("dirs: " + ctx.dirs)
        println("host: " + ctx.host)
    }

}

