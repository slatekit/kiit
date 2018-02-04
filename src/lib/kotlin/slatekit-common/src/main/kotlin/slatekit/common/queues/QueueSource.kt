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

import slatekit.common.Result


interface QueueSource {

    val name:String

    fun init(): Unit {}


    fun close(): Unit {}


    fun count(): Int


    fun next(): Any?


    fun nextBatch(size: Int = 10): List<Any>? = null


    fun <T> nextBatchAs(size: Int = 10): List<T>? = null


    fun complete(item: Any?): Unit {}


    fun completeAll(items: List<Any>?): Unit {}


    fun abandon(item: Any?): Unit {}


    fun send(msg: Any, tagName: String = "", tagValue: String = ""): Result<String>


    fun send(message: String, attributes: Map<String, Any>): Result<String>


    fun sendFromFile(fileNameLocal: String, tagName: String = "", tagValue: String = ""): Result<String>


    fun toString(item: Any?): String {
        return when(item){
            is QueueSourceData -> item.message.toString()
            else       -> item?.toString() ?: ""
        }
    }
}
