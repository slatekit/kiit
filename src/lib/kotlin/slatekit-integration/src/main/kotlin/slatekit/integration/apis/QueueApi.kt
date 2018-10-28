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
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiWithSupport
import slatekit.common.content.Doc
import slatekit.common.queues.QueueSource

@Api(area = "cloud", name = "queues", desc = "api info about the application and host",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.all)
class QueueApi(val queue: QueueSource, override val context: slatekit.core.common.AppContext) : ApiWithSupport {

    @ApiAction(desc = "close the queue")
    fun close() {
        return queue.close()
    }

    @ApiAction(desc = "get the total items in the queue")
    fun count(): Int {
        return queue.count()
    }

    @ApiAction(desc = "get the next item in the queue")
    fun next(complete: Boolean): Any? {
        val item = queue.next()
        if (complete) {
            queue.complete(item)
        }
        return item
    }

    @ApiAction(desc = "get the next set of items in the queue")
    fun nextBatch(size: Int = 10, complete: Boolean): List<Any> {
        val items = queue.nextBatch(size)
        items?.let { all ->
            for (item in items) {
                if (complete) {
                    queue.complete(item)
                }
            }
        }
        return items ?: listOf()
    }

    fun getContent(msg: Any?): String {
        return (queue as slatekit.common.queues.QueueSourceMsg).getMessageBody(msg)
    }

    @ApiAction(desc = "gets next item and saves it to file")
    fun nextToFile(complete: Boolean, fileNameLocal: String): Any? {
        val item = queue.next()
        if (complete) {
            queue.complete(item)
        }
        return writeToFile(item, fileNameLocal, 0, { m -> getContent(m) })
    }

    @ApiAction(desc = "gets next set of items and saves them to files")
    fun nextBatchToFiles(size: Int = 10, complete: Boolean, fileNameLocal: String): List<String?> {
        val items = queue.nextBatch(size)
        val result = items?.let { all ->
            all.mapIndexed { index, any -> writeToFile(all[index], fileNameLocal, index, { m -> getContent(m) }) }
        } ?: listOf("No items available")
        return result
    }

    @ApiAction(desc = "sends a message to the queue")
    fun send(msg: String, tagName: String = "", tagValue: String = ""): slatekit.common.ResultEx<String> {
        return queue.send(msg, tagName, tagValue)
    }

    @ApiAction(desc = "sends a message to queue using content from file")
    fun sendFromFile(uri: String, tagName: String = "", tagValue: String = ""): slatekit.common.ResultEx<String> {
        return queue.sendFromFile(uri, tagName, tagValue)
    }

    @ApiAction(desc = "sends a message to queue using content from file")
    fun sendFromDoc(doc: Doc, tagName: String = "", tagValue: String = ""): slatekit.common.ResultEx<String> {
        return queue.send(doc.content, tagName, tagValue)
    }
}
