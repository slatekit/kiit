package slatekit

import slatekit.apis.support.Authenticator
import slatekit.app.App
import slatekit.app.AppOptions
import slatekit.cli.CliSettings
import slatekit.context.Context
import slatekit.common.args.ArgsSchema
import slatekit.common.utils.B64Java8
import slatekit.common.crypto.Encryptor
import slatekit.common.info.About
import slatekit.common.info.ApiKey
import slatekit.connectors.cli.CliApi
import slatekit.results.Success
import slatekit.serialization.Serialization
import slatekit.results.Failure

class SlateKit(ctx: Context) : App<Context>(ctx, AppOptions(showWelcome = true)), SlateKitServices {

    companion object {

        // setup the command line arguments.
        // NOTE:
        // 1. These values can can be setup in the env.conf file
        // 2. If supplied on command line, they override the values in .conf file
        // 3. If any of these are required and not supplied, then an error is display and program exists
        // 4. Help text can be easily built from this schema.
        val schema = ArgsSchema()
                .text("", "env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
                .text("", "region", "the region linked to app", false, "us", "us", "us|europe|india|*")
                .text("", "log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")


        /**
         * Default static info about the app.
         * This can be overriden in your env.conf file
         */
        val about = About(
                company = "slatekit",
                area = "tools",
                name = "cli",
                desc = "Slate Kit CLI for creating projects and access to other tools",
                region = "NY",
                url = "www.slatekit.life",
                contact = "user@company.co",
                tags = "sample, template, app",
                examples = "http://www.slatekit.com"
        )

        /**
         * Encryptor for files
         */
        val encryptor = Encryptor("aksf2409bklja24b", "k3l4lkdfaoi97042", B64Java8)
    }


    override suspend fun init() {
        println("initializing")
    }


    /**
     * executes the app
     *
     * @return
     */
    override suspend fun exec(): Any? {
        // Create the CLI
        // All commands are dispatched to it as it handles the
        // integration between CLI inputs -> API requests
        val cli = build()

        // Determine if running in CLI interactive mode or executing a project generator
        val args = ctx.args
        when (args.parts.isEmpty()) {
            true -> run(cli)
            false -> gen(cli)
        }
        return OK
    }


    override suspend fun done(result:Any?) {
        println("ending")
    }


    /**
     * Generate the project
     */
    private suspend fun gen(cli: CliApi) {
        // slatekit new api -name="MyApp1" -package="company1.apps"
        //
        // NOTES:
        // 1. Slate Kit is the actual bash/batch script generated with gradle application plugin
        // 2. APIs in slate kit have a 3 part routing convention AREA API ACTION
        // 3. The GeneratorAPI is annotated with "area" = "slatekit" for discovery
        // 4. So we append the "slatekit" to the parts field parsed from the Args
        // The parts are ["slatekit", "new", "app"]
        val args = ctx.args
        val copy = args.withPrefix("slatekit")
        val result = cli.executeArgs(copy)
        when(result) {
            is Failure -> println("Failure: " + result.error)
            is Success -> println("Success: " + result.value.desc)
        }
    }


    /**
     * Begin interactive mode
     */
    private suspend fun run(cli: CliApi) {
        cli.run()
    }


    private fun build(): CliApi {
        // The APIs ( DocApi, SetupApi are authenticated using an sample API key )
        val keys = listOf(ApiKey(name = "cli", key = "abc", roles = "dev,qa,ops,admin"))

        // Authenticator using API keys
        // Production usage should use industry standard Auth components such as OAuth, JWT, etc.
        val auth = Authenticator(keys)

        // Load all the Slate Kit Universal APIs
        val apis = apis()

        // Makes the APIs accessible on the CLI
        val cli = CliApi(
                ctx = ctx,
                auth = auth,
                settings = CliSettings(enableLogging = true, enableOutput = true),
                apiItems = apis,
                metaTransform = {
                    listOf("api-key" to keys.first().key)
                },
                serializer = Serialization::serialize
        )
        return cli
    }
}