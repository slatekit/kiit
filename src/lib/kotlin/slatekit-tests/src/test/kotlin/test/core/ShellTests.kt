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

import org.junit.Test
import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.apis.svcs.Authenticator
import slatekit.cli.CliRequest
import slatekit.cli.CliResponse
import slatekit.common.info.ApiKey
import slatekit.common.info.Credentials
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.info.Host
import slatekit.integration.apis.InfoApi
import slatekit.integration.apis.CliApi
import slatekit.integration.apis.VersionApi
import slatekit.integration.common.AppEntContext
import slatekit.results.StatusCodes
import slatekit.results.getOrElse


class ShellTests  {


    @Test fun can_execute_command() {
      val cli = getCli()
      val result = cli.executeText("app.version.host")

      val res = result.getOrElse {
        CliResponse( CliRequest.build(Args.default(), ""),true, 1, mapOf(), "" )
      }
      val req = res.request
      assert( req.area == "app" )
      assert( req.name == "version" )
      assert( req.action == "host" )
      assert( req.args.line == "app.version.host" )
      assert( res.value is Host)
      assert( res.success)
    }


    @Test fun can_handle_help() {
      val cli = getCli()
      val result = cli.executeText("?")
      assert( result.code == StatusCodes.HELP.code )
      assert( result.msg  == StatusCodes.HELP.msg)
    }


    @Test fun can_handle_help_for_area() {
      val cli = getCli()
      val result = cli.executeText("app ?")
      //assert( result.getOrElse { null } == null )
      assert( result.code == StatusCodes.HELP.code )
      assert( result.msg  == StatusCodes.HELP.msg)
    }


    @Test fun can_handle_help_for_area_api() {
      val cli = getCli()
      val result = cli.executeText("app.info ?")
      //assert( result.getOrElse { null } == null )
      assert( result.code == StatusCodes.HELP.code )
      assert( result.msg  == StatusCodes.HELP.msg)
    }


    @Test fun can_handle_help_for_area_api_action() {
      val cli = getCli()
      val result = cli.executeText("app.version.host ?")
      //assert( result.getOrElse { null } == null )
      assert( result.code == StatusCodes.HELP.code )
      assert( result.msg  == StatusCodes.HELP.msg)
    }



  private fun getCli(): CliApi {

    val ctx = AppEntContext.sample(Config(),"id", "slate.tests", "slate unit tests", "slatekit")

    val apiKeys = listOf(
            ApiKey("user", "7BF84B28FC8A41BBA3FDFA48D2B462DA", "user"),
            ApiKey("po", "0F66CD55079C42FF85C001846472343C", "user,po"),
            ApiKey("qa", "EB7EB37764AD4411A1763E6A593992BD", "user,po,qa"),
            ApiKey("dev", "3E35584A8DE0460BB28D6E0D32FB4CFD", "user,po,qa,dev"),
            ApiKey("ops", "5020F4A237A443B4BEDC37D8A08588A3", "user,po,qa,dev,ops"),
            ApiKey("admin", "54B1817194C1450B886404C6BEA81673", "user,po,qa,dev,ops,admin")
      )

    // 1. Get the user login info from .slate
    val creds =
            Credentials("1", "kishore", "kishore@abc.com", apiKeys.last().key, "test", "ny")

    // 2. Register the apis using default textType ( uses permissions in annotations on class )
    val apis = listOf(
            Api(InfoApi(ctx)    , setup = Annotated, declaredOnly = true, roles = "qa", protocol = "*"),
            Api(VersionApi(ctx), setup = Annotated, declaredOnly = true, roles = "qa", protocol = "*")
    )

    // 3. Build up the cli services that handles all the command line features.
    // And setup the api container to hold all the apis.
    val cli = CliApi(ctx.toAppContext(), Authenticator(apiKeys), apiItems = apis) { meta ->
      listOf("api-key" to creds.key)
    }
    return cli
  }
}
