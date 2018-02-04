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

import slatekit.common.Random
import slatekit.common.Random.stringGuid
import slatekit.common.Result
import slatekit.common.Uris
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.successOrError
import java.io.File
import java.util.concurrent.LinkedBlockingQueue


/**
 * For internal-use only for proto-typing/unit-tests
 * Refer to the AWS SQS Cloud Queue for  implementation.
 */
class QueueSourceDefault(override val name:String = "",
                         val converter:((Any) -> Any)? = null,
                         val size:Int = -1 ) : QueueSource, QueueSourceMsg {

    private val _list = if(size <= 0 ) LinkedBlockingQueue<Any>() else LinkedBlockingQueue(size)
    private val _object = Object()

    override fun init(): Unit {
    }


    override fun count(): Int = _list.size


    override fun next(): Any? = _list.poll()


    @Suppress("UNCHECKED_CAST")
    override fun <T> nextBatchAs(size: Int): List<T>? =
            nextBatch(size)?.let { all -> all.map { item -> item as T } }


    override fun nextBatch(size: Int): List<Any>? =
            if (_list.isEmpty()) null
            else {
                val results = mutableListOf<Any>()
                val actualSize = Math.min(size, _list.size)
                for (ndx in 0..actualSize - 1) {
                    val msg = _list.poll()
                    if(msg != null) {
                        results += msg
                    }
                }
                results.toList()
            }


    override fun send(msg: Any, tagName: String, tagValue: String): Result<String> {
        val id = stringGuid()
        val result = _list.offer(QueueSourceData(msg, mapOf(tagName to tagValue), id))
        return successOrError(result, id)
    }


    override fun send(message: String, attributes: Map<String, Any>): Result<String> {
        val id = Random.stringGuid()
        _list += QueueSourceData(message, attributes, id)
        return success(id)
    }


    override fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): Result<String> {
        val path = Uris.interpret(fileNameLocal)

        return path?.let { pathLocal ->
            val content = File(pathLocal).readText()
            send(content, tagName, tagValue)
        } ?: failure(msg = "Invalid file path: " + fileNameLocal)
    }


    override fun complete(item: Any?): Unit {
        // Not implemented / needed for this type
    }


    override fun completeAll(items: List<Any>?): Unit {
        // Not implemented / needed for this type
    }


    override fun abandon(item: Any?): Unit {
        // Not implemented / needed for this type
    }


    override fun getMessageBody(msgItem: Any?): String {
        return getMessageItemProperty(msgItem, { data -> data.message.toString() })
    }


    override fun getMessageTag(msgItem: Any?, tagName: String): String {
        return getMessageItemProperty(msgItem, { data ->
            data.tags?.let { tags ->
                tags.get(tagName).toString()
            } ?: ""
        })
    }


    fun getMessageItemProperty(msgItem: Any?, callback: (QueueSourceData) -> String): String {
        val item = msgItem?.let { item ->
            if (item is QueueSourceData) {
                callback(item)
            }
            else
                ""
        } ?: ""
        return item
    }


    fun discard(item: Any): Unit {
        synchronized(_object, {
            val data = item as QueueSourceData
            val pos = _list.indexOf(data)
            if (pos > -1) {
                _list.remove(pos)
            }
        })
    }
}
