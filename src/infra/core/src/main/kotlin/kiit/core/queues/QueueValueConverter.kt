package kiit.core.queues


/**
 * This is for support of type safe queues where the value
 * in the queue can only be 1 type. Such as a
 * 1. Push notification
 * 2. User Event
 * 3. Message
 *
 * However, to allow for any arbitrary structure such as a
 * JSON string, a default [QueueStringConverter] is provided
 * below.
 */
interface QueueValueConverter<T> {
    fun encode(item:T?):String?
    fun decode(content:String?):T?
}


class QueueStringConverter() : QueueValueConverter<String> {
    override fun decode(content:String?):String? = content
    override fun encode(item:String?):String? = item
}