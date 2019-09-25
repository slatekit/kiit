package slatekit.jobs

import slatekit.common.Status
import slatekit.common.ids.Identity
import java.util.concurrent.atomic.AtomicReference

interface Workable<T> {

    /**
     * Identity of this worker
     */
    val id: Identity


    /**
     * Stats on this worker
     */
    val stats:WorkerStats


    /**
     * Current work status of this worker
     */
    fun status(): Status


    /**
     * Get key/value pairs representing information about this worker.
     * e.g. such as settings
     */
    fun info():List<Pair<String, String>> = listOf()


    /**
     * Life-cycle hook to allow for initialization
     */
    fun init() {
    }


    /**
     * Life-cycle hook to allow for completion
     */
    fun done() {
    }


    /**
     * Life-cycle hook to allow for failure
     */
    fun fail(err:Throwable?) {
        notify("Errored: " + err?.message, null)
    }


    /**
     * Transition current status to the one supplied
     */
    fun transition(state: Status) {
        notify(state.name, null)
    }


    /**
     * Send out notifications
     */
    fun notify(desc:String?, extra:List<Pair<String,String>>?){
    }
}



open class Worker<T>(override val id:Identity,
                     override val stats: WorkerStats) : Workable<T> {

    private val _status = AtomicReference<Status>(Status.InActive)


    override fun status(): Status = _status.get()
}



/**
 * A worker with a self managed work source
 */
interface FreeWorker<T> : Workable<T> {
    fun work():WorkState
}



/**
 * A worker that works using a supplied Task from Queue
 */
interface TaskWorker<T> : Workable<T> {
    fun work(task:Task):WorkState
}