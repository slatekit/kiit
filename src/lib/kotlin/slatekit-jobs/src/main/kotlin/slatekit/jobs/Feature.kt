package slatekit.jobs

import slatekit.results.Outcome
import slatekit.results.getOrElse


interface Feature {

    suspend fun check(context: JobContext, worker: Workable<*>, task: Task, state:Outcome<WorkState>):Boolean


    suspend fun process(ok:Boolean, context: JobContext, worker: Workable<*>, task: Task, state: Outcome<WorkState>,
                        operation:suspend(WorkState, Workable<*>, Task) -> WorkState ): Boolean {
        val canPerform = state.success == ok
        return if(canPerform) {
            val actual = state.getOrElse { WorkState.Unknown }
            val result = operation(actual, worker, task)
            result != WorkState.Done
        } else {
            true
        }
    }
}



class Features(val all:List<Feature>): Feature {

    override suspend fun check(context: JobContext, worker: Workable<*>, task: Task, state:Outcome<WorkState>):Boolean {
        val first = all.firstOrNull { !it.check(context, worker, task, state)  }
        return first != null
    }
}