---
layout: start_page
title: module Utils
permalink: /scratch-apis2
---

# Apis
The apis in Slate Kit are built to be **protocol independent**. 
This means that you can build your APIs 1 time and they can be made to be available on the command line shell or as a Web API, or both. This is done using various techniques outlined below. Before going into the details, first review the terminology.

# Setup 

```scala 
  // REGISTRATION: Create instance of the API container to "host" all APIs
  // NOTES:
  // 1. The ApiContainerCLI extends from ApiContainer
  // 2. The ApiContainerCLI only overrides some methods to display help/docs.
  // 3. If you want to run the APIs via Web/Http, refer to the Server documentation.
  // 4. The Web Server Api container simply uses the ApiContainerWeb which extends from
  //    ApiContainer and also only overrides some methods for help/docs
  // 5. Ultimately, to run the container inside of a CLI or Web, the Slate Shell component is used
  //    and the Slate Server is used. They provide a wrapper to marshall the data from CLI/Web to
  //    the container. ( Refer to CLI and Server for more info )
  val apis = new ApiContainerCLI()

```


```scala 
  // Provide the container with a context which contains some dependent services and info about the app 
  // NOTE: This context is made available to all the APIs your register with the container.
  // So every API can perform logging, get config settings, encrypt/decrypt etc.
  apis.ctx = new AppContext (
    env  = new EnvItem("local", "dev", "local:dev"),
    cfg  = new Conf(),
    log  = new LoggerConsole(),
    con  = new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi"),
    enc  = new Encryptor("wejklhviuxywehjk", "3214maslkdf03292"),
    inf  = new About("myapp", "sample app", "product group 1", "ny", version = "1.1.0")
  )

```

```scala 
  // REG 1. Register the api ( uses roles/auth values from annotations on class )
  apis.register[AppUsersApi](new AppUsersApi())

  // REG 2. Register the api ( allow API actions from subclass also )
  apis.register[AppUsersApi](new AppUsersApi(), declaredOnly = false )

  // REG 3. Register the api and explicitly set the roles allowed to access the api
  //        This overrides the "roles" value in the @Api annotation
  apis.register[AppUsersApi](new AppUsersApi(), declaredOnly = false, roles = Some("admin"))

  // REG 4a. Register the api and set access to api only from CLI/command line container
  //        This overrides the "access" value in the @Api annotation
  apis.register[AppUsersApi](new AppUsersApi(), declaredOnly = false, roles = Some("admin"), access = Some("cli"))

  // REG 4b. Register the api and set access to api only from web/http container
  //        This overrides the "access" value in the @Api annotation
  apis.register[AppUsersApi](new AppUsersApi(), declaredOnly = false, roles = Some("admin"), access = Some("web"))

  // REG 4c. Register the api and set access to api from both CLI/Web
  //        This overrides the "access" value in the @Api annotation
  apis.register[AppUsersApi](new AppUsersApi(), declaredOnly = false, roles = Some("admin"), access = Some("*"))

  // REG 5. Register the api and supply your auth mode that will check for roles/permissions.
  //        This overrides the "auth" value in the @Api annotation
  apis.register[AppVersionApi](new AppVersionApi(), declaredOnly = false, roles = Some("admin"), access = Some("*"), auth = Some("key-roles"))

```

```scala
  // REG 6: Setup your authorization provider
  // NOTE:
  // 1. For the AppUsersApi, the application roles are checked.
  // 2. For the AppAboutApi, the api keys are checked.
  apis.auth = Some(new MyAuthProvier(
    List[ApiKey](
        new ApiKey("devs" , "94744D43418C421586E544DE0BB4CC37", "dev"          ),
        new ApiKey("ops"  , "677961A57F82440AB6F4E1F1EAC721ED", "dev,ops"      ),
        new ApiKey("admin", "A100E22B5544423693FABD81791FAC80", "dev,ops,admin")
      )) )
	  
```


```scala 
  class MyAuthProvier(keys:List[ApiKey]) extends ApiAuth(keys, None)
    with ResultSupportIn
  {

    /**
     * Handle authorization via api-keys for non-application level features,
     * such getting basic metadata, or diagnostics for your deployed application.
     * e.g. get the version number of your application.
     * @param cmd
     * @param roles
     * @param rolesParent
     * @return
     */
    override def isApiKeyValid(cmd:ApiCmd, roles:String, rolesParent:String):Result = {
      super.isApiKeyValid(cmd, roles, rolesParent)
    }

      /**
     * Handle authorization via app roles.
     * @param cmd        : The abstract ApiRequest ( could be a CLI or Web Request )
     * @param roles      : The roles value from the ApiAction annotation
     * @param roleParent : The roles value from the Api annotation
     * @return
     */
    override def isAppRoleValid(cmd:ApiCmd, roles:String, roleParent:String):Result = {

      // NOTES: The ApiCmd will either represent a Web Request or a CLI request.
      // Slate abstracts the protocol requests using a "Unified Request data structure"

      // get the header named "Authorization"
      // EXAMPLE: Implement your code here to get the correct header
      val header = cmd.opts.get.getString("Authorization")

      // Decrypt the token
      // EXAMPLE: Implement your code here to get/decrypt the token
      val token = "user_01_sample_token"

      // Get the roles for the user with the token.
      // EXAMPLE: Implement your code here to get the roles for the user of the token
      val roles = "user,dev,qa,ops,admin"

      // EXAMPLE: Return sample success for now
      success("sample authorization provider")
    }
  }
  
```

# Api Containers
All your APIs run inside of what we call an Api Container. There are 3 types of api containers:

1. Api Web Container   : A container to host your APIs as Web APIs via Http protocol using Akka-Http 
2. Api CLI Container   : A container to host your APIs on the command line and supports some level of automation
3. Api Queue Container : A container to host your APIs where all calls are made via a message queue ( not yet available )

# Api Result

This is a simple case class that wraps a return value with a success/fail flag, a message, status code, error and data.

1. The status codes match Http Status Codes.
2. The result can be used to easily convert to an Http response. 
3. There is also a builder trait that can build results mimicking common http responses.
4. Provides an effective way to keep complex logic in a service layer 

```scala

  import slate.common.Result
  import slate.common.results.{ResultCode, ResultSupport}

    // CASE 1: The Result class is a glorified wrapper around a status code
    // and contains the following.
    // 1. success / failure  - flag
    // 2. message / error    - informative message
    // 3. data               - data being returned
    // 4. code               - matches http status codes
    // 5. tag                - tag for external id references
    // 
    // In functional programming terms, it is implemented as a Monad and 
    // works as a replacement for the Try/Success/Error Monad.
    val result1 = SuccessResult[String](
      value = "userId:1234567890",
      code  = ResultCode.SUCCESS,
      msg   = Some("user created"),
      ext   = Some("user created"),
      tag   = Some("tag001" )
    )
	
    val result2 = FailureResult[String](
      code  = ResultCode.BADREQUEST,
      msg   = Some("invalid login"),
      tag   = Some("tag001" )
    )

    // CASE 2: Success ( 200 )
    // You can easily build up the results using the helper methods in the ResultSupportIn trait
    // There are methods that exist that match the most common http statuses
    val res1 = success(data = 123456, msg = Some("user created"), tag = Some("promoCode:ny001"))
    val res2 = badRequest[String](msg = Some("invalid login"), tag = Some("promoCode:ny001"))
	
    printResult(res1)
	
```


# Api Routing 

In Slate Kit, you can easily access your apis via Web(http) or command line (CLI ) using a simple routing format. We call this the **unified routing format**.  

1. It is **NOT** Rest based by design!
2. It is designed for simplicity
3. Favours convention over configuration
4. Provides consistency between web and command line

### format = {area}.{api}.{action} key=value*

### {area}
This is a root level category containing a collection of apis.
e.g. app, admin, sys 


### {api}
This is a single api which may support 1 or more actions 
e.g. users, invites, jobs

