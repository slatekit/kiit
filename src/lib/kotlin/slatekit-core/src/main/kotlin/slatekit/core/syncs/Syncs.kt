package slatekit.core.syncs

import slatekit.common.functions.FunctionInfo
import slatekit.common.functions.Functions
import slatekit.results.builders.Notices
import slatekit.results.builders.Tries
import slatekit.results.getOrElse

/**
 * Command manager to run commands and get back the status of each command
 * and their last results.
 * @param all
 */
class Syncs(override val all: List<Sync>) : Functions<Sync> {

    /**
     * Runs the command with the supplied name
     * @param name
     * @return
     */
    fun run(name: String) {
        val sync = getOrNull(name)
        when(sync) {
            null -> Notices.errored<Int>("$name not found")
            else -> sync.force()
        }
    }

    /**
     * Gets the state of the command with the supplied name
     * @param name
     * @return
     */
    fun state(name: String): SyncState {
        val sync = getOrNull(name)
        val result = when(sync) {
            null -> Tries.errored("$name not found")
            else -> Tries.success(sync.lastStatus())
        }
        return result.getOrElse{ SyncState.empty(FunctionInfo(name, "")) }
    }
}
