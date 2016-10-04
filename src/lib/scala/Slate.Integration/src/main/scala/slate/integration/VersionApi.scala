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

import slate.core.apis.{Api, ApiAction}
import slate.core.common.svcs.ApiWithSupport

@Api(area = "sys", name = "version", desc = "api to get version information", roles= "ops", auth="key-roles", verb = "*", protocol = "*")
class VersionApi extends ApiWithSupport
{

  @ApiAction(name = "", desc= "get the version of the application", roles= "@parent", verb = "@parent", protocol = "@parent")
  def app():String = {
    context.app.about.version
  }


  @ApiAction(name = "", desc= "gets the version of java", roles= "@parent", verb = "@parent", protocol = "@parent")
  def java():String = {
    context.app.lang.version
  }


  @ApiAction(name = "", desc= "gets the version of scala", roles= "@parent", verb = "@parent", protocol = "@parent")
  def scala():String = {
    context.app.lang.versionNum
  }


  @ApiAction(name = "", desc= "gets the version of the system", roles= "@parent", verb = "@parent", protocol = "@parent")
  def host():String = {
    context.app.host.version
  }
}