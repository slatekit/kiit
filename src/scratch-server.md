---
layout: start_page
title: module Utils
permalink: /scratch-server
---

# Apis
The apis in Slate Kit are built to be **protocol independent**. 
This means that you can build your APIs 1 time and they can be made to be available on the command line shell or as a Web API, or both. This is done using various techniques outlined below. Before going into the details, first review the terminology.


# Build your project
```scala 

cd %APP_DIR%
sbt package

```

# Copy to temp dir
```scala 

REM Copy classes over from target to APP_HOME\dist\temp
xcopy /e /v /y %APP_TARG% %APP_TEMP%\

```


# Create a jar 
```scala 

REM move to temp and jar all the files using custom manifest
cd %APP_TEMP%
jar -cvfm %APP_JAR% %APP_BUILD%\manifest.txt *

```


# Proc file 
```scala 

web: java -jar blend.jar

```



# Proc file 
```scala 

web: java -jar blend.jar

```



# Manifest 

```bat 
Manifest-Version: 1.0
Main-Class: myapp.web.WebApp
Class-Path: akka-actor_2.11-2.3.11.jar
  akka-http-core-experimental_2.11-1.0-RC4.jar
  akka-http-experimental_2.11-1.0-RC4.jar
  akka-http-spray-json-experimental_2.11-1.0-RC4.jar
  akka-parsing-experimental_2.11-1.0-RC4.jar
  akka-stream-experimental_2.11-1.0-RC4.jar
  aws-java-sdk-1.10.55.jar
  myapp_2.11-1.0.jar
  commons-codec-1.9.jar
  commons-logging-1.2.jar
  config-1.2.1.jar
  httpclient-4.5.1.jar
  httpcore-4.4.3.jar
  jackson-annotations-2.5.0.jar
  jackson-core-2.5.3.jar
  jackson-databind-2.5.3.jar
  jline-2.10.4.jar
  joda-time-2.8.1.jar
  json_simple-1.1.jar
  mysql-connector-java-5.1.38-bin.jar
  reactive-streams-1.0.0.jar
  scala-library.jar
  scala-reflect.jar
  slate-cloud_2.11-1.0.jar
  slate-common_2.11-1.0.jar
  slate-core_2.11-1.0.jar
  slate-ext_2.11-1.0.jar
  spray-json_2.11-1.3.1.jar

```

# App 

```scala 

class SampleAppBatch extends AppProcess with ResultSupportIn
{
	// ...
}
```

# Import 
```scala
// Support components
// 1. Current environment ( dev, qa )
// 2. Logger
// 3. Result[T] success/failure
import slate.common.envs.{Env, EnvItem}
import slate.common.SuccessResult
import slate.common.info.About
import slate.common.logging.LoggerConsole

// Core API components
import slate.core.apis.{ApiReg, ApiAuth}
import slate.core.common.{Conf, AppContext}

// Entities for database access
import slate.entities.core.Entities

// Server components
import slate.server.core.Server

// Sample APIs ( get version, app info )
import slate.integration.{VersionApi, AppApi}
import slate.examples.common.UserApi

```


# Args 

```scala 

class SampleAppBatch extends AppProcess with ResultSupportIn
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

# Load common config 
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
  def main(args: Array[String]): Unit = {
    AppRunner.run(new SampleAppBatch(), Some(args))
  }
}

```


# Server Setup 
```scala 

package sampleapp.server

// Sample services
import sampleapp.core.common.{AppApiKeys, AppAuth, AppEncryptor}
import sampleapp.core.models.{Movie, User}
import sampleapp.core.services.{MovieApi, MovieService, UserService, UserApi}

// Slate Result Monad + database/logger/application metadata
import slate.common.{Result}
import slate.common.app.AppMeta
import slate.common.databases.DbLookup
import slate.common.info.{Lang, Host}
import slate.common.logging.LoggerConsole

// Slate Base Application ( to support command line args, environments, config etc )
import slate.core.app.{AppRunner, AppProcess}
import slate.core.common.AppContext

// Slate entities ( mini-ORM )
import slate.entities.core.Entities
import slate.entities.repos.{EntityRepoMySql}

import slate.integration.{VersionApi, AppApi}
import slate.server.core.Server
import scala.reflect.runtime.universe.{typeOf}

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
class SampleAppServer extends AppProcess
{
  // setup the command line arguments.
  // NOTE(s):
  // 1. These values can can be setup in the env.conf file
  // 2. If supplied on command line, they override the values in .conf file
  // 3. If any of these are required and not supplied, then an error is display and program exists
  // 4. Help text can be easily built from this schema.
  argsSchema.addText("env"        , "the environment to run in", false, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
            .addText("region"     , "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*" )
            .addText("port"       , "the port to run on"       , false, "5000" , "5000" , "5000|80")
            .addText("domain"     , "domain association"       , false, "::0"  , "::0"  , "::0|mycompany.com")
            .addText("log.level"  , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")


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
    // Shows startup summary before execution.
    options.printSummaryBeforeExec = true

    // Initialize the context with common app info
    // The database can be set up in the "env.conf" shared inherited config or
    // overridden in the environment specific e.g. "env.qa.conf"
    ctx = new AppContext (
      app  = new AppMeta(),
      env  = env,
      cfg  = conf,
      log  = new LoggerConsole(getLogLevel()),
      ent  = new Entities(),
      inf  = aboutApp(),
      host = Host.local(),
      lang = Lang.asScala(),
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
    ctx.ent.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= typeOf[UserService], repository= new EntityRepoMySql[User](typeOf[User]))
    ctx.ent.register[Movie](isSqlRepo= true, entityType = typeOf[Movie],
      serviceType= typeOf[MovieService], repository= new EntityRepoMySql[Movie](typeOf[Movie]))
  }


  /**
   * You implement this method to executes the app
   *
   * @return
   */
  override def onExecute():Result[Any] =
  {
    info("server starting")

    // 1. Build the auth provider
    val sampleKeys = AppApiKeys.fetch()
    val selectedKey = sampleKeys(5)
    val auth = new AppAuth("test-mode", "slatekit", "johndoe", selectedKey, sampleKeys)

    // 2. Initialize server with port, domain, context (see above) and auth provider
    val server = new Server( args.getIntOrElse("port", 5000),
                             args.getStringOrElse("domain", "::0"),
                             ctx, auth
                            )
    // 3. Register the APIs within the api container
    server.apis.register[AppApi]    (new AppApi()    , true  )
    server.apis.register[VersionApi](new VersionApi(), true  )
    server.apis.register[UserApi]    (new UserApi()   , false )
    server.apis.register[MovieApi]   (new MovieApi()   , false )
    server.apis.init()

    // 4. Run the server ( this starts the life-cycle init, execute, shutdown )
    server.run()

    info("server stopped")

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
   * HOOK for adding items to the summary of data shown at the end of app execution
   */
  override def collectSummaryExtra(): Option[List[String]] =
  {
    Some(List[String](
      "region" + " = " + args.getStringOrElse("region", "n/a")
    ))
  }
}

```


# Server Auth

```scala 

package sampleapp.core.common

import slate.common.{Strings, Result, Files, ApiKey}
import slate.common.results.ResultSupportIn
import slate.core.apis.{ApiCmd, ApiAuth}
import slate.core.common.Conf

/**
  * Sample custom auth provider for your application.
  *
  * @param mode        : "test-mode"     - for demo purposes to return hard-coded roles
  * @param appDir      : "mycompany"     - app directory in user folder to store configs e.g. c:/users/jdoe/mycompany
  * @param user        : "john.doe"      - hard-coded user name for demo purposes
  * @param selectedKey : "dev,ops,admin" - hard-coded user roles for demo purposes
  * @param keys        : the list of api keys when using api key based authorization.
  */
class AppAuth (val mode:String, val appDir:String,
               val user:String, val selectedKey:ApiKey, keys:List[ApiKey])
  extends ApiAuth(keys, None)
with ResultSupportIn
{

  /**
    * Override and implement this method if you want to completely handle authorization your self.
    * @param cmd         : The protocol independent API request. e.g. http://abc.com/api/myapp/users/activate
    * @param mode        : The auth-mode of the api ( refer to auth-mode for protocolo independent APIs )
    * @param actionRoles : The role setup on the API action
    * @param parentRoles : The role setup on the API itself
    *
    * IMPORTANT:
    * 1. all you need to do to implement your authorization is to implement the getUserRoles below
    *
    * NOTES:
    * 1. see base class implementation for details.
    * 2. the auth modes on the apis can be "app-roles" or "key-roles" ( api-keys )
    * 3. the base class properly delegates handling the auth modes.
    * @return
    */
  override def isAuthorized(cmd:ApiCmd, mode:String, actionRoles:String, parentRoles:String)
  :Result[Boolean] = {
      super.isAuthorized(cmd, mode, actionRoles, parentRoles)
  }


  /**
    * Called by system to handle authorization for an API action with auth mode = "key-roles"
    * @param cmd         : The protocol independent API request. e.g. http://abc.com/api/myapp/users/activate
    * @param actionRoles : The role setup on the API action
    * @param parentRoles : The role setup on the API itself
    * @return
    */
  override def isKeyRoleValid(cmd:ApiCmd, actionRoles:String, parentRoles:String):Result[Boolean] = {

    super.isKeyRoleValid(cmd, actionRoles, parentRoles)
  }


  /**
    * Called by system to handle authorization for an API action with auth mode = "app-roles"
    * @param cmd         : The protocol independent API request. e.g. http://abc.com/api/myapp/users/activate
    * @param actionRoles : The role setup on the API action
    * @param parentRoles : The role setup on the API itself
    * @return
    */
  override def isAppRoleValid(cmd:ApiCmd, actionRoles:String, parentRoles:String): Result[Boolean] = {

    super.isAppRoleValid(cmd, actionRoles, parentRoles)
  }

  /**
    * Called by system to get the users roles from API request (ApiCmd)
    * @param cmd   : The protocol independent API request. e.g. http://abc.com/api/myapp/users/activate
    *
    * IMPORTANT
    * 1. for sample / demo purposes this returns the hard-coded user/roles if mode = 'test-mode'
    *
    * NOTES:
    * 1. you need to implement this method to provide the system with the roles from request.
    * 2. the api cmd could be a http request, in which case you can access the headers via cmd.opts.
    * 3. you have freedom to implement any authorization scheme you want.
    *
    * @return
    */
  override protected def getUserRoles(cmd:ApiCmd):String = {

    // CASE 1: sample demo
    if(mode == "test-mode"){
      return selectedKey.roles
    }

    // CASE 2: When running a console/cli app
    // You get get the user/roles from some local encrypted file saved on the user directory
    if(mode == "user-dir"){
      val path = Files.loadUserAppFile(appDir, "login.txt").getAbsolutePath
      val conf = new Conf(Some(path))
      val roles = conf.getString("roles")
      return roles
    }

    // CASE 3: Running a web server
    // Get the roles from the opts abstraction member representing http headers
    if(mode == "header"){
      val headers = cmd.opts
      val roles = headers.map[String]( ops => ops.getString("auth.roles")).getOrElse("")

      // NOTE: Handle encryption etc. for production
      // This is just here for sample purpose to show getting auth/user info from
      // protocol independent APIs ( see slatekit.com for more info ).
      return roles
    }
    ""
  }
}

```

# Server simple

```scala
    
  def runServer(): Unit = {
  
    // 1. Build the auth provider 
    // ( Sample auth below always return true authenticating requests)
    // mode        = "key-roles" | "app-roles"
    // request     = the request ( see Unified Requests in APIs section )
    // actionRoles = the roles on the api action
    // parentRoles = the roles on the parent api
    val auth = new ApiAuth(None,
      callback = Some((mode, request, actionRoles, parentRoles) => {
    	new SuccessResult(true, 200)
      })
    )
	
    // 2. Initialize server with port, domain, context (see below) and auth provider
    val server = new Server(  port       = 5000 ,
                              interface  = "::0",
                              ctx        = context() ,
                              auth       = auth,
                              apiItems   = Some(
                                List[ApiReg](
                                  new ApiReg(new AppApi()    , true  ),
                                  new ApiReg(new VersionApi(), true  ),
                                  new ApiReg(new UserApi()   , false )
                                )
                              )
                            )
    
    // 3. Run the server ( this starts the life-cycle init, execute, shutdown )
    server.run()
  }

  // Slate Kit app, apis, server use an AppContext which 
  // contains basic startup info and services like 
  // current environment ( dev, qa, etc ), logger, config, encryptor
  // and info about the app.
  def context():AppContext = {
    new AppContext (
      env  = EnvItem( "ny.dev.01", Env.DEV ),
      cfg  = new Conf(),
      log  = new LoggerConsole(),
      inf  = new About("slatekit.sample-server", "SlateKit Server", "example", "slatekit"),
      ent  = new Entities(None)
    )
  }

```

# Server Extendable

```scala
   
  def runServer(): Unit = {
  
    // 1. Build the auth provider 
    // ( Sample auth below always return true authenticating requests)
    // mode        = "key-roles" | "app-roles"
    // request     = the request ( see Unified Requests in APIs section )
    // actionRoles = the roles on the api action
    // parentRoles = the roles on the parent api
    val auth = new ApiAuth(None,
      callback = Some((mode, request, actionRoles, parentRoles) => {
    	new SuccessResult(true, 200)
      })
    )
	
    // 2. Initialize server with port, domain, context (see below) and auth provider
    val server = new Server(  port       = 5001 ,
                               interface  = "::0",
                               ctx        = context   ,
                               auth       = sampleAuth
                             )

    // 3. Register your APIs.
    server.apis.register[AppApi]    (new AppApi()    , true  )
    server.apis.register[VersionApi](new VersionApi(), true  )
    server.apis.register[UserApi]   (new UserApi()   , false )
	
    // 4. Run the server ( this starts the life-cycle init, execute, shutdown )
    server.run()
  }

  // Slate Kit app, apis, server use an AppContext which 
  // contains basic startup info and services like 
  // current environment ( dev, qa, etc ), logger, config, encryptor
  // and info about the app.
  def context():AppContext = {
    new AppContext (
      env  = EnvItem( "ny.dev.01", Env.DEV ),
      cfg  = new Conf(),
      log  = new LoggerConsole(),
      inf  = new About("slatekit.sample-server", "SlateKit Server", "example", "slatekit"),
      ent  = new Entities(None)
    )
  }

```