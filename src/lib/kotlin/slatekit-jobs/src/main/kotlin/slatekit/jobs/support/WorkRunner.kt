package slatekit.jobs.support

import slatekit.common.Status
import slatekit.jobs.Task
import slatekit.jobs.WorkResult
import slatekit.jobs.Worker
import slatekit.jobs.WorkerContext
import slatekit.results.*
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries

object WorkRunner {

    /**
     * Calls this worker with life-cycle hooks and automatic transitioning to proper state
     */
    suspend fun <T> run(worker: Worker<T>): Try<Status> {
        val result = Tries.attempt {
            worker.transition(Status.Starting)
            worker.info().forEach { println(it) }
            worker.init()

            worker.transition(Status.Running)
            worker.work()

            worker.transition(Status.Complete)
            worker.done()
            Status.Complete
        }
        when(result){
            is Success -> { }
            is Failure -> {
                worker.transition(Status.Failed)
                worker.fail(result.error)
            }
        }
        return result
    }


    /**
     * Starts this worker with life-cycle hooks and automatic transitioning to proper state
     * However, allows execution to be managed externally as it could be running for a long time
     */
    suspend fun <T> attemptStart(worker: Worker<T>, handleDone:Boolean = true, handleFailure:Boolean = true, task: Task = Task.empty): Try<WorkResult> {
        val result = Tries.attempt {
            start(worker, handleDone, task)
        }
        if(handleFailure) {
            when (result) {
                is Success -> {
                }
                is Failure -> {
                    worker.transition(Status.Failed)
                    worker.fail(result.error)
                }
            }
        }
        return result
    }


    /**
     * Starts this worker with life-cycle hooks and automatic transitioning to proper state
     * However, allows execution to be managed externally as it could be running for a long time
     */
    suspend fun <T> start(worker: Worker<T>, handleDone:Boolean, task: Task = Task.empty): WorkResult {
        worker.transition(Status.Starting)
        worker.info().forEach { println(it) }
        worker.init()

        worker.transition(Status.Running)
        val state = worker.work(task)
        if(state == WorkResult.Done && handleDone) {
            worker.transition(Status.Complete)
            worker.done()
        }
        return state
    }


    /**
     * Makes the worker work ( this can be used for resuming )
     */
    suspend fun <T> work(worker: Worker<T>): Try<WorkResult> {
        val result = Tries.attempt {
            worker.transition(Status.Running)
            worker.transition(Status.Running)
            val state = worker.work()
            if(state == WorkResult.Done) {
                worker.transition(Status.Complete)
                worker.done()
            }
            state
        }
        when(result){
            is Success -> { }
            is Failure -> {
                worker.transition(Status.Failed)
                worker.fail(result.error)
            }
        }
        return result
    }


    suspend fun <T> record(context: WorkerContext, operation: suspend (Worker<*>) -> T):Outcome<T> {
        val worker: Worker<*> = context.worker
        val calls = context.stats.calls
        calls.inc()
        return try {
            val result = operation(worker)
            calls.passed()
            Outcomes.success(result)
        } catch (ex:Exception){
            calls.failed(ex)
            Outcomes.errored(ex)
        }
    }
}