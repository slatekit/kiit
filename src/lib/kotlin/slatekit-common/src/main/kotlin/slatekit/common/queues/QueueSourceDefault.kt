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
import java.io.File


/**
 * Used in Unit-Tests, for internal-use only.
 * Refer to the AWS SQS Cloud Queue for an actual implementation.
 *
 * NOTE: This should not be used in production environment.
 */
class QueueSourceDefault(val converter:((Any) -> Any)? = null  ) : QueueSource, QueueSourceMsg {

    private val _list = mutableListOf<Any>()
    private val _object = Object()

    override fun init(): Unit {
    }


    override fun count(): Int =
            synchronized(_object, {
                _list.size
            })


    override fun next(): Any? {
        val item = synchronized(_object, {
            if (_list.isEmpty()) {
                null
            }
            else {
                _list.removeAt(0)
            }
        })
        return item
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> nextBatchAs(size: Int): List<T>? =
            nextBatch(size)?.let { all -> all.map { item -> item as T } }


    override fun nextBatch(size: Int): List<Any>? =
            synchronized(_object, {
                if (_list.isEmpty()) null
                else {
                    val results = mutableListOf<Any>()
                    val actualSize = Math.min(size, _list.size)
                    for (ndx in 0..actualSize - 1) {
                        val msg = _list.removeAt(0)
                        results += msg
                    }
                    results.toList()
                }
            })


    override fun send(msg: Any, tagName: String, tagValue: String): Result<String> =
            synchronized(_object, {
                val id = stringGuid()
                _list += QueueSourceData(msg, mapOf(tagName to tagValue), id)
                success(id)
            })


    override fun send(message: String, attributes: Map<String, Any>): Result<String> =
            synchronized(_object, {
                val id = Random.stringGuid()
                _list += QueueSourceData(message, attributes, id)
                success(id)
            })


    override fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): Result<String> {
        val path = Uris.interpret(fileNameLocal)

        return path?.let { pathLocal ->
            val content = File(pathLocal).readText()
            send(content, tagName, tagValue)
        } ?: failure(msg = "Invalid file path: " + fileNameLocal)
    }


    override fun complete(item: Any?): Unit {
        item?.let { i -> discard(i) }
    }


    override fun completeAll(items: List<Any>?): Unit {
        synchronized(_object, {
            items?.let { all ->
                all.forEach { item ->
                    val pos = _list.indexOf(item)
                    _list.removeAt(pos)
                }
            }
        })
    }


    override fun abandon(item: Any?): Unit {
        item?.let { discard(it) }
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
