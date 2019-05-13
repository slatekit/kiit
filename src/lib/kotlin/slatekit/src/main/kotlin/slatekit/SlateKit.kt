package slatekit

import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.apis.svcs.Authenticator
import slatekit.app.App
import slatekit.cli.CliSettings
import slatekit.common.args.ArgsSchema
import slatekit.common.db.DbType
import slatekit.common.encrypt.B64Java8
import slatekit.common.encrypt.Encryptor
import slatekit.common.info.About
import slatekit.common.info.ApiKey
import slatekit.common.info.Credentials
import slatekit.integration.apis.*
import slatekit.results.Success
import slatekit.results.Try
import slatekit.docs.DocApi
import slatekit.info.Dependency
import slatekit.info.DependencyApi
import slatekit.info.DependencyModule
import slatekit.info.DependencyService
import slatekit.integration.common.AppEntContext
import slatekit.integration.mods.Mod
import slatekit.integration.mods.ModService
import slatekit.integration.mods.ModuleContext
import slatekit.orm.migrations.MigrationService
import slatekit.orm.migrations.MigrationSettings
import slatekit.orm.orm
import slatekit.setup.SetupApi
import slatekit.setup.SetupService

class SlateKit(ctx: AppEntContext) : App<AppEntContext>(ctx), SlateKitServices {

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


        // Application level
        // All application level components should go here.
        ctx.ent.orm<Long, Dependency>(DbType.DbTypeMemory, Dependency::class, Long::class,null, DependencyService::class)
        return super.init()
    }


    /**
     * executes the app
     *
     * @return
     */
    override suspend fun execute(): Try<Any> {
        // =========================================================================
        // 4: Register the APIS
        // =========================================================================
        // Build up the shell services that handles all the command line features.
        // And setup the api container to hold all the apis.
        val keys = listOf(ApiKey("cli", "abc", "dev,qa,ops,admin"))
        val creds = Credentials("1", "john doe", "jdoe@abc.com", key = keys.first().key)
        val auth = Authenticator(keys)

        // Module api
        val moduleApi = ModuleApi(moduleContext(), ctx)
        moduleApi.register(DependencyModule(ctx, moduleContext()))

        // APIs
        val requiredApis = listOf(
                Api(SetupApi(ctx, SetupService(ctx)), declaredOnly = true, setup = Annotated),
                Api(DocApi(ctx)                     , declaredOnly = true, setup = Annotated),
                Api(InfoApi(ctx)                    , declaredOnly = true, setup = Annotated),
                Api(VersionApi(ctx)                 , declaredOnly = true, setup = Annotated),
                Api(moduleApi                       , declaredOnly = true, setup = Annotated)
        )
        val optionalApis = loadOptionalApis()
        val allApis = requiredApis.plus(optionalApis)

        val cli = CliApi(
                ctx = ctx,
                auth = auth,
                settings = CliSettings(enableLogging = true, enableOutput = true),
                apiItems = allApis,
                metaTransform = {
                    listOf("api-key" to creds.key)
                }
        )

        // =========================================================================
        // 5: Run the CLI
        // =========================================================================
        cli.run()
        return Success(true)
    }


    private fun loadOptionalApis():List<Api>{

        // @param key : "email"
        fun load(key:String, call:() -> Api ):Api? {
            val enabled = ctx.cfg.getBoolOrElse(key, false)
            return if(enabled) call() else null
        }
        val apis = listOf(
            load( "email")  { Api(EmailApi(emails(), ctx) , declaredOnly = true, setup = Annotated) },
            load( "files")  { Api(FilesApi(files(), ctx)  , declaredOnly = true, setup = Annotated) },
            load( "queues") { Api(QueueApi(queues(), ctx) , declaredOnly = true, setup = Annotated) },
            load( "sms")    { Api(SmsApi(sms(), ctx)      , declaredOnly = true, setup = Annotated) },
            load( "db")     { Api(DependencyApi(ctx)  , declaredOnly = false, setup = Annotated)    }
        )
        return apis.filterNotNull()
    }


    override suspend fun end(): Try<Boolean> {
        println("ending")
        return super.end()
    }


    fun migrations(): MigrationService {
        // entity migration services ( to install/uninstall )
        val migrationSettings = MigrationSettings(enableLogging = true, enableOutput = true)
        val migrationService = MigrationService(ctx.ent, ctx.ent.dbs, migrationSettings, ctx.dirs)
        return migrationService
    }


    fun moduleContext(): ModuleContext {
        // Services/depenencies for all modules
        val moduleService = ctx.ent.getSvc<Long, Mod>(Mod::class) as ModService
        val moduleContext = ModuleContext(moduleService, migrations())
        return moduleContext
    }
}