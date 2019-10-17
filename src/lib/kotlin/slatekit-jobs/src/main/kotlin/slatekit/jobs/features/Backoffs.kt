package slatekit.jobs.features

import slatekit.common.paged.Pager
import slatekit.jobs.*
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success

/**
 * Rule to control the backoff/rescheduling strategy of the worker
 */
class Backoffs(val backoffs: Pager<Int>) : Strategy {

    private val _lookups = mutableMapOf<String, Pager<Int>>()

    override suspend fun check(context: JobContext, worker: Workable<*>, task: Task, state: Outcome<WorkState>): Boolean {
        return when (state) {
            is Failure -> true
            is Success -> {
                when (state.value) {
                    is WorkState.Done -> true
                    is WorkState.More -> false
                    is WorkState.Next -> {
                        val id = worker.id
                        val backoffs = if (_lookups.containsKey(id.id)) {
                            _lookups[id.id]!!
                        } else {
                            val workerBackoffs = backoffs.clone()
                            _lookups[id.id] = workerBackoffs
                            workerBackoffs
                        }
                        val next = state.value as WorkState.Next
                        if (next.processed == 0L) {
                            slatekit.common.TODO.IMPLEMENT("jobs", "implement backoff strategy")
                            backoffs.next() == 1
                        } else {
                            false
                        }
                    }
                    else -> false
                }
            }
        }
    }
}