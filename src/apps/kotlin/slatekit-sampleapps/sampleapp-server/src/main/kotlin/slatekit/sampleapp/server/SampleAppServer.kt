/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2016 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */

package slatekit.sampleapp.server

import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.apis.core.Events
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.common.DateTime
import slatekit.common.args.ArgsSchema
import slatekit.common.auth.Roles
import slatekit.common.info.About
import slatekit.common.metrics.MetricsLite
import slatekit.core.app.App
import slatekit.core.app.AppRunner
import slatekit.core.common.AppContext
import slatekit.integration.apis.InfoApi
import slatekit.integration.apis.VersionApi
import slatekit.integration.common.AppEntContext
import slatekit.providers.logs.logback.LogbackLogs
import slatekit.results.Success
import slatekit.results.Try
import slatekit.sampleapp.core.apis.*
import slatekit.sampleapp.core.common.AppApiKeys
import slatekit.sampleapp.core.common.AppAuth
import slatekit.sampleapp.core.common.AppEncryptor
import slatekit.sampleapp.core.models.Movie
import slatekit.sampleapp.core.models.User
import slatekit.sampleapp.core.services.*
import slatekit.server.ktor.KtorServer
import test.common.SampleAnnoApi


/**
 * Entry point into the sample console application.
 */
fun main(args: Array<String>) {
    AppRunner.run(
            rawArgs = args,
            about   = SampleAppServer.about,
            schema  = SampleAppServer.schema,
            enc     = AppEncryptor,
            logs    = LogbackLogs(),
            builder = { ctx:AppContext -> SampleAppServer(ctx) }
    )
}

class SampleAppServer(context: AppContext) : App(context) {
    val ctxEnt = AppEntContext.fromAppContext(context)

