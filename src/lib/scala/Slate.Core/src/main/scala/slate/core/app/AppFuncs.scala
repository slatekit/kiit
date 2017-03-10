/**
  * <slate_header>
  * url: www.slatekit.com
  * git: www.github.com/code-helix/slatekit
  * org: www.codehelix.co
  * author: Kishore Reddy
  * copyright: 2016 CodeHelix Solutions Inc.
  * license: refer to website and/or github
  * about: A Scala utility library, tool-kit and server backend.
  * mantra: Simplicity above all else
  * </slate_header>
  */
package slate.core.app

import slate.common.app._
import slate.common.args.{Args, ArgsFuncs}
import slate.common.databases.{DbConEmpty, DbConString, DbLookup}
import slate.common.databases.DbLookup._
import slate.common.envs._
import slate.common.logging.{LoggerConsole, LogLevel}
import slate.common.results.{ResultSupportIn}
import slate.common.templates.{TemplatePart, Subs}
import slate.common.{Result, Strings}
import slate.common.conf.ConfigBase
import slate.common.info.{Folders, About}
import slate.core.common.{Conf, AppContext}
import slate.entities.core.Entities

object AppFuncs extends ResultSupportIn {

  /**
    * The list of available environments to choose from.
    * An environment definition is defined by its name, mode
    * The key is built up from name and mode as {name}.{mode}
    * e.g. "qa1.QA"
    *
    * Each of these environments should map to an associated env.{name}.conf
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
  def envs(): List[Env] = {
    List(
      Env("loc", Dev , desc = "Dev environment (local)" ),
      Env("dev", Dev , desc = "Dev environment (shared)" ),
      Env("qa1", Qa  , desc = "QA environment  (current release)" ),
      Env("qa2", Qa  , desc = "QA environment  (last release)" ),
      Env("stg", Uat , desc = "STG environment (demo)" ),
      Env("pro", Prod, desc = "LIVE environment" )
    )
  }


  /**
    * Builds the DbLookup containing the database connections :
    * 1. default connection
    * 2. named connections
    * 3. grouped connections
    *
    * @return
    */
  def dbs(conf:ConfigBase): DbLookup = {
    defaultDb(conf.dbCon("db").getOrElse(DbConEmpty))
  }



