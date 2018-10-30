package slatekit.workers

import slatekit.common.DateTime
import slatekit.workers.status.RunStateComplete
import slatekit.workers.status.RunStateRunning
import slatekit.workers.core.QueueInfo
import slatekit.workers.core.Utils
import java.util.concurrent.ThreadPoolExecutor

/**
 * Interface to handle execution of the background workers.
 * You can implement your own runner.
 */
interface Manager {
    fun manage(sys: System)
}

/**
 * This is the default implementation of the Manager class for now
 * The manager class is responsible for getting jobs off a queue
 * and delegating the jobs to the appropriate worker.
 * You can customize this as needed to build your own manager
 * and supply it to the system class.
 */
open class DefaultManager(val sys: System, val jobBatchSize: Int = 10) : Manager {

    private val registry = Registry(sys)
    private val logger = sys.ctx.logs.getLogger("workers")

    // # threads = # cores
    private val threads = Runtime.getRuntime().availableProcessors()
    // TODO: What should be a preferred queue size ?
    private val queueSize = threads * 3
    private val executor = Utils.newFixedThreadPoolWithQueueSize(threads, queueSize)
    private val threadPool = executor as ThreadPoolExecutor
    private var queuePos = 0

    override fun manage(sys: System) {

        var state = sys.getState()

        // This could have been paused/stopped
        // and therefore could be resumed/started later.
        // RunStateComplete is the only state that allows
        // this code to keep going
        while (state != RunStateComplete) {

            // Same as not paused / stopped, so proceed to
            // run the workers.
            if (state == RunStateRunning) {

                if (threadPool.queue.size < queueSize) {

                    val queuePosition = getQueueIndex()
                    val queue = sys.queues.prioritizedQueues[queuePosition]
                    val batch = getJobBatch(queuePosition)
                    val worker = getWorker(queue)
                    batch?.let {
                        worker?.let {
                            if (!batch.isEmpty) {
                                executor.submit({
                                    process(batch, worker)
                                })
                            }
                        }
                    }
                }
            }

            // Enable pause ?
            if (sys.settings.pauseBetweenCycles) {
                Thread.sleep(sys.settings.pauseTimeInSeconds * 1000L)
            }
            state = sys.getState()
        }
    }

    /**
     * Current approach just cycles through all the weighted queues in order.
     * This ensure all queues are processed factoring in prioritization
     */
    private fun getQueueIndex(): Int {

        queuePos++
        if (queuePos >= sys.queues.prioritizedQueues.size) {
            queuePos = 0
        }
        return queuePos
    }

    /**
     * Gets the next batch of jobs from the next queue.
     *
     */
    private fun getJobBatch(queuePosition: Int): Batch? {
        val queueOpt = registry.getQueueAt(queuePosition)
        return queueOpt?.let { queue ->
            val jobs = registry.getBatch(queue, jobBatchSize)

            if (jobs != null && !jobs.isEmpty()) {
                logger.info("No jobs for queue: ${queue.name}")
                Batch(queue, jobs, DateTime.now())
            } else {
                Batch(queue, listOf(), DateTime.now())
            }
        }
    }

    /**
     * Gets the next worker that can handle jobs from the supplied queue
     */
    private fun getWorker(queue: QueueInfo): Worker<*>? {
        val worker = registry.getWorker(queue.name)
        return worker
    }

    private fun process(batch: Batch, worker: Worker<*>) {
        worker.work(batch)
    }
}
