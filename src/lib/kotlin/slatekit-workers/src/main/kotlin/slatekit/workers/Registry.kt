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
package slatekit.workers

import java.util.*

class Registry(val sys: System) {

    /**
     * Lookup of queues to workers that can handle the queue
     */
    val queueToWorkers: Map<String, List<Worker<*>>> = toLookup(sys.queues.queues, sys.getWorkers())

    /**
     * Gets a random queue from the list of queues, factoring in the queue priority
     */
    fun getQueue(): Queue {
        val queue = sys.queues.next()
        return queue
    }

    /**
     * Gets a random queue from the list of queues, factoring in the queue priority
     */
    fun getQueueAt(pos: Int): Queue? {
        val queue = sys.queues.prioritizedQueues[pos]
        return queue
    }

    /**
     * Gets a batch of jobs from the next queue
     */
    fun getBatch(queueInfo: Queue, size: Int): List<Job>? {
        val queue = queueInfo.queue
        val entries = queue.next(size)
        return entries?.map { entry -> Job(entry, queueInfo) }
    }

    /**
     * Gets a random worker that can handle the given queue
     */
    fun getWorker(queue: String): Worker<*>? {
        val workers = queueToWorkers[queue]
        val worker = workers?.let { all ->
            if (all.isEmpty()) null
            else if (all.size == 1) all.first()
            else {
                val available = all.filter { it.isAvailable() }
                if (available.isEmpty()) null
                else if (available.size == 1) available.first()
                else available.get(Random().nextInt(available.size))
            }
        }
        return worker
    }

    fun toLookup(queues: List<Queue>, workers: List<Worker<*>>): Map<String, List<Worker<*>>> {

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
    fun getWorkers(queue: Queue, workers: List<Worker<*>>): List<Worker<*>> {
        return workers.filter { worker ->
            worker.queues.contains(queue.name) || worker.queues.contains("*")
        }
    }
}
