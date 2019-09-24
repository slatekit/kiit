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
    fun <T> run(worker: Worker<T>): Try<Status> {
        val result = Tries.attempt {
            worker.transition(Status.Starting, Track.all)
            worker.init()

            worker.transition(Status.Running, Track.all)
            worker.work()

            worker.transition(Status.Complete, Track.all)
            worker.end()
            Status.Complete
        }
        handleResult<T>(worker, result)
        return result
    }


    /**
     * Starts this worker with life-cycle hooks and automatic transitioning to proper state
     * However, allows execution to be managed externally as it could be running for a long time
     */
    fun <T> start(worker: Worker<T>): Try<Status> {
        val result = Tries.attempt {
            worker.transition(Status.Starting, Track.all)
            worker.init()

            worker.transition(Status.Running, Track.all)
            worker.work()
            Status.Running
        }
        handleResult<T>(worker, result)
        return result
    }


    fun <T> handleResult(worker:Worker<T>, result:Try<Status>){
        when(result){
            is Success -> { }
            is Failure -> {
                worker.transition(Status.Failed, Track.all)
                worker.fail(result.error)
            }
        }
    }
}