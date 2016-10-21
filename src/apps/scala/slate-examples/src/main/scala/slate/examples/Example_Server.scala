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
import slate.common.args.Args
import slate.common.conf.Config
import slate.common.info.{Lang, Host, About}
import slate.common.results.ResultSupportIn
import slate.core.apis.{ApiCmd, ApiAction, Api}
import slate.core.common.AppContext
import slate.core.common.svcs.{ApiWithSupport, ApiEntityWithSupport}
//</doc:import_required>

//<doc:import_examples>
import slate.common._
import slate.entities.core._
import slate.common.encrypt.Encryptor
import slate.common.logging.LoggerConsole
import slate.core.cmds.Cmd
import scala.collection.mutable.Map
//</doc:import_examples>

class Example_Server extends Cmd("types")  with ResultSupportIn {

  // Sample user class
  class User extends EntityUnique {

    @Field("", true, 30)
    var email = ""


    @Field("", true, 30)
    var firstName = ""


    @Field("", true, 30)
    var lastName = ""


    @Field("", true, 30)
    var isMale = false


    @Field("", true, 0)
    var age = 35


    def init(first:String, last:String): User =
    {
      firstName = first
      lastName = last
      this
    }


    def fullname:String =
    {
      firstName + " " + lastName
    }


    override def toString():String =
    {
      email + ", " + firstName + ", " + lastName + ", " + isMale + ", " + age
    }
  }


  @Api(area = "app", name = "users", desc = "api to access and manage users 3", roles= "@admin")
  class UserApi extends ApiWithSupport with ResultSupportIn
  {
    var user = new User


    @ApiAction(name = "", desc = "registers new user", roles= "@parent")
    def register(user:User): Result[Boolean] =
    {
      this.user = user
     ok(Some("registerd user"))
    }


    @ApiAction(name = "", desc = "activates a users account 3", roles= "@parent")
    def activate(phone:String, code:Int, isPremiumUser:Boolean, date:DateTime): Result[Boolean] =
    {
      ok(Some(s"activated $phone, $code, $isPremiumUser, $date"))
    }


    @ApiAction(name = "", desc = "invites a new user", roles= "@parent")
    def invite(email:String, phone:String, promoCode:String): Result[Boolean] =
    {
      ok(Some(s"sent invitation to $email, $phone, $promoCode"))
    }
  }


  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:setup>
    // Setup 1: Initialize with just the port
    //val server1 = new SlateServer( 5000 )

    // Setup 2: Initialize with all the application context which contains
    // typical info needed for any application:
    // 1. env : dev, qa, staging, prod
    // 2. cfg : config values
    // 3. log : logger
    // 4. enc : encrypt/decrypt
    // 5. info: info about the app, including contact information
    // NOTES: You can provide your own implementation of the Config, Logger, Encryptor
    // For a full example, refer to the sample app.
    val appCtx = new AppContext (
      env  = envs.EnvItem( "ny.dev.01", envs.Env.DEV ),
      cfg  = new Config ( Map[String,String]("env" -> "dev") ),
      log  = new LoggerConsole(),
      inf  = new About("slatekit.examples", "SlateKit Examples", "example", "slatekit", "mobile", "ny", "http://myapp.com", "kishore@abc.com"),
      ent  = new Entities(),
      enc  = Some(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292"))
    )
    //val server = new Server(port = 5000, interface = "::0", ctx = appCtx )

    // Setup 3: Now register your apis( regular old scala classes )
    //server.apis.register[UserApi](new UserApi(), declaredOnly = false)

    // An http web call like :
    // http://domain.com/api/app.users.invite
    // {
    //    email='kishore@abc.com'
    //    phone='123456789'
    //    promoCode=abc123
    // }
    //
    // will get converted to a ApiCmd. Simulate that here for testing.
    // So simulate that here. ( NOTE: The data in http is headers/form is not copied,
    // the Akka-Http data is wrapped around another slate object )
    //val args = Args.parse("app.users.invite -email='kishore@abc.com' -phone='123456789' -promoCode=abc123").data.asInstanceOf[Args]
    //val cmd = ApiCmd("app.users.invite", args, Some(args), None)
    //server.apis.callCommand(cmd)

    // Execute the server to start it up
    //server.run()

    //</doc:setup>

    //<doc:examples>


    //</doc:examples>
    ok()
  }
}
