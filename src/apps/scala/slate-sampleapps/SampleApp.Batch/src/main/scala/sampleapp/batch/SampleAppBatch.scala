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

package sampleapp.batch

import sampleapp.core.common.AppEncryptor
import sampleapp.core.models.{Movie, User}
import sampleapp.core.services.{MovieService, UserService}

import slate.common.Result
import slate.common.app.AppMeta
import slate.common.args.ArgsSchema
import slate.common.console.{ConsoleSettings, ConsoleWriter}
import slate.common.databases.DbLookup
import slate.common.encrypt.Encryptor
import slate.common.info.{About, Lang, Host}
import slate.common.logging.LoggerConsole
import slate.core.app.{AppRunner, AppProcess}
import slate.core.common.{AppContext, Conf}
import slate.entities.core.Entities
import slate.entities.repos.EntityRepoInMemory

import scala.reflect.runtime.universe.{typeOf}

object SampleAppBatch
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
    AppRunner.run(new SampleAppBatch(), Option(args))
  }
}



/**
  * Sample console application.
  *
  * IMPORTANT
  * 1. You can further extend the slate AppProcess ( refer to AppBase in SampleApp.Core
  * 2. The onInit method is ONLY provided here to show how the context can be set up
  * 3. The AppBase class ( in SampleApp.Core ) can be used to have a common base class with
  *    the onInit method already implemented for your specific needs.
  *
  * NOTE(s):
  * 1. you can extend from AppBase ( SampleApp.Core ) to avoid initializing context in onInit here
  * 2. command line arguments are optional but set up here for demo purposes
  */
class SampleAppBatch extends AppProcess
{
  // setup the command line arguments.
  // NOTE:
  // 1. These values can can be setup in the env.conf file
  // 2. If supplied on command line, they override the values in .conf file
  // 3. If any of these are required and not supplied, then an error is display and program exists
  // 4. Help text can be easily built from this schema.
  override lazy val argsSchema = new ArgsSchema()
            .text("env"        , "the environment to run in"      , false, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
            .text("region"     , "the region linked to app"       , false, "us"   , "us"   , "us|europe|india|*")
            .text("config.loc" , "location of config files"       , false, "jar"  , "jar"  , "jar|conf")
            .text("log.level"  , "the log level for logging"      , false, "info" , "info" , "debug|info|warn|error")


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
    // Initialize the context with common app info
    // The database can be set up in the "env.conf" shared inherited config or
    // overridden in the environment specific e.g. "env.qa.conf"
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

    // Initialize the database if enabled
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
    ctx.ent.register[User](isSqlRepo= false, entityType = typeOf[User],
      serviceType= typeOf[UserService], repository= new EntityRepoInMemory[User](typeOf[User]))
  }


  /**
   * You implement this method to executes the app
   *
   * @return
   */
  override def onExecute():Result[Any] =
  {
    info("app executing now")

    info(conf.getString("env.desc"))
    info(conf.getString("log.level"))

    // Feature 1: Log methods available from LogSupport trait
    // NOTE: This uses the console logger setup in the context in init.
    info("")
    info ("LOGGING examples: ==================================================")
    debug("debug example using trait method from LogSupportIn")
    info ("info  example using trait method from LogSupportIn")
    warn ("warn  example using trait method from LogSupportIn")
    error("error example using trait method from LogSupportIn")
    fatal("fatal example using trait method from LogSupportIn")
    info ("")

    // Feature 2: Encrypt / Decrypt support using the Encryptor setup in context
    // NOTE: This uses the encryptor setup in the context in the init.
    info ("ENCRYPTION examples: ===============================================")
    val encrypted = encrypt("Hello World")
    info(s"encrypted 'hello world' = ${encrypted}" )
    info(s"decrypted '${encrypted}' = " + decrypt(encrypted))
    info ("")

    // Feature 3: Get config settings
    // NOTE: This uses the config setup in the context in the init
    info ("CONFIG examples: ==================================================")
    info ("app.name = " + conf.getString("app.name"))
    info ("====================================================================")
    info("simulating work for 1 second. please wait...")
    Thread.sleep(1000)

    info("app completed")

    ok()
  }


  /**
   * HOOK for when app is shutting down
   */
  override def onShutdown(): Unit =
  {
    info("app shutting down")
  }


  /**
   * HOOK for adding items to the summary of data shown at the end of app execution
   */
  override def collectSummaryExtra(): Option[List[(String,String)]] =
  {
    Some(List[(String,String)](
      ("region",  args.getStringOrElse("region", "n/a"))
    ))
  }
}