package kiit.tasks

import kiit.common.DateTimes
import kiit.results.Err
import kiit.results.Outcome
import kiit.results.builders.Outcomes
import kotlin.reflect.KCallable



data class ExecutionResult(val stats: Stats, val result : Outcome<*>)


class Executor {


    /**
     * Execute the action's operation/function, using the task applied.
     * The return type of the operation is required to be an Outcome<*>
     */
    suspend fun execLambda(action: Action, task:Task, op: (suspend (Task) -> Outcome<*>)) : ExecutionResult {
        val started = DateTimes.now()
        val result = try {
            val outcome = op(task)
            val ended = DateTimes.now()
            ExecutionResult(Stats(started, ended), outcome)
        } catch (ex:Exception) {
            val outcome = Outcomes.unexpected<String>(Err.of("action=${action.fullName}", ex))
            val ended = DateTimes.now()
            println("event=TASK_FAILED, name=${task.name}, id=${task.uuid}, message=${ex.message}")
            ExecutionResult(Stats(started, ended), outcome)
        }
        return result
    }


    /**
     * Execute the action's operation/function, using the task applied.
     * The return type of the operation is required to be an Outcome<*>
     */
    suspend fun execMethod(action: Action, task:Task, method: KCallable<*>) : ExecutionResult {
        val started = DateTimes.now()
        val result = try {
            val params = arrayOf(task)
            val result = if (method.isSuspend) {
                kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn { cont ->
                    method.call(*params, cont)
                }
            } else {
                method.call(*params)
            }
            val outcome = result as Outcome<*>
            val ended = DateTimes.now()
            ExecutionResult(Stats(started, ended), outcome)
        } catch (ex:Exception) {
            println("event=TASK_FAILED, name=${task.name}, id=${task.uuid}, message=${ex.message}")
            val outcome = Outcomes.unexpected<String>(Err.of("action=${action.fullName}, message=${ex.message}", ex))
            val ended = DateTimes.now()
            ExecutionResult(Stats(started, ended), outcome)
        }
        return result
    }
}