/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.integration

import slate.core.apis.{Api, ApiAction}
import slate.core.apis.svcs.ApiWithSupport

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