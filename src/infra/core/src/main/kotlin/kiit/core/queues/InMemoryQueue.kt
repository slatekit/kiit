/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 * 
  *  </kiit_header>
 */

package kiit.core.queues

import kiit.common.*
import kiit.common.utils.Random.uuid
import kiit.common.io.Uris
import kiit.common.utils.Random
import kiit.results.Codes
import kiit.results.Failure
import kiit.results.Success
import kiit.results.Try
import kiit.results.builders.Tries
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
 * For production usage, use the [kiit.providers.aws.aws.AwsCloudQueue]
 */
class InMemoryQueue<T>(
        override val name: String = "",
        override val converter: QueueValueConverter<T>,
        val size: Int = -1
) : Queue<T> {

    private val list = if (size <= 0) LinkedBlockingQueue<QueueEntry<T>>() else LinkedBlockingQueue(size)
    private val obj = Object()

    /**
     * Initialization hook
     */
    override fun init() {}


    /**
     * Close the queue ( allows for a shutdown hook )
     */
    override fun close() {}


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
    override fun send(value: T, attributes: Map<String, Any>?): Try<String> {
        val entry = QueueEntrySimple(value, attributes, uuid())
        list += entry
        return Success(entry.id)
    }


    /**
     * Completes the item ( removing it from the queue )
     */
    override fun done(entry: QueueEntry<T>?):Try<QueueEntry<T>> {
        return entry?.let {
            Tries.of {
                discard(it)
                it
            }
        } ?: Tries.invalid()
    }


    /**
     * Removes the item from the queue
     */
    override fun abandon(entry: QueueEntry<T>?):Try<QueueEntry<T>> {
        return done(entry)
    }


    /**
     * Sends an item from a file into the queue
     */
    override fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): Try<String> {
        val path = Uris.interpret(fileNameLocal)

        return path?.let { pathLocal ->
            val content = File(pathLocal).readText()
            val value = converter.decode(content)
            value?.let {
                send(it, tagName, tagValue)
            } ?: kiit.results.Failure(Exception("Invalid file path: $fileNameLocal"))
        } ?: kiit.results.Failure(Exception("Invalid file path: $fileNameLocal"))
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

        fun stringQueue(size:Int = -1): Queue<String> {
            return InMemoryQueue<String>("", QueueStringConverter(), size)
        }
    }
}
