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

import slatekit.common.*
import slatekit.common.utils.Random.uuid
import slatekit.common.io.Uris
import slatekit.common.utils.Random
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try
import java.io.File
import java.util.concurrent.LinkedBlockingQueue

/**
 * NOTE: This is NOT for PRODUCTION use !!!!!!!!!!!
 *
 * This should only be used for :
 * 1. prototyping
 * 2. unit-testing
 * 3. mocks
 *
 * For production usage, use the [slatekit.cloud.aws.AwsCloudQueue]
 */
class QueueSourceInMemory<T>(
    override val name: String = "",
    override val converter: QueueValueConverter<T>,
    val size: Int = -1
) : QueueSource<T> {

    private val list = if (size <= 0) LinkedBlockingQueue<QueueEntry<T>>() else LinkedBlockingQueue(size)
    private val obj = Object()

    /**
     * Count of items in the queue
     */
    override fun count(): Int = list.size


    /**
     * Gets the next 1 item in the queue
     */
    override fun next(): QueueEntry<T>? {
        val result = list.poll()
        return when(result) {
            null -> null
            else -> result
        }
    }


    /**
     * Gets the next X items from the queue
     */
    override fun next(size: Int): List<QueueEntry<T>>? {
        return if (list.isEmpty()) null
        else {
            val results = mutableListOf<QueueEntry<T>>()
            val actualSize = Math.min(size, list.size)
            for (ndx in 0 until actualSize) {
                val entry = list.poll()
                if (entry != null) {
                    results.add(entry)
                }
            }
            results
        }
    }


    /**
     * Sends the item to the queue, with the supplied tag name/value
     */
    override fun send(value: T, tagName: String, tagValue: String): Try<String> {
        val entry = QueueEntrySimple(value, mapOf(tagName to tagValue), uuid())
        val success = list.offer(entry)
        return when(success){
            true -> Success(entry.id)
            false -> Failure(Exception("Error sending msg with $tagName"))
        }
    }


    /**
     * Sends the item to the queue with additional message tags/attributes
     */
    override fun send(value: T, attributes: Map<String, Any>): Try<String> {
        val entry = QueueEntrySimple(value, attributes, uuid())
        list += entry
        return Success(entry.id)
    }


    /**
     * Completes the item ( removing it from the queue )
     */
    override fun complete(entry: QueueEntry<T>?) {
        entry?.let { discard(it) }
    }


    /**
     * Completes all the items ( removing them from the queue )
     */
    override fun completeAll(entries: List<QueueEntry<T>>?) {
        entries?.forEach { discard(it) }
    }


    /**
     * Removes the item from the queue
     */
    override fun abandon(entry: QueueEntry<T>?) {
        entry?.let { discard(it) }
    }


    /**
     * Sends an item from a file into the queue
     */
    override fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): Try<String> {
        val path = Uris.interpret(fileNameLocal)

        return path?.let { pathLocal ->
            val content = File(pathLocal).readText()
            val value = converter.convertFromString(content)
            value?.let {
                send(it, tagName, tagValue)
            } ?: slatekit.results.Failure(Exception("Invalid file path: $fileNameLocal"))
        } ?: slatekit.results.Failure(Exception("Invalid file path: $fileNameLocal"))
    }

    /**
     *
     */
    fun discard(entry: QueueEntry<T>) {
        synchronized(obj) {
            val pos = list.indexOf(entry)
            if (pos > -1) {
                list.remove(entry)
            }
        }
    }


    data class QueueEntrySimple<T>(
            val entry:T?,
            val tags: Map<String, Any>? = null,
            override val id: String = Random.uuid(),
            override val createdAt: DateTime = DateTime.now()
    ) : QueueEntry<T> {

        /**
         * This is the value itself for the default implementation
         * NOTE:  for AWS SQS, this should point it its Message model
         */
        override val raw: Any? = this


        override fun getValue():T? {
            return entry
        }


        override fun getTag(name:String):String? {
            return when(tags){
                null -> null
                else -> if( tags.containsKey(name) ) tags[name]?.toString()  else null
            }
        }
    }


    companion object {

        fun stringQueue(size:Int = -1):QueueSource<String> {
            return QueueSourceInMemory<String>("", QueueStringConverter(), size)
        }
    }
}
