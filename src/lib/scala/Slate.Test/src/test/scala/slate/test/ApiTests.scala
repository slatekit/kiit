/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slate.test

import slate.common.app.AppMeta
import slate.common.envs.EnvItem
import slate.entities.core.Entities
import slate.test.common.MyAuthProvider
import org.scalatest.{FunSpec, BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.{ApiKey, Result}
import slate.common.databases.DbConString
import slate.common.encrypt.Encryptor
import slate.common.info.{Lang, Host, About}
import slate.common.logging.LoggerConsole
import slate.common.results.{ResultSupportIn, ResultCode}
import slate.core.apis._
import slate.core.common.{Conf, AppContext}
import slate.test.common.UserApi


class ApiTests extends FunSpec with BeforeAndAfter with BeforeAndAfterAll with ResultSupportIn {

  describe( "API Container" ) {

    it("can register api") {
      val apis = getApis()
      apis.register[UserApi](new UserApi())
      assert(apis.getOrCreateArea("app") != null)
      assert(apis.getOrCreateArea("app")("users").container == apis)
    }


    it("can check action does NOT exist") {
      val apis = getApis()
      apis.register[UserApi](new UserApi())
      assert(!apis.contains("app.users.fakeMethod").success)
    }


    it("can check action exists") {
      val apis = getApis()
      apis.register[UserApi](new UserApi())
      assert(apis.contains("app.users.activate").success)
    }


    it("can execute public action") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, null,
        "app.users.rolesNone",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        success("rolesNone", msg=Some("1 abc"))
      )
    }
  }



  describe( "API Authorization" ) {

    describe( "using App roles on actions" ) {

      it("should work when role is any ( * )") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"),
          "app.users.rolesAny",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          success("rolesAny", msg=Some("1 abc"))
        )
      }


      it("should fail for any role ( * ) with no user") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, null,
          "app.users.rolesAny",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          unAuthorized[String](Some("Unable to authorize, authorization provider not set"))
        )
      }


      it("should work for a specific role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"),
          "app.users.rolesSpecific",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          success("rolesSpecific", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "ops"),
          "app.users.rolesSpecific",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          unAuthorized[String](Some("unauthorized"))
        )
      }


      it("should work for a specific role when referring to its parent role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "admin"),
          "app.users.rolesParent",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          success("rolesParent", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when referring to its parent role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"),
          "app.users.rolesParent",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          unAuthorized[String](Some("unauthorized"))
        )
      }
    }


    describe( "using Key roles on actions" ) {

      it("should work when role is any ( * )") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "dev"),
          "app.users.rolesAny",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, String)](("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD"))),
          success("rolesAny", msg=Some("1 abc"))
        )
      }


      it("should fail for any role ( * ) with no user") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, null,
          "app.users.rolesAny",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          unAuthorized[String](Some("Unable to authorize, authorization provider not set"))
        )
      }


      it("should work for a specific role") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "dev"),
          "app.users.rolesSpecific",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, String)](("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD"))),
          success("rolesSpecific", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "qa"),
          "app.users.rolesSpecific",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, String)](("api-key", "EB7EB37764AD4411A1763E6A593992BD"))),
          unAuthorized[String](Some("unauthorized"))
        )
      }


      it("should work for a specific role when referring to its parent role") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "admin"),
          "app.users.rolesParent",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, String)](("api-key", "54B1817194C1450B886404C6BEA81673"))),
          success("rolesParent", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when referring to its parent role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "dev"),
          "app.users.rolesParent",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, String)](("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD"))),
          unAuthorized[String](Some("unauthorized"))
        )
      }
    }
  }



  describe( "Using container type CLI" ) {

    it("should work when setup as protocol * and request is CLI") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"),
        "app.users.protocolAny",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        success("protocolAny", msg=Some("1 abc"))
      )
    }


    it("should work when setup as protocol CLI and request is CLI") {
      ensureCall("*", "cli", ApiConstants.AuthModeAppRole, ("kishore", "dev"),
        "app.users.protocolCLI",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        success("protocolCLI", msg=Some("1 abc"))
      )
    }


    it("should work when setup as parent protocol CLI and request is CLI") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"),
        "app.users.protocolParent",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        success("protocolParent", msg=Some("1 abc"))
      )
    }


    it("should FAIL when setup as protocol WEB and request is CLI") {
      ensureCall("cli", "web", ApiConstants.AuthModeAppRole, ("kishore", "dev"),
        "app.users.protocolWeb",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        notFound[String](Some("app.users.protocolWeb not found"))
      )
    }
  }


  private def getApis(protocol:String = "cli"):ApiContainer = {

    // 1. apis
    val apis = new ApiContainer(protocol)

    // 2. context for common services
    apis.ctx = new AppContext (
      app  = new AppMeta(),
      env  = EnvItem("local", "dev"),
      cfg  = new Conf(),
      log  = new LoggerConsole(),
      ent  = new Entities(),
      host = Host.local(),
      lang = Lang.asScala(),
      inf  = new About("myapp", "sample app", "product group 1", company = "slatekit", region = "ny", version = "1.1.0"),
      con  = Some(new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi")),
      enc  = Some(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292"))
    )

    // 3. Auth
    apis.auth = None
    apis
  }


  /**
    * @param protocolCt  : The protocol(s) for the container cli|web|*
    * @param protocolApi : The protocol(s) for the api cli|web|*
    * @param authMode    : ApiConstants.AuthModeXXX
    * @param user        : ("kishore", "dev" ) or ("kishore", "{api-key}")
    * @param path        : "app.users.activate"
    * @param inputs      : List[(String,String)]( ("code", "1") )
    * @param opts        : Some(List[(String,String)]( ("api-key", "abcdefghij") ))
    * @param expected    : Result(true, "ok", 200, 123 )
    */
  private def ensureCall(protocolCt:String,
                         protocolApi:String,
                         authMode:String,
                         user:(String,String),
                         path:String,
                         inputs:Option[List[(String,String)]],
                         opts:Option[List[(String,String)]],
                         expected:Result[String]):Unit = {

    val cmd = ApiHelper.buildCmd(path, inputs, opts)
    val apis = getApis(protocolCt)

    apis.register[UserApi](new UserApi(), auth = Some(authMode), protocol = Some(protocolApi))

    // set the auth
    if(user != null) {
      val keys = List[ApiKey](
        new ApiKey("user" , "7BF84B28FC8A41BBA3FDFA48D2B462DA", "user"                    ),
        new ApiKey("po"   , "0F66CD55079C42FF85C001846472343C", "user,po"                 ),
        new ApiKey("qa"   , "EB7EB37764AD4411A1763E6A593992BD", "user,po,qa"              ),
        new ApiKey("dev"  , "3E35584A8DE0460BB28D6E0D32FB4CFD", "user,po,qa,dev"          ),
        new ApiKey("ops"  , "5020F4A237A443B4BEDC37D8A08588A3", "user,po,qa,dev,ops"      ),
        new ApiKey("admin", "54B1817194C1450B886404C6BEA81673", "user,po,qa,dev,ops,admin")
      )
      apis.auth = Some(new MyAuthProvider(user._1, user._2, keys))
    }

    val actual = apis.callCommand( cmd )

    assert( actual.code == expected.code)
    assert( actual.success == expected.success)
    assert( actual.msg == expected.msg)
  }
}
