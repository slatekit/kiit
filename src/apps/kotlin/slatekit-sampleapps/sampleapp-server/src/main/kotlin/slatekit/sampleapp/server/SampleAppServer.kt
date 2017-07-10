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
import slatekit.core.app.AppRunner.build
import slatekit.integration.AppApi
import slatekit.integration.VersionApi
import slatekit.sampleapp.core.common.AppApiKeys
import slatekit.sampleapp.core.common.AppAuth
import slatekit.sampleapp.core.common.AppEncryptor
import slatekit.sampleapp.core.models.Movie
import slatekit.sampleapp.core.models.User
import slatekit.sampleapp.core.services.*
import slatekit.server.Server


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
    val ctx = build(
            args = args,
            enc = AppEncryptor
    )

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
    val server = Server(
            port = 5000,
            prefix = "/api/",
            info = true,
            ctx = ctx,
            auth = auth,
            apis = listOf(
                    ApiReg(AppApi(ctx), true),
                    ApiReg(VersionApi(ctx), true),
                    ApiReg(UserApi(ctx), false),
                    ApiReg(MovieApi(ctx), false),
                    ApiReg(SampleApi(ctx), false)
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
    // 2. Default parameter values for api actions/methods
    // 3. API classes are currently singletons - ( support creation mode: singleton | 1 instance per request )
}