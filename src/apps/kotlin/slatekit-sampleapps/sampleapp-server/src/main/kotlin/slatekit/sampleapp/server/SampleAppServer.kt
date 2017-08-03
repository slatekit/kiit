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

import slatekit.apis.ApiReg
import slatekit.common.DateTime
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.envs.Dev
import slatekit.common.envs.Env
import slatekit.common.info.About
import slatekit.common.info.Host
import slatekit.common.info.Lang
import slatekit.common.log.LoggerConsole
import slatekit.core.app.AppRunner
import slatekit.core.common.AppContext
import slatekit.entities.core.Entities
import slatekit.integration.apis.AppApi
import slatekit.integration.apis.VersionApi
import slatekit.integration.common.AppEntContext
import slatekit.sampleapp.core.apis.*
import slatekit.sampleapp.core.common.AppApiKeys
import slatekit.sampleapp.core.common.AppAuth
import slatekit.sampleapp.core.common.AppEncryptor
import slatekit.sampleapp.core.models.Movie
import slatekit.sampleapp.core.models.User
import slatekit.sampleapp.core.services.*
import slatekit.server.Server
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
            enc = AppEncryptor
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
    val server = Server(
            port   = 5000,
            prefix = "/api/",
            docs   = true,
            docKey = "abc123",
            auth   = auth,
            ctx    = ctx,
            apis   = listOf(
                    // Sample APIs for demo purposes
                    // Instances are created per request.
                    // The primary constructor must have either 0 parameters
                    // or a single paramter taking the same Context as ctx above )

                    // Example 1: without annotations ( pure kotlin objects )
                    ApiReg(SamplePOKOApi::class      , area = "samples", declaredOnly = false),

                    // Example 2: passing in and returning data-types
                    ApiReg(SampleTypes1Api::class    , area = "samples", declaredOnly = false),
                    ApiReg(SampleTypes2Api::class    , area = "samples", declaredOnly = false),

                    // Example 3: annotations
                    ApiReg(SampleTypes3Api::class    , declaredOnly = false),
                    ApiReg(SampleAnnoApi::class      , declaredOnly = false),

                    // Example 4: using REST ( you must register the REST rewrite module
                    ApiReg(SampleRESTApi::class      , area = "samples", declaredOnly = false),

                    // Example 5: File download
                    ApiReg(SampleFiles3Api::class     , declaredOnly = false),

                    // Example 6: Inheritance with APIs
                    ApiReg(SampleExtendedApi::class     , area = "samples", declaredOnly = false),

                    // Example 7: Singleton APIS - 1 instance for all requests
                    // NOTE: be careful and ensure that your APIs are stateless
                    // This example shows integration with the ORM
                    ApiReg(SampleEntityApi(ctx)      , area = "samples", declaredOnly = false),

                    // Example 8: Middleware
                    ApiReg(SampleErrorsApi(true)           , area = "samples", declaredOnly = false),
                    ApiReg(SampleMiddlewareApi(true, true) , area = "samples", declaredOnly = false),

                    // Example 9: Provided by Slate Kit
                    ApiReg(AppApi(ctx)          , declaredOnly = true ),
                    ApiReg(VersionApi(ctx)      , declaredOnly = true ),

                    // Example 10: More examples from the sample app
                    ApiReg(UserApi(ctx)         , declaredOnly = false),
                    ApiReg(MovieApi(ctx)        , declaredOnly = false)

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