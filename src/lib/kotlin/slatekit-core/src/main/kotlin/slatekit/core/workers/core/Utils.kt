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
package slatekit.core.workers.core

import slatekit.common.queues.QueueSourceMsg
import slatekit.core.workers.Job
import slatekit.core.workers.Worker

object Utils {

    /**
     * Converts a message from any queue into a Job
     */
    fun toJob(item:Any, queueInfo: QueueInfo, queue:QueueSourceMsg): Job {
        val id = queue.getMessageTag(item, "id")
        val task = queue.getMessageTag(item, "task")
        val body = queue.getMessageBody(item)
        val job = Job(id, queueInfo.name, task, body, item)
        return job
    }


    fun toWorkerLookup(queues:List<QueueInfo>, workers:List<Worker<*>>): Map<String, List<Worker<*>>> {

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
    fun getWorkers(queue: QueueInfo, workers:List<Worker<*>>):List<Worker<*>> {
        return workers.filter { worker ->
            worker.queues.contains(queue.name) || worker.queues.contains("*")
        }
    }
}
