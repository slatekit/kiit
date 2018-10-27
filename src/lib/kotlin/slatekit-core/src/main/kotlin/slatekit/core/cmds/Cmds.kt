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

package slatekit.core.cmds

/**
 * Command manager to run commands and get back the status of each command
 * and their last results.
 * @param cmds
 */
class Cmds(cmds: List<Cmd>) {

    /**
     * Create a lookup of command name to command
     */
    val cmdLookup = cmds.map { cmd -> cmd.name to cmd }.toMap()

    /**
     * names of commands
     */
    val names: List<String> = cmds.map { cmd -> cmd.name }

    /**
     * number of commands
     */
    val size: Int = cmdLookup.size

    /**
     * whether or not there is a command with the supplied name.
     * @param name
     * @return
     */
    fun contains(name: String): Boolean = cmdLookup.contains(name)

    /**
     * Runs the command with the supplied name
     * @param name
     * @return
     */
    fun run(name: String, args: Array<String>? = null): CmdResult {
        val result =
                if (!contains(name)) {
                    CmdFuncs.errorResult(name, "Not found")
                } else {
                    // Get result
                    cmdLookup[name]?.execute(args) ?: CmdFuncs.errorResult(name, "Not found")
                }
        return result
    }

    /**
     * Gets the state of the command with the supplied name
     * @param name
     * @return
     */
    fun state(name: String): CmdState {
        val result =
                if (!contains(name)) {
                    CmdFuncs.errorState(name, "Not found")
                } else {
                    cmdLookup[name]?.lastStatus() ?: CmdFuncs.errorState(name, "Not found")
                }
        return result
    }
}
