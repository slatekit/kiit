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

import slatekit.common.DateTime
import slatekit.workers.core.QueueInfo

/**
 * Represents a batch of jobs from a specific queue
 */
data class Batch(val queue: QueueInfo, val jobs: List<Job>, val timestamp: DateTime) {
    val isEmpty = jobs.isEmpty()
}
