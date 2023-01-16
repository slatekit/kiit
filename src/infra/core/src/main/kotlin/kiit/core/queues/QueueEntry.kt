/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package kiit.core.queues

import slatekit.common.DateTime


/**
 * Entry item the In-Memory Queue
 * @param value     : The value representing the entry
 * @param id        : Unique Id of the message
 * @param tags      : Key / Value pairs for metadata / attributes
 * @param createdAt : Created at timestamp
 */
interface QueueEntry<T> {

    /**
     * Id of the entry ( e.g. uuid )
     */
    val id: String


    /**
     * Underlying raw value ( could be the raw queue model of the provider )
     */
    val raw: Any?


    /**
     * Timestamp when it was created
     */
    val createdAt: DateTime


    /**
     * Gets the value stored in this entry
     */
    fun getValue(): T?


    /**
     * Gets the named tag stored in this entry
     * NOTE: Most queues have the concept of tags as key/value pairs
     */
    fun getTag(name: String): String?
}