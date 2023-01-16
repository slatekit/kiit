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

package kiit.providers.aws

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.*
import kiit.common.DateTime
import kiit.common.utils.Random
import kiit.common.info.ApiLogin
import kiit.common.io.Uris
import kiit.common.ext.toStringUtc
import kiit.common.Provider
import kiit.core.queues.QueueEntry
import kiit.core.queues.QueueValueConverter
import kiit.core.queues.CloudQueue
import kiit.results.Try
import kiit.results.builders.Tries
import java.io.File
import java.io.IOException

/**
 *
 * @param credentials : The aws credentials
 * @param region : AWS Region e.g. Regions.US_EAST_1
 * @param name : Name of the SQS Queue
 * @param converter: Converts the queue entry content to a value of type T
 * @param waitTimeInSeconds: Set value between 1-20 to enable long polling
 * @see:
 * 1. https://github.com/ex-aws/ex_aws/issues/486
 * 2. https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-sqs-long-polling.html
 */
class SQS<T>(
        credentials: AWSCredentials,
        val region: Regions,
        override val name: String,
        override val converter: QueueValueConverter<T>,
        val waitTimeInSeconds: Int = 0
) : CloudQueue<T>, AwsSupport {

    private val SOURCE = "aws:sqs"
    private val sqs: AmazonSQSClient = AwsFuncs.sqs(credentials, region)
    private val queueUrl = sqs.getQueueUrl(this.name).queueUrl

    override val provider: Any = sqs


    override suspend fun init() {

    }

    override suspend fun close() {

    }

    /**Gets the total number of items in the queue
     *
     * @return
     */
    override suspend fun count(): Int {
        val count = executeSync(SOURCE, "count", rethrow = true, data = null, call = {

            val request = GetQueueAttributesRequest(queueUrl).withAttributeNames("All")
            val atts = sqs.getQueueAttributes(request).attributes

            // get count
            if (atts.containsKey("ApproximateNumberOfMessages"))
                Integer.parseInt(atts.get("ApproximateNumberOfMessages"))
            else
                0
        })
        return count ?: 0
    }


    /**Gets the next item in the queue
     *
     * @return : An message object from the underlying queue provider
     */
    override suspend fun next(): QueueEntry<T>? {
        val result = next(1)
        return result.firstOrNull()
    }


    /**Gets the next batch of items in the queue
     *
     * @param size : The number of items to get at once
     * @return : A list of message object from the underlying queue provider
     */
    override suspend fun next(size: Int): List<QueueEntry<T>> {
        val results = executeSync(SOURCE, "nextbatch", data = size, call = { ->
            val reqRaw = ReceiveMessageRequest(queueUrl)
                    .withMaxNumberOfMessages(size)
            val req1 = if (waitTimeInSeconds > 0) reqRaw.withWaitTimeSeconds(waitTimeInSeconds) else reqRaw
            val req2 = req1.withAttributeNames(QueueAttributeName.All)
            val req = req2.withMessageAttributeNames(QueueAttributeName.All.name)
            val msgs = sqs.receiveMessage(req).messages
            val entries = if (msgs.isEmpty()) listOf() else msgs.map { createEntry(it) }
            entries
        })
        return results ?: listOf()
    }

    /** Send a message using either a simple string or a map
     * contains the message data and attributes
     *
     * @param msg: String message, or map containing the fields "message", and "atts"
     */
    override suspend fun send(value: T, tagName: String, tagValue: String): Try<String> {
        return when (tagName) {
            null, "" -> send(value, mapOf("id" to Random.uuid(), "createdAt" to DateTime.now().toStringUtc()))
            else -> send(value, mapOf(tagName to tagValue, "id" to Random.uuid(), "createdAt" to DateTime.now().toStringUtc()))
        }
    }


    /**Sends the message with the attributes supplied to the queue
     *
     * @param value : The message to send
     * @param attributes : Additional attributes to put into the message
     */
    override suspend fun send(value: T, attributes: Map<String, Any>?): Try<String> {
        // Send the message, any message that fails will get caught
        // and the onError method is called for that message
        return executeResultSync<String>(SOURCE, "send", data = value, call = {
            val message = converter.encode(value) ?: ""
            val req = SendMessageRequest(queueUrl, message)

            // Add the attributes
            attributes?.let {
                req.withMessageAttributes(it.map { pair ->
                    Pair(pair.key, MessageAttributeValue().withDataType("String").withStringValue(pair.value.toString()))
                }.toMap())
            }

            val result = sqs.sendMessage(req)
            result.messageId
        })
    }


    override suspend fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): Try<String> {
        val path = Uris.interpret(fileNameLocal)
        return path?.let { pathLocal ->
            val content = File(pathLocal).readText()
            val value = converter.decode(content)
            value?.let {
                send(value, tagName, tagValue)
            } ?: kiit.results.Failure(IOException("Invalid file path: $fileNameLocal"))
        } ?: kiit.results.Failure(IOException("Invalid file path: $fileNameLocal"),
                msg = "Invalid file path: $fileNameLocal")
    }


    /** Abandons the message supplied    *
     *
     * @param entry : The message to abandon/delete
     */
    override suspend fun abandon(entry: QueueEntry<T>?):Try<QueueEntry<T>> {
        return entry?.let {
            Tries.of {
                discard(it, "abandon")
                it
            }
        } ?: Tries.invalid()
    }


    /** Completes the message by deleting it from the queue
     *
     * @param entry : The message to complete
     */
    override suspend fun done(entry: QueueEntry<T>?):Try<QueueEntry<T>> {
        return entry?.let {
            Tries.of {
                discard(it, "done")
                it
            }
        } ?: Tries.invalid()
    }


    private fun getOrDefault(map: Map<String, Any>, key: String, defaultVal: Any): Any {
        return map.getOrDefault(key, defaultVal)
    }

    /**
     * Discards the entry by deleting it from SQS
     */
    private fun discard(item: QueueEntry<T>, action: String) {
        when (item.raw) {
            is Message -> {
                executeSync(SOURCE, action, data = item, call = {
                    val message = item.raw as Message
                    val msgHandle = message.receiptHandle

                    sqs.deleteMessage(DeleteMessageRequest(queueUrl, msgHandle))
                })
            }
            else -> {
                throw Exception("Incorrect QueueEntry for AWS Queue")
            }
        }
    }


    private fun createEntry(msg: Message): QueueEntry<T> {
        val bodyAsString = msg.body
        val item = converter.decode(bodyAsString)
        val id = AwsQueueEntry.getMessageTag(msg, "id")
        val timestamp = AwsQueueEntry.getMessageTag(msg, "createdAt")
        val createdAt = if (timestamp.isNullOrEmpty()) DateTime.now() else DateTime.parse(timestamp)
        val entry = AwsQueueEntry(item, msg, id, createdAt)
        return entry
    }


    data class AwsQueueEntry<T>(
            val entry: T?,
            val message: Message,
            override val id: String = Random.uuid(),
            override val createdAt: DateTime = DateTime.now()
    ) : QueueEntry<T> {

        /**
         * This is the value itself for the default implementation
         * NOTE:  for AWS SQS, this should point it its Message model
         */
        override val raw: Any? = message


        override fun getValue(): T? {
            return entry
        }


        /**
         * Gets the named tag stored in this entry
         */
        override fun getTag(name: String): String? {
            return getMessageTag(message, name)
        }


        companion object {

            fun getMessageTag(msg: Message, tagName: String): String {
                val atts = msg.messageAttributes
                return if (atts.isEmpty() || !atts.containsKey(tagName))
                    ""
                else {
                    val tagVal = atts.get(tagName)
                    tagVal?.stringValue ?: ""
                }
            }
        }
    }

    companion object {
        fun <T> of(cls:Class<*>, region: String, name: String, apiKey: ApiLogin, converter: QueueValueConverter<T>, waitTimeInSeconds: Int = 0): Try<SQS<T>> {
            return build(region) { regions ->
                SQS<T>(AwsFuncs.credsWithKeySecret(apiKey.key, apiKey.pass), regions, name, converter, waitTimeInSeconds)
            }
        }

        fun <T> of(cls:Class<*>, region: String, name: String, converter: QueueValueConverter<T>, confPath: String? = null, confSection: String? = null, waitTimeInSeconds: Int = 0): Try<SQS<T>> {
            return build(region) { regions ->
                SQS<T>(AwsFuncs.creds(cls, confPath, confSection), regions, name, converter, waitTimeInSeconds)
            }
        }
    }
}
