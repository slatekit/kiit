package slatekit.jobs

import slatekit.common.Status
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes


object WorkerUtils {

    fun validate(worker: Workable<*>, action: WorkAction): Boolean {
        val nextState = toState(action)
        return when(nextState) {
            null -> false
            else -> {
                val currState = worker.status()
                val isRunning = currState.value == Status.Running.value
                when(action) {
                    is WorkAction.Start   -> !isRunning
                    is WorkAction.Process -> isRunning
                    is WorkAction.Control -> isRunning
                    else -> {
                        // No reason to:
                        // 1. Pause if already "Paused"
                        // 2. Stop  if already "Stopped"
                        val isValid = currState != nextState
                        isValid
                    }
                }
            }
        }
    }


    fun toState(action: WorkAction): Status? {
        return when(action) {
            is WorkAction.Start   -> Status.Running
            is WorkAction.Stop    -> Status.Stopped
            is WorkAction.Pause   -> Status.Paused
            is WorkAction.Resume  -> Status.Running
            is WorkAction.Process -> Status.Running
            is WorkAction.Control -> Status.Running
            else                  -> null
        }
    }


    suspend fun handlePausable(worker: Workable<*>, operation: suspend (Outcome<Pausable>) -> Unit ) {
        when(worker){
            is Pausable -> operation(Outcomes.success(worker))
            else        -> operation(Outcomes.errored("Job ${worker.id.name} does not implement Pausable and can not handle a pause/stop/resume action"))
        }
    }

}