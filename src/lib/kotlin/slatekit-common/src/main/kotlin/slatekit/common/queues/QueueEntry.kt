/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.queues

import slatekit.common.DateTime
import slatekit.common.Random


/**
 * Entry item the In-Memory Queue
 * @param value     : The value representing the entry
 * @param id        : Unique Id of the message
 * @param tags      : Key / Value pairs for metadata / attributes
 * @param createdAt : Created at timestamp
 */
data class QueueEntry<T>(val value: T,
                         val id: String = Random.uuid(),
                         val tags: Map<String, Any>? = null,
                         val createdAt: DateTime = DateTime.now())