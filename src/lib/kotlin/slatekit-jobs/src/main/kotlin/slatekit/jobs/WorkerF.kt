package slatekit.jobs

import slatekit.common.Identity

/**
 * Worker that wraps a function call
 * @param id: Identity of worker
 * @param op: Operation that actually does the work
 */
class WorkerF<T>(id: Identity, val op: (suspend (Task) -> WResult)) : Worker<T>(id) {

    /**
     * Performs the work
     * @param task: The task to perform.
     * NOTE: If this worker manages it's own work load/queue/source, then this task is
     * provided by the work() method and assigned Task.owned. Otherwise, the task is
     * supplied by the @see[slatekit.jobs.Manager]
     */
    override suspend fun work(task: Task): WResult {
        return op.invoke(task)
    }
}
