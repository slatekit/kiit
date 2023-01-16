/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 * 
  *  </kiit_header>
 */

package kiit.integration.apis

import kiit.apis.Api
import kiit.apis.Action
import kiit.apis.AuthModes
import kiit.apis.Verbs
import kiit.apis.support.FileSupport
import kiit.context.Context
import kiit.common.Sources
import kiit.common.types.ContentFile
import kiit.common.crypto.Encryptor
import kiit.common.log.Logger
import kiit.core.queues.AsyncQueue
import kiit.results.Try

@Api(area = "cloud", name = "queues", desc = "api info about the application and host",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.ALL])
class QueueApi(val queue: AsyncQueue<String>, override val context: Context) : FileSupport {

    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()


    @Action(desc = "close the queue")
    suspend fun close() {
        return queue.close()
    }

    @Action(desc = "get the total items in the queue")
    suspend fun count(): Int {
        return queue.count()
    }

    @Action(desc = "get the next item in the queue")
    suspend fun next(complete: Boolean): Any? {
        val item = queue.next()
        if (complete) {
            queue.done(item)
        }
        return item
    }

    @Action(desc = "get the next set of items in the queue")
    suspend fun nextBatch(size: Int = 10, complete: Boolean): List<Any> {
        val items = queue.next(size)
        items?.let { all ->
            for (item in items) {
                if (complete) {
                    queue.done(item)
                }
            }
        }
        return items ?: listOf()
    }

    @Action(desc = "gets next item and saves it to file")
    suspend fun nextToFile(complete: Boolean, fileNameLocal: String): Any? {
        val item = queue.next()
        if (complete) {
            queue.done(item)
        }
        return writeToFile(item, fileNameLocal, 0) { m -> item?.getValue() ?: "" }
    }

    @Action(desc = "gets next set of items and saves them to files")
    suspend fun nextBatchToFiles(size: Int = 10, complete: Boolean, fileNameLocal: String): List<String?> {
        val items = queue.next(size)
        val result = items?.let { all ->
            val res= all.mapIndexed { index, entry ->
                val content = entry.getValue() ?: ""
                writeToFile(all[index], fileNameLocal, index) { content }
                content
            }
            res
        } ?: listOf<String?>("No items available")
        return result
    }

    @Action(desc = "sends a message to the queue")
    suspend fun send(msg: String, tagName: String = "", tagValue: String = ""): Try<String> {
        return queue.send(msg, tagName, tagValue)
    }

    @Action(desc = "sends a message to queue using content from file")
    suspend fun sendFromFile(uri: String, tagName: String = "", tagValue: String = ""): Try<String> {
        return queue.sendFromFile(uri, tagName, tagValue)
    }

    @Action(desc = "sends a message to queue using content from file")
    suspend fun sendFromDoc(doc: ContentFile, tagName: String = "", tagValue: String = ""): Try<String> {
        return queue.send(String(doc.data), tagName, tagValue)
    }
}
