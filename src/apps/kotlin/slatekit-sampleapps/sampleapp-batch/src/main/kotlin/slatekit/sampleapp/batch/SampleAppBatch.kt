/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2016 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */

package slatekit.sampleapp.batch


import slatekit.common.Result
import slatekit.common.ResultEx
import slatekit.common.Success
import slatekit.common.args.ArgsSchema
import slatekit.core.app.AppProcess
import slatekit.core.app.AppRunner
import slatekit.core.common.AppContext
import slatekit.entities.core.Entities
import slatekit.integration.common.AppEntContext
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.sampleapp.core.common.AppEncryptor
import slatekit.sampleapp.core.models.User
import slatekit.sampleapp.core.services.UserService


/**
 * Entry point into the sample console application.
 * set APP_HOME=.
 * set APP_CLASSPATH=%APP_HOME%\bin\*;%APP_HOME%\lib\*;%APP_HOME%\ext\*
 * java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt -conf.dir='file://./conf/sampleapp-batch/'
 * java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt -env=dev -log.level=info -config.location = "jars"
 * java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt -env=dev -log.level=info -config.location = "conf"
 * java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt -env=dev -log.level=info -config.location = "file://./conf-sample-batch"
 * java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt --version
 * java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt --about
 * java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt ?
 *
 * @param args
 */
fun main(args: Array<String>): Unit {

    AppRunner.run(

            // 2. The custom app that extends from AppProcess
            SampleAppBatch(

                    // 3. Build the Application context for the app.
                    // NOTES:
                    // The AppContext ( see docs online and example app info )
                    // is a container to store core dependencies such as
                    //    - selected environment
                    //    - config settings
                    //    - logger
                    //    - encryptor
                    //    - info about app
                    // There are different ways you can build up the context:
                    // 1. Manually      ( explictly supply the components - see below )
                    // 2. Automatically ( using helper functions to that check command line args )
                    AppRunner.build(
                            args = args,
                            enc = AppEncryptor,
                            schema = schema,
                            converter = ::convert
                    )
            )
    )
}


// setup the command line arguments.
// NOTE:
// 1. These values can can be setup in the env.conf file
// 2. If supplied on command line, they override the values in .conf file
// 3. If any of these are required and not supplied, then an error is display and program exits
// 4. Help text can be easily built from this schema.
val schema = ArgsSchema()
        .text("env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
        .text("region", "the region linked to app", false, "us", "us", "us|europe|india|*")
        .text("config.loc", "location of config files", false, "jar", "jar", "jar|conf")
        .text("log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")


/**
 * Converts a built AppContext into a final one for use in this app.
 * NOTE: This is allow customization of any member of the app context:
 * e.g.
 * - encryptor
 * - logger
 * - database
 * - metadata etc
 *
 * @param ctx
 * @return
 */
fun convert(ctx: AppContext): AppContext {
    return ctx.copy(inf = ctx.inf.copy(url = "http://apps.companyabc.com/wiki"))
}


/**
 * Sample console application.
 *
 * IMPORTANT
 * 1. You can further extend the slate AppProcess ( refer to AppBase in SampleApp.Core
 * 2. The onInit method is ONLY provided here to show how the context can be set up
 * 3. The AppBase class ( in SampleApp.Core ) can be used to have a common base class with
 *    the onInit method already implemented for your specific needs.
 *
 * NOTE(s):
 * 1. you can extend from AppBase ( SampleApp.Core ) to avoid initializing context in onInit here
 * 2. command line arguments are optional but set up here for demo purposes
 */
class SampleAppBatch(context: AppContext?) : AppProcess(context) {
    val ctxEnt = AppEntContext.fromAppContext(ctx)

    /**
     * initialize app context, database and ORM / entities.
     *
     * NOTE: If you extend this class from AppBase ( see SampleApp.Core project ),
     * which contains this init code. That way you don't have to duplicate if for the app types
     * below. This approach works in the initialization of app context is same for all the app types.
     * 1. console
     * 2. cli
     * 3. server
     */
    override fun onInit(): Unit {
        // 4. Setup the User entity services
        // NOTE(s):
        // 1. See the ORM documentation for more info.
        // 2. The entity services uses a Generic Service/Repository pattern for ORM functionality.
        // 3. The services support CRUD operations out of the box for single-table mapped entities.
        // 4. This uses an In-Memory repository for demo but you can use EntityRepoMySql for MySql
        ctxEnt.ent.register<User>(isSqlRepo = false,
                entityType = User::class,
                serviceType = UserService::class,
                repository = EntityRepoInMemory<User>(User::class),
                serviceCtx = ctxEnt)
    }


    /**
     * You implement this method to executes the app
     *
     * @return
     */
    override fun onExecute(): ResultEx<Any> {
        info("app executing now")

        info("conf source:" + conf.origin())
        info("app arg: " + appMeta().start.args)
        info("app dir: " + appMeta().start.rootDir)
        info("app env: " + appMeta().start.env)
        info("app cfg: " + appMeta().start.config)
        info(conf.getString("env.desc"))
        info(conf.getString("log.level"))

        // Feature 1: Log methods available from LogSupport trait
        // NOTE: This uses the console logger setup in the context in init.
        info("")
        info("LOGGING examples: ==================================================")
        debug("debug example using trait method from LogSupportIn")
        info("info  example using trait method from LogSupportIn")
        warn("warn  example using trait method from LogSupportIn")
        error("error example using trait method from LogSupportIn")
        fatal("fatal example using trait method from LogSupportIn")

        debug( { "lazy debug example using trait method from LogSupportIn" } )
        info ( { "lazy info  example using trait method from LogSupportIn" } )
        warn ( { "lazy warn  example using trait method from LogSupportIn" } )
        error( { "lazy error example using trait method from LogSupportIn" } )
        fatal( { "lazy fatal example using trait method from LogSupportIn" } )
        info("")

        // Feature 2: Encrypt / Decrypt support using the Encryptor setup in context
        // NOTE: This uses the encryptor setup in the context in the init.
        info("ENCRYPTION examples: ===============================================")
        val encrypted = encrypt("Hello World")
        info("encrypted 'hello world' = ${encrypted}")
        info("decrypted '${encrypted}' = " + decrypt(encrypted))
        info("")

        // Feature 3: Get config settings
        // NOTE: This uses the config setup in the context in the init
        info("CONFIG examples: ==================================================")
        info("app.name = " + conf.getString("app.name"))
        info("====================================================================")
        info("simulating work for 1 second. please wait...")
        Thread.sleep(1000)

        info("app completed")

        return Success(true)
    }


    /**
     * HOOK for when app is shutting down
     */
    override fun onEnd(): Unit {
        info("app shutting down")
    }


    /**
     * HOOK for adding items to the summary of data shown at the end of app execution
     */
    override fun collectSummaryExtra(): List<Pair<String, String>> {
        return listOf(
                Pair("region", ctx.arg.getStringOrElse("region", "n/a"))
        )
    }
}