package slatekit.samples.api


// Slate Kit - Common Utilities
import slatekit.common.args.ArgsSchema
import slatekit.common.crypto.*
import slatekit.common.info.*
import slatekit.results.*

// Slate Kit - App ( provides args, help, life-cycle methods, etc )
import slatekit.app.App
import slatekit.app.AppOptions
import slatekit.common.utils.B64Java8
import slatekit.context.Context


class App(ctx: Context) : App<Context>(ctx, AppOptions(showWelcome = true)) {

    companion object {

        // setup the command line arguments.
        // NOTE:
        // 1. These values can can be setup in the env.conf file
        // 2. If supplied on command line, they override the values in .conf file
        // 3. If any of these are required and not supplied, then an error is display and program exists
        // 4. Help text can be easily built from this schema.
        val schema = ArgsSchema()
                .text("","env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
                .text("","region", "the region linked to app", false, "us", "us", "us|europe|india|*")
                .text("","log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")


        /**
         * Default static info about the app.
         * This can be overriden in your env.conf file
         */
        val about = About(
                company = "slatekit",
                area = "samples",
                name = "apis",
                desc = "Sample Web API Server using Slate Kit Universal APIs",
                region = "NY",
                url = "www.slatekit.com",
                contact = "user@company.co",
                tags = "sample, template, app",
                examples = "http://www.slatekit.com"
        )

        /**
         * Encryption support
         */
        val encryptor = Encryptor("aksf2409bklja24b", "k3l4lkdfaoi97042", B64Java8)
    }


    /**
     * Initialization Life-Cycle method
     */
    override suspend fun init() {
        println("initializing")
    }


    /**
     * executes the app
     *
     * @return
     */
    override suspend fun exec(): Any? {
        val server = Server(ctx)
        return server.execute()
    }


    override suspend fun done(result:Any?) {
        println("ending")
    }
}