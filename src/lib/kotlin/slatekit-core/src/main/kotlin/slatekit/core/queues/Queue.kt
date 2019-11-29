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

package slatekit.core.queues

import slatekit.results.Try


/**
 * Interface for a general purpose persistent queue ( AWS, etc )
 */
interface Queue<T> {

    /**
     * Name of the queue
     */
    val name: String


    /**
     * Handles conversion to / from String to the type
     */
    val converter: QueueValueConverter<T>


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
    fun next(): QueueEntry<T>?


    /**
     * Get the next batch of items
     */
    fun next(size: Int = 10): List<QueueEntry<T>>? = null


    /**
     * Completes the item ( essentially removing it from the queue )
     * Basically an ack ( acknowledgement )
     */
    fun done(entry: QueueEntry<T>?) {}


    /**
     * Completes the items ( essentially removing it from the queue )
     * Basically an ack ( acknowledgement )
     */
    fun done(entries: List<QueueEntry<T>>?) {}


    /**
     * Removes the item from the queue
     */
    fun abandon(entry: QueueEntry<T>?) {}


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


    fun toString(item: T?): String {
        return converter.encode(item) ?: ""
    }
}
