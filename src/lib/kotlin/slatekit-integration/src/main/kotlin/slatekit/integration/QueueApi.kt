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

package slatekit.integration

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.svcs.ApiWithSupport
import slatekit.common.Doc
import slatekit.common.Result
import slatekit.common.queues.QueueSource
import slatekit.common.queues.QueueSourceMsg
import slatekit.core.common.AppContext

@Api(area = "infra", name = "queues", desc = "api info about the application and host", roles = "admin", auth = "key-roles", verb = "post", protocol = "*")
class QueueApi(val queue: QueueSource, context: AppContext) : ApiWithSupport(context) {


    @ApiAction(desc = "close the queue", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun close(): Unit {
        return queue.close()
    }


    @ApiAction(desc = "get the total items in the queue", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun count(): Int {
        return queue.count()
    }


    @ApiAction(desc = "get the next item in the queue", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun next(complete: Boolean): Any? {
        val item = queue.next()
        if (complete) {
            queue.complete(item)
        }
        return item
    }


    @ApiAction(desc = "get the next set of items in the queue", roles = "@parent", verb = "@parent", protocol = "@parent")
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
        return (queue as QueueSourceMsg).getMessageBody(msg)
    }


    @ApiAction(desc = "gets next item and saves it to file", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun nextToFile(complete: Boolean, fileNameLocal: String): Any? {
        val item = queue.next()
        if (complete) {
            queue.complete(item)
        }
        return writeToFile(item, fileNameLocal, 0, { m -> getContent(m) })
    }


    @ApiAction(desc = "gets next set of items and saves them to files", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun nextBatchToFiles(size: Int = 10, complete: Boolean, fileNameLocal: String): List<String?> {
        val items = queue.nextBatch(size)
        val result = items?.let { all ->
            all.mapIndexed { index, any -> writeToFile(all[index], fileNameLocal, index, { m -> getContent(m) }) }
        } ?: listOf("No items available")
        return result
    }


    @ApiAction(desc = "sends a message to the queue", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun send(msg: String, tagName: String = "", tagValue: String = ""): Result<String> {
        return queue.send(msg, tagName, tagValue)
    }


    @ApiAction(desc = "sends a message to queue using content from file", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun sendFromFile(uri: String, tagName: String = "", tagValue: String = ""): Result<String> {
        return queue.sendFromFile(uri, tagName, tagValue)
    }


    @ApiAction(desc = "sends a message to queue using content from file", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun sendFromDoc(doc: Doc, tagName: String = "", tagValue: String = ""): Result<String> {
        return queue.send(doc.content, tagName, tagValue)
    }
}
