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

import slate.common.Result
import slate.core.apis.{ApiAction, Api}
import slate.core.apis.svcs.ApiWithSupport
import slate.core.cmds.{CmdResult, CmdState, Cmds}
import slate.core.common.AppContext

@Api(area = "app", name = "info", desc = "api info about the application and host", roles= "admin", auth="key-roles", verb = "post", protocol = "*")
class CmdApi ( cmd:Cmds, context:AppContext ) extends ApiWithSupport(context)
{

  @ApiAction(name = "", desc= "get the number of commands available", roles= "@parent", verb = "@parent", protocol = "@parent")
  def names():List[String] = cmd.names


  @ApiAction(name = "", desc= "get the number of commands available", roles= "@parent", verb = "@parent", protocol = "@parent")
  def size():Int = cmd.size


  @ApiAction(name = "", desc= "get whether the cmds contain the cmd", roles= "@parent", verb = "@parent", protocol = "@parent")
  def exists(name:String):Boolean = cmd.contains(name)


  @ApiAction(name = "", desc= "runs the command by its name", roles= "@parent", verb = "@parent", protocol = "@parent")
  def run(name:String):CmdResult = cmd.run(name, None)


  @ApiAction(name = "", desc= "get the current state of the command", roles= "@parent", verb = "@parent", protocol = "@parent")
  def state(name:String):CmdState = cmd.state(name)
}