### {action}
A action is the lowest level in the route and typically maps to a method/function.
e.g. create, update, activate

```scala
 // 1. WEB: with inputs in query string
 // unified routing format: area/apiname/action?&phone=123456789&active=true&email=kishore@abc.com
 http://mycompany.com/api/app/users/activate?code=1&phone=123456789&active=true&email=kishore@abc.com
 
 
 // 2. WEB: with inputs as json post data 
 // unified routing format: area/apiname/action { id=2, phone=123456789, active=true, email="kishore@abc.com" } 
 http://mycompany.com/api/app/users/activate 
 { 
   code=2, phone=123456789, active=true, email="kishore@abc.com" 
 } 
 
 
 // 3. CLI: command line syntax
 // unified routing format: area.api.action -key=value*
 app.users.activate -code=2 -phone=123456789 -active=true -email="kishore@abc.com"
 
```


# Api Discovery

With the unified routing format in place, we can easily support a simple and fast way to discover all the areas, apis, and actions in the system. The unified discovery approach

### web: {area}/{api}/{action}-help

### cli: {area}.{api}.{action}?

```scala
 // Web 1. WEB: to show all the areas in the system 
 http://mycompany.com/api-help
 
 // Web 2. WEB: to show all the apis in an area 
 http://mycompany.com/api/app-help
 
 // Web 3. WEB: to show all the actions in an api 
 http://mycompany.com/api/app/users-help
  
 // Web 4. WEB: to show all inputs to an api 
 http://mycompany.com/api/app/users/activate-help
 
 
 // CLI 1. WEB: to show all the areas in the system 
 ?
 
 // CLI 2. WEB: to show all the apis in an area 
 app ?
 
 // CLI 3. WEB: to show all the actions in an api 
 app.users ?
  
 // CLI 4. WEB: to show all inputs to an api 
 app.users.activate ?
 
 
```

**NOTES**: 

1. You can authorize access to the docs
2. The ? is not used in the web as it would conflict with query string


# Api Setup

The apis are set up to be simple Scala classes with annotations.

1. Put an @Api annotation to indicate its a unified api 
2. You specify the **area** and **api** in the @Api annotation
3. Put an @ApiAction annotation to indicate that a specific method is publicly available as an action 
4. You can put a role permission at the api itself 
5. You can put a role permission at the api action / method annotation 
6. An action annotation can reference its parent role permission via "@parent" 
7. You can hook in any authentication system you prefer. see details below
8. The access role field can indicate whether to make the api available 'cli', 'web' or '*' for all


```scala 

import slate.core.apis._

@Api(area = "app", name = "users", desc = "sample api", roles= "admin", auth = "app-roles", verb = "post", protocol = "*")
class UserApi extends ApiBaseEntity[User]
{
   @ApiAction(name = "", desc = "activates a users account 3", roles= "@parent")
  def activate(phone:String, code:Int, isPremiumUser:Boolean, date:DateTime): Result =
  {
    success( s"activated $phone, $code, $isPremiumUser, $date" )
  }
  
  // .. more
}

```


# API Marshalling

```scala 
/**
 * Represents a web endpoint / http request translated to small command
 * @param path      : route(endpoint) e.g. /{area}/{name}/{action} e.g. /app/reg/activateUser
 * @param parts     : list of the parts of the action e.g. [ "app", "reg", "activateUser" ]
 * @param area      : action represented by route e.g. app in "app.reg.activateUser"
 * @param name      : name represented by route   e.g. reg in "app.reg.activateUser"
 * @param action    : action represented by route e.g. activateUser in "app.reg.activateUser"
 * @param verb      : get / post ( similar to http verb )
 * @param opts      : options representing settings/configurations ( similar to http-headers )
 * @param args      : arguments to the command
 */
case class ApiCmd (
                     path       :String            ,
                     parts      :List[String]      ,
                     area       :String            = "",
                     name       :String            = "",
                     action     :String            = "",
                     verb       :String            = "",
                     args       :Option[Inputs]    = None,
                     opts       :Option[Inputs]    = None
                   )
{
}

```