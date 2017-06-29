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
import slatekit.common.Result
import slatekit.common.args.ArgsSchema
import slatekit.common.results.ResultFuncs.success
import slatekit.core.app.AppOptions
import slatekit.core.app.AppProcess
import slatekit.core.app.AppRunner.build
import slatekit.core.app.AppRunner.run
import slatekit.core.common.AppContext
import slatekit.integration.AppApi
import slatekit.integration.EntitiesApi
import slatekit.integration.VersionApi
import slatekit.sampleapp.core.common.*
import slatekit.sampleapp.core.models.*
import slatekit.sampleapp.core.services.*
import slatekit.server.Server
import slatekit.server.ServerConfig


  /**
   * Entry point into the sample console application.
   *
   * java -jar sample_app.jar -env=dev -log.level=info -config.location = "jars"
   * java -jar sample_app.jar -env=dev -log.level=info -config.location = "conf"
   * java -jar sample_app.jar -env=dev -log.level=info -config.location = "file://./conf-sample-batch"
   * java -jar sample_app.jar -env=dev -log.level=info -config.location = "file://./conf-sample-shell"
   * java -jar sample_app.jar -env=dev -log.level=info -config.location = "file://./conf-sample-server"
   * java -jar sample_app.jar --version
   * java -jar sample_app.jar --about
   * java -jar sample_app.jar ?
   *
   * @param args
   */
  fun main(args: Array<String>): Unit {
    // 1. Run calls the template methods ( init, exec, shutdown )
    run(

            // 2. Instance of AppProcess
            SampleAppServer(

                    // 3. Build the Application context for the app.
                    // NOTE: The app context contains the selected
                    // environment, logger, conf, command line args,
                    // database, encryptor, and many other components
                    build(
                            args = args,
                            schema = schema,
                            enc = AppEncryptor,
                            converter = { ctx -> convert(ctx) }
                    )
            )
    )
  }


  // setup the command line arguments.
  // NOTE(s):
  // 1. These values can can be setup in the env.conf file
  // 2. If supplied on command line, they override the values in .conf file
  // 3. If any of these are required and not supplied, then an error is display and program exists
  // 4. Help text can be easily built from this schema.
  var schema = ArgsSchema()
    .text("env"        , "the environment to run in", false, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
    .text("region"     , "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*" )
    .text("port"       , "the port to run on"       , false, "5000" , "5000" , "5000|80")
    .text("domain"     , "domain association"       , false, "::0"  , "::0"  , "::0|mycompany.com")
    .text("log.level"  , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")


  /**
   * Converts a built AppContext into a final one for use in this app.
   * NOTE: This is allow customization of any member of the app context:
   * e.g.
   * - encryptor
   * - logger
   * - database
   * - metadata etc
   *
   * @param ctx
   * @return
   */
  fun convert(ctx: AppContext): AppContext {
    return ctx.copy(enc = AppEncryptor)
  }



/**
  * Sample Server application.
  *
  * IMPORTANT
  * 1. You can further extend the slate AppProcess ( refer to AppBase in SampleApp.Core )
  * 2. The onInit method is ONLY provided here to show how the context can be set up
  * 3. The AppBase class ( in SampleApp.Core ) can be used to have a common base class with
  *    the onInit method already implemented for your specific needs.
  *
  * NOTE(s):
  * 1. you can extend from AppBase ( SampleApp.Core ) to avoid initializing context in onInit here
  * 2. command line arguments are optional but set up here for demo purposes
  */
class SampleAppServer(context:AppContext) : AppProcess(context)
{
  // For server, print all info at startup
  override val options = AppOptions(printSummaryBeforeExec = true)


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
  override fun onInit(): Unit
  {
    // 4. Setup the User entity services
    // NOTE(s):
    // 1. See the ORM documentation for more info.
    // 2. The entity services uses a Generic Service/Repository pattern for ORM functionality.
    // 3. The services support CRUD operations out of the box for single-table mapped entities.
    // 4. This uses an In-Memory repository for demo but you can use EntityRepoMySql for MySql

    // =========================================================================
    // NOTE: Uncomment below to use MySql based Repositories
    // =========================================================================
    // ctx.ent.register[User](isSqlRepo= true, entityType = typeOf[User],
    //   serviceType= typeOf[UserService], repository= EntityRepoMySql[User](typeOf[User]))
    // ctx.ent.register[Movie](isSqlRepo= true, entityType = typeOf[Movie],
    //   serviceType= typeOf[MovieService], repository= EntityRepoMySql[Movie](typeOf[Movie]))

    // =========================================================================
    // NOTE: Comment below to use MySql based Repositories
    // =========================================================================
    ctx.ent.register<User>(isSqlRepo= false, entityType = User::class, serviceType  = UserService::class, serviceCtx = ctx)
    ctx.ent.register<Movie>(isSqlRepo= false, entityType = Movie::class, serviceType = MovieService::class, serviceCtx = ctx)
  }



  /**
   * You implement this method to executes the app
   *
   * @return
   */
  override fun onExecute(): Result<Any>
  {
    info("server starting")

    // 1. Build the auth provider
    val sampleKeys = AppApiKeys.fetch()
    val selectedKey = sampleKeys[5]
    val auth = AppAuth("header", "slatekit", "johndoe", selectedKey, sampleKeys)

    // 2. Initialize server with port, domain, context (see above) and auth provider
    val server = Server(
            ctx,
            auth,
            listOf(
               ApiReg(AppApi(ctx)    , true  ),
               ApiReg(VersionApi(ctx), true  ),
               ApiReg(UserApi(ctx)   , false ),
               ApiReg(MovieApi(ctx)  , false ),
               ApiReg(SampleApi(ctx)  , false )
            ),
            ServerConfig()
    )
    // 3. Init the APIs within the api container
    server.apis

    // 4. Run the server ( this starts the life-cycle init, execute, shutdown )
    server.run()

    info("server stopped")

    return success(true)
  }
}