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

package sampleapp.cli

import slate.common._
import slate.common.app.AppMeta
import slate.common.args.ArgsSchema
import slate.common.databases.DbLookup
import slate.common.info.{About, Lang, Host}
import slate.common.logging.LoggerConsole
import slate.core.apis.{ApiReg, ApiBase, ApiAuth, ApiContainerCLI}
import slate.core.app.{AppRunner, AppProcess}
import slate.core.common.AppContext
import slate.core.shell.{ShellSettings, ShellAPI}
import slate.entities.core.Entities
import slate.entities.models.ModelSettings
import slate.entities.repos.{EntityRepoMySql, EntityRepoInMemory}
import slate.integration.{EntitiesApi, VersionApi, AppApi}

import scala.reflect.runtime.universe.{typeOf}

import sampleapp.core.common._
import sampleapp.core.models._
import sampleapp.core.services._


object SampleAppCLI
{

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
  def main(args: Array[String]): Unit = {
    // Supply the args passed into app to runner.
    // NOTE: The args format is -key=value.
    AppRunner.run(new SampleAppCLI(), Option(args))
  }
}



/**
  * Sample CLI application.
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
class SampleAppCLI extends AppProcess
{

  // setup the command line arguments.
  // NOTE:
  // 1. These values can can be setup in the env.conf file
  // 2. If supplied on command line, they override the values in .conf file
  // 3. If any of these are required and not supplied, then an error is display and program exists
  // 4. Help text can be easily built from this schema.
  override lazy val argsSchema = new ArgsSchema()
            .text("env"        , "the environment to run in", false, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
            .text("region"     , "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*")
            .text("log.level"  , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")


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
  override def onInit(): Unit =
  {
    // 2. Initialize the context with common app info
    // NOTE:
    // - Environment selection ( dev, qa, prod ) is set in env.conf
    // - Database selection.
    ctx = new AppContext (
      env  = env,
      cfg  = conf,
      log  = new LoggerConsole(getLogLevel()),
      ent  = new Entities(),
      inf  = aboutApp(),
      con  = conf.dbCon(),
      enc  = Some(AppEncryptor),
      dirs = Some(folders())
    )

    // 3. Initialize the database if enabled
    // NOTE(s):
    // 1. There is a sample mysql database connection in common environment config "env.conf".
    // 2. It is currently disabled for loading via the db.enabled = false flag.
    // 3. To enable loading of the connection and making it available in ctx.con
    //    set db.enabled = true
    DbLookup.setDefault(ctx.con)

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
    //   serviceType= typeOf[UserService], repository= new EntityRepoMySql[User](typeOf[User]))
    // ctx.ent.register[Movie](isSqlRepo= true, entityType = typeOf[Movie],
    //   serviceType= typeOf[MovieService], repository= new EntityRepoMySql[Movie](typeOf[Movie]))

    // =========================================================================
    // NOTE: Comment below to use MySql based Repositories
    // =========================================================================
    ctx.ent.register[User](isSqlRepo= false, entityType = typeOf[User], serviceType= typeOf[UserService])
    ctx.ent.register[Movie](isSqlRepo= false, entityType = typeOf[Movie], serviceType= typeOf[MovieService])
  }


  /**
    * executes the app
    *
    * @return
    */
  override def onExecute():Result[Any] =
  {
    writer.text("************************************")
    writer.title("Welcome to SampleApp.CLI")
    writer.text("************************************")
    writer.line()
    writer.text("starting in environment: " + this.ctx.env.key +
              " " + this.ctx.cfg.getStringOrElse("env.desc", ""))

    // 1. Create a sample user for authentication.
    val sampleKeys = AppApiKeys.fetch()
    val selectedKey = sampleKeys(5)
    val creds = new Credentials("1", "john doe", "jdoe@gmail.com", key = selectedKey.key)
    val auth = new AppAuth("test-mode", "slatekit", "johndoe", selectedKey, sampleKeys)

    // 2. Build up the shell services that handles all the command line features.
    // And setup the api container to hold all the apis.
    val shell = new ShellAPI(creds, ctx, auth, "sampleapp",
      new ShellSettings( enableLogging = true, enableOutput = true),
      Some(
        List[ApiReg](
          new ApiReg(new AppApi()    , true  ),
          new ApiReg(new VersionApi(), true  ),
          new ApiReg(new UserApi()   , false ),
          new ApiReg(new MovieApi()  , false ),
          new ApiReg(new EntitiesApi(new ModelSettings(true, true)), true )
        )
      )
    )

    shell.apis.init()

    // 4. Run the server ( this starts the life-cycle init, execute, shutdown )
    shell.run()

    success(true)
  }


  /**
    * HOOK for when app is shutting down
    */
  override def onShutdown(): Unit =
  {
    info("app shutting down")
  }


  /**
    * HOOK for adding more items to the summary of data shown at the end
    *
    */
  override def collectSummaryExtra(): Option[List[String]] =
  {
    Some(List[String](
      meta.about.name + ": extra 1  = extra summary data1"
    ))
  }
}