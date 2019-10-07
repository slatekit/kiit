package slatekit.jobs

import slatekit.common.Status
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes


object WorkerUtils {

    fun validate(worker: Workable<*>, action: JobAction): Boolean {
        val nextState = toState(action)
        return when(nextState) {
            null -> false
            else -> {
                val currState = worker.status()
                val isRunning = currState.value == Status.Running.value
                when(action) {
                    is JobAction.Start   -> !isRunning
                    is JobAction.Process -> isRunning
                    is JobAction.Control -> isRunning
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


    fun toState(action: JobAction): Status? {
        return when(action) {
            is JobAction.Start   -> Status.Running
            is JobAction.Stop    -> Status.Stopped
            is JobAction.Pause   -> Status.Paused
            is JobAction.Resume  -> Status.Running
            is JobAction.Process -> Status.Running
            is JobAction.Control -> Status.Running
            else                  -> null
        }
    }


    suspend fun handlePausable(worker: Workable<*>, operation: suspend (Workable<*>, Outcome<Pausable>) -> Unit ) {
        when (worker) {
            is Pausable -> operation(worker, Outcomes.success(worker))
            else -> operation(worker, Outcomes.errored("Job ${worker.id.name} does not implement Pausable and can not handle a pause/stop/resume action"))
        }
    }

}