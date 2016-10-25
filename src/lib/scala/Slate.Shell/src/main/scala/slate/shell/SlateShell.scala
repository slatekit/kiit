/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.shell

import slate.common.app.{AppRunConst, AppMeta}
import slate.common.args.ArgsSchema
import slate.common.databases.DbLookup
import slate.common.encrypt.Encryptor
import slate.common.logging.LoggerConsole
import slate.common._
import slate.common.info.{Folders, Lang, Host, About}
import slate.core.apis.{ApiReg, ApiAuth}
import slate.core.app.{AppProcess, AppRunner}
import slate.core.common.{Conf, AppContext}
import slate.entities.core.Entities
import slate.tools.codegen.CodeGenApi
import slate.tools.docs.DocApi
import slate.integration.{VersionApi,AppApi}
import slate.core.shell._

/**
  * Created by kreddy on 3/23/2016.
  */
object SlateShell extends AppProcess {


  def main(args: Array[String]): Unit = {
    AppRunner.run(this, Some(args))
  }


  // STEP 1. setup the command line arguments.
  // NOTE:
  // 1. These values can can be setup in the env.conf file
  // 2. If supplied on command line, they override the values in .conf file
  // 3. If any of these are required and not supplied, then an error is display and program exists
  // 4. Help text can be easily built from this schema.
  override lazy val argsSchema = new ArgsSchema()
            .text("env"   , "the environment to run in", false, ""     , "dev"  , "dev1|qa1|stg1|pro" )
            .text("log"   , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")


  /**
    * initialize app and metadata
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
      enc  = Some(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")),
      dirs = Some(folders)
    )

    // 3. Initialize the database if enabled
    DbLookup.setDefault(ctx.con)
  }


  /**
    * executes the app
    *
    * @return
    */
  override def onExecute():Result[Any] =
  {
    writer.text("************************************")
    writer.title("Welcome to Slate.Shell")
    writer.text("************************************")
    writer.line()

    // 1. Get the user login info from .slate
    val creds = new Credentials("1", "john doe", "jdoe@gmail.com", key = buildApiKeys()(5).key)
    val auth = new ApiAuth(buildApiKeys(), null)

    // 2. Build up the shell services that handles all the command line features.
    // And setup the api container to hold all the apis.
    val shell = new ShellAPI(creds, ctx, auth, "sampleapp", new ShellSettings( enableLogging = true, enableOutput = true),
      apiItems = Some(List[ApiReg](
        new ApiReg(new AppApi()    , true, Some("qa"), protocol = Some("*")),
        new ApiReg(new VersionApi(), true, Some("qa"), protocol = Some("*")),
        new ApiReg(new DocApi()    , true, Some("qa"), protocol = Some("*")),
        new ApiReg(new CodeGenApi(), true, Some("qa"), protocol = Some("*"))
      )
    ))

    // 4. Initialize the apis
    shell.apis.init()

    // 5. Provide the apis to the shell and run!
    shell.run()

    success(true)
  }


  /**
    * called when app is done
    */
  override def onShutdown(): Unit =
  {
    info("slate.shell shutting down")
  }


  private def buildApiKeys():List[ApiKey] = {
    List[ApiKey](
        new ApiKey("user" , "7BF84B28FC8A41BBA3FDFA48D2B462DA", "user"                    ),
        new ApiKey("po"   , "0F66CD55079C42FF85C001846472343C", "user,po"                 ),
        new ApiKey("qa"   , "EB7EB37764AD4411A1763E6A593992BD", "user,po,qa"              ),
        new ApiKey("dev"  , "3E35584A8DE0460BB28D6E0D32FB4CFD", "user,po,qa,dev"          ),
        new ApiKey("ops"  , "5020F4A237A443B4BEDC37D8A08588A3", "user,po,qa,dev,ops"      ),
        new ApiKey("admin", "54B1817194C1450B886404C6BEA81673", "user,po,qa,dev,ops,admin")
      )
  }
}
