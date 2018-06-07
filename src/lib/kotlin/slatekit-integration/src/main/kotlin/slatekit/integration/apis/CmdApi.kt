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

package slatekit.integration.apis


import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.core.cmds.CmdResult
import slatekit.core.cmds.CmdState
import slatekit.core.cmds.Cmds
import slatekit.core.common.AppContext


@Api(area = "infra", name = "commands", desc = "api info about the application and host",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.post, protocol = Protocols.all)
class CmdApi(val cmd: Cmds, context: AppContext) {

    @ApiAction(desc = "get the number of commands available")
    fun names(): List<String> = cmd.names


    @ApiAction(desc = "get the number of commands available")
    fun size(): Int = cmd.size


    @ApiAction(desc = "get whether the cmds contain the cmd")
    fun exists(name: String): Boolean = cmd.contains(name)


    @ApiAction(desc = "runs the command by its name")
    fun run(name: String): CmdResult = cmd.run(name)


    @ApiAction(desc = "get the current state of the command")
    fun state(name: String): CmdState = cmd.state(name)
}
