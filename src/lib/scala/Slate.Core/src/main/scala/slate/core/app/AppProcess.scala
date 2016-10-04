package slate.core.app

import slate.common.app.{AppRunConst, AppMetaSupport, AppMeta}
import slate.common.args.{ArgsHelper, ArgsSchema, Args}
import slate.common.conf.ConfigBase
import slate.common.encrypt.EncryptSupportIn
import slate.common.i18n.I18nSupportIn
import slate.common.info._
import slate.common._
import slate.common.logging.LogLevel.LogLevel
import slate.common.logging.{Logger, LoggerConsole, LogLevel, LogSupportIn}
import slate.common.results.ResultSupportIn
import slate.common.subs.Subs
import slate.core.common.{Conf, AppContext}
import slate.entities.core.Entities

import scala.collection.immutable.HashMap
import scala.collection.mutable.{ListBuffer, ArrayBuffer}
import scala.util.Try

/**
 * Application base class providing most of the scaffolding to support command line argument
  * checking, app metadata, life-cycle template methods and more. This allows derived classes
  * to be very thin and focus on simply executing main logic of the app.
 */
class AppProcess extends AppMetaSupport
  with EncryptSupportIn
  with LogSupportIn
  with I18nSupportIn
  with ResultSupportIn
{
  var ctx:AppContext = null
  var meta = new AppMeta()
  var options = new AppOptions()

  // Wrapper for println with color coding and semantics ( title, subtitle, url, error )
  val writer = new ConsoleWriter()

  // env.conf is the base config with settings common to all other configs ( dev, qa, stg, pro )
  var confBase:ConfigBase = new Conf(Some("env.conf"))

  // the environment specific conf e.g. "env.dev.conf"
  var conf:ConfigBase = null

  // The selected environment at startup ( can come from command line or conf )
  var env:EnvItem = null

  // arguments supplied on the command line: parsed
  var args:Args = null

  // the schema of the all supported command line args for this app
  val argsSchema = new ArgsSchema()

  // the raw command line arguments: un-parsed
  var argsRaw:Option[Array[String]] = None


  /**
   * gets the application metadata containing information about this shell application,
   * host, language runtime. The meta can be updated in the derived class.
   *
   * @return
   */
  override def appMeta(): AppMeta = meta

  /**
   * checks the command line arguments.
   *
   * @param raw
   */
  def check(raw:Option[Array[String]]): Result[Any] = {

    argsRaw = raw

    // 1. Parse args
    val result = Args.parseArgs(raw.getOrElse(Array[String]()), "-", "=", false)

    // 2. Bad args?
    if (!result.success) {
      writer.line()
      writer.error("Input parameters invalid")
      writer.line()
      println(argsSchema.buildHelp())
      return badRequest(Some("invalid arguments supplied"))
    }

    // 3. Help request
    val helpCheck = argsRaw.fold[Result[String]](failure())( args => isAppInfoRequest(args.toList))
    if(helpCheck.success) {
      return help()
    }

    // 4. Determine env, log, config from inputs if supplied
    args = result.get

    // 5. Invalid inputs
    val checkResult = argsSchema.validate(args)
    if(!checkResult.success){
      writer.line()
      writer.error(checkResult.msg.getOrElse(""))
      writer.line()
      argsSchema.buildHelp()
      return checkResult
    }
    success(args)
  }


  /**
   * initializes this app before applying the arguments
   * this is good place to set app metadata.
   */
  def init(): Unit =
  {
    // 1. Load the base conf "env.conf" from the directory specified.
    // or specified in the "conf.dirs" config setting in the env.conf file
    // a) -conf="jars"                  = embedded in jar files
    // b) -conf="conf"                  = expect directory ./conf
    // c) -conf="file://./conf-samples  = expect directory ./conf-samples
    // d) not specified = defaults to jars.
    // NOTES:
    // 1. The location of the directory can be overriden on the command line
    // 2. The conf base is loaded again since if the "-help" arg was supplied
    // if will get the info from the confBase ( env.conf )
    confBase = new Conf(Some(getConfPath("env.conf", None)))

    // 2. The environment can be selected in the following order:
    // - command line ( via "-env=dev"   )
    // - env.conf ( via env.name = dev )
    // getEnv will first look for selected environment from args, then in config.
    val envInput = getEnv()

    // 2. Validate the environment
    val envCheck = validateEnv(envInput)
    if(!envCheck.success){
      writer.error(envCheck.msg.getOrElse(""))
      throw new IllegalArgumentException(envCheck.msg.getOrElse(""))
    }

    // 3. Get the env from validation ( e.g. could supply short hand
    // "dev" on command line but get the full env info from match
    env = envCheck.get

    // 4. We now have the environment to use ( e.g. "dev" )
    // Now load the final environment specific override
    // for directory reference provide: "file://./conf/"
    val overrideConfPath = Some( getConfPath(s"env.${env.name}.conf", Some(confBase)))
    conf = Conf.loadWithFallbackConfig(overrideConfPath, confBase)

    // 5. Let derived app build initialize itself. it may also build the context using the
    // env, conf base, conf objects.
    onInit()

    // 6. NOTE: The derived class may not implement onInit to build the ctx.
    // In this case create the minimal version of ctx.
    if(ctx == null){
      ctx = new AppContext (
        app  = new AppMeta(),
        env  = env,
        cfg  = conf,
        log  = new LoggerConsole(),
        ent  = new Entities(),
        inf  = aboutApp(),
        lang = Lang.asScala(),
        host = Host.local(),
        dirs = Some(folders())
      )
    }

    // 7. Create the folders for the application if applicable
    try {
      ctx.dirs.map(dirs => dirs.create())
    }
    catch {
      case e:Exception => {
        println("Error while creating directories for application in user.home directory")
      }
    }
  }


  /**
    * used for derived class to handle command line args
    *
    */
  def onInit(): Unit =
  {
  }


  /**
   * accepts command line args
    *
   */
  def accept(): Unit =
  {
    // Set the support services ( log, cfg, enc )
    _log = Option(ctx.log)
    _enc = ctx.enc

    // Get the host and language
    meta = ctx.app
    meta.about = ctx.inf
    meta.host = Host.local()
    meta.lang = Lang.asScala()
    onAccept()

    // set the startup info to track times.
    meta.start = new StartInfo(argsRaw, ctx.log.name, s"env.${env.name}.conf", env.name)
  }


  /**
   * used for derived class to handle command line args
    *
   */
  def onAccept(): Unit =
  {

  }


  /**
   * executes this application
    *
    * @return
   */
  def exec(): Result[Any] =
  {
    meta.status.started = DateTime.now
    meta.status.status = "started"

    if(options.printSummaryBeforeExec)
    {
      logStart()
    }

    var res:Result[Any] = null
    try {
      res = onExecute()
    }
    catch {
      case e: Exception => {
        error("error while executing app : " + e.getMessage)
        meta.status.error = e.getMessage
        meta.status.errors += 1
      }
    }

    meta.status.status = "ended"
    meta.status.ended = DateTime.now

    res
  }


  /**
   * the method that does all the work of this application.
   * should be overriden in base class
    *
    * @return
   */
  def onExecute(): Result[Any] =
  {
    success[Any]("default")
  }


  /**
   * runs shutdown logic
   */
  def shutdown(): Unit =
  {
    try {
      onShutdown()
    }
    catch {
      case e: Exception => {
        error("error while shutting down app : " + e.getMessage)
      }
    }
    if(options.printSummaryOnShutdown)
    {
      logSummary()
    }
  }


  /**
   * derived classes can implement this
   */
  def onShutdown(): Unit =
  {
  }


  /**
   * builds a list of properties fully describing this app by adding
   * all the properties from the about, host and lang fields.
    *
    * @return
   */
  def info() : List[(String,Any)] = {
    meta.info()
  }


  /**
   * prints the summary of the arguments
   */
  def logStart():Unit =
  {
    info( "===============================================================")
    this.appLogStart( (name, value) => info( name + value) )
    info( "STARTING : "                                    )
    info( "===============================================================")
  }


  /**
   * prints the summary of the arguments
   */
  def logSummary():Unit =
  {
    val args = ListBuffer[String]()
    info( "===============================================================")
    info( "SUMMARY : ")
    info( "===============================================================")
    collectSummary(args)

    val extra = collectSummaryExtra()
    if(extra.isDefined ) {
      args ++= extra.get
      for (arg <- args) {
        info(arg)
      }
    }
    info( "===============================================================")
  }


  def collectSummaryExtra(): Option[List[String]] =
  {
    None
  }


  protected def isAppInfoRequest(raw:List[String]):Result[String] = {

    // Case 1: Exit ?
    if (ArgsHelper.isExit(raw, 0))
    {
      writer.error("exiting")
      return exit()
    }
    // Case 2a: version ?
    if (ArgsHelper.isVersion(raw, 0))
    {
      writer.line()
      writer.highlight("version :", false)
      writer.url(aboutApp().version, true)
      writer.line()
      return help()
    }
    // Case 2b: about ?
    // Case 3a: Help ?
    if (ArgsHelper.isAbout(raw, 0) || ArgsHelper.isHelp(raw, 0))
    {
      writer.line()
      writer.text("==============================================")
      writer.title("ABOUT")
      aboutApp().log( (key, value) => {
        writer.highlight(key + " : ", false)
        writer.text(value, true)
      })
      writer.text("==============================================")
      writer.line()
      writer.title("ARGS")
      argsSchema.buildHelp()
      writer.line()
      return help()
    }
    failure[String]()
  }


  /**
   * The list of available environments to choose from.
   * An environment definition is defined by its name, mode
   * The key is built up from name and mode as {name}.{mode}
   * e.g. "qa1.QA"
   *
   * Each of these environments has an associated env.{name}.conf
   * config file in the /resources/ directory.
   * e.g.
   * /resources/env.conf     ( common      config )
   * /resources/env.loc.conf ( local       config )
   * /resources/env.dev.conf ( development config )
   * /resources/env.qa1.conf ( qa1         config )
   * /resources/env.qa2.conf ( qa2         config )
   * /resources/env.stg.conf ( staging     config )
   * /resources/env.pro.conf ( production  config )
   *
   * @return
   */
  protected def envs(): List[EnvItem] = {
    List[EnvItem](
      EnvItem("loc", Env.DEV),
      EnvItem("dev", Env.DEV),
      EnvItem("qa1", Env.QA),
      EnvItem("qa2", Env.QA),
      EnvItem("stg", Env.UAT),
      EnvItem("pro", Env.PROD)
    )
  }


  /**
    * builds all the info for this application including its id, name, company, contact info, etc.
    *
    * @return
    */
  protected def aboutApp():About = {
    // Get info about app from base config "env.conf" which is common to all environments.
    new About(
      id       = confBase.getStringOrElse("app.id"       , "sampleapp.console"),
      name     = confBase.getStringOrElse("app.name"     , "Sample App - Console"),
      desc     = confBase.getStringOrElse("app.desc"     , "Sample to show the base application"),
      company  = confBase.getStringOrElse("app.company"  , "slatekit"),
      region   = confBase.getStringOrElse("app.region"   , "ny"),
      version  = confBase.getStringOrElse("app.version"  , "0.9.1"),
      url      = confBase.getStringOrElse("app.url"      , "http://sampleapp.slatekit.com"),
      group    = confBase.getStringOrElse("app.group"    , "products-dept"),
      contact  = confBase.getStringOrElse("app.contact"  , "kishore@abc.co"),
      tags     = confBase.getStringOrElse("app.tags"     , "slate,shell,cli"),
      examples = confBase.getStringOrElse("app.examples" , "")
    )
  }


  /**
    * builds a list of directories used by the application for logs/output ( NOT BINARIES ).
    * Folders represent the names/locations of the directories
    * used by this application.
    * The structure is a parent/child one based on company/apps/app
    * e.g.
    * - Company
    *    - apps
    *        - app 1
    *            - logs
    *            - cache
    *            - output
    *        - app 2
    *
    * @return
    */
  protected def folders():Folders = {

    val about = aboutApp()
    new Folders(
      location = AppRunConst.LOCATION_USERDIR,
      root    = Some(Strings.toId(about.company)),
      group   = Some(Strings.toId(about.group)),
      app     = about.id,
      cache   = "cache",
      inputs  = "inputs",
      logs    = "logs",
      outputs = "outputs"
    )
  }


  /**
    * builds a list of substitutions ( variables ) that can be used dynamically
    * throughout the application to refer to various parts/settings of the app.
    * e.g. used in the .conf files to load settings from a file where the name
    * of the file can be based off the name of the application, company name, etc.
    *
    * @return
    */
  protected def vars(): Subs = {
    val about = aboutApp()
    val subs = new Subs()
    subs("user.home"    ) = (s) => System.getProperty("user.home")
    subs("company.id"   ) = (s) => Strings.toId(about.company)
    subs("company.name" ) = (s) => about.company
    subs("company.dir"  ) = (s) => "@{user.home}/@{company.id}"
    subs("root.dir"     ) = (s) => "@{company.dir}"
    subs("group.id"     ) = (s) => Strings.toId(about.group)
    subs("group.name"   ) = (s) => about.group
    subs("group.dir"    ) = (s) => "@{root.dir}/@{group.id}"
    subs("app.id"       ) = (s) => about.id
    subs("app.name"     ) = (s) => about.name
    subs("app.dir"      ) = (s) => "@{root.dir}/@{group.id}/@{app.id}"
    subs
  }


  protected def getOverrideSetting(key:String, defaultValue:Option[String],
                                   confSource:Option[ConfigBase] = None):String = {

    // 1. Check if supplied on command line
    val argSetting = args.getStringOrElse(key, "")

    // 2. Supplied on the command line!
    // This overrides the config
    if(!Strings.isNullOrEmpty(argSetting)) {
      return argSetting
    }
    // 3. Check the config
    val actualConfig = confSource.getOrElse(conf)
    val confSetting = actualConfig.getStringOrElse(key, defaultValue.getOrElse(""))

    // 4. Supplied in config!
    if(!Strings.isNullOrEmpty(confSetting)) {
      return confSetting
    }
    defaultValue.getOrElse("")
  }


  /**
    * gets the selected environment by key "env" from command line args first or env.conf second
    *
    * @return
    */
  protected def getEnv(): EnvItem = {
    val env = getOverrideSetting("env", Some("loc"), Some(confBase))
    Env.parse(env)
  }


  /**
    * gets log level by key "log.level" from command line args first or environment config 2nd
    *
    * @return
    */
  protected def getLogLevel(): LogLevel = {
    val level = getOverrideSetting("log.level", Some("info"))
    Logger.parseLogLevel(level)
  }


  /**
    * gets log name by key "log.name" from command line args first or environment config 2nd
    *
    * @return
    */
  protected def getLogName(): String = {
    val log = getOverrideSetting("log.name", Some("@{app}-@{env}-@{date}.log"))
    interpolate(log)
  }


  /**
    * Replaces any ids in the text with known values.
    *
    * @param raw : "@{app}-@{env}-@{date}.log"
    * @return "myapp-dev-20160710-930am.log"
    */
  protected def interpolate(raw:String): String = {
    var text = raw
    text = text.replace("@{app}", ctx.inf.name)
    text = text.replace("@{env}", ctx.env.name)
    text = text.replace("@{date}", DateTime.now().toStringNumeric())
    text
  }


  /**
   * validates the environment against the supported
   *
   * @param env
   * @return
   */
  protected def validateEnv(env:EnvItem): Result[EnvItem] = {
    val matched = envs().filter( item => Strings.isMatch(item.name, env.name ))
    if(matched != null && matched.size > 0)
      return success(matched.head)

    failure(Some(s"Unknown environment name : ${env.name} supplied"))
  }


  protected def getConfPath(file:String, conf:Option[ConfigBase]):String = {
    val pathFromArgs = Option(args.getStringOrElse("conf.dir", null))
    val location = pathFromArgs.getOrElse( conf.fold("")( c => c.getStringOrElse("conf.dir", "")))
    val prefix = location match {
      case "jars" => ""
      case "conf" => "file://./conf/"
      case ""     => ""
      case _      => location
    }
    prefix + file
  }


  protected def getApiKey(name:String):ApiCredentials = {
    val key = ctx.cfg.apiKey(name)
    Ensure.isTrue(key.isDefined, s"Api Key with name $name is not configured")
    key.get 
  }


  private def collectSummary(args:ListBuffer[String]): Unit =
  {
    this.appLogEnd( (name, value) => {
      args += name + value
    })
  }
}

