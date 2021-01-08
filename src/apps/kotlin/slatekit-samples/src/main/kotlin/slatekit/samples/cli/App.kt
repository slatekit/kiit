package slatekit.samples.cli

import slatekit.app.App
import slatekit.app.AppOptions
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
class App(ctx: Context) : App<Context>(ctx, AppOptions(showWelcome = false, showDisplay = false)) {

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
                name = "cli",
                desc = "Sample CLI - Command Line Interactive application",
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
        return super.init()
    }


    override suspend fun exec(): Any? {
        // You can type the following to run
        // samples.cli.about
        // samples.cli.inc
        // samples.cli.value
        // samples.cli.add -value=2
        // samples.cli.greet -greeting="whats up"
        // samples.cli.movies
        // samples.cli.inputs -name="kishore" -isActive=true -age=41 -dept=2 -account=123 -average=2.4 -salary=120000 -date="2019-04-01T11:05:30Z"
        val cli = CLI(ctx)
        return cli.execute()
    }


    override suspend fun done(result:Any?) {
        println("ending")
    }
}