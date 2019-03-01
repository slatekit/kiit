package slatekit.integration.errors

import slatekit.query.Query
import slatekit.common.queues.QueueSourceMsg
import slatekit.integration.common.AppEntContext
import slatekit.query.where
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try

class ErrorItemQueue(queueName: String = "errors", appEntContext: AppEntContext) : QueueSourceMsg<ErrorItem> {

    override fun convert(value: String): ErrorItem? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val svc = appEntContext.ent.getSvc<Long, ErrorItem>(ErrorItem::class)

    override val name: String = queueName

    override fun count(): Int {
        return svc.count().toInt()
    }

    override fun next(): ErrorItem? {
        return svc.findFirst(Query().where(ErrorItem::status, "=", ErrorItemStatus.Active.value))
    }

    override fun send(msg: ErrorItem, tagName: String, tagValue: String): Try<String> {
        return when (msg) {
            is ErrorItem -> create(msg)
            else -> Failure(Exception("Msg must be an ErrorItem"))
        }
    }

    override fun send(message: ErrorItem, attributes: Map<String, Any>): Try<String> {
        return Success("")
    }

    override fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): Try<String> {
        return Success("")
    }

    override fun getMessageBody(msgItem: ErrorItem?): String {
        return when (msgItem) {
            is ErrorItem -> msgItem.request
            else -> ""
        }
    }

    override fun getMessageTag(msgItem: ErrorItem?, tagName: String): String {
        return when (msgItem) {
            is ErrorItem -> msgItem.request
            else -> ""
        }
    }

    private fun create(item: ErrorItem): Try<String> {
        val result = svc.create(item)
        return if (result > 0) {
            Success(result.toString())
        } else {
            Failure(Exception("Error storing error message"))
        }
    }
}