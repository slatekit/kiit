package slatekit.samples.cli

import slatekit.apis.*
import slatekit.apis.routes.Api
import slatekit.apis.support.Authenticator
import slatekit.cli.CliSettings
import slatekit.context.Context
import slatekit.common.types.Content
import slatekit.common.info.ApiKey
import slatekit.connectors.cli.CliApi
import slatekit.results.Try
import slatekit.serialization.Serialization
import slatekit.samples.common.apis.BasicApi

class CLI(val ctx: Context) {

    /**
     * executes the app
     *
     * @return
     */
    suspend fun execute(): Try<Any> {

        // 1. The API keys( DocApi, SetupApi are authenticated using an sample API key )
        val keys = listOf(ApiKey( name ="cli", key = "abc", roles = "dev,qa,ops,admin"))

        // 2. Authentication
        val auth = Authenticator(keys)

        // 3. Load all the Slate Kit Universal APIs
        val apis = apis()

        // 4. Makes the APIs accessible on the CLI runner
        val cli = CliApi(
                ctx = ctx,
                auth = auth,
                settings = CliSettings(enableLogging = true, enableOutput = true),
                apiItems = apis,
                metaTransform = {
                    listOf("api-key" to keys.first().key)
                },
                serializer = {item, type -> Content.csv(Serialization.csv().serialize(item))}
        )

        // 5. Run interactive mode
        cli.showOverview("Slate Kit CLI Sample")
        return cli.run()
    }


    fun apis(): List<Api> {
        return listOf(
                Api(klass = BasicApi::class, singleton = BasicApi(ctx), setup = SetupType.Annotated)
        )
    }
}