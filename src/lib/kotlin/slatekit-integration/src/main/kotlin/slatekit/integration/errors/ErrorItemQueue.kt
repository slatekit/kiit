package slatekit.integration.errors

import slatekit.common.Failure
import slatekit.common.ResultEx
import slatekit.common.Success
import slatekit.common.query.Query
import slatekit.common.queues.QueueSourceMsg
import slatekit.integration.common.AppEntContext
import slatekit.meta.where

class ErrorItemQueue(queueName: String = "errors", appEntContext: AppEntContext) : QueueSourceMsg {

    val svc = appEntContext.ent.getSvc<ErrorItem>(ErrorItem::class)

    override val name: String = queueName

    override fun count(): Int {
        return svc.count().toInt()
    }

    override fun next(): Any? {
        return svc.findFirst(Query().where(ErrorItem::status, "=", ErrorItemStatus.Active.value))
    }

    override fun send(msg: Any, tagName: String, tagValue: String): ResultEx<String> {
        return when (msg) {
            is ErrorItem -> create(msg)
            is String -> send(message = msg, attributes = mapOf())
            else -> Failure(Exception("Msg must be an ErrorItem"))
        }
    }

    override fun send(message: String, attributes: Map<String, Any>): ResultEx<String> {
        return Success("")
    }

    override fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): ResultEx<String> {
        return Success("")
    }

    override fun getMessageBody(msgItem: Any?): String {
        return when (msgItem) {
            is ErrorItem -> msgItem.request
            else -> ""
        }
    }

    override fun getMessageTag(msgItem: Any?, tagName: String): String {
        return when (msgItem) {
            is ErrorItem -> msgItem.request
            else -> ""
        }
    }

    private fun create(item: ErrorItem): ResultEx<String> {
        val result = svc.create(item)
        return if (result > 0) {
            Success(result.toString())
        } else {
            Failure(Exception("Error storing error message"))
        }
    }
}