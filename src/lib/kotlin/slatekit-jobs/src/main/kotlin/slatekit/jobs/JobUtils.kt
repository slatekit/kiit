package slatekit.jobs

import slatekit.common.Status
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes


object JobUtils {

    fun validate(action: JobAction, currState: Status): Boolean {
        val nextState = toState(action)
        return when(nextState) {
            null -> false
            else -> {
                val isRunning = currState.value == Status.Running.value
                val isValid = when(action) {
                    is JobAction.Start   -> !isRunning
                    is JobAction.Process -> isRunning
                    is JobAction.Control -> isRunning
                    is JobAction.Resume  -> true
                    else -> {
                        // No reason to:
                        // 1. Pause if already "Paused"
                        // 2. Stop  if already "Stopped"
                        currState != nextState
                    }
                }
                isValid
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
}