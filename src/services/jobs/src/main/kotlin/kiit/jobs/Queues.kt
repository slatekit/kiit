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
package kiit.jobs

import java.util.*
import kiit.jobs.Queue

/**
 * Contains the collection of all the available queues and provides
 * functionality to lookup, and get the next queue while factoring in queue priority
 * NOTE: This is for future state. It was originally implemented but not used due to scope of work.
 */
class Queues(val queues: List<Queue>, applyPrioritization: Boolean = true) {

    val prioritized: List<Queue> = if (applyPrioritization) prioritize(queues) else queues
    private val lookup: Map<String, Queue> = prioritized.map { it -> it.name to it }.toMap()
    private val random = Random()

    fun size(): Int = queues.size

    /**
     * gets the next queue based on weighted priority of the queues
     */
    fun next(): Queue {
        val ndx = nextPos()
        val queue = prioritized[ndx]
        return queue
    }

    /**
     * Gets the queue at the supplied position if the position is a valid range
     */
    operator fun get(pos: Int): Queue? {
        if (pos < 0 || pos >= prioritized.size) return null
        return prioritized[pos]
    }

    /**
     * Gets the queue by name if it exists
     */
    operator fun get(name: String): Queue? {
        return lookup[name]
    }

    /**
     * gets the next queue based on weighted priority of the queues
     */
    private fun nextPos(): Int {
        val ndx = random.nextInt(prioritized.size)
        return ndx
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
        @JvmStatic
        fun prioritize(queues: List<Queue>): List<Queue> {
            val buffer = mutableListOf<Queue>()
            queues.forEach { queueInfo ->
                for (ndx in 1..queueInfo.priority.value) {
                    buffer.add(queueInfo)
                }
            }
            return buffer.toList()
        }
    }
}
