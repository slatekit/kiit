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

package slatekit.integration


import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.svcs.ApiWithSupport
import slatekit.core.cmds.CmdResult
import slatekit.core.cmds.CmdState
import slatekit.core.cmds.Cmds
import slatekit.core.common.AppContext


@Api(area = "sys", name = "command", desc = "api info about the application and host", roles = "admin", auth = "key-roles", verb = "post", protocol = "*")
class CmdApi(val cmd: Cmds, context: AppContext) : ApiWithSupport(context) {

    @ApiAction(name = "", desc = "get the number of commands available", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun names(): List<String> = cmd.names


    @ApiAction(name = "", desc = "get the number of commands available", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun size(): Int = cmd.size


    @ApiAction(name = "", desc = "get whether the cmds contain the cmd", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun exists(name: String): Boolean = cmd.contains(name)


    @ApiAction(name = "", desc = "runs the command by its name", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun run(name: String): CmdResult = cmd.run(name)


    @ApiAction(name = "", desc = "get the current state of the command", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun state(name: String): CmdState = cmd.state(name)
}
