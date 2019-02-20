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

import slatekit.results.Try


/**
 * Interface for a general purpose persistent queue ( AWS, etc )
 */
interface QueueSource<T> {

    /**
     * Name of the queue
     */
    val name: String

    /**
     * Initialization hook
     */
    fun init() {}

    /**
     * Close the queue
     */
    fun close() {}

    /**
     * Get total number of items in queue
     */
    fun count(): Int

    /**
     * Get the next item in the queue
     */
    fun next(): T?

    /**
     * Get the next batch of items
     */
    fun next(size: Int = 10): List<T>? = null

    /**
     * Completes the item ( essentially removing it from the queue )
     * Basically an ack ( acknowledgement )
     */
    fun complete(item: T?) {}

    /**
     * Completes the items ( essentially removing it from the queue )
     * Basically an ack ( acknowledgement )
     */
    fun completeAll(items: List<T>?) {}

    /**
     * Removes the item from the queue
     */
    fun abandon(item: T?) {}

    /**
     * Sends the message to the queue
     */
    fun send(value: T, tagName: String = "", tagValue: String = ""): Try<String>

    /**
     * Sends the item as a message to the queue
     */
    fun send(value: T, attributes: Map<String, Any>): Try<String>

    /**
     * Sends the item to the queue from a local file path
     */
    fun sendFromFile(fileNameLocal: String, tagName: String = "", tagValue: String = ""): Try<String>

    /**
     * Converts a String value into the value for the queue
     */
    fun convert(value:String):T?


    fun toString(item: T?): String {
        return when (item) {
            is QueueEntry<*> -> item.value.toString()
            else -> item?.toString() ?: ""
        }
    }
}
