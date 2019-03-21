package slatekit.workers

import slatekit.common.utils.Pager
import slatekit.common.DateTime
import slatekit.common.TODO
import slatekit.common.Status
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

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
    private val log = sys.ctx.logs.getLogger("slatekit.workers.system")
    private val metrics = sys.metrics
    private val waitTimes = Pager(sys.settings.exponentialWaitTimes, true)

    // # threads = # cores
    private val threads = Runtime.getRuntime().availableProcessors()
    // TODO: What should be a preferred queue size ?
    private val queueSize = threads * 3
    private val executor = newFixedThreadPoolWithQueueSize(threads, queueSize)
    private val threadPool = executor as ThreadPoolExecutor
    private var queuePos = 0

    override fun manage(sys: System) {

        var state = sys.getState()

        // This could have been paused/stopped
        // and therefore could be resumed/started later.
        // RunStateComplete is the only state that allows
        // this code to keep going
        while (state != Status.Complete) {

            // Same as not paused / stopped, so proceed to
            // run the workers.
            if (state == Status.Running) {

                if (threadPool.queue.size < queueSize) {

                    val queuePosition = getQueueIndex()
                    val queue = sys.queues.prioritizedQueues[queuePosition]
                    val batch = getJobBatch(queuePosition)
                    val worker = getWorker(queue)
                    batch?.let {
                        worker?.let {
                            if (!batch.isEmpty) {
                                executor.submit {
                                    process(batch, worker)
                                }
                            }
                        }
                    }
                }
            }

            // Enable pause ?
            if (sys.settings.pauseBetweenCycles) {
                TODO.IMPLEMENT("workers", "Use Kotlin CoRoutines for non-blocking delay") {
                    val pauseTimeSeconds = waitTimes.next()
                    log.info("pausing for $pauseTimeSeconds")
                    Thread.sleep(waitTimes.next() * 1000L)
                }
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
    private fun getJobBatch(queuePosition: Int): JobBatch? {

        // 1. Get the next queue to process
        val queueOpt = registry.getQueueAt(queuePosition)

        return queueOpt?.let { queue ->

            // 2. Get jobs in batches
            val jobs = registry.getBatch(queue, jobBatchSize)

            // 3. Any ?
            if (jobs != null && !jobs.isEmpty()) {
                log.info("No jobs for queue: ${queue.name}")
                JobBatch(jobs, queue, DateTime.now())
            } else {
                log.info("Got jobs from queue: ${queue.name} : ${jobs?.size ?: 0}")
                JobBatch(listOf(), queue, DateTime.now())
            }
        }
    }

    /**
     * Gets the next worker that can handle jobs from the supplied queue
     */
    private fun getWorker(queue: Queue): Worker<*>? {
        return registry.getWorker(queue.name)
    }


    private fun process(batch: JobBatch, worker: Worker<*>) {
        perform(worker) {
            worker.work(this, batch)
        }
    }


    /**
     * Wraps an operation with useful logging indicating starting/completion of action
     */
    val perform = { worker: Worker<*>, action: () -> Unit ->
        val name = worker.about.name
        log.info("trigger worker $name starting")
        metrics.count("worker.$name", null)
        action()
    }


    companion object {
        /**
         * @see https://stackoverflow.com/questions/2265869/elegantly-implementing-queue-length-indicators-to-executorservices
         * @see https://stackoverflow.com/questions/2247734/executorservice-standard-way-to-avoid-to-task-queue-getting-too-full
         */
        fun newFixedThreadPoolWithQueueSize(nThreads: Int, queueSize: Int): ExecutorService {
            return ThreadPoolExecutor(
                    nThreads, nThreads,
                    5000L, TimeUnit.MILLISECONDS,
                    ArrayBlockingQueue(queueSize, true), ThreadPoolExecutor.CallerRunsPolicy()
            )
        }
    }
}
