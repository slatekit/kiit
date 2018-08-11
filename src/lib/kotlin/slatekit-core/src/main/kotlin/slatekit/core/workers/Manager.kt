package slatekit.core.workers

import slatekit.common.queues.QueueSourceMsg
import slatekit.core.workers.core.QueueInfo
import slatekit.core.workers.core.Utils
import java.util.*


/**
 * TODO: Document design goal for this class
 */
open class Manager (val sys: System) {


    val logger = sys.ctx.logs.getLogger(this.javaClass)


    /**
     * Lookup of queues to workers that can handle the queue
     */
    val queueToWorkers:Map<String, List<Worker<*>>> = Utils.toWorkerLookup(sys.queues.queues, sys.getWorkers())


    /**
     * Manages the jobs by:
     * 1. getting the next queue
     * 2. getting jobs from the queue
     * 3. passing the job to a worker that can handle items from that queue
     */
    open fun manage() {
        val queue = getQueue()
        val jobs = getBatch(queue, 10)

        if (jobs == null || jobs.isEmpty()) {
            logger.info("No jobs for queue: ${queue.name}")
        } else {
            val worker = getWorker(queue.name)
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


    /**
     * Gets a random queue from the list of queues, factoring in the queue priority
     */
    fun getQueue():QueueInfo {
        val queue = sys.queues.next()
        return queue
    }


    /**
     * Gets a batch of jobs from the next queue
     */
    fun getBatch(queueInfo: QueueInfo, size:Int):List<Job>? {
        val queue = queueInfo.queue as QueueSourceMsg
        val items = queue.nextBatch(size)
        return items?.map { item ->  Utils.toJob(item, queueInfo, queue) }
    }


    /**
     * Gets a random worker that can handle the given queue
     */
    fun getWorker(queue:String):Worker<*>? {
        val workers = queueToWorkers[queue]
        val worker = workers?.let { all ->
            if(all.isEmpty()) null
            else if(all.size == 1) all.first()
            else {
                val available = all.filter { it.isAvailable() }
                if(available.isEmpty()) null
                else if(available.size == 1) available.first()
                else available.get(Random().nextInt(available.size))
            }
        }
        return worker
    }
}
