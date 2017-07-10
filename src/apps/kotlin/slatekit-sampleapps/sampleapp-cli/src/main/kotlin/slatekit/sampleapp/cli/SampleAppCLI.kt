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

package slatekit.sampleapp.cli

import slatekit.apis.ApiReg
import slatekit.common.Credentials
import slatekit.common.DateTime
import slatekit.core.app.AppRunner.build
import slatekit.core.cli.CliSettings
import slatekit.integration.AppApi
import slatekit.integration.CliApi
import slatekit.integration.EntitiesApi
import slatekit.integration.VersionApi
import slatekit.sampleapp.core.common.AppApiKeys
import slatekit.sampleapp.core.common.AppAuth
import slatekit.sampleapp.core.common.AppEncryptor
import slatekit.sampleapp.core.models.Movie
import slatekit.sampleapp.core.models.User
import slatekit.sampleapp.core.services.*


/**
 * Entry point into the sample console application.
 */
fun main(args: Array<String>): Unit {
    // =========================================================================
    // 1: Build the application context
    // =========================================================================
    // NOTE: The app context contains the selected environment, logger,
    // conf, command line args database, encryptor, and many other components
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
    //    entityType = typeOf[Movie],
    //    serviceType= typeOf[MovieService],
    //    repository= EntityRepoMySql[Movie](typeOf[Movie]))
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
    // 4: Register the APIS
    // =========================================================================
    // Build up the shell services that handles all the command line features.
    // And setup the api container to hold all the apis.
    val sampleKeys = AppApiKeys.fetch()
    val selectedKey = sampleKeys[5]
    val creds = Credentials("1", "john doe", "jdoe@gmail.com", key = selectedKey.key)
    val auth = AppAuth("test-mode", "slatekit", "johndoe", selectedKey, sampleKeys)
    val shell = CliApi(creds, ctx, auth, "sampleapp",
            CliSettings(enableLogging = true, enableOutput = true),
            listOf(
                    ApiReg(AppApi(ctx), true),
                    ApiReg(VersionApi(ctx), true),
                    ApiReg(UserApi(ctx), false),
                    ApiReg(MovieApi(ctx), false),
                    ApiReg(SampleApi(ctx), false),
                    ApiReg(EntitiesApi(ctx), true)
            )
    )

    // =========================================================================
    // 5: Run the CLI
    // =========================================================================
    shell.run()
}