package slatekit.tools

import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.apis.svcs.Authenticator
import slatekit.app.App
import slatekit.cli.CliSettings
import slatekit.common.Context
import slatekit.common.args.ArgsSchema
import slatekit.common.encrypt.B64Java8
import slatekit.common.encrypt.Encryptor
import slatekit.common.info.About
import slatekit.common.info.ApiKey
import slatekit.common.info.Credentials
import slatekit.integration.apis.*
import slatekit.results.Success
import slatekit.results.Try
import slatekit.tools.docs.DocApi

class SlateKit(ctx: Context) : App<Context>(ctx) {

    companion object {

        // setup the command line arguments.
        // NOTE:
        // 1. These values can can be setup in the env.conf file
        // 2. If supplied on command line, they override the values in .conf file
        // 3. If any of these are required and not supplied, then an error is display and program exists
        // 4. Help text can be easily built from this schema.
        val schema = ArgsSchema()
                .text("env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
                .text("region", "the region linked to app", false, "us", "us", "us|europe|india|*")
                .text("log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")


        /**
         * Default static info about the app.
         * This can be overriden in your env.conf file
         */
        val about = About(
                id = "slatekit",
                name = "Slate Kit",
                desc = "Slate Kit CLI for creating projects and access to other tools",
                company = "codehelix.co",
                region = "NY",
                version = "1.0.0",
                url = "www.blend.life",
                group = "codehelix",
                contact = "kishore@codehelix.co",
                tags = "sample, template, app",
                examples = "http://www.slatekit.com"
        )

        /**
         * Encryptor for files
         */
        val encryptor = Encryptor("aksf2409bklja24b", "k3l4lkdfaoi97042", B64Java8)
    }


    override fun init(): Try<Boolean> {
        println("initializing")
        return super.init()
    }


    /**
     * executes the app
     *
     * @return
     */
    override fun execute(): Try<Any> {
        // =========================================================================
        // 4: Register the APIS
        // =========================================================================
        // Build up the shell services that handles all the command line features.
        // And setup the api container to hold all the apis.
        val keys = listOf(ApiKey("cli", "abc", "dev,qa,ops,admin"))
        val creds = Credentials("1", "john doe", "jdoe@abc.com", key = keys.first().key)
        val auth = Authenticator(keys)
        val cli = CliApi(creds, ctx, auth,
                CliSettings(enableLogging = true, enableOutput = true),
                listOf(
                        // Sample APIs for demo purposes
                        // Instances are created per request.
                        // The primary constructor must have either 0 parameters
                        // or a single paramter taking the same Context as ctx above )

                        // Example 1: without annotations ( pure kotlin objects )
                        Api(DocApi::class, setup = Annotated, declaredOnly = true),
                        Api(InfoApi::class, setup = Annotated, declaredOnly = true),
                        Api(VersionApi::class, setup = Annotated, declaredOnly = true)
//                        Api(EmailApi::class, setup = Annotated, declaredOnly = true),
//                        Api(SmsApi::class, setup = Annotated, declaredOnly = true),
//                        Api(FilesApi::class, setup = Annotated, declaredOnly = true),
//                        Api(QueueApi::class, setup = Annotated, declaredOnly = true)
                )
        )

        // =========================================================================
        // 5: Run the CLI
        // =========================================================================
        cli.run()
        return Success(true)
    }


    override fun end(): Try<Boolean> {
        println("ending")
        return super.end()
    }
}