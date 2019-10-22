package slatekit.jobs

import slatekit.functions.policy.Policies
import slatekit.jobs.support.Runner
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

/**
 * Responsible for executing the "work" method on a worker in various ways:
 * 1. impose  : execute with policies imposed ( middle ware   ) associated w/ worker
 * 2. record  : execute with recording ( metrics, task )
 * 3. direct  : execute directly
 * 4. resume  : execute the resume method on a pauseable
 */
class WorkExecutor(val context: WorkerContext,
                   val workWithPolicies: suspend (Task) -> Outcome<WorkResult>,
                   val resumeWithPolicies: suspend (Task) -> Outcome<WorkResult>) {


    /**
     * Executes the worker with policies if available, recorded otherwise
     */
    suspend fun execute(task:Task):Outcome<WorkResult> {
        return when(context.policies.isEmpty()) {
            true  -> direct(task)
            false -> impose(task)
        }
    }


    /**
     * Excecutes the worker directly without any metrics/policies
     */
    suspend fun direct(task:Task):Outcome<WorkResult> {
        return Outcomes.of {
            context.worker.work(task)
        }
    }


    /**
     * Executes the worker with recorded Calls
     */
    suspend fun record(task:Task):Outcome<WorkResult> {
        return Runner.record(context) {
            it.work(task)
        }
    }


    /**
     * Executes the worker with policies ( which may impose limits/restrictions/etc )
     */
    suspend fun impose(task:Task):Outcome<WorkResult> {
        return workWithPolicies(task)
    }


    /**
     * Executes the worker with recorded Calls
     */
    suspend fun resume(reason:String, task:Task):Outcome<WorkResult> {
        return resumeWithPolicies(task)
    }


    companion object {

        /**
         * Builds the executor for a worker which a
         * 1. composition of policies(middleware)
         * 2. final call to worker.work
         */
        fun of(context: WorkerContext): WorkExecutor {
            val rawWork: suspend (Task) -> Outcome<WorkResult> = { task ->
                Runner.record(context) {
                    it.work(task)
                }
            }
            val rawResume: suspend (Task) -> Outcome<WorkResult> = { task ->
                Runner.record(context) {
                    (it as Pausable).resume("", task)
                }
            }

            return when (context.policies.isEmpty()) {
                true  -> {
                    WorkExecutor(context, rawWork, rawResume)
                }
                false -> {
                    val work = Policies.chain(context.policies, rawWork)
                    val resume = Policies.chain(context.policies, rawResume)
                    WorkExecutor(context, work, resume)
                }
            }
        }
    }
}