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
import slatekit.common.Random.uuid
import slatekit.results.Try
import java.io.File
import java.util.concurrent.LinkedBlockingQueue

/**
 * For internal-use only for proto-typing/unit-tests
 * Refer to the AWS SQS Cloud Queue for  implementation.
 */
class QueueSourceDefault<T>(
    override val name: String = "",
    val converter: ((Any) -> Any)? = null,
    val size: Int = -1
) : QueueSource<T>, QueueSourceMsg<T> {

    private val _list = if (size <= 0) LinkedBlockingQueue<Any>() else LinkedBlockingQueue(size)
    private val _object = Object()

    override fun init() { }

    override fun count(): Int = _list.size

    override fun next(): T? {
        val result = _list.poll()
        return when(result) {
            null -> null
            else -> result as T
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun next(size: Int): List<T>? =
            if (_list.isEmpty()) null
            else {
                val results = mutableListOf<T>()
                val actualSize = Math.min(size, _list.size)
                for (ndx in 0 until actualSize) {
                    val msg = _list.poll()
                    if (msg != null) {
                        results.add(msg as T )
                    }
                }
                results.toList()
            }

    override fun send(value: T, tagName: String, tagValue: String): Try<String> {
        val id = uuid()
        val success = _list.offer(QueueEntry<T>(value, id, mapOf(tagName to tagValue)))
        return if (success) slatekit.results.Success(id) else slatekit.results.Failure(Exception("Error sending msg with $tagName"))
    }

    override fun send(value: T, attributes: Map<String, Any>): Try<String> {
        val id = Random.uuid()
        _list += QueueEntry(value, id, attributes)
        return slatekit.results.Success(id)
    }

    override fun complete(item: T?) {
        // Not implemented / needed for this type
    }

    override fun completeAll(items: List<T>?) {
        // Not implemented / needed for this type
    }

    override fun abandon(item: T?) {
        // Not implemented / needed for this type
    }

    override fun getMessageBody(msgItem: T?): String {
        return getMessageItemProperty(msgItem) { data -> data.value.toString() }
    }

    override fun getMessageTag(msgItem: T?, tagName: String): String {
        return getMessageItemProperty(msgItem) { data ->
            data.tags?.let { tags ->
                tags.get(tagName).toString()
            } ?: ""
        }
    }

    /**
     * Converts a String value into the value for the queue
     */
    override fun convert(value:String):T? {
        return null
    }

    override fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): Try<String> {
        val path = Uris.interpret(fileNameLocal)

        return path?.let { pathLocal ->
            val content = File(pathLocal).readText()
            val value = convert(content)
            value?.let {
                send(it, tagName, tagValue)
            } ?: slatekit.results.Failure(Exception("Invalid file path: $fileNameLocal"))
        } ?: slatekit.results.Failure(Exception("Invalid file path: $fileNameLocal"))
    }


    private fun getMessageItemProperty(msgItem: Any?, callback: (QueueEntry<T>) -> String): String {
        val item = msgItem?.let { item ->
            if (item is QueueEntry<*>) {
                callback(item as QueueEntry<T>)
            } else
                ""
        } ?: ""
        return item
    }

    fun discard(item: T) {
        synchronized(_object) {
            val data = item as QueueEntry<T>
            val pos = _list.indexOf(data)
            if (pos > -1) {
                _list.remove(pos)
            }
        }
    }
}
