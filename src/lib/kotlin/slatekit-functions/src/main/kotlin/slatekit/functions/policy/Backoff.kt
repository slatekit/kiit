package slatekit.functions.policy

import slatekit.common.paged.Pager
import slatekit.results.Outcome

class Backoff<I,O>(val times: Pager<Int>) : Policy<I, O> {
    override suspend fun run(i: I, operation: suspend (I) -> Outcome<O>): Outcome<O> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * Feature to control the backoff/rescheduling strategy of the worker
 */
//class Backoffs(val backoffs: Pager<Int>) : Feature {
//
//    private val _lookups = mutableMapOf<String, Pager<Int>>()
//
//    override suspend fun check(context: JobContext, worker: Workable<*>, task: Task, state: Outcome<WorkState>): Boolean {
//        return when (state) {
//            is Failure -> true
//            is Success -> {
//                when (state.value) {
//                    is WorkState.Done -> true
//                    is WorkState.More -> false
//                    is WorkState.Next -> {
//                        val id = worker.id
//                        val backoffs = if (_lookups.containsKey(id.id)) {
//                            _lookups[id.id]!!
//                        } else {
//                            val workerBackoffs = backoffs.clone()
//                            _lookups[id.id] = workerBackoffs
//                            workerBackoffs
//                        }
//                        val next = state.value as WorkState.Next
//                        if (next.processed == 0L) {
//                            slatekit.common.TODO.IMPLEMENT("jobs", "implement backoff strategy")
//                            backoffs.next() == 1
//                        } else {
//                            false
//                        }
//                    }
//                    else -> false
//                }
//            }
//        }
//    }
//}