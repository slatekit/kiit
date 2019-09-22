package slatekit.workers

import slatekit.common.Status
import slatekit.common.ids.Identity
import slatekit.common.queues.QueueEntry
import slatekit.common.queues.QueueSource
import slatekit.results.Try

interface WorkerBase {

    /**
     * Identity of this worker
     */
    val id:Identity


    /**
     * Current work status of this worker
     */
    fun status():Status


    /**
     * Life-cycle hook to allow for initialization
     */
    fun init() {
    }


    /**
     * Life-cycle hook to allow for completion
     */
    fun done() {
        notify(Status.Complete.name, null)
    }


    /**
     * Life-cycle hook to allow for failure
     */
    fun fail(err:Throwable?) {
        notify("Errored: " + err?.message, null)
    }


    /**
     * Transition current status to the one supplied
     */
    fun transition(state:Status) {
        notify(state.name, null)
    }


    /**
     * Send out notifications
     */
    fun notify(desc:String?, extra:List<Pair<String,String>>?){
    }
}


/**
 * Represents a type of worker that has/manages their own job queue
 * In this case, the work method is called, and it either returns
 * 1. hasMore = true  indicated it completed
 * 2. hasMore = false indicated the work method can be called again at a later point
 */
interface FreeWorker : WorkerBase {
    fun work():JobState
}



/**
 * Represents a type of worker that is given a job(s) to work on ( e.g. from a channel / queue / etc )
 */
interface JobWorker<T> : WorkerBase {
    /**
     * For batching purposes
     */
    fun work(sender: Any, batch: JobBatch) {
        val jobs = batch.jobs
        val queueSource = batch.queue.queue
        if (!jobs.isEmpty()) {
            jobs.forEach { job ->
                // Attempt to work on the job
                val result = work(job)

                // Acknowledge/Abandon
                complete(sender, queueSource, job, result)
            }
        }
    }


    /**
     * Works on the job while also handling metrics, middleware, events
     * @return
     */
    fun work(job: Job): Try<T>


    /**
     * Ensure the job is marked as completed ( e.g. with a queue, complete it )
     */
    fun complete(sender: Any, queue: QueueSource<String>, job:Job, result:Try<*>) {
        when(result.success){
            true  -> queue.complete(job.source as QueueEntry<String>)
            false -> queue.abandon(job.source  as QueueEntry<String>)
        }
    }
}