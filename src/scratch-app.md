---
layout: start_page
title: module Utils
permalink: /scratch-app
---

# Example 

```scala 

  import slate.core.app.AppProcess
  import slate.core.app.AppFuncs._

  class SampleApp extends AppProcess
  {
    /**
     * life-cycle method: called at the beginning
     * when app is starting up, and the context 
     * has been setup.
     */
    override def onInit(): Unit = {
      println("app init")
    }


    /**
     * life-cycle method: called to execute your logic 
     */
    override def onExecute():Result[Any] = {
      info("app executing now")

      // simulate work
      Thread.sleep(1000)

      success()
    }


    /**
     * life-cycle method: called at the end
     * when app is shutting down
     */
    override def onEnd(): Unit = {
      info("app shutting down")
    }


    /**
     * fill extra items into the diagnostics summary shown at end of shutdown
     */
    override def collectSummaryExtra(): Option[List[(String,String)]] = {
      Some(List[(String,String)](
        ("region"    , "london"),
        ("department", "operations")
      ))
    }
  }

  
  object SampleApp {

    /**
      * STEP 1. setup the command line arguments.
      * NOTE:
      * 1. These values can can be setup in the env.conf file
      * 2. If supplied on command line, they override the values in .conf file
      * 3. If any of these are required and not supplied, then an error is display and program exists
      * 4. Help text can be easily built from this schema.
      */
    val schema = new ArgsSchema()
      .text("env", "the environment to run in", false, "", "dev", "dev1|qa1|stg1|pro")
      .text("log", "the log level for logging", false, "info", "info", "debug|info|warn|error")
    
    
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
    def convert(ctx: AppContext): AppContext = {
      ctx.copy(enc = Option(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")))
    }
    
    
    def main(args: Array[String]): Unit = {
    
      // 1. Run calls the template methods ( init, exec, shutdown )
      run (
    
        // 2. Instance of AppProcess
        new SampleApp (
    
          // 3. Build the Application context for the app.
          // NOTE: The app context contains the selected
          // environment, logger, conf, command line args,
          // database, encryptor, and many other components
          build (
            args      = Some(args),
            schema    = Some(schema),
            converter = Some(convert)
          )
        )
      )
    }
  }	
```

# App Setup 

```scala 

  import slate.core.app.AppProcess
  import slate.core.app.AppFuncs._

  class SampleApp extends AppProcess
  {
    /**
     * life-cycle method: called to execute your logic 
     */
    override def onExecute():Result[Any] = {
      info("app executing now")

      // simulate work
      Thread.sleep(1000)

      success()
    }
  }

  
  object SampleApp {

    /**
      * setup the command line arguments.
      */
    val schema = new ArgsSchema()
      .text("env", "the environment to run in", false, "", "dev", "dev1|qa1|stg1|pro")
      .text("log", "the log level for logging", false, "info", "info", "debug|info|warn|error")
    
    
    /**
      * Converts a built AppContext into a final one for use in this app.
      * This is to allow customization of any member of the app context:
      * e.g.
      * - encryptor, logger, database 
      */
    def convert(ctx: AppContext): AppContext = {
      ctx.copy(enc = Option(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")))
    }
    
    
    def main(args: Array[String]): Unit = {
    
      // 1. Run calls the template methods ( init, exec, shutdown )
      run (
    
        // 2. Instance of AppProcess
        new SampleApp (
    
          // 3. Build the Application context for the app.
          // NOTE: The app context contains the selected
          // environment, logger, conf, command line args,
          // database, encryptor, and many other components
          build (
            args      = Some(args),
            schema    = Some(schema),
            converter = Some(convert)
          )
        )
      )
    }
  }	
```



# Env - definition 
```scala 
import slate.common.{Env, EnvItem}

  import slate.core.app.AppProcess

  class AppProcess
  {
  
    // ... code 
  
    /**
    * The list of available environments to choose from.
    * An environment definition is defined by its name and mode
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
    * @return
    */
    def envs(): List[EnvItem] = {
      List[EnvItem](
        EnvItem("loc", Env.DEV , desc = "Dev environment (local)"           ),
        EnvItem("dev", Env.DEV , desc = "Dev environment (shared)"          ),
        EnvItem("qa1", Env.QA  , desc = "QA environment  (current release)" ),
        EnvItem("qa2", Env.QA  , desc = "QA environment  (last release)"    ),
        EnvItem("stg", Env.UAT , desc = "STG environment (demo)"            ),
        EnvItem("pro", Env.PROD, desc = "LIVE environment"                  )
      )
    }
  }
	
```


# Env - config 

```python 
  # environment selection
  # this can be overriden on the commandline via -env=qa
  env = "loc"
 
```


# Env - env info 

```java
  // describe the environment 
  env {
    name : "qa1"
    mode : "qa"
    desc : "shared quality assurance environment 1"
  }
 
```

# Env - command line 

```scala 
 // You can run your app and supply the environment name on the command line
 // environemnt: -env=name 
 // log level  : -log-level=name 
 
 >: sampleapp -env="loc"  -log.level=debug 
 >: sampleapp -env="dev"  -log.level=info 
 >: sampleapp -env="qa1"  -log.level=warn 
 >: sampleapp -env="stg"  -log.level=error 
 >: sampleapp -env="pro"  -log.level=error 
 
```


# Env - access 

```scala 

  override def onExecute():Result =
  {
    // You have access the the selected environment via the "env" member
    info("selected environment is : " + env.name )
  
    success()
  }
 
```



# Settings - access 

```scala 

  // The conf is an inherited config that loads the environment
  // specific config ( env.qa.conf ) plus with the common config ( env.conf )
  // It is a decorated config container that first checks for a setting in
  // the environment specific for an overriden value before checking the common config.
  println ( conf.getString("app.name") )
  println ( conf.getBool("log.enabled") )

  // The dbCon convenience method load up a database connection with
  // driver:String, url:String, user:String, password:String
  // the keys are expected to be:
  // db.driver      = "com.mysql.jdbc.Driver"
  // db.url         = "jdbc:mysql://localhost/db1"
  // db.user        = "root"
  // db.pswd        = "123456789"
  val dbCon = conf.dbCon()
  println( dbCon )

  // NOTE: For more details on how to load configs and db connections from the config
  // refer to the Example_Configs.scala example
 
```


# Env - validation 

```scala 
 import slate.core.app.AppProcess

  class AppProcess
  {
    // ....
	
    /**
    * validates the environment name supplied.
    * @param env
    * @return
    */
    protected def validateEnv(env:EnvItem): Result = {
      var success = false
      var message = ""
      
      env.name match {
        case "loc"   => success = true
        case "dev"   => success = true
        case "qa1"   => success = true
        case "stg"   => success = true
        case "pro"   => success = true
        case _       => message = s"Unknown environment : ${env.name} supplied"
      }
      Result(success)
    }
  }
 
```


# App Full Example 

```scala 

import slate.core.app.AppProcess

class SampleAppBatch extends AppProcess
{
  /**
   * Initialize your app here
   *
   * @return
  */
  override def onInit(): Unit =
  {
    // The base init can take care of some things such as:
    // 1. loading the common config "resources/env.conf" as "confBase"
    // 2. loading the selected environment "env" from either 
    //    the command line first ( -env="dev1:dev" ) or "env.conf" as "conf"
    // 3. setting up "conf" inherited config from "env.dev.conf" + "env.conf"
    
    // Initialize the context with common app info
    ctx = new AppContext (
	  env  = EnvItem( "ny.dev.01", envs.Env.DEV ),
	  cfg  = conf,
	  log  = new LoggerConsole(getLogLevel()),
	  ent  = new Entities(Option(dbs())),
	  inf  = aboutApp(),
	  dbs  = Option(dbs()),
	  enc  = Some(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")),
	  dirs = Some(folders())
	)
    // your code here.
  }


  /**
   * You implement this method to executes the app
   *
   * @return
   */
  override def onExecute():Result =
  {
    // simulate work
    info("simulating work for 2 seconds. please wait...")
    Thread.sleep(2000)

    success()
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
   * @param args
   */
  override def collectSummaryExtra(args:ListBuffer[String]): Unit =
  {
    args += (meta.about.name + ": extra 1  = extra summary data1")
    args += (meta.about.name + ": extra 2  = extra summary data2")
  }


  /**
   * build information about this application for diagnostics
   * @return
   */
  override def buildAbout():About = {
    // Get info about app from base config "env.conf" which is common to all environments.
    new About(
      name     = confBase.getString("app.name"     , "Sample App"),
      desc     = confBase.getString("app.desc"     , "Sample to show the base application"),
      region   = confBase.getString("app.region"   , "ny"),
      version  = confBase.getString("app.version"  , "0.9.1"),
      url      = confBase.getString("app.url"      , "http://sampleapp.slatekit.com"),
      group    = confBase.getString("app.group"    , "codehelix.co"),
      contact  = confBase.getString("app.contact"  , "kishore@codehelix.co"),
      tags     = confBase.getString("app.tags"     , "slate,shell,cli"),
      examples = confBase.getString("app.examples" , "")
    )
  }   
   
  // ...
}

```

# Context 

```scala 

  override def onInit(): Unit =
  {
    // The base class init() can take care of some things such as:
    // 1. loading the "env.conf" config file as "confBase"
    // 2. loading the "env" from either the command line first ( -env="dev1:dev" ) or "env.conf"
    // 3. setting up "conf" inherited confi from "env.dev.conf" + "env.conf"

    // 2. Initialize the context with common app info
    // The database can be set up in the "env.conf" shared inherited config or
    // overridden in the environment specific e.g. "env.qa.conf"
    ctx = new AppContext (
      env  = EnvItem( "ny.dev.01", envs.Env.DEV ),
      cfg  = conf,
      log  = new LoggerConsole(getLogLevel()),
      ent  = new Entities(Option(dbs())),
      inf  = aboutApp(),
      dbs  = Option(dbs()),
      enc  = Some(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")),
      dirs = Some(folders())
    )
  }

```


# Config file 
```python

# environment selection
# this can be overriden on the commandline via -env=qa
env = "dev"

# This config file is :
# 1. common to all other config files
# 2. inherited by all other config files.
# 3. settings can be overriden in the derived files.
# these can be overriden in each of the environment specific config files
# e.g.
# dev = env.dev.conf ( development )
# qa  = env.qa1.conf ( qa testing  )
# loc = env.loc.conf ( local dev   )
# pro = env.pro.conf ( production  )
app.id      = "slate.examples"
app.company = "codehelix"
app.name    = "Sample App"
app.desc    = "Sample console application to show the base application features"
app.region  = "ny"
app.version = "0.9.1"
app.url     = "http://sampleapp.slatekit.com"
app.group   = "codehelix.co"
app.contact = "kishore@codehelix.co"
app.tags    = "slate,shell,cli"
app.examples = "sampleapp -env=dev -log.level=debug -region='ny' -enc=false"

# log and level
log.name    = "@{app.name}-@{env.name}-@{date}.log"
log.enabled = true
log.level   = "info"

# DB Settings - defaulted to dev database
db.enabled     = false
db.source      = "conf"
db.driver      = "com.mysql.jdbc.Driver"
db.url         = "jdbc:mysql://localhost/db1"
db.user        = "root"
db.pswd        = "123456789"

```


## Config 2: Environment specific

```python
# Example: Override the db settings for this environment
# and use encrypted values.
db.enabled     = true
db.source      = "conf"
db.driver      = "com.mysql.jdbc.Driver"
db.url         = "jdbc:mysql://localhost/test1"
db.user        = "7g7bENenJnO2RCZ9iBnBZw"
db.pswd        = "8r4AbhQyvlzSeWnKsamowA"


```


## Config 3: User folder 

```python

# Example: Override the db settings for prod to indicate
# loading the info from a file called "db-pro.txt" from the users folder.
# e.g. /<user>/<company>/<app>/db-pro.txt
#
# This is an added security measure to avoid production settings
# being stored in the config directory/files. User folder is :
#
# 1. more secure
# 2. not stored in source control
# 3. stored in /<user>/<company>/<app>/db.pro.txt
#
# NOTE: This is a pattern recommended by amazon.
db.location: "user://mycompany/app1/conf/db-pro.conf"

```

# User file 

```python

db.driver = "com.mysql.jdbc.Driver"
db.url    = "jdbc:mysql://localhost/test1"
db.user   = "root"
db.pswd   = "123456789"

```


# Args 

```scala 

class SampleAppBatch extends AppProcess 
{

  // setup the command line arguments.
  // NOTE:
  // 1. These values can can be setup in the env.conf file
  // 2. If supplied on command line, they override the values in .conf file
  // 3. If any of these are required and not supplied, then an error is display and program exists
  // 4. Help text can be easily built from this schema.
  argsSchema.addText("env"   , "the environment to run in", true , "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
            .addText("log"   , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")
            .addFlag("enc"   , "whether encryption is on" , false, "false", "false", "true|false")
            .addText("region", "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*")

	// ...
}

```

# About 

