package slatekit.core.workers



/**
 * This is the default implementation of the Manager class for now
 * The manager class is responsible for getting jobs off a queue
 * and delegating the jobs to the appropriate worker.
 * You can customize this as needed to build your own manager
 * and supply it to the system class.
 */
open class Manager (val sys: System, val registry: Registry) {

    val logger = sys.ctx.logs.getLogger(this.javaClass)


    /**
     * Manages the jobs by:
     * 1. getting the next queue
     * 2. getting jobs from the queue
     * 3. passing the job to a worker that can handle items from that queue
     *
     * NOTE: This code is run in a parallel by the runner by submitting
     * the manage method to an executor service.
     * This requires worker/queues to also be thread-safe.
     * Workers only hold metrics as state ( the metrics being Atomic counters )
     * However, more importantly, the queue ( currently AWS SQS queue ) prevents
     * other clients from obtaining the same messages ( for some x amount of time )
     * once they have been claimed, so there is some level "thread-safety" with SQS.
     */
    open fun manage() {
        val queue = registry.getQueue()
        val jobs = registry.getBatch(queue, 10)

        if (jobs == null || jobs.isEmpty()) {
            logger.info("No jobs for queue: ${queue.name}")
        } else {
            val worker = registry.getWorker(queue.name)
            val size = queue.queue.count()
            logger.info("Processing: ${queue.name}: $size, ${worker?.about?.name}")
            jobs.forEach { job ->
                worker?.let {
                    val result = worker.work(job)
                    if (result.success) {
                        queue.queue.complete(job.source)
                    } else {
                        queue.queue.abandon(job.source)
                    }
                }
            }
        }
    }
}
