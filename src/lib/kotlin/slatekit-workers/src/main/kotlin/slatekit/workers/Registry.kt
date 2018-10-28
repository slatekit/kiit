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

import slatekit.common.queues.QueueSourceMsg
import slatekit.workers.core.QueueInfo
import slatekit.workers.core.Utils
import java.util.*

class Registry(val sys: System) {

    /**
     * Lookup of queues to workers that can handle the queue
     */
    val queueToWorkers: Map<String, List<Worker<*>>> = Utils.toWorkerLookup(sys.queues.queues, sys.getWorkers())

    /**
     * Gets a random queue from the list of queues, factoring in the queue priority
     */
    fun getQueue(): QueueInfo {
        val queue = sys.queues.next()
        return queue
    }

    /**
     * Gets a random queue from the list of queues, factoring in the queue priority
     */
    fun getQueueAt(pos: Int): QueueInfo? {
        val queue = sys.queues.prioritizedQueues[pos]
        return queue
    }

    /**
     * Gets a batch of jobs from the next queue
     */
    fun getBatch(queueInfo: QueueInfo, size: Int): List<Job>? {
        val queue = queueInfo.queue as QueueSourceMsg
        val items = queue.nextBatch(size)
        return items?.map { item ->
            Utils.toJob(item, queueInfo, queue)
        }
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
}
