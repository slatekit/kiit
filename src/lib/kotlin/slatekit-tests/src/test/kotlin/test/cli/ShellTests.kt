/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slate.test

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.apis.routes.Api
import slatekit.apis.SetupType
import slatekit.apis.support.Authenticator
import slatekit.cli.CliRequest
import slatekit.cli.CliResponse
import slatekit.common.info.ApiKey
import slatekit.common.info.Credentials
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.types.Content
import slatekit.common.info.Host
import slatekit.common.types.Contents
import slatekit.integration.apis.InfoApi
import slatekit.connectors.cli.CliApi
import slatekit.integration.apis.VersionApi
import slatekit.connectors.entities.AppEntContext
import slatekit.results.Codes
import slatekit.results.Status
import slatekit.results.getOrElse
import kiit.serialization.Serialization
import test.TestApp


class ShellTests  {


    @Test fun can_execute_command() {
      val cli = getCli()
      val result = runBlocking { cli.executeText("app.version.host") }

      val res = result.getOrElse {
        CliResponse( CliRequest.build(Args.empty(), ""),true, Status.toType(Codes.SUCCESS), Codes.SUCCESS.name, 1, mapOf(), "" )
      }
      val req = res.request
      Assert.assertTrue( req.area == "app" )
      Assert.assertTrue( req.name == "version" )
      Assert.assertTrue( req.action == "host" )
      Assert.assertTrue( req.args.line == "app.version.host" )
      Assert.assertTrue( res.value is Host)
      Assert.assertTrue( res.success)
    }


    @Test fun can_handle_help() {
      val cli = getCli()
      val result = runBlocking { cli.executeText("?") }
      Assert.assertTrue( result.code == Codes.HELP.code )
      Assert.assertTrue( result.desc  == Codes.HELP.desc)
    }


    @Test fun can_handle_help_for_area() {
      val cli = getCli()
      val result = runBlocking {  cli.executeText("app ?") }
      //Assert.assertTrue( result.getOrElse { null } == null )
      Assert.assertTrue( result.code == Codes.HELP.code )
      Assert.assertTrue( result.desc  == Codes.HELP.desc)
    }


    @Test fun can_handle_help_for_area_api() {
      val cli = getCli()
      val result = runBlocking { cli.executeText("app.info ?") }
      //Assert.assertTrue( result.getOrElse { null } == null )
      Assert.assertTrue( result.code == Codes.HELP.code )
      Assert.assertTrue( result.desc  == Codes.HELP.desc)
    }


    @Test fun can_handle_help_for_area_api_action() {
      val cli = getCli()
      val result = runBlocking { cli.executeText("app.version.host ?") }
      //Assert.assertTrue( result.getOrElse { null } == null )
      Assert.assertTrue( result.code == Codes.HELP.code )
      Assert.assertTrue( result.desc  == Codes.HELP.desc)
    }



  private fun getCli(): CliApi {

    val app = TestApp::class.java
    val ctx = AppEntContext.sample(app, Config(app),"id", "slate.tests", "slate unit tests", "slatekit")

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
            Api(InfoApi(ctx), setup = SetupType.Annotated, declaredOnly = true, roles = listOf("qa")),
            Api(VersionApi(ctx), setup = SetupType.Annotated, declaredOnly = true, roles = listOf("qa"))
    )

    // 3. Build up the cli services that handles all the command line features.
    // And setup the api container to hold all the apis.
    val cli = CliApi(ctx.toAppContext(), Authenticator(apiKeys), apiItems = apis,
            serializer = { item, type -> Contents.csv(Serialization.csv().serialize(item) )}
    ) { meta ->
      listOf("api-key" to creds.key)
    }
    return cli
  }
}
