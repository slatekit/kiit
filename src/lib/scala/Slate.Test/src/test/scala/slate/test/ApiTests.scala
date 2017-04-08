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


import slate.core.apis.containers.ApiContainerCLI
import slate.core.apis.core.{Errors, Auth}
import slate.core.apis.support.ApiHelper

import scala.reflect.runtime.universe.{typeOf, Type}
import slate.common.args.Args
import slate.common.envs.{Env, Dev}
import slate.entities.core.Entities
import slate.test.common.{User, UserApi2, MyAuthProvider, UserApi}
import org.scalatest.{FunSpec, BeforeAndAfter, BeforeAndAfterAll}
import slate.common.{ApiKey, Result}
import slate.common.databases.DbConString
import slate.common.databases.DbLookup._
import slate.common.info.{About}
import slate.common.logging.LoggerConsole
import slate.common.results.{ResultSupportIn}
import slate.core.apis._
import slate.core.common.{Conf, AppContext}
import slate.tests.common.{MyAppContext, MyEncryptor}


class ApiTests extends FunSpec with BeforeAndAfter with BeforeAndAfterAll with ResultSupportIn {

  describe( "API Container" ) {

    it("can register api") {
      val apis = getApis()
      apis.ctx.ent.register[User](false, typeOf[User], serviceCtx = Some(apis.ctx))
      apis.register[UserApi](new UserApi(apis.ctx))
      assert(apis.getLookup("app") != null)
      //assert(apis.getOrCreateArea("app")("users").container == apis)
    }


    it("can check action does NOT exist") {
      val apis = getApis()
      apis.ctx.ent.register[User](false, typeOf[User], serviceCtx = Some(apis.ctx))
      apis.register[UserApi](new UserApi(apis.ctx))
      assert(!apis.asInstanceOf[ApiContainerCLI].contains("app.users.fakeMethod").success)
    }


    it("can check action exists") {
      val apis = getApis()
      apis.ctx.ent.register[User](false, typeOf[User], serviceCtx = Some(apis.ctx))
      apis.register[UserApi](new UserApi(apis.ctx))
      assert(apis.asInstanceOf[ApiContainerCLI].contains("app.users.activate").success)
    }


    it("can execute public action") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, null, None,
        "app.users.rolesNone",
        Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
        None,
        success("rolesNone", msg = Some("1 abc"))
      )
    }
  }


  describe( "API Data-types" ) {

    it("can get raw request") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.argTypeRequest",
        Some(List[(String, Any)](("id", "2"))),
        None,
        success("ok", msg = Some("raw request id: 2"))
      )
    }


    it("can get file") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.argTypeFile",
        Some(List[(String, Any)](("doc", "user://slatekit/temp/test1.txt"))),
        None,
        success("ok", msg = Some("slatekit file reference test 1"))
      )
    }


    it("can get list") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.argTypeListInt",
        Some(List[(String, Any)](("items", List[Int](1,2,3)))),
        None,
        success("ok", msg = Some(",1,2,3"))
      )
    }


    it("can get list via conversion") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.argTypeListInt",
        Some(List[(String, Any)](("items", "1,2,3"))),
        None,
        success("ok", msg = Some(",1,2,3"))
      )
    }


    it("can get map") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.argTypeMapInt",
        Some(List[(String, Any)](("items", Map[String,Int]("a" -> 1, "b" -> 2)))),
        None,
        success("ok", msg = Some(",a=1,b=2"))
      )
    }


    it("can get map via conversion") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.argTypeMapInt",
        Some(List[(String, Any)](("items", "a=1,b=2"))),
        None,
        success("ok", msg = Some(",a=1,b=2"))
      )
    }
  }


  describe( "API Decryption" ) {

    it("can decrypt int") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.decInt",
        Some(List[(String, Any)](("id", MyEncryptor.encrypt("2")))),
        None,
        success("ok", msg = Some("decrypted int : 2"))
      )
    }


    it("can decrypt long") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.decLong",
        Some(List[(String, Any)](("id", MyEncryptor.encrypt("2")))),
        None,
        success("ok", msg = Some("decrypted long : 2"))
      )
    }


    it("can decrypt double") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.decDouble",
        Some(List[(String, Any)](("id", MyEncryptor.encrypt("2.2")))),
        None,
        success("ok", msg = Some("decrypted double : 2.2"))
      )
    }


    it("can decrypt string") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.decString",
        Some(List[(String, Any)](("id", MyEncryptor.encrypt("slatekit")))),
        None,
        success("ok", msg = Some("decrypted string : slatekit"))
      )
    }
  }


  describe("API Error handling") {

    it("can handle error via global default handler") {
      var errorCount = 0
      var errorPath = ""
      val errorHandler = new Errors(Some((ctx, req, ex) => {
        errorCount += 1
        errorPath = req.fullName
        this.badRequest(msg=Some("customer handler"))
      }))
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, null, None,
        "app.users.testException",
        Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
        None,
        unexpectedError(msg=Some("error executing : app.users.testException, check inputs"))
      )
      assert( errorCount == 0)
      assert( errorPath  == "")
    }


    it("can handle error via global custom handler") {
      var errorCount = 0
      var errorPath = ""
      val errorHandler = new Errors(Some((ctx, req, ex) => {
        errorCount += 1
        errorPath = req.fullName
        this.unexpectedError(msg=Some("global custom handler"))
      }))
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, null, Some(errorHandler),
        "app.users.testException",
        Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
        None,
        unexpectedError(msg=Some("global custom handler"))
      )
      assert( errorCount == 1)
      assert( errorPath  == "app.users.testException")
    }


    it("can handle error via api custom handler") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, null, None,
        "app.users2.testException",
        Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
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
          Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
          None,
          success("rolesAny", msg=Some("1 abc"))
        )
      }


      it("should fail for any role ( * ) with no user") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, null, None,
          "app.users.rolesAny",
          Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
          None,
          unAuthorized[String](msg = Some("Unable to authorize, authorization provider not set"))
        )
      }


      it("should work for a specific role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
          "app.users.rolesSpecific",
          Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
          None,
          success("rolesSpecific", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "ops"), None,
          "app.users.rolesSpecific",
          Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
          None,
          unAuthorized[String](Some("unauthorized"))
        )
      }


      it("should work for a specific role when referring to its parent role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "admin"), None,
          "app.users.rolesParent",
          Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
          None,
          success("rolesParent", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when referring to its parent role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
          "app.users.rolesParent",
          Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
          None,
          unAuthorized[String](Some("unauthorized"))
        )
      }
    }


    describe( "using Key roles on actions" ) {

      it("should work when role is any ( * )") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "dev"), None,
          "app.users.rolesAny",
          Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, Any)](("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD"))),
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
          Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, Any)](("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD"))),
          success("rolesSpecific", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "qa"), None,
          "app.users.rolesSpecific",
          Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, Any)](("api-key", "EB7EB37764AD4411A1763E6A593992BD"))),
          unAuthorized[String](Some("unauthorized"))
        )
      }


      it("should work for a specific role when referring to its parent role") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "admin"), None,
          "app.users.rolesParent",
          Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, Any)](("api-key", "54B1817194C1450B886404C6BEA81673"))),
          success("rolesParent", msg=Some("1 abc"))
        )
      }


      it("should fail for a specific role when referring to its parent role when user has a different role") {
        ensureCall("*", "*", ApiConstants.AuthModeKeyRole, ("kishore", "dev"), None,
          "app.users.rolesParent",
          Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
          Some(List[(String, Any)](("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD"))),
          unAuthorized[String](Some("unauthorized"))
        )
      }
    }
  }



  describe( "API Container Type CLI" ) {

    it("should work when setup as protocol * and request is CLI") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.protocolAny",
        Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
        None,
        success("protocolAny", msg=Some("1 abc"))
      )
    }


    it("should work when setup as protocol CLI and request is CLI") {
      ensureCall("*", "cli", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.protocolCLI",
        Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
        None,
        success("protocolCLI", msg=Some("1 abc"))
      )
    }


    it("should work when setup as parent protocol CLI and request is CLI") {
      ensureCall("*", "*", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.protocolParent",
        Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
        None,
        success("protocolParent", msg=Some("1 abc"))
      )
    }


    it("should FAIL when setup as protocol WEB and request is CLI") {
      ensureCall("cli", "web", ApiConstants.AuthModeAppRole, ("kishore", "dev"), None,
        "app.users.protocolWeb",
        Some(List[(String, Any)](("code", "1"), ("tag", "abc"))),
        None,
        notFound[String](msg = Some("app.users.protocolWeb not found"))
      )
    }
  }


  private def getApis(protocol:String = "cli",
                      auth:Option[Auth] = None,
                      apiRegs:Option[List[ApiReg]] = None,
                      errors:Option[Errors] = None):ApiContainer = {

    // 1. context for common services
    val ctx = new AppContext (
      arg  = Args(),
      env  = Env("local", Dev),
      cfg  = new Conf(),
      log  = new LoggerConsole(),
      ent  = new Entities(),
      inf  = new About("myapp", "sample app", "product group 1", "slatekit", "ny", "", "", "", "1.1.0", "", ""),
      dbs  = Some(defaultDb(new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi"))),
      enc  = Some(MyEncryptor)
    )
    // 2. apis
    val apis = new ApiContainerCLI(ctx, auth, apiRegs, errors)
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
                         errors:Option[Errors],
                         path:String,
                         inputs:Option[List[(String,Any)]],
                         opts:Option[List[(String,Any)]],
                         expected:Result[String]):Unit = {

    val appCtx = AppContext.sample("tests", "tests", "tests", "slatekit")
    appCtx.ent.register[User](false, typeOf[User], serviceCtx = Some(appCtx))
    val cmd = ApiHelper.buildCmd(path, inputs, opts)
    val regs = List[ApiReg](
      new ApiReg(new UserApi(appCtx), false, auth = Some(authMode)),
      new ApiReg(new UserApi2(appCtx), false, auth = Some(authMode))
    )

    // set the auth
    val apis = if(user != null) {
      val keys = List[ApiKey](
        ApiKey("user" , "7BF84B28FC8A41BBA3FDFA48D2B462DA", "user"                    ),
        ApiKey("po"   , "0F66CD55079C42FF85C001846472343C", "user,po"                 ),
        ApiKey("qa"   , "EB7EB37764AD4411A1763E6A593992BD", "user,po,qa"              ),
        ApiKey("dev"  , "3E35584A8DE0460BB28D6E0D32FB4CFD", "user,po,qa,dev"          ),
        ApiKey("ops"  , "5020F4A237A443B4BEDC37D8A08588A3", "user,po,qa,dev,ops"      ),
        ApiKey("admin", "54B1817194C1450B886404C6BEA81673", "user,po,qa,dev,ops,admin")
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
    val actual = apis.call( cmd )

    assert( actual.code == expected.code)
    assert( actual.success == expected.success)
    assert( actual.msg == expected.msg)
  }
}