```scala 

  /**
   * build information about this application for diagnostics
   * @return
   */
  override def buildAbout():About = {
    // Get info about app from base config "env.conf" which is common to all environments.
    new About(
      id       = confBase.getStringOrElse("app.id"       , "sampleapp.console"),
      name     = confBase.getStringOrElse("app.name"     , "Sample App"),
      desc     = confBase.getStringOrElse("app.desc"     , "Sample to show the base application"),
      region   = confBase.getStringOrElse("app.region"   , "ny"),
      version  = confBase.getStringOrElse("app.version"  , "0.9.1"),
      url      = confBase.getStringOrElse("app.url"      , "http://sampleapp.slatekit.com"),
      group    = confBase.getStringOrElse("app.group"    , "codehelix.co"),
      contact  = confBase.getStringOrElse("app.contact"  , "kishore@codehelix.co"),
      tags     = confBase.getStringOrElse("app.tags"     , "slate,shell,cli"),
      examples = confBase.getStringOrElse("app.examples" , "")
    )
  }   

  
```

# Help 
```bat 

 ==============================================
 ABOUT
 name     :  Sample App
 desc     :  Sample console application to show the base application features
 group    :  codehelix.co
 region   :  ny
 url      :  http://sampleapp.slatekit.com
 contact  :  kishore@codehelix.co
 version  :  0.9.1
 tags     :  slate,shell,cli
 examples :  sampleapp -env=dev -log.level=debug -region='ny' -enc=false
 ==============================================

 ARGS
 -env     :  the environment to run in
             ! required  [String]  e.g. dev
 -log     :  the log level for logging
             ? optional  [String]  e.g. info
 -enc     :  whether encryption is on
             ? optional  [String]  e.g. false
 -region  :  the region linked to app
             ? optional  [String]  e.g. us
			 
```


# Cmd Line
```scala 
	
  sampleapp ?
  sampleapp help
  sampleapp about 
  
```

# Config
```scala 
	
  // env.conf is the base config with settings common to all other configs ( dev, qa, stg, pro )
  val confBase = new Conf().init(file = Some("env.conf"))
  
```



# Running App 

```scala 

package sampleapp.batch

import slate.core.app.AppRunner

object SampleAppRunner
{
  /**
    * STEP 1. setup the command line arguments.
    * NOTE:
    * 1. These values can can be setup in the env.conf file
    * 2. If supplied on command line, they override the values in .conf file
    * 3. If any of these are required and not supplied, then an error is display and program exists
    * 4. Help text can be easily built from this schema.
    */
  val schema = new ArgsSchema()
    .text("env", "the environment to run in", false, "", "dev", "dev1|qa1|stg1|pro")
    .text("log", "the log level for logging", false, "info", "info", "debug|info|warn|error")


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
  def convert(ctx: AppContext): AppContext = {
    ctx.copy(enc = Option(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")))
  }


  def main(args: Array[String]): Unit = {

    // 1. Run calls the template methods ( init, exec, shutdown )
    run (

      // 2. Instance of AppProcess
      new SlateShell (

        // 3. Build the Application context for the app.
        // NOTE: The app context contains the selected
        // environment, logger, conf, command line args,
        // database, encryptor, and many other components
        build (
          args      = Some(args),
          schema    = Some(schema),
          converter = Some(convert)
        )
      )
    )
  }
}

```


# AppRunner 
```scala 

package slate.core.app

import slate.common.{Result}

object AppRunner
{
  /**
   * runs the app with the arguments supplied.
   * @param app
   * @param args
   * @return
   */
  def run(app: AppProcess, args:Option[Array[String]]): Result[Any] =
  {
    var res:Result[Any] = NoResult

    try {
      // 1. Check the command line args
      val result = check(args, app.argsSchema)
      if (!result.success) {
        handleHelp(app, result)
        return result
      }

      // 2. Configure args
      app.args(args, result.get)

      // 3. Begin app workflow
      app.init()

      // 4. Accept the initialize
      // NOTE: This serves as a hook for post initialization
      app.accept()

      // 5. Execute the app
      res = app.exec()

      // 6 Shutdown the app
      app.shutdown()
    }
    catch {
      case ex:Exception => {
        println("Unexpected error : " + ex.getMessage)
        res = failure( msg = Some("Unexpected error running application: " + ex.getMessage ),
                       err = Some(ex)
        )
      }
    }
    finally {
      // Reset any color changes
      println(Console.RESET)
    }

    // 7. Return the result from execution
    res
  }
  
  // more...
}

```


# Output 

```scala 


  override def onInit(): Unit =
  {
    options.printSummaryBeforeExec = false
    options.printSummaryOnShutdown = true
	
    // ...  
  }

```



# Output - Custom

```scala 


  override def collectSummaryExtra(): Option[List[String]] =
  {
    Some(List[(String,String)](
      ("region"    , "london"    ),
      ("department", "operations")
    ))
  }

```