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
import slatekit.apis.Action
import slatekit.apis.setup.AuthModes
import slatekit.apis.setup.Protocols
import slatekit.apis.setup.Verbs
import slatekit.cmds.CommandResult
import slatekit.cmds.CommandState
import slatekit.cmds.Commands
import slatekit.common.CommonContext
import slatekit.results.Outcome

@Api(area = "infra", name = "commands", desc = "api info about the application and host",
        auth = AuthModes.keyed, roles = "admin", verb = Verbs.Auto, protocol = Protocols.All)
class CmdApi(val cmd: Commands, context: CommonContext) {

    @Action(desc = "get the number of commands available")
    fun names(): List<String> = cmd.names

    @Action(desc = "get the number of commands available")
    fun size(): Int = cmd.size

    @Action(desc = "get whether the cmds contain the cmd")
    fun exists(name: String): Boolean = cmd.contains(name)

    @Action(desc = "runs the command by its name")
    fun run(name: String): Outcome<CommandResult> = cmd.run(name)

    @Action(desc = "get the current state of the command")
    fun state(name: String): Outcome<CommandState> = cmd.state(name)
}
