package slatekit.samples.app

import slatekit.app.App
import slatekit.app.AppOptions
import slatekit.common.DateTimes
import slatekit.context.Context
import slatekit.common.args.ArgsSchema
import slatekit.common.utils.B64Java8
import slatekit.common.crypto.Encryptor
import slatekit.common.info.About


/**
 * Slate Kit Application template
 * This provides support for command line args, environment selection, confs, life-cycle methods and help usage
 * @see https://www.slatekit.com/arch/app/
 */
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
                .text("","log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")


        /**
         * Default static info about the app.
         * This can be overriden in your env.conf file
         */
        val about = About(
                company = "slatekit",
                area = "samples",
                name = "app",
                desc = "Sample Console Application with command line args, environments, logs, and help docs",
                region = "",
                url = "myapp.url",
                contact = "",
                tags = "app",
                examples = ""
        )

        /**
         * Encryption support
         */
        val encryptor = Encryptor("aksf2409bklja24b", "k3l4lkdfaoi97042", B64Java8)
    }


    override suspend fun init() {
        println("initializing")
    }


    override suspend fun exec(): Any? {
        val logger = ctx.logs.getLogger("app")
        val values = listOf("a" to 1, "b" to true, "c" to "kotlin", "d" to DateTimes.now())
        logger.warn("test warn", values)
        println("executing")
        println("Your work should be done here...")
        return OK
    }


    override suspend fun done(result:Any?) {
        println("ending")
    }
}