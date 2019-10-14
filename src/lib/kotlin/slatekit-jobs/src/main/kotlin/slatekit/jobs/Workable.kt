package slatekit.jobs

import slatekit.common.Status
import slatekit.common.StatusCheck
import slatekit.common.ids.Identity
import java.util.concurrent.atomic.AtomicReference

interface Workable<T> : StatusCheck {

    /**
     * Identity of this worker
     */
    val id: Identity


    /**
     * Stats on this worker
     */
    val stats:WorkerStats


    /**
     * Get key/value pairs representing information about this worker.
     * e.g. such as settings
     */
    fun info():List<Pair<String, String>> = listOf()


    /**
     * Life-cycle hook to allow for initialization
     */
    suspend fun init() {
    }


    /**
     * Performs the work
     * This assumes that this work manages it's own work load/queue/source
     */
    suspend fun work(): WorkState {
       return work(Task.owned)
    }


    /**
     * Performs the work
     * @param task: The task to perform.
     * NOTE: If this worker manages it's own work load/queue/source, then this task is
     * provided by the work() method and assigned Task.owned
     */
    suspend fun work(task:Task): WorkState


    /**
     * Life-cycle hook to allow for completion
     */
    suspend fun done() {
    }


    /**
     * Life-cycle hook to allow for failure
     */
    suspend fun fail(err:Throwable?) {
        notify("Errored: " + err?.message, null)
    }


    /**
     * Transition current status to the one supplied
     */
    suspend fun transition(state: Status) {
        notify(state.name, null)
    }


    /**
     * Send out notifications
     */
    fun notify(desc:String?, extra:List<Pair<String,String>>?){
    }
}