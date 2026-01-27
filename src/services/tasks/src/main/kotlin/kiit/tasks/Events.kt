package kiit.tasks

import kiit.utils.events.Emitter

class Events(val actions: Emitter<Action> = Emitter<Action>(),
             val workers: Emitter<Worker> = Emitter<Worker>(),
             val queuing: Emitter<Task>   = Emitter<Task>(),
             val results: Emitter<ActionResult> = Emitter<ActionResult>()) {
    
    suspend fun onAction(listener: suspend (Action) -> Unit) {
        actions.on(EVENT_ACTION_CHANGE, listener)
    }

    suspend fun onQueued(listener: suspend (Task) -> Unit) {
        queuing.on(EVENT_QUEUED_INSERT, listener)
    }


    suspend fun onWorker(listener: suspend (Worker) -> Unit) {
        workers.on(EVENT_WORKER_CHANGE, listener)
    }


    suspend fun onResult(listener: suspend (ActionResult) -> Unit) {
        results.on(EVENT_RESULT_READY, listener)
    }


    companion object {
        val EVENT_QUEUED_INSERT = "queued-insert"
        val EVENT_ACTION_CHANGE = "action-change"
        val EVENT_WORKER_CHANGE = "worker-change"
        val EVENT_RESULT_READY  = "result-ready"
    }
}


