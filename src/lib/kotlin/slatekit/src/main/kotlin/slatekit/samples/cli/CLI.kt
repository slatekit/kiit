package slatekit.samples.cli

import slatekit.apis.*
import slatekit.apis.support.Authenticator
import slatekit.cli.CliSettings
import slatekit.common.Context
import slatekit.common.Source
import slatekit.common.types.Content
import slatekit.common.info.ApiKey
import slatekit.integration.apis.CliApi
import slatekit.results.Try
import slatekit.samples.common.apis.SampleApi

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
                serializer = {item, type -> Content.csv(slatekit.meta.Serialization.csv().serialize(item))}
        )

        // 5. Run interactive mode
        return cli.run()
    }


    fun apis(): List<slatekit.apis.core.Api> {
        return listOf(
                slatekit.apis.core.Api(
                        cls = SampleApi::class,
                        setup = Setup.Annotated,
                        declaredOnly = true,
                        auth = AuthMode.Keyed,
                        roles = slatekit.apis.core.Roles(listOf("*")),
                        verb = Verb.Auto,
                        sources = slatekit.apis.core.Sources(listOf(Source.All)),
                        singleton = SampleApi(ctx)
                )
        )
    }
}