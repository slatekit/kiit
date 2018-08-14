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
package slatekit.core.workers

import slatekit.core.workers.core.QueueInfo
import java.util.*


/**
 * Contains the collection of all the available queues and provides
 * functionality to lookup, and get the next queue while factoring in queue priority
 */
class Queues(val queues:List<QueueInfo>) {

    val prioritizedQueues:List<QueueInfo> =
        prioritize(queues)
    val lookup:Map<String, QueueInfo> = prioritizedQueues.map { it -> it.name to it }.toMap()
    val random = Random()


    fun size():Int = queues.size


    /**
     * gets the next queue based on weighted priority of the queues
     */
    fun next(): QueueInfo {
        val ndx = nextRandomQueuePos()
        val queue = prioritizedQueues[ndx]
        return queue
    }


    /**
     * gets the next queue based on weighted priority of the queues
     */
    fun nextRandomQueuePos(): Int {
        val ndx = random.nextInt(prioritizedQueues.size)
        return ndx
    }


    /**
     * Gets the queue at the supplied position if the position is a valid range
     */
    operator fun get(pos:Int): QueueInfo? {
        if(pos < 0 || pos >= prioritizedQueues.size ) return null
        return prioritizedQueues[pos]
    }


    /**
     * Gets the queue by name if it exists
     */
    operator fun get(name:String): QueueInfo? {
        return lookup[name]
    }


    companion object {

        /**
         * Creates a list of queue infos that are "weighted" by priority.
         * This is done by creating more entries of the queueInfo based on the priority
         *
         * e.g.
         *
         * Given priority values
         * low :  1
         * mid :  2
         * high:  3
         *
         * ORIGINAL:
         * position : 0   1   2
         * queue    : q1  q2  q3
         * priority : 1   2   3
         *
         *
         * WEIGHTED:
         * position : 0   1   2   3   4   5
         * queue    : q1  q2  q2  q3  q3  q3
         * priority : 1   2   2   3   3   3
         *
         * Now the number of queues in the list are proportional to their priority.
         * This allows us to randomize the next queue for selection and have higher
         * priority queues have a higher probability of being selected.
         */
        fun prioritize(queues:List<QueueInfo>): List<QueueInfo> {
            val buffer = mutableListOf<QueueInfo>()
            queues.forEach { queueInfo ->
                for( ndx in 1 .. queueInfo.priority.value) {
                    buffer.add(queueInfo)
                }
            }
            return buffer.toList()
        }
    }
}
