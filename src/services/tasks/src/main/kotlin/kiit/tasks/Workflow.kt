package kiit.tasks

import kiit.results.Err
import kiit.results.Outcome
import kiit.results.builders.Outcomes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield


interface Workflow {
    val context: WorkContext
    val worker: Worker

    suspend fun process(task: Task): Outcome<Status>
}


abstract class WorkflowBase(override val context: WorkContext, override val worker: Worker) : Workflow {

    suspend fun ensure(states:List<Status>, op: suspend () -> Outcome<Status> ):Outcome<Status> {
        if(worker.isFailed()) {
            return Outcomes.errored(Err.of("Worker state = failed"))
        }
        if(worker.isCompleted()) {
            return Outcomes.errored(Err.of("Worker state = completed"))
        }
        val state = worker.context.action.status.get()
        val currentStatus = state.status
        val matched = states.firstOrNull { it == currentStatus }
        return when(matched){
            null -> Outcomes.errored(Err.of("Worker state = ${state.status.name}"))
            else -> op()
        }
    }

    protected suspend fun ensureIsAny(states:List<Status>, op: suspend () -> Unit ) {
        val state = worker.context.action.status.get()
        val currentStatus = state.status
        val matched = states.firstOrNull { it == currentStatus }
        if(matched != null) {
            op()
        }
    }

    protected suspend fun ensureIsNot(states:List<Status>, op: suspend () -> Unit ) {
        val state = worker.context.action.status.get()
        val currentStatus = state.status
        val matched = states.firstOrNull { it != currentStatus }
        if(matched != null) {
            op()
        }
    }
}