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


import slatekit.common.args.ArgsSchema
import slatekit.common.info.About
import slatekit.core.app.App
import slatekit.core.app.AppRunner
import slatekit.core.common.AppContext
import slatekit.integration.common.AppEntContext
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.providers.logs.logback.LogbackLogs
import slatekit.results.Success
import slatekit.results.Try
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
fun main(args: Array<String>) {

    AppRunner.run(
            rawArgs = args,
            about   = SampleAppBatch.about,
            schema  = SampleAppBatch.schema,
            enc     = AppEncryptor,
            logs    = LogbackLogs(),
            builder = { ctx:AppContext -> SampleAppBatch(ctx) }
    )
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
class SampleAppBatch(context: AppContext) : App<AppEntContext>(AppEntContext.fromAppContext(context)) {

    companion object {

        /**
         * setup the command line arguments.
         * NOTE:
         * 1. These values can can be setup in the env.conf file
         * 2. If supplied on command line, they override the values in .conf file
         * 3. If any of these are required and not supplied, then an error is display and program exits
         * 4. Help text can be easily built from this schema.
         */
        val schema = ArgsSchema()
                .text("env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
                .text("region", "the region linked to app", false, "us", "us", "us|europe|india|*")
                .text("config.loc", "location of config files", false, "jar", "jar", "jar|conf")
                .text("log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")


        /**
         * Default static info about the app.
         * This can be overriden in your env.conf file
         */
        val about = About(
                id = "sample_app_batch",
                name = "Sample App Batch",
                desc = "Sample Batch / Script ",
                company = "Slatekit",
                region = "NY",
                version = "1.0.0",
                url = "www.slatekit.com",
                group = "codehelix",
                contact = "kishore@codehelix.co",
                tags = "sample, template, app",
                examples = "http://www.slatekit.com/kotlin-core-app.html"
        )
    }

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
    override fun init(): Try<Boolean> {
        // 4. Setup the User entity services
        // NOTE(s):
        // 1. See the ORM documentation for more info.
        // 2. The entity services uses a Generic Service/Repository pattern for ORM functionality.
        // 3. The services support CRUD operations out of the box for single-table mapped entities.
        // 4. This uses an In-Memory repository for demo but you can use EntityRepoMySql for MySql
        ctx.ent.register(
                entityType = User::class,
                serviceType = UserService::class,
                repository = EntityRepoInMemory<User>(User::class),
                serviceCtx = ctx)
        return Success(true)
    }


    /**
     * You implement this method to executes the app
     *
     * @return
     */
    override fun execute(): Try<Boolean> {
        info("app executing now")

        info("conf source:" + ctx.cfg.origin())
        info("app arg: " + ctx.start.args)
        info("app dir: " + ctx.start.rootDir)
        info("app env: " + ctx.start.env)
        info("app cfg: " + ctx.start.config)
        info(ctx.cfg.getString("env.desc"))
        info(ctx.cfg.getString("log.level"))

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
        info("app.name = " + ctx.cfg.getString("app.name"))
        info("====================================================================")
        info("simulating work for 1 second. please wait...")
        Thread.sleep(1000)

        info("app completed")

        return Success(true)
    }


    /**
     * HOOK for when app is shutting down
     */
    override fun end(): Try<Boolean> {
        info("app shutting down")
        return Success(true)
    }
}
