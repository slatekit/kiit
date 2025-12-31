package kiit.tasks

import kiit.common.Identity
import kotlinx.coroutines.CoroutineScope

/**
 * Contextual information about work that a worker can perform.
 * This is given to the @see[Worker] and links the following
 * 1. Action to Worker
 * 2. Action to Queue
 */
data class WorkContext(val action: Action,
                       val scope: CoroutineScope = TaskService.scope,
                       val queue:Queue? = null) {
    val id: Identity = action.id


    fun newTask(origin:String): TaskEntry {
        return Task.of("v1", origin, queue?.name ?: "", action.fullName, "")
    }
}
