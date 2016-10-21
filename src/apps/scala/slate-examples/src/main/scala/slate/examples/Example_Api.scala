/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.examples


//<doc:import_required>

import slate.common.app.AppMeta
import slate.common.databases.DbConString
import slate.common.encrypt.Encryptor
import slate.common.envs.EnvItem
import slate.common.info.{About, Lang, Host}
import slate.common.logging.LoggerConsole
import slate.common.results.ResultSupportIn
import slate.core.apis._
import slate.core.common.{Conf, AppContext}
import slate.entities.core.Entities

//</doc:import_required>

//<doc:import_examples>
import slate.common.{ApiKey, DateTime, Result}
import slate.core.cmds.Cmd
import slate.examples.common.{UserApi, User}
//</doc:import_examples>


class Example_Api extends Cmd("types")  with ResultSupportIn {

  //<doc:setup>
  // =======================================================================================
  // SETUP 1: Set up a class as an API
  // API classes in Slate Kit are designed to be "Protocol Independent".
  // You write them once and they can be run as Web API or on the CLI ( command line )
  //
  // NOTES:
  // 1. Add the @Api annotation on the class
  // 2. Add the @ApiAction on any methods you want to expose as Api actions.
  // 3. The auth mode is set ot "app-roles"
  // 4. This example API will be used to show application level authorization using a custom
  //    authorization provider that you can easily build.
  // =======================================================================================
  @Api(area = "app", name = "users", desc = "registers users", roles= "admin", auth = "app-roles", protocol = "*")
  class AppUsersApi extends ApiBase {
    var user = new User

    @ApiAction(name = "", desc = "activates a users account", roles = "@parent")
    def activate(phone: String, code: Int, isPremiumUser: Boolean, date: DateTime): Result[Boolean] = {
      ok(Some(s"activated $phone, $code, $isPremiumUser, $date"))
    }


    @ApiAction(name = "", desc = "get total number of users", roles = "@parent")
    def total() : Long = {
      2
    }


    @ApiAction(name = "", desc = "get total number of users", roles = "@parent")
    def rawCommand(cmd:ApiCmd) : Boolean = {

      println("handling the raw command that abstracts both a CLI command and a Web API request")
      // NOTE: Use this if you do NOT want container to auto map arguments to your method parameters

      // Full name of route
      println(cmd.fullName)

      // The parts of the route area.name.action
      // e.g.
      // 1. CLI : app.users.activate
      // 2. Web : http://company.com/api/api/users/activate
      println(s"${cmd.area} ${cmd.name} ${cmd.action}")
      println(cmd.parts)

      // Represent the arguments
      println(cmd.args)

      // Represents additional "headers" for both command line and web API.
      println(cmd.opts)
      true
    }
  }


  // =======================================================================================
  // SETUP 2: Set up a class as an API that is only used by Developers/Admins to get
  // the version of your deployed application.
  //
  // NOTE: This example will show how to use Api keys for authorization.
  // The "auth" mode is set to "key-roles"
  // =======================================================================================
  @Api(area = "app", name = "about", desc = "registers users", roles= "admin", auth = "key-roles", protocol = "*")
  class AppVersionApi extends ApiBase {
    var user = new User

    @ApiAction(name = "", desc = "gets the version", roles = "@parent")
    def version() : String = {
      "1.1.0"
    }
  }


  // =======================================================================================
  // AUTH PROVIDER: Implement your own custom authentication/permission provider.
  // NOTE: You can also use own pre-built authorization provider which has
  // support for users, roles, permissions
  // =======================================================================================
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
    override def isKeyRoleValid(cmd:ApiCmd, roles:String, rolesParent:String):Result[Boolean] = {
      super.isKeyRoleValid(cmd, roles, rolesParent)
    }

