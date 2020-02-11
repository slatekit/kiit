package slatekit.jobs.support

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.Duration
import slatekit.common.DateTime
import slatekit.common.Status
import slatekit.common.Event
import slatekit.jobs.*
import slatekit.jobs.workers.Worker
import slatekit.results.Codes

object JobUtils {

    fun validate(action: JobAction, currState: Status): Boolean {
        val nextState = toState(action)
        return when (nextState) {
            null -> false
            else -> {
                val isRunning = currState.value == Status.Running.value
                val isValid = when (action) {
                    is JobAction.Start -> !isRunning
                    is JobAction.Process -> isRunning
                    is JobAction.Control -> isRunning
                    is JobAction.Resume -> true
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
        return when (action) {
            is JobAction.Start -> Status.Running
            is JobAction.Stop -> Status.Stopped
            is JobAction.Pause -> Status.Paused
            is JobAction.Resume -> Status.Running
            is JobAction.Process -> Status.Running
            is JobAction.Control -> Status.Running
            else -> null
        }
    }

    fun toEvent(started: DateTime, desc: String, target: String, worker: Worker<*>): Event {
        // Convert the worker info / state / stats into a generalized event
        val id = worker.id
        val status = worker.status()
        val calls = worker.stats.calls
        val counts = worker.stats.counts
        val now = DateTime.now()
        val duration = Duration.between(started, now).seconds
        val code = when (status) {
            is Status.InActive -> Codes.SUCCESS
            is Status.Starting -> Codes.SUCCESS
            is Status.Idle -> Codes.PENDING
            is Status.Running -> Codes.SUCCESS
            is Status.Paused -> Codes.PENDING
            is Status.Stopped -> Codes.PENDING
            is Status.Complete -> Codes.EXIT
            is Status.Failed -> Codes.ERRORED
        }
        val ev = Event(
            area = id.area,
            name = id.name,
            agent = id.agent.name,
            env = id.env,
            uuid = id.instance,
            desc = desc,
            status = code,
            target = target,
            tag = "",
            fields = listOf(
                Triple("started   ", started.toString(), ""),
                Triple("duration  ", "$duration secs", ""),
                Triple("status    ", status.name, ""),
                Triple("called    ", calls.totalRuns().toString(), ""),
                Triple("processed ", counts.totalProcessed().toString(), ""),
                Triple("succeeded ", counts.totalSucceeded().toString(), ""),
                Triple("invalid   ", counts.totalInvalid().toString(), ""),
                Triple("ignored   ", counts.totalIgnored().toString(), ""),
                Triple("errored   ", counts.totalErrored().toString(), ""),
                Triple("unexpected", counts.totalUnexpected().toString(), "")
            )
        )
        return ev
    }

    /**
     * Performs the operation if the action supplied is correct with regard to the current state.
     */
    suspend fun perform(job: Job, action: JobAction, currentState: Status, launch: Boolean, scope: CoroutineScope, operation: suspend() -> Unit) {
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
