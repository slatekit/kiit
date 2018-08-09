package slatekit.core.workers

import slatekit.common.status.RunStatePending


/**
 * A manager manages a work group by
 * 1. calling the work method on idle workers
 * 2. providing workers with a work items from a queue ( if applicable )
 * 3. ensure the workers work method is done on a background thread ( ExecuteService )
 */
open class Manager (val groupName: String, val sys: System) {

    /**
     * Starts the group and continuously manages
     * the workers by calling their work method if
     * the workers are idle. This manage method is called by the System.
     * NOTE: This is open to allow derived classes to customize
     * the management ( e.g. based on priority, queues, etc )
     */
    open fun manage():Unit {
        val group = sys.get(groupName)
        group?.let{ grp ->
            grp.all.forEach{ worker ->
                if ( worker.isIdle()) {
                    sys.sendToWork( worker )
                }
            }
        }
    }
}