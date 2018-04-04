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

import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.common.Credentials
import slatekit.common.DateTime
import slatekit.core.app.AppRunner.build
import slatekit.core.cli.CliSettings
import slatekit.integration.apis.AppApi
import slatekit.integration.apis.CliApi
import slatekit.integration.apis.EntitiesApi
import slatekit.integration.apis.VersionApi
import slatekit.integration.common.AppEntContext
import slatekit.sampleapp.core.apis.*
import slatekit.sampleapp.core.common.AppApiKeys
import slatekit.sampleapp.core.common.AppAuth
import slatekit.sampleapp.core.common.AppEncryptor
import slatekit.sampleapp.core.models.Movie
import slatekit.sampleapp.core.models.User
import slatekit.sampleapp.core.services.*
import test.common.SampleAnnoApi


/**
 * Entry point into the sample console application.
 */
fun main(args: Array<String>): Unit {
    // =========================================================================
    // 1: Build the application context
    // =========================================================================
    // NOTE: The app context contains the selected environment, logger,
    // conf, command line args database, encryptor, and many other components
    val ctx = AppEntContext.fromAppContext(build(
            args = args,
            enc = AppEncryptor
    ))

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
    val shell = CliApi(creds, ctx, auth, CliSettings(enableLogging = true, enableOutput = true),
            listOf(
                    // Sample APIs for demo purposes
                    // Instances are created per request.
                    // The primary constructor must have either 0 parameters
                    // or a single paramter taking the same Context as ctx above )

                    // Example 1: without annotations ( pure kotlin objects )
                    Api(SamplePOKOApi::class      , area = "samples", name = "SamplePOKO", declaredOnly = false),

                    // Example 2: passing in and returning data-types
                    Api(SampleTypes1Api::class    , area = "samples", name = "SampleTypes1", declaredOnly = false),
                    Api(SampleTypes2Api::class    , area = "samples", name = "Sampletypes2", declaredOnly = false),

                    // Example 3: annotations
                    Api(SampleTypes3Api::class    , setup = Annotated, declaredOnly = false),
                    Api(SampleAnnoApi::class      , setup = Annotated, declaredOnly = false),

                    // Example 4: using REST ( you must register the REST rewrite module
                    Api(SampleRESTApi::class      , area = "samples", name = "SampleREST", declaredOnly = false),

                    // Example 5: File download
                    Api(SampleFiles3Api::class    , area = "samples", name = "SampleFiles",declaredOnly = false),

                    // Example 6: Inheritance with APIs
                    Api(SampleExtendedApi::class     , area = "samples", name = "SampleExtended", declaredOnly = false),

                    // Example 7: Singleton APIS - 1 instance for all requests
                    // NOTE: be careful and ensure that your APIs are stateless
                    // This example shows integration with the ORM
                    Api(SampleEntityApi(ctx)      , area = "samples", name = "SampleEntity", declaredOnly = false),

                    // Example 8: Middleware
                    Api(SampleErrorsApi(true)                , area = "samples", name = "SampleErrors", declaredOnly = false),
                    Api(SampleMiddlewareApi() , area = "samples", name = "SampleTypes1", declaredOnly = false),

                    // Example 9: Provided by Slate Kit
                    Api(AppApi(ctx)          , setup = Annotated, declaredOnly = true ),
                    Api(VersionApi(ctx)      , setup = Annotated, declaredOnly = true ),

                    // Example 10: More examples from the sample app
                    Api(UserApi(ctx)         , setup = Annotated, declaredOnly = false),
                    Api(MovieApi(ctx)        , setup = Annotated, declaredOnly = false)

            )
    )

    // =========================================================================
    // 5: Run the CLI
    // =========================================================================
    shell.run()
}
