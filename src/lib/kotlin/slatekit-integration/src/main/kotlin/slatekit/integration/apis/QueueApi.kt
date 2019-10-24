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

package slatekit.integration.apis

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Protocols
import slatekit.apis.Verbs
import slatekit.apis.support.FileSupport
import slatekit.common.Context
import slatekit.common.content.Doc
import slatekit.common.queues.QueueSource
import slatekit.results.Try

@Api(area = "cloud", name = "queues", desc = "api info about the application and host",
        auth = AuthModes.keyed, roles = "admin", verb = Verbs.Auto, protocols = Protocols.All)
class QueueApi(val queue: QueueSource<String>, override val context: Context) : FileSupport {

    @Action(desc = "close the queue")
    fun close() {
        return queue.close()
    }

    @Action(desc = "get the total items in the queue")
    fun count(): Int {
        return queue.count()
    }

    @Action(desc = "get the next item in the queue")
    fun next(complete: Boolean): Any? {
        val item = queue.next()
        if (complete) {
            queue.complete(item)
        }
        return item
    }

    @Action(desc = "get the next set of items in the queue")
    fun nextBatch(size: Int = 10, complete: Boolean): List<Any> {
        val items = queue.next(size)
        items?.let { all ->
            for (item in items) {
                if (complete) {
                    queue.complete(item)
                }
            }
        }
        return items ?: listOf()
    }

    @Action(desc = "gets next item and saves it to file")
    fun nextToFile(complete: Boolean, fileNameLocal: String): Any? {
        val item = queue.next()
        if (complete) {
            queue.complete(item)
        }
        return writeToFile(item, fileNameLocal, 0) { m -> item?.getValue() ?: "" }
    }

    @Action(desc = "gets next set of items and saves them to files")
    fun nextBatchToFiles(size: Int = 10, complete: Boolean, fileNameLocal: String): List<String?> {
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
    fun send(msg: String, tagName: String = "", tagValue: String = ""): Try<String> {
        return queue.send(msg, tagName, tagValue)
    }

    @Action(desc = "sends a message to queue using content from file")
    fun sendFromFile(uri: String, tagName: String = "", tagValue: String = ""): Try<String> {
        return queue.sendFromFile(uri, tagName, tagValue)
    }

    @Action(desc = "sends a message to queue using content from file")
    fun sendFromDoc(doc: Doc, tagName: String = "", tagValue: String = ""): Try<String> {
        return queue.send(doc.content, tagName, tagValue)
    }
}
