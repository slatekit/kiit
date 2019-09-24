package slatekit.jobs

import slatekit.common.Status
import slatekit.common.ids.Identity

interface Worker<T> {

    /**
     * Identity of this worker
     */
    val id: Identity


    /**
     * Current work status of this worker
     */
    fun status(): Status


    /**
     * Life-cycle hook to allow for initialization
     */
    fun init() {
    }


    /**
     * Life-cycle hook to allow for completion
     */
    fun done() {
        notify(Status.Complete.name, null)
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