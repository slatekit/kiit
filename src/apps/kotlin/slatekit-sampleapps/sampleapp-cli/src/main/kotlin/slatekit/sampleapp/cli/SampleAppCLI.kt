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

import slatekit.apis.codegen.CodeGenApi
import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.common.DateTime
import slatekit.common.args.ArgsSchema
import slatekit.common.info.About
import slatekit.common.info.Credentials
import slatekit.core.app.App
import slatekit.core.app.AppRunner
import slatekit.core.cli.CliSettings
import slatekit.core.common.AppContext
import slatekit.integration.apis.CliApi
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


/**
 * Entry point into the sample console application.
 */
fun main(args: Array<String>) {

    AppRunner.run(
            rawArgs = args,
            about   = SampleAppCLI.about,
            schema  = SampleAppCLI.schema,
            enc     = AppEncryptor,
            logs    = LogbackLogs(),
            builder = { ctx: AppContext -> SampleAppCLI(ctx) }
    )
}


class SampleAppCLI(context: AppContext) : App<AppEntContext>(AppEntContext.fromAppContext(context)) {

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
                id = "sample_app_cli",
                name = "Sample App CLI",
                desc = "Sample Command Line ",
                company = "Slatekit",
                region = "NY",
                version = "1.0.0",
                url = "www.slatekit.com",
                group = "codehelix",
                contact = "kishore@codehelix.co",
                tags = "sample, template, app",
                examples = "http://www.slatekit.com/kotlin-core-app.html"
        )
    }

    /**
     * initialize app context, database and ORM / entities.
     *
     * NOTE: If you extend this class from AppBase ( see SampleApp.Core project ),
     * which contains this init code. That way you don't have to duplicate if for the app types
     * below. This approach works in the initialization of app context is same for all the app types.
     * 1. console
     * 2. cli
     * 3. server
     */
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
        //    entityType = typeOf[Movie],
        //    serviceType= typeOf[MovieService],
        //    repository= EntityRepoMySql[Movie](typeOf[Movie]))
        ctx.ent.register<User>(entityType = User::class, serviceType = UserService::class, serviceCtx = ctx)
        ctx.ent.register<Movie>(entityType = Movie::class, serviceType = MovieService::class, serviceCtx = ctx)
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
        return Success(true)
    }


    /**
     * You implement this method to executes the app
     *
     * @return
     */
    override fun execute(): Try<Boolean> {
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
                        Api(CodeGenApi(), setup = Annotated),

                        Api(SamplePOKOApi::class, area = "samples", name = "SamplePOKO", declaredOnly = false, desc = "Sample to show APIs as pure class methods"),

                        // Example 2: passing in and returning data-types
                        Api(SampleTypes1Api::class, area = "samples", name = "SampleTypes1", declaredOnly = false, desc = "Sample to show APIs with basic datatypes"),
                        Api(SampleTypes2Api::class, area = "samples", name = "Sampletypes2", declaredOnly = false, desc = "Sample to show APIs with objects, lists, maps"),

                        // Example 3: annotations
                        Api(SampleTypes3Api::class, setup = Annotated, declaredOnly = false),
                        //Api(SampleAnnoApi::class      , setup = Annotated, declaredOnly = false),

                        // Example 4: using REST ( you must register the REST rewrite module
                        //Api(SampleRESTApi::class      , area = "samples", name = "SampleREST", declaredOnly = false, desc = "Sample to show APIs that are REST-like"),

                        // Example 5: File download
                        //Api(SampleFiles3Api::class    , area = "samples", name = "SampleFiles",declaredOnly = false, desc = "Sample to show APIs with file upload/download"),

                        // Example 6: Inheritance with APIs
                        //Api(SampleExtendedApi::class     , area = "samples", name = "SampleExtended", declaredOnly = false, desc = "Sample to show APIs with inherited members"),

                        // Example 7: Singleton APIS - 1 instance for all requests
                        // NOTE: be careful and ensure that your APIs are stateless
                        // This example shows integration with the ORM
                        //Api(SampleEntityApi(ctx)      , area = "samples", name = "SampleEntity", declaredOnly = false, desc = "Sample to show APIs with built in support for entities/CRUD"),

                        // Example 8: Middleware
                        //Api(SampleErrorsApi(true)  , area = "samples", name = "SampleErrors", declaredOnly = false, desc = "Sample to show APIs with error handling"),
                        //Api(SampleMiddlewareApi() , area = "samples", name = "SampleTypes1", declaredOnly = false, desc = "Sample to show APIs with middle ware ( hooks, filters )"),

                        // Example 9: Provided by Slate Kit
                        //Api(AppApi(ctx)          , setup = Annotated, declaredOnly = true ),
                        //Api(VersionApi(ctx)      , setup = Annotated, declaredOnly = true ),

                        // Example 10: More examples from the sample app
                        Api(UserApi(ctx), setup = Annotated, declaredOnly = false),
                        Api(MovieApi(ctx), setup = Annotated, declaredOnly = false)

                )
        )

        // =========================================================================
        // 5: Run the CLI
        // =========================================================================
        shell.run()
        return Success(true)
    }
}