  /**
    * builds all the info for this application including its
    * id, name, company, contact info, etc.
    *
    * These can be overriden in the config
    *
    * @return
    */
  def about( conf:ConfigBase ):About = {
    // Get info about app from base config "env.conf" which is common to all environments.
    new About(
      id       = conf.getStringOrElse("app.id"       , "sampleapp.console"),
      name     = conf.getStringOrElse("app.name"     , "Sample App - Console"),
      desc     = conf.getStringOrElse("app.desc"     , "Sample to show the base application"),
      company  = conf.getStringOrElse("app.company"  , "slatekit"),
      region   = conf.getStringOrElse("app.region"   , "ny"),
      version  = conf.getStringOrElse("app.version"  , "0.9.1"),
      url      = conf.getStringOrElse("app.url"      , "http://sampleapp.slatekit.com"),
      group    = conf.getStringOrElse("app.group"    , "products-dept"),
      contact  = conf.getStringOrElse("app.contact"  , "kishore@abc.co"),
      tags     = conf.getStringOrElse("app.tags"     , "slate,shell,cli"),
      examples = conf.getStringOrElse("app.examples" , "")
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
  def folders(conf:ConfigBase):Folders = {

    val abt = about(conf)
    Folders.userDir (
      root    = Strings.toId(abt.company),
      group   = Strings.toId(abt.group),
      app     = abt.id
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
  def vars(conf:ConfigBase): Subs = {
    val abt = about(conf)
    new Subs(Some(List[(String,(TemplatePart)=> String)](
      ("user.home"    , (s) => System.getProperty("user.home")    ),
      ("company.id"   , (s) => Strings.toId(abt.company)          ),
      ("company.name" , (s) => abt.company                        ),
      ("company.dir"  , (s) => "@{user.home}/@{company.id}"       ),
      ("root.dir"     , (s) => "@{company.dir}"                   ),
      ("group.id"     , (s) => Strings.toId(abt.group)            ),
      ("group.name"   , (s) => abt.group                          ),
      ("group.dir"    , (s) => "@{root.dir}/@{group.id}"          ),
      ("app.id"       , (s) => abt.id                             ),
      ("app.name"     , (s) => abt.name                           ),
      ("app.dir"      , (s) => "@{root.dir}/@{group.id}/@{app.id}")
    )))
  }


  def getConfPath(args:Args, file:String, conf:Option[ConfigBase]):String = {
    val pathFromArgs = Option(args.getStringOrElse("conf.dir", ""))
    val location = pathFromArgs.getOrElse( conf.fold("")( c => c.getStringOrElse("conf.dir", "")))
    val prefix = location match {
      case "jars" => ""
      case "conf" => "file://./conf/"
      case ""     => ""
      case _      => location
    }
    prefix + file
  }


  /**
    * Checks the command for either an instructions about app or for exiting:
    * 1. exit
    * 2. version
    * 3. about
    *
    * @param raw
    * @return
    */
  def isMetaCommand(raw:List[String]):Result[String] = {

    // Case 1: Exit ?
    if (ArgsFuncs.isExit(raw, 0)) {
      exit()
    }
    // Case 2a: version ?
    else if (ArgsFuncs.isVersion(raw, 0)) {
      help()
    }
    // Case 2b: about ?
    // Case 3a: Help ?
    else if (ArgsFuncs.isAbout(raw, 0) || ArgsFuncs.isHelp(raw, 0)) {
      help()
    }
    else {
      failure[String]()
    }
  }


  /**
    * gets the selected environment by key "env" from command line args first or env.conf second
    *
    * @return
    */
  def getEnv(args:Args, conf:ConfigBase): Env = {
    val env = getConfOverride(args, conf, "env", Some("loc"))
    Env.parse(env)
  }



  /**
    * gets log level by key "log.level" from command line args first or environment config 2nd
    *
    * @return
    */
  def getLogLevel(args:Args, conf:ConfigBase): LogLevel = {
    val level = getConfOverride(args, conf, "log.level", Some("info"))
    slate.common.logging.Logger.parseLogLevel(level)
  }


  /**
    * gets log name by key "log.name" from command line args first or environment config 2nd
    *
    * @return
    */
  def getLogName(args:Args, conf:ConfigBase): String = {
    val log = getConfOverride(args, conf, "log.name", Some("@{app}-@{env}-@{date}.log"))
    log
  }


  /**
    * returns a function for getting an overriden setting ( from command line vs config file )
    *
    * @return
    */
  def getOverride(args:Args, conf:ConfigBase): (String, Option[String]) => String =
    getConfOverride(args, conf, _, _)


  def getConfOverride(args:Args, conf:ConfigBase, key:String, defaultValue:Option[String] ):String = {

    val finalDefaultValue = defaultValue.getOrElse("")

    // 1. From cmd line args
    val arg = args.getStringOrElse(key, "")

    // 2. From env.conf ( or respective environment config )
    val cfg = conf.getStringOrElse(key, finalDefaultValue)

    // 3. Cmd line override
    if(!Strings.isNullOrEmpty(arg))
      arg
    else
      Option(cfg).getOrElse(finalDefaultValue)
  }


  def buildAppInputs(args:Args): Result[AppInputs] = {
    // 1. Load the base conf "env.conf" from the directory specified.
    // or specified in the "conf.dirs" config setting in the env.conf file
    // a) -conf="jars"                  = embedded in jar files
    // b) -conf="conf"                  = expect directory ./conf
    // c) -conf="file://./conf-samples  = expect directory ./conf-samples
    // d) not specified = defaults to jars.
    // NOTES:
    // 1. The location of the directory can be over-riden on the command line
    // 2. The conf base is loaded again since if the "-help" arg was supplied
    // if will get the info from the confBase ( env.conf )
    val confBase = new Conf(Some(getConfPath(args, "env.conf", None)))

    // 2. The environment can be selected in the following order:
    // - command line ( via "-env=dev"   )
    // - env.conf ( via env.name = dev )
    // getEnv will first look for selected environment from args, then in config.
    val envSelected = getEnv(args, confBase)

    // 2. Validate the environment
    // Get all
    val allEnvs = envs()
    val envCheck = new Envs(allEnvs, allEnvs.headOption).validate(envSelected)

    envCheck.fold[Result[AppInputs]]( failure[AppInputs](msg = envCheck.msg) ) ( env => {

      // 4. We now have the environment to use ( e.g. "dev" )
      // Now load the final environment specific override
      // for directory reference provide: "file://./conf/"
      val overrideConfPath = Some( getConfPath(args, s"env.${env.name}.conf", Some(confBase)))
      val confEnv = Conf.loadWithFallbackConfig(overrideConfPath, confBase)
      success(new AppInputs(args, envSelected, confBase, confEnv))
    })
  }


  def buildContext(appInputs:AppInputs):AppContext = {

    val conf = appInputs.confEnv
      new AppContext (
        arg  = appInputs.args,
        env  = appInputs.env,
        cfg  = conf,
        log  = new LoggerConsole(),
        ent  = new Entities(Option(dbs(conf))),
        dbs  = Option(dbs(conf)),
        inf  = about(conf),
        dirs = Some(folders(conf))
      )
  }
}
