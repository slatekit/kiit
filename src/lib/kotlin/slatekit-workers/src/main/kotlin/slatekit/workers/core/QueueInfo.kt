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

import slatekit.common.queues.QueueSource

/**
 * Wraps the underlying queue holding the messages with other metadata ( name, priority )
 * The metadata can be extended in the future
 *
 * @param name : Name of the queue ( e.g. "notifications" )
 * @param priority : Priority of the queue ( low, medium, high ) for weighted selection
 * @param queue : The actual queue source / implementation
 */
data class QueueInfo(val name: String, val priority: Priority, val queue: QueueSource<String>)
