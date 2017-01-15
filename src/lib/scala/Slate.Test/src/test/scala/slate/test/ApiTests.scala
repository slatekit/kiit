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

import slate.common.envs.EnvItem
import slate.entities.core.Entities
import slate.test.common.{UserApi2, MyAuthProvider, UserApi}
import org.scalatest.{FunSpec, BeforeAndAfter, BeforeAndAfterAll}
import slate.common.{ApiKey, Result}
import slate.common.databases.DbConString
import slate.common.databases.DbLookup._
import slate.common.encrypt.Encryptor
import slate.common.info.{About}
import slate.common.logging.LoggerConsole
import slate.common.results.{ResultSupportIn}
import slate.core.apis._
import slate.core.common.{Conf, AppContext}


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
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, null, None,
        "app.users.rolesNone",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        success("rolesNone", msg = Some("1 abc"))
      )
    }
  }


  describe("API Error handling") {

    it("can handle error via global default handler") {
      var errorCount = 0
      var errorPath = ""
      val errorHandler = new ApiErrorHandler(Some((ctx, req, ex) => {
        errorCount += 1
        errorPath = req.fullName
        this.badRequest(msg=Some("customer handler"))
      }))
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, null, None,
        "app.users.testException",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        unexpectedError(msg=Some("error executing : app.users.testException, check inputs"))
      )
      assert( errorCount == 0)
      assert( errorPath  == "")
    }


    it("can handle error via global custom handler") {
      var errorCount = 0
      var errorPath = ""
      val errorHandler = new ApiErrorHandler(Some((ctx, req, ex) => {
        errorCount += 1
        errorPath = req.fullName
        this.unexpectedError(msg=Some("global custom handler"))
      }))
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, null, Some(errorHandler),
        "app.users.testException",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        unexpectedError(msg=Some("global custom handler"))
      )
      assert( errorCount == 1)
      assert( errorPath  == "app.users.testException")
    }


    it("can handle error via api custom handler") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, null, None,
        "app.users2.testException",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        unexpectedError(msg=Some("unexpected error in api"))
      )
    }
  }



  describe( "API Authorization" ) {

    describe( "using App roles on actions" ) {

      it("should work when role is any ( * )") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
          "app.users.rolesAny",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          success("rolesAny", msg=Some("1 abc"))
        )
      }


      it("should fail for any role ( * ) with no user") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, null, None,
          "app.users.rolesAny",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          unAuthorized[String](msg = Some("Unable to authorize, authorization provider not set"))
        )
      }


      it("should work for a specific role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
          "app.users.rolesSpecific",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          success("rolesSpecific", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "ops"), None,
          "app.users.rolesSpecific",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          unAuthorized[String](Some("unauthorized"))
        )
      }


      it("should work for a specific role when referring to its parent role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "admin"), None,
          "app.users.rolesParent",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          success("rolesParent", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when referring to its parent role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
          "app.users.rolesParent",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          unAuthorized[String](Some("unauthorized"))
        )
      }
    }


    describe( "using Key roles on actions" ) {

      it("should work when role is any ( * )") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "dev"), None,
          "app.users.rolesAny",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, String)](("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD"))),
          success("rolesAny", msg=Some("1 abc"))
        )
      }


      it("should fail for any role ( * ) with no user") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, null, None,
          "app.users.rolesAny",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          None,
          unAuthorized[String](msg = Some("Unable to authorize, authorization provider not set"))
        )
      }


      it("should work for a specific role") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "dev"), None,
          "app.users.rolesSpecific",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, String)](("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD"))),
          success("rolesSpecific", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "qa"), None,
          "app.users.rolesSpecific",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, String)](("api-key", "EB7EB37764AD4411A1763E6A593992BD"))),
          unAuthorized[String](Some("unauthorized"))
        )
      }


      it("should work for a specific role when referring to its parent role") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "admin"), None,
          "app.users.rolesParent",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, String)](("api-key", "54B1817194C1450B886404C6BEA81673"))),
          success("rolesParent", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when referring to its parent role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "dev"), None,
          "app.users.rolesParent",
          Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, String)](("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD"))),
          unAuthorized[String](Some("unauthorized"))
        )
      }
    }
  }



  describe( "API Container Type CLI" ) {

    it("should work when setup as protocol * and request is CLI") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.protocolAny",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        success("protocolAny", msg=Some("1 abc"))
      )
    }


    it("should work when setup as protocol CLI and request is CLI") {
      ensureCall("*", "cli", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.protocolCLI",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        success("protocolCLI", msg=Some("1 abc"))
      )
    }


    it("should work when setup as parent protocol CLI and request is CLI") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.protocolParent",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        success("protocolParent", msg=Some("1 abc"))
      )
    }


    it("should FAIL when setup as protocol WEB and request is CLI") {
      ensureCall("cli", "web", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.protocolWeb",
        Some(List[(String, String)](("code", "1"), ("tag", "abc"))),
        None,
        notFound[String](msg = Some("app.users.protocolWeb not found"))
      )
    }
  }


  private def getApis(protocol:String = "cli",
                      auth:Option[ApiAuth] = None,
                      apiRegs:Option[List[ApiReg]] = None,
                      errors:Option[ApiErrorHandler] = None):ApiContainer = {

    // 1. context for common services
    val ctx = new AppContext (
      env  = EnvItem("local", "dev"),
      cfg  = new Conf(),
      log  = new LoggerConsole(),
      ent  = new Entities(),
      inf  = new About("myapp", "sample app", "product group 1", company = "slatekit", region = "ny", version = "1.1.0"),
      dbs  = Some(defaultDb(new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi"))),
      enc  = Some(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292"))
    )
    // 2. apis
    val apis = new ApiContainer(ctx, auth, protocol, apiRegs, errors)
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
                         errors:Option[ApiErrorHandler],
                         path:String,
                         inputs:Option[List[(String,String)]],
                         opts:Option[List[(String,String)]],
                         expected:Result[String]):Unit = {

    val cmd = ApiHelper.buildCmd(path, inputs, opts)
    val regs = List[ApiReg](
      new ApiReg(new UserApi(), false, auth = Some(authMode)),
      new ApiReg(new UserApi2(), false, auth = Some(authMode))
    )

    // set the auth
    val apis = if(user != null) {
      val keys = List[ApiKey](
        new ApiKey("user" , "7BF84B28FC8A41BBA3FDFA48D2B462DA", "user"                    ),
        new ApiKey("po"   , "0F66CD55079C42FF85C001846472343C", "user,po"                 ),
        new ApiKey("qa"   , "EB7EB37764AD4411A1763E6A593992BD", "user,po,qa"              ),
        new ApiKey("dev"  , "3E35584A8DE0460BB28D6E0D32FB4CFD", "user,po,qa,dev"          ),
        new ApiKey("ops"  , "5020F4A237A443B4BEDC37D8A08588A3", "user,po,qa,dev,ops"      ),
        new ApiKey("admin", "54B1817194C1450B886404C6BEA81673", "user,po,qa,dev,ops,admin")
      )
      val auth = Some(new MyAuthProvider(user._1, user._2, Some(keys)))
      val apis = getApis(protocolCt, auth, apiRegs = Some(regs), errors = errors)
      apis
    }
    else {
      val apis = getApis(protocolCt, apiRegs = Some(regs), errors = errors)
      apis
    }

    //apis.register[UserApi](new UserApi(), auth = Some(authMode), protocol = Some(protocolApi))
    val actual = apis.callCommand( cmd )

    assert( actual.code == expected.code)
    assert( actual.success == expected.success)
    assert( actual.msg == expected.msg)
  }
}
