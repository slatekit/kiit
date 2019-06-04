package slatekit

import slatekit.apis.svcs.Authenticator
import slatekit.app.App
import slatekit.app.AppOptions
import slatekit.cli.CliSettings
import slatekit.common.args.ArgsSchema
import slatekit.common.db.DbType
import slatekit.common.encrypt.B64Java8
import slatekit.common.encrypt.Encryptor
import slatekit.common.info.About
import slatekit.common.info.ApiKey
import slatekit.info.Dependency
import slatekit.info.DependencyService
import slatekit.integration.apis.*
import slatekit.results.Success
import slatekit.results.Try
import slatekit.integration.common.AppEntContext
import slatekit.integration.mods.Mod
import slatekit.integration.mods.ModService
import slatekit.orm.orm

class SlateKit(ctx: AppEntContext, val interactive:Boolean) : App<AppEntContext>(ctx, AppOptions(printSummaryBeforeExec = true)), SlateKitServices {

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
                id = "slatekit",
                name = "Slate Kit",
                desc = "Slate Kit CLI for creating projects and access to other tools",
                company = "codehelix.co",
                region = "NY",
                version = "1.0.0",
                url = "www.slatekit.life",
                group = "codehelix",
                contact = "user@company.co",
                tags = "sample, template, app",
                examples = "http://www.slatekit.com"
        )

        /**
         * Encryptor for files
         */
        val encryptor = Encryptor("aksf2409bklja24b", "k3l4lkdfaoi97042", B64Java8)
    }


    override suspend fun init(): Try<Boolean> {
        println("initializing")

        // System level ( slate kit )
        // This ModServices allow storing/checking for installed modules in the DB
        ctx.ent.orm<Long, Mod>(DbType.DbTypeMemory, Mod::class, Long::class,null, ModService::class)
        ctx.ent.orm<Long, Dependency>(DbType.DbTypeMemory, Dependency::class, Long::class,null, DependencyService::class)
        return super.init()
    }


    /**
     * executes the app
     *
     * @return
     */
    override suspend fun exec(): Try<Any> {
        // The APIs ( DocApi, SetupApi are authenticated using an sample API key )
        val keys = listOf(ApiKey( name ="cli", key = "abc", roles = "dev,qa,ops,admin"))

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
                }
        )

        // Finally, run the CLI to interact w/ the APIs
        cli.run()

        return Success(true)
    }


    override suspend fun end(): Try<Boolean> {
        println("ending")
        return super.end()
    }
}