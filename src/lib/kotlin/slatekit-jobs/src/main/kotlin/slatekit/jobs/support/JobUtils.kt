package slatekit.jobs.support

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import slatekit.common.Status
import slatekit.common.Identity
import slatekit.jobs.*
import slatekit.jobs.workers.WorkResult
import slatekit.results.Codes

object JobUtils {

    fun isWorker(identity: Identity):Boolean {
        return identity.tags.contains("worker")
    }


    fun validate(action: Action, currState: Status): Boolean {
        val nextState = toState(action)
        return when (nextState) {
            null -> false
            else -> {
                val isRunning = currState.value == Status.Running.value
                val isValid = when (action) {
                    is Action.Start -> !isRunning
                    is Action.Process -> isRunning
                    is Action.Check -> isRunning
                    is Action.Resume -> true
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

    fun toState(action: Action): Status? {
        return when (action) {
            is Action.Start -> Status.Running
            is Action.Stop -> Status.Stopped
            is Action.Pause -> Status.Paused
            is Action.Resume -> Status.Running
            is Action.Process -> Status.Running
            is Action.Check -> Status.Running
            else -> null
        }
    }

    fun toStatus(result:WorkResult): Status {
        return when(result) {
            is WorkResult.Next  -> Status.Running
            is WorkResult.More  -> Status.Running
            is WorkResult.Stop  -> Status.Stopped
            is WorkResult.Done  -> Status.Completed
            is WorkResult.Fail  -> Status.Failed
            is WorkResult.Delay -> Status.Paused
            else                -> Status.Running
        }
    }

    fun toCode(status: Status): slatekit.results.Status {
        return when (status) {
            is Status.InActive -> Codes.INACTIVE
            is Status.Started -> Codes.STARTING
            is Status.Waiting     -> Codes.WAITING
            is Status.Running  -> Codes.RUNNING
            is Status.Paused   -> Codes.PAUSED
            is Status.Stopped  -> Codes.STOPPED
            is Status.Completed -> Codes.COMPLETE
            is Status.Failed   -> Codes.ERRORED
            else               -> Codes.SUCCESS
        }
    }


    /**
     * Performs the operation if the action supplied is correct with regard to the current state.
     */
    suspend fun perform(job: Job, action: Action, currentState: Status, launch: Boolean, scope: CoroutineScope, operation: suspend() -> Unit) {
        // Check state move
        if (!JobUtils.validate(action, currentState)) {
            val currentStatus = job.status()
            job.error(currentStatus, "Can not handle work while job is $currentStatus")
        } else {
            if (launch) {
                scope.launch {
                    operation()
                }
            } else {
                operation()
            }
        }
    }
}
