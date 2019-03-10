/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.workers.core

import slatekit.common.queues.QueueEntry
import slatekit.workers.Job
import slatekit.workers.Worker
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.ExecutorService

object Utils {

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

    /**
     * Converts a message from any queue into a Job
     */
    fun toJob(entry: QueueEntry<String>, queueInfo: QueueInfo): Job {
        val id = entry.getTag("id") ?: ""
        val refId = entry.getTag("refId") ?: ""
        val task = entry.getTag("task") ?: ""
        val body = entry.getValue()?.toString() ?: ""
        val job = Job(id, queueInfo.name, task, body, refId, entry)
        return job
    }

    fun toWorkerLookup(queues: List<QueueInfo>, workers: List<Worker<*>>): Map<String, List<Worker<*>>> {

        // Get a mapping between 1 queue to all workers that can handle the queue
        // NOTE: "*" is designated as a wildcard to indicate that a worker can handle
        // item from any queue.
        return queues.map { it ->
            // Queue name -> List( worker 1, worker 2, worker 3 )
            Pair(it.name, getWorkers(it, workers))
        }.toMap()
    }

    /**
     * Gets all the workers that can handle the queue of items
     */
    fun getWorkers(queue: QueueInfo, workers: List<Worker<*>>): List<Worker<*>> {
        return workers.filter { worker ->
            worker.queues.contains(queue.name) || worker.queues.contains("*")
        }
    }
}
