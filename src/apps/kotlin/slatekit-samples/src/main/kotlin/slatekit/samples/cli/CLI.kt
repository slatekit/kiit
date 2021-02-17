package slatekit.samples.cli

import slatekit.apis.*
import slatekit.apis.routes.Api
import slatekit.apis.support.Authenticator
import slatekit.cli.CliSettings
import slatekit.common.types.Content
import slatekit.common.info.ApiKey
import slatekit.common.types.ContentType
import slatekit.connectors.cli.CliApi
import slatekit.connectors.entities.AppEntContext
import slatekit.entities.EntityId
import slatekit.entities.EntityLongId
import slatekit.entities.EntityService
import slatekit.results.Try
import slatekit.serialization.Serialization
import slatekit.integration.apis.*
import slatekit.integration.mods.*
import slatekit.migrations.MigrationService
import slatekit.migrations.MigrationSettings
import slatekit.samples.common.apis.SampleAPI
import slatekit.samples.common.models.Movie

class CLI(val ctx: AppEntContext) {

    /**
     * executes the CLI integrated with the API module
     * to be able to call APIs on the command line
     * @return
     */
    suspend fun execute(): Try<Any> {

        // 1. The API keys( DocApi, SetupApi are authenticated using an sample API key )
        val keys = listOf(ApiKey( name ="cli", key = "abc", roles = "dev,qa,ops,admin"))

        // 2. Authentication
        val auth = Authenticator(keys)

        // 3. Load all the Slate Kit Universal APIs
        ctx.ent.register<Long, Movie>(EntityLongId(), "movie") { repo -> EntityService( repo ) }
        ctx.ent.register<Long, Mod>(EntityLongId(), "mod") { repo -> ModService(ctx.ent, repo)}
        val modSvc = ctx.ent.getServiceByType(Mod::class) as ModService
        val migSvc = MigrationService(ctx.ent, ctx.ent.dbs, MigrationSettings(true, true), ctx.dirs)
        val modCtx = ModuleContext(modSvc, migSvc )
        val modApi = ModuleApi(modCtx, ctx)
        modApi.register(MovieModule(ctx, modCtx))
        val apis = apis(modApi)

        // 4. Makes the APIs accessible on the CLI runner
        val cli = CliApi(
                ctx = ctx,
                auth = auth,
                settings = CliSettings(enableLogging = true, enableOutput = true),
                apiItems = apis,
                metaTransform = {
                    listOf("api-key" to keys.first().key)
                },
                serializer = {item, type -> print(item, type)}
        )

        // 5. Run interactive mode
        cli.showOverview("Slate Kit CLI Sample")
        return cli.run()
    }


    fun apis(modApi:ModuleApi): List<Api> {
        return listOf(
                Api(klass = SampleAPI::class, singleton = SampleAPI(ctx), setup = SetupType.Annotated),
                Api(klass = EntitiesApi::class, singleton = EntitiesApi(ctx), setup = SetupType.Annotated),
                Api(klass = ModuleApi::class, singleton = modApi, setup = SetupType.Annotated)
        )
    }


    private fun print(item:Any?, type:ContentType) : Content {
        val text = Serialization.json().serialize(item)
        val wrap = """{ "value" : $text }""".trimMargin()
        val body = org.json.JSONObject(wrap)
        val pretty = body.toString(4)
        val content = Content.text(pretty)
        return content
    }

    class MovieModule(ctx:AppEntContext, mod:ModuleContext) : Module(ctx, mod) {
        override val info: ModuleInfo = ModuleInfo(
                name = Movie::class.qualifiedName!!,
                desc = "Supports user registration",
                version = "1.0",
                isInstalled = false,
                isEnabled = true,
                isDbDependent = true,
                totalModels = 1,
                source = Movie::class.qualifiedName!!,
                dependencies = "none",
                models = listOf<String>(Movie::class.qualifiedName!!)
        )
    }
}