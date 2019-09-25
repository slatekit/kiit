package slatekit.jobs

import slatekit.common.Status
import slatekit.common.Track
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Tries

object Workers {

    /**
     * Runs this worker with life-cycle hooks and automatic transitioning to proper state
     */
    fun <T> run(worker: FreeWorker<T>): Try<Status> {
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
    fun <T> start(worker: FreeWorker<T>): Try<WorkState> {
        val result = Tries.attempt {
            worker.transition(Status.Starting)
            worker.info().forEach { println(it) }
            worker.init()

            worker.transition(Status.Running)
            worker.work()
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
}