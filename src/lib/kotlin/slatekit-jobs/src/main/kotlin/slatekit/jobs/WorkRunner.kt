package slatekit.jobs

import slatekit.common.DateTime
import slatekit.common.Status
import slatekit.common.Track
import slatekit.results.*
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries

object WorkRunner {

    /**
     * Runs this worker with life-cycle hooks and automatic transitioning to proper state
     */
    fun <T> run(worker: Worker<T>): Try<Status> {
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
    fun <T> attemptStart(worker: Worker<T>, handleDone:Boolean = true, handleFailure:Boolean = true): Try<WorkState> {
        val result = Tries.attempt {
            start(worker, handleDone)
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
    fun <T> start(worker: Worker<T>, handleDone:Boolean): WorkState {
        worker.transition(Status.Starting)
        worker.info().forEach { println(it) }
        worker.init()

        worker.transition(Status.Running)
        val state = worker.work()
        if(state == WorkState.Done && handleDone) {
            worker.transition(Status.Complete)
            worker.done()
        }
        return state
    }


    /**
     * Makes the worker work ( this can be used for resuming )
     */
    fun <T> work(worker: Worker<T>): Try<WorkState> {
        val result = Tries.attempt {
            worker.transition(Status.Running)
            worker.transition(Status.Running)
            val state = worker.work()
            if(state == WorkState.Done) {
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



    suspend fun <T> record(worker: Worker<*>, operation: suspend (Worker<*>) -> T):Outcome<T> {
        worker.stats.lastRunTime.set(DateTime.now())
        worker.stats.totalRuns.incrementAndGet()
        return try {
            val result = operation(worker)
            worker.stats.totalRunsPassed.incrementAndGet()
            Outcomes.success(result)
        } catch (ex:Exception){
            worker.stats.totalRunsFailed.incrementAndGet()
            worker.stats.lasts.unexpected(Task.empty, Err.of(ex))
            Outcomes.errored(ex)
        }
    }
}