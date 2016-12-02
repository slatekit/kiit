/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */
package slate.core.app

import slate.common.app.AppRunConst
import slate.common.args.{Args, ArgsHelper}
import slate.common.databases.{DbConString, DbLookup}
import slate.common.databases.DbLookup._
import slate.common.envs.{Env, Envs, EnvItem}
import slate.common.results.ResultSupportIn
import slate.common.templates.{TemplatePart, Subs}
import slate.common.{Result, Strings}
import slate.common.conf.ConfigBase
import slate.common.info.{Folders, About}

object AppFuncs extends ResultSupportIn {

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
  def envs(): List[EnvItem] = {
    List[EnvItem](
      EnvItem("loc", Env.DEV , desc = "Dev environment (local)" ),
      EnvItem("dev", Env.DEV , desc = "Dev environment (shared)" ),
      EnvItem("qa1", Env.QA  , desc = "QA environment  (current release)" ),
      EnvItem("qa2", Env.QA  , desc = "QA environment  (last release)" ),
      EnvItem("stg", Env.UAT , desc = "STG environment (demo)" ),
      EnvItem("pro", Env.PROD, desc = "LIVE environment" )
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
    defaultDb(conf.dbCon("db").getOrElse(DbConString.empty))
  }



  /**
    * builds all the info for this application including its id, name, company, contact info, etc.
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
    new Folders(
      location = AppRunConst.LOCATION_USERDIR,
      root    = Some(Strings.toId(abt.company)),
      group   = Some(Strings.toId(abt.group)),
      app     = abt.id,
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


  def checkCmd(raw:List[String]):Result[String] = {

    // Case 1: Exit ?
    if (ArgsHelper.isExit(raw, 0))
    {
      exit()
    }
    // Case 2a: version ?
    else if (ArgsHelper.isVersion(raw, 0))
    {
      help()
    }
    // Case 2b: about ?
    // Case 3a: Help ?
    else if (ArgsHelper.isAbout(raw, 0) || ArgsHelper.isHelp(raw, 0))
    {
      help()
    }
    else {
      failure[String]()
    }
  }
}
