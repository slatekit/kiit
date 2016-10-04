/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.integration

import slate.common.databases.Db
import slate.common.info._
import slate.common.serialization.{SerializerUtils, SerializerProps}
import slate.core.apis.{ApiCmd, Api, ApiAction}
import slate.core.common.svcs.ApiWithSupport

@Api(area = "app", name = "info", desc = "api info about the application and host", roles= "admin", auth="key-roles", verb = "post", protocol = "*")
class AppApi extends ApiWithSupport
{

  @ApiAction(name = "", desc= "get info about the application", roles= "@parent", verb = "@parent", protocol = "@parent")
  def app(format:String = "props"):About = {
    context.app.about
  }


  @ApiAction(name = "", desc= "get info about the application", roles= "@parent", verb = "@parent", protocol = "@parent")
  def cmd(cmd:ApiCmd):About = {
    println(cmd.fullName)
    context.app.about
  }


  @ApiAction(name = "", desc= "gets info about the language", roles= "@parent", verb = "@parent", protocol = "@parent")
  def lang(format:String = "props"):Lang = {
    context.app.lang
  }


  @ApiAction(name = "", desc= "gets info about the host", roles= "@parent", verb = "@parent", protocol = "@parent")
  def host(format:String = "props"):Host = {
    context.app.host
  }


  @ApiAction(name = "", desc= "gets info about the start up time", roles= "@parent", verb = "@parent", protocol = "@parent")
  def start(format:String = "props"):StartInfo = {
    context.app.start
  }


  @ApiAction(name = "", desc= "gets info about the status", roles= "@parent", verb = "@parent", protocol = "@parent")
  def status(format:String = "props"):Status = {
    context.app.status
  }


  @ApiAction(name = "", desc= "gets all info", roles= "@parent", verb = "@parent", protocol = "@parent")
  def all(format:String = "props"):String = {
    SerializerUtils.asJson(context.app.info())
  }
}