    companion object {

        /**
         * setup the command line arguments.
         * NOTE:
         * 1. These values can can be setup in the env.conf file
         * 2. If supplied on command line, they override the values in .conf file
         * 3. If any of these are required and not supplied, then an error is display and program exits
         * 4. Help text can be easily built from this schema.
         */
        val schema = ArgsSchema()
                .text("env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
                .text("region", "the region linked to app", false, "us", "us", "us|europe|india|*")
                .text("config.loc", "location of config files", false, "jar", "jar", "jar|conf")
                .text("log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")


        /**
         * Default static info about the app.
         * This can be overriden in your env.conf file
         */
        val about = About(
                id = "sample_app_server",
                name = "Sample App Server",
                desc = "Sample Server",
                company = "Slatekit",
                region = "NY",
                version = "1.0.0",
                url = "www.slatekit.com",
                group = "codehelix",
                contact = "kishore@codehelix.co",
                tags = "sample, template, server",
                examples = "http://www.slatekit.com/kotlin-core-apis.html"
        )
    }


    override fun init(): Try<Boolean> {
        // =========================================================================
        // 2: Setup the entity services
        // =========================================================================
        // NOTES:
        // 1. See the ORM documentation for more info.
        // 2. The entity services uses a Generic Service/Repository pattern for ORM functionality.
        // 3. The services support CRUD operations out of the box for single-table mapped entities.
        // 4. This uses an In-Memory repository for demo but you can use EntityRepoMySql for MySql
        // ctx.ent.register[Movie](
        //    isSqlRepo= true,
        //    entityType  = Movie::class,
        //    serviceType = MovieService::class,
        //    repository  = EntityRepoMySql<Movie>(Movie::class)
        // )
        ctxEnt.ent.register<User>(entityType = User::class, serviceType = UserService::class, serviceCtx = ctx)
        ctxEnt.ent.register<Movie>(entityType = Movie::class, serviceType = MovieService::class, serviceCtx = ctx)
        val svc = ctxEnt.ent.getSvc<Movie>(Movie::class)


        // =========================================================================
        // 3: Create some sample data for demo purposes.
        // =========================================================================
        // NOTE: See the list actions on the CLI for the movies API via :>sampleapp.movies?
        svc.create(
                Movie(
                        title = "Indiana Jones: Raiders of the Lost Ark",
                        category = "Adventure",
                        playing = false,
                        cost = 10,
                        rating = 4.5,
                        released = DateTime.of(1985, 8, 10)
                ))
        svc.create(
                Movie(
                        title = "WonderWoman",
                        category = "action",
                        playing = true,
                        cost = 100,
                        rating = 4.2,
                        released = DateTime.of(2017, 7, 4)
                ))
        return Success(true)
    }

    /**
     * You implement this method to executes the app
     *
     * @return
     */
    override fun execute(): Try<Boolean> {
        // =========================================================================
        // 4: Setup the server with APIS w/ sample Auth provider
        // =========================================================================
        val sampleKeys = AppApiKeys.fetch()
        val selectedKey = sampleKeys[5]
        val auth = AppAuth("header", "slatekit", "johndoe", selectedKey, sampleKeys)
        val server = KtorServer(
                port = 5000,
                prefix = "/api/",
                static = true,
                staticDir = "",
                docs = true,
                docKey = "abc123",
                auth = auth,
                ctx = ctx,
                metrics = MetricsLite.build(),
                events = Events(),
                apis = apis()
        )

        // =========================================================================
        // 5: Run the Server
        // =========================================================================
        server.run()
        return Success(true)
    }


    private fun apis():List<Api> {

        return listOf(
                // Sample APIs for demo purposes
                // Instances are created per request.
                // The primary constructor must have either 0 parameters
                // or a single paramter taking the same Context as ctx above )

                // Example 1: without annotations ( pure kotlin objects )
                Api(SamplePOKOApi::class, area = "samples", name = "SamplePOKO", declaredOnly = false, desc = "Sample to show APIs as pure class methods",
                        auth = AuthModes.apiKey, roles = Roles.all, verb = Verbs.auto, protocol = Protocols.all),

                // Example 2: passing in and returning data-types
                Api(SampleTypes1Api::class, area = "samples", name = "SampleTypes1", declaredOnly = false, desc = "Sample to show APIs with basic datatypes",
                        auth = AuthModes.apiKey, roles = Roles.all, verb = Verbs.auto, protocol = Protocols.all),

                Api(SampleTypes2Api::class, area = "samples", name = "Sampletypes2", declaredOnly = false, desc = "Sample to show APIs with objects, lists, maps",
                        auth = AuthModes.apiKey, roles = Roles.all, verb = Verbs.auto, protocol = Protocols.all),

                // Example 3: annotations
                Api(SampleTypes3Api::class, setup = Annotated, declaredOnly = false),
                Api(SampleAnnoApi::class, setup = Annotated, declaredOnly = false),

                // Example 4: using REST ( you must register the REST rewrite module
                Api(SampleRESTApi::class, area = "samples", name = "SampleREST", declaredOnly = false, desc = "Sample to show APIs that are REST-like"),

                // Example 5: File download
                Api(SampleFiles3Api::class, area = "samples", name = "SampleFiles", declaredOnly = false, desc = "Sample to show APIs with file upload/download",
                        auth = AuthModes.apiKey, roles = Roles.all, verb = Verbs.auto, protocol = Protocols.all),

                // Example 6: Inheritance with APIs
                Api(SampleExtendedApi::class, area = "samples", name = "SampleExtended", declaredOnly = false, desc = "Sample to show APIs with inherited members"),

                // Example 7: Singleton APIS - 1 instance for all requests
                // NOTE: be careful and ensure that your APIs are stateless
                // This example shows integration with the ORM
                Api(SampleEntityApi(ctxEnt), area = "samples", name = "SampleEntity", declaredOnly = false, desc = "Sample to show APIs with built in support for entities/CRUD"),

                // Example 8: Middleware
                Api(SampleErrorsApi(true), area = "samples", name = "SampleErrors", declaredOnly = false, desc = "Sample to show APIs with error handling"),
                Api(SampleMiddlewareApi(), area = "samples", name = "SampleMiddleware", declaredOnly = false, desc = "Sample to show APIs with middle ware ( hooks, filters )"),

                // Example 9: Provided by Slate Kit
                Api(InfoApi(ctxEnt), setup = Annotated, declaredOnly = true),
                Api(VersionApi(ctxEnt), setup = Annotated, declaredOnly = true),

                // Example 10: More examples from the sample app
                Api(UserApi(ctxEnt), setup = Annotated, declaredOnly = false),
                Api(MovieApi(ctxEnt), setup = Annotated, declaredOnly = false)
        )
    }

    // NOTES: You can test the apis via the following:

    // HEADERS:
    // api-key : 54B1817194C1450B886404C6BEA81673

    // VERB,  HEADERS,      URL                                                BODY ( json )
    // get    see above   http://localhost:5000/api/sys/version/java         { }
    // post   see above   http://localhost:5000/api/sys/app/lang             { }
    // post   see above   http://localhost:5000/api/sys/app/host             { }
    // post   see above   http://localhost:5000/api/sys/app/about            { }
    // get    see above   http://localhost:5000/api/app/movies/total         { }
    // get    see above   http://localhost:5000/api/app/movies/getAll        { }
    // get    see above   http://localhost:5000/api/app/users/total          { }
    // post   see above   http://localhost:5000/api/app/users/create         { "email" : "batman@gotham.com", "first" : "bruce", "last" : "wayne", "isMale" : true, "age" : 32, "phone" : "123456789", "country" : "us" }
    // post   see above   http://localhost:5000/api/app/users/create         { "email" : "superman@metropolis.com", "first" : "clark", "last" : "kent", "isMale" : true, "age" : 32, "phone" : "987654321", "country" : "us" }
    // post   see above   http://localhost:5000/api/app/users/create         { "email" : "wonderwoman@themyscira.com", "first" : "diana", "last" : "price", "isMale" : false, "age" : 32, "phone" : "111111111", "country" : "us" }
    // get    see above   http://localhost:5000/api/app/users/getById?id=2
    // get    see above   http://localhost:5000/api/app/users/getAll         { }
    // put    see above   http://localhost:5000/api/app/users/updatePhone    { "id" : 1, "phone": "1112223334" }
    // get    see above   http://localhost:5000/api/app/users/first          { }
    // get    see above   http://localhost:5000/api/app/users/last           { }
    // get    see above   http://localhost:5000/api/app/users/recent?count=2
    // get    see above   http://localhost:5000/api/app/users/oldest?count=2
    // delete see above   http://localhost:5000/api/app/users/deleteById     { "id" : 2 }
    // get    see above   http://localhost:5000/api/app/users/total          { }
    // get    see above   http://localhost:5000/api/app/users/getAll         { }

    // LIMITATIONS:
    // 1. File upload ( WIP - Work in progress )
    // 2. Default parameter values for api actions/methods ( difficult to get metadata from reflection )

    // SAMPLE APIS:
    // 1. SampleApi   : Sample api showing basic usages
    // 2. RestApi     : Restful apis
    // 3. EntityApi   : Entity support built in
    // 4. PokoApi     : No annotations - pure kotlin + Slate Kit Request/Result
    // 5. AdvancedApi : Includes decryption, smart-strings, file download
    // 6. AuthApi     : Authorization features
}
