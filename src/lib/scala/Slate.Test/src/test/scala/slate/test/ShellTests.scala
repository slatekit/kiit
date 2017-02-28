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

package slate.test

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSpec}
import slate.common.{ApiKey,Credentials}
import slate.common.results.{ResultCode, ResultSupportIn}
import slate.core.apis.ApiAuth
import slate.core.common.{AppContext}
import slate.integration.{ShellAPI, VersionApi, AppApi}

class ShellTests extends FunSpec with BeforeAndAfter with BeforeAndAfterAll with ResultSupportIn {

  describe( "can execute commands" ) {

    it("can execute command") {
      val shell = getShell()
      val result = shell.onCommandExecute("app.info.host")
      assert( result.isDefined )
      assert( result.get.area == "app" )
      assert( result.get.name == "info" )
      assert( result.get.action == "host" )
      assert( result.get.line == "app.info.host" )
      assert( result.get.result.success == true)
    }
  }


  describe( "can request help" ) {

    it("can handle help ?") {
      val shell = getShell()
      val result = shell.onCommandExecute("?")
      assert( result.isDefined == false )
      assert( result.code == ResultCode.HELP )
      assert( result.msg  == Some("help") )
    }


    it("can handle help for area ?") {
      val shell = getShell()
      val result = shell.onCommandExecute("app ?")
      assert( result.isDefined == false )
      assert( result.code == ResultCode.HELP )
      assert( result.msg  == Some("area ?") )
    }


    it("can handle help for area.api ?") {
      val shell = getShell()
      val result = shell.onCommandExecute("app.info ?")
      assert( result.isDefined == false )
      assert( result.code == ResultCode.HELP )
      assert( result.msg  == Some("area.api ?") )
    }


    it("can handle help for area.api.action ?") {
      val shell = getShell()
      val result = shell.onCommandExecute("app.info.host ?")
      assert( result.isDefined == false )
      assert( result.code == ResultCode.HELP )
      assert( result.msg  == Some("area.api.action ?") )
    }
  }


  private def getShell(): ShellAPI = {

    val ctx = AppContext.sample("id", "slate.tests", "slate unit tests", "slatekit")

    val apiKeys = List[ApiKey](
        ApiKey("user" , "7BF84B28FC8A41BBA3FDFA48D2B462DA", "user"                    ),
        ApiKey("po"   , "0F66CD55079C42FF85C001846472343C", "user,po"                 ),
        ApiKey("qa"   , "EB7EB37764AD4411A1763E6A593992BD", "user,po,qa"              ),
        ApiKey("dev"  , "3E35584A8DE0460BB28D6E0D32FB4CFD", "user,po,qa,dev"          ),
        ApiKey("ops"  , "5020F4A237A443B4BEDC37D8A08588A3", "user,po,qa,dev,ops"      ),
        ApiKey("admin", "54B1817194C1450B886404C6BEA81673", "user,po,qa,dev,ops,admin")
      )

    // 1. Get the user login info from .slate
    val creds = new Credentials("1", "kishore", "kishore@abc.com", "3E35584A8DE0460BB28D6E0D32FB4CFD", "test", "ny")

    // 2. Build up the shell services that handles all the command line features.
    // And setup the api container to hold all the apis.
    val shell = new ShellAPI(creds, ctx, new ApiAuth(Some(apiKeys), None), "sampleapp")

    // 4. Register the apis using default mode ( uses permissions in annotations on class )
    shell.apis.register[AppApi]    (new AppApi(ctx)    , true, Some("qa"), protocol = Some("*"))
    shell.apis.register[VersionApi](new VersionApi(ctx), true, Some("qa"), protocol = Some("*") )
    shell.apis.init()

    shell
  }

}