      /**
     * Handle authorization via app roles.
     * @param cmd        : The abstract ApiRequest ( could be a CLI or Web Request )
     * @param roles      : The roles value from the ApiAction annotation
     * @param roleParent : The roles value from the Api annotation
     * @return
     */
    override def isAppRoleValid(cmd:ApiCmd, roles:String, roleParent:String):Result[Boolean] = {

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
      ok(Some("sample authorization provider"))
    }
  }


  // =======================================================================================
  // Provide the container with a context about application and some dependent services.
  // NOTE: This context is made available to all the APIs your register with the container.
  // So every API can perform logging, get config settings, encrypt/decrypt etc.
  // The value here are somewhat hard-coded for demonstration purposes.
  // You should setup the context that is more production quality ( see the Slate.Shell setup )
  // The context contains:
  //
  // - The current environment ( e.g. local, dev, qa, stg, pro )
  // - Config ( config wrapper to access data in application.conf in this case )
  // - Logger
  // - Db connection
  // - Encryption service
  // - App information
  val ctx = new AppContext (
    env  = EnvItem("local", "dev"),
    cfg  = new Conf(),
    log  = new LoggerConsole(),
    inf  = new About("slatekit.examples", "myapp", "sample app", "slatekit", "product group 1", region= "ny", version = "1.1.0"),
    lang = Lang.asScala(),
    host = Host.local(),
    ent  = new Entities(),
    con  = Some(new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi")),
    enc  = Some(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292"))
  )
  // REG 6: Setup your authorization provider
  // NOTE:
  // 1. For the AppUsersApi, the application roles are checked.
  // 2. For the AppAboutApi, the api keys are checked.
  val auth = Some(new MyAuthProvier(
    List[ApiKey](
        new ApiKey("devs" , "94744D43418C421586E544DE0BB4CC37", "dev"          ),
        new ApiKey("ops"  , "677961A57F82440AB6F4E1F1EAC721ED", "dev,ops"      ),
        new ApiKey("admin", "A100E22B5544423693FABD81791FAC80", "dev,ops,admin")
      )) )

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
  val apis = new ApiContainerCLI(ctx, auth)

  // REG 1. Register the api ( uses roles/auth values from annotations on class )
  apis.register[AppUsersApi](new AppUsersApi())

  // REG 2. Register the api ( allow API actions from subclass also )
  apis.register[AppUsersApi](new AppUsersApi(), declaredOnly = false )

  // REG 3. Register the api and explicitly set the roles allowed to access the api
  //        This overrides the "roles" value in the @Api annotation
  apis.register[AppUsersApi](new AppUsersApi(), declaredOnly = false, roles = Some("admin"))

  // REG 4a. Register the api and set access to api only from CLI/command line container
  //        This overrides the "access" value in the @Api annotation
  apis.register[AppUsersApi](new AppUsersApi(), declaredOnly = false, roles = Some("admin"), protocol = Some("cli"))

  // REG 4b. Register the api and set access to api only from web/http container
  //        This overrides the "access" value in the @Api annotation
  apis.register[AppUsersApi](new AppUsersApi(), declaredOnly = false, roles = Some("admin"), protocol = Some("web"))

  // REG 4c. Register the api and set access to api from both CLI/Web
  //        This overrides the "access" value in the @Api annotation
  apis.register[AppUsersApi](new AppUsersApi(), declaredOnly = false, roles = Some("admin"), protocol = Some("*"))

  // REG 5. Register the api and supply your auth mode that will check for roles/permissions.
  //        This overrides the "auth" value in the @Api annotation
  apis.register[AppVersionApi](new AppVersionApi(), declaredOnly = false, roles = Some("admin"), protocol = Some("*"), auth = Some("key-roles"))

  //</doc:setup>

  override protected def executeInternal(args: Any) : AnyRef = {

    //<doc:examples>
    // The point of the api runner is that it allows your api ( decorated )
    // with Api, ApiAction, ApiArg annotations to be called via:
    //
    // 1. command line shell
    // 2. web api requests
    //
    // Also, the api runner and apiBase class and api annotations provide support for:
    //
    // 1. authorization
    // 2. auditing
    // 3. encryption / decryption
    // 4. status updates
    // 5. error handling
    //
    // This approach essentially makes your api protocal agnostic!!

    // CASE 1: check if api action exists ( false )
    printResult( apis.contains( "app.users.fakeMethod" ) )


    // CASE 2: check if api action exists ( true )
    printResult( apis.contains( "app.users.total" ) )


    // CASE 3a: validate the parameters ( fails - total takes 0 parameters )
    printResult( apis.check( "app.users.total -test:5" ) )


    // CASE 3b: validate the parameters ( fails - activate takes 3 parameters )
    printResult( apis.check( "app.users.activate" ) )


    // CASE 3c: validate the parameters ( fails - invite requires 'phone' and 'date' param )
    printResult( apis.check( "app.users.activate -code:1234 -isPremiumUser:true" ) )


    // CASE 3d: validate the parameters ( succeeds )
    printResult( apis.check( "app.users.activate -phone:1234567890 -code:1234 -isPremiumUser:true -date:20160315" ) )


    // CASE 4: call an api action without parameters
    printResult( apis.call( "app.users.total" ) )


    // CASE 5: call an api action with multiple parameters
    printResult( apis.call( "app.users.activate -phone:1234567890 -code:1234 -isPremiumUser:true -date:20160315" ) )
    //</doc:examples>

    ok()
  }


  def printResult( result:Result[Any] ): Unit =
  {
    println()
    println(s"success: ${result.success}")
    println(s"message: ${result.msg}")
    println(s"code   : ${result.code}")
    println(s"data   : ${result.getOrElse("null")}")
  }
}
