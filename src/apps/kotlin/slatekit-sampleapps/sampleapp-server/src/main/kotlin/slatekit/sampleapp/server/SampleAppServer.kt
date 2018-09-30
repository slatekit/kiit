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
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.common.DateTime
import slatekit.common.auth.Roles
import slatekit.core.app.AppRunner
import slatekit.integration.apis.AppApi
import slatekit.integration.apis.VersionApi
import slatekit.integration.common.AppEntContext
import slatekit.providers.logs.logback.LogbackLogs
import slatekit.sampleapp.core.apis.*
import slatekit.sampleapp.core.common.AppApiKeys
import slatekit.sampleapp.core.common.AppAuth
import slatekit.sampleapp.core.common.AppEncryptor
import slatekit.sampleapp.core.models.Movie
import slatekit.sampleapp.core.models.User
import slatekit.sampleapp.core.services.*
import slatekit.server.Server
import slatekit.server.ktor.KtorServer
import test.common.SampleAnnoApi
import test.common.SampleApi


/**
 * Entry point into the sample console application.
 */
fun main(args: Array<String>): Unit {
    // =========================================================================
    // 1: Build the application context
    // =========================================================================
    // NOTE: The app context contains the selected environment, logger,
    // conf, command line args database, encryptor, and many other components.
    // You can build the context manually or automatically using the AppFuncs
    // build function which will factor in inputs from the command line.
    // Fore more info on the context, see the utils online.
    val ctxRaw = AppRunner.build(
            args = args,
            enc = AppEncryptor,
            logs = LogbackLogs()
    )
    val ctx = AppEntContext.fromAppContext(ctxRaw)

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
    ctx.ent.register<User>(isSqlRepo = false, entityType = User::class, serviceType = UserService::class, serviceCtx = ctx)
    ctx.ent.register<Movie>(isSqlRepo = false, entityType = Movie::class, serviceType = MovieService::class, serviceCtx = ctx)
    val svc = ctx.ent.getSvc<Movie>(Movie::class)

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

    // =========================================================================
    // 4: Setup the server with APIS w/ sample Auth provider
    // =========================================================================
    val sampleKeys = AppApiKeys.fetch()
    val selectedKey = sampleKeys[5]
    val auth = AppAuth("header", "slatekit", "johndoe", selectedKey, sampleKeys)
    val enc = AppEncryptor
    val server = KtorServer(
            port      = 5000,
            prefix    = "/api/",
            static    = true,
            staticDir = "",
            docs      = true,
            docKey    = "abc123",
            auth      = auth,
            ctx       = ctx,
            apis      = listOf(
                    // Sample APIs for demo purposes
                    // Instances are created per request.
                    // The primary constructor must have either 0 parameters
                    // or a single paramter taking the same Context as ctx above )

                    // Example 1: without annotations ( pure kotlin objects )
                    Api(SamplePOKOApi::class      , area = "samples", name = "SamplePOKO", declaredOnly = false, desc = "Sample to show APIs as pure class methods"),

                    // Example 2: passing in and returning data-types
                    Api(SampleTypes1Api::class    , area = "samples", name = "SampleTypes1", declaredOnly = false, desc = "Sample to show APIs with basic datatypes"),
                    Api(SampleTypes2Api::class    , area = "samples", name = "Sampletypes2", declaredOnly = false, desc = "Sample to show APIs with objects, lists, maps"),

                    // Example 3: annotations
                    Api(SampleTypes3Api::class    , setup = Annotated, declaredOnly = false),
                    Api(SampleAnnoApi::class      , setup = Annotated, declaredOnly = false),

                    // Example 4: using REST ( you must register the REST rewrite module
                    Api(SampleRESTApi::class      , area = "samples", name = "SampleREST", declaredOnly = false, desc = "Sample to show APIs that are REST-like"),

                    // Example 5: File download
                    Api(SampleFiles3Api::class    , area = "samples", name = "SampleFiles",declaredOnly = false, desc = "Sample to show APIs with file upload/download",
                        auth = AuthModes.apiKey, roles = Roles.all, verb = Verbs.auto, protocol = Protocols.all),

                    // Example 6: Inheritance with APIs
                    Api(SampleExtendedApi::class     , area = "samples", name = "SampleExtended", declaredOnly = false, desc = "Sample to show APIs with inherited members"),

                    // Example 7: Singleton APIS - 1 instance for all requests
                    // NOTE: be careful and ensure that your APIs are stateless
                    // This example shows integration with the ORM
                    Api(SampleEntityApi(ctx)      , area = "samples", name = "SampleEntity", declaredOnly = false, desc = "Sample to show APIs with built in support for entities/CRUD"),

                    // Example 8: Middleware
                    Api(SampleErrorsApi(true)  , area = "samples", name = "SampleErrors", declaredOnly = false, desc = "Sample to show APIs with error handling"),
                    Api(SampleMiddlewareApi() , area = "samples", name = "SampleTypes1", declaredOnly = false, desc = "Sample to show APIs with middle ware ( hooks, filters )"),

                    // Example 9: Provided by Slate Kit
                    Api(AppApi(ctx)          , setup = Annotated, declaredOnly = true ),
                    Api(VersionApi(ctx)      , setup = Annotated, declaredOnly = true ),

                    // Example 10: More examples from the sample app
                    Api(UserApi(ctx)         , setup = Annotated, declaredOnly = false),
                    Api(MovieApi(ctx)        , setup = Annotated, declaredOnly = false)

            )
    )

    // =========================================================================
    // 5: Run the Server
    // =========================================================================
    server.run()

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
