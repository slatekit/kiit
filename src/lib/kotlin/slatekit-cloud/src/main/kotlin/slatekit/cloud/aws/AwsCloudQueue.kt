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

package slatekit.cloud.aws

import slatekit.common.TODO
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.*
import slatekit.common.security.ApiLogin
import slatekit.common.Failure
import slatekit.common.ResultEx
import slatekit.common.Uris
import slatekit.core.cloud.CloudQueueBase
import java.io.File
import java.io.IOException

/**
 *
 * @param queue : Name of the SQS Queue
 * @param creds : The aws credentials
 * @param waitTimeInSeconds: Set value between 1-20 to enable long polling
 * @see:
 * 1. https://github.com/ex-aws/ex_aws/issues/486
 * 2. https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-sqs-long-polling.html
 */
class AwsCloudQueue(
    queue: String,
    creds: AWSCredentials,
    val waitTimeInSeconds: Int = 0
) : CloudQueueBase(), AwsSupport {

    private val _queue = queue
    private val _sqs: AmazonSQSClient = AwsFuncs.sqs(creds)
    private val _queueUrl = _sqs.getQueueUrl(_queue).queueUrl
    private val SOURCE = "aws:sqs"
    override val name = queue

    constructor(queue: String, apiKey: ApiLogin, waitTimeInSeconds: Int = 0) :
            this(queue, AwsFuncs.credsWithKeySecret(apiKey.key, apiKey.pass), waitTimeInSeconds)

    constructor(queue: String, confPath: String? = null, section: String? = null, waitTimeInSeconds: Int = 0) :
            this (queue, AwsFuncs.creds(confPath, section), waitTimeInSeconds)

    /**
     * hook for any initialization
     */
    override fun init() {
    }

    /**Gets the total number of items in the queue
     *
     * @return
     */
    override fun count(): Int {
        val count = execute<Int>(SOURCE, "count", rethrow = true, data = null, call = { ->

            val request = GetQueueAttributesRequest(_queueUrl).withAttributeNames("All")
            val atts = _sqs.getQueueAttributes(request).attributes

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
    override fun next(): Any? {
        val result = nextBatch(1)
        return result.firstOrNull()
    }

    /**Gets the next batch of items in the queue
     *
     * @param size : The number of items to get at once
     * @return : A list of message object from the underlying queue provider
     */
    override fun nextBatch(size: Int): List<Any> {
        val results = execute<List<Any>>(SOURCE, "nextbatch", data = size, call = { ->
            val reqRaw = ReceiveMessageRequest(_queueUrl)
                .withMaxNumberOfMessages(size)
            val req1 = if (waitTimeInSeconds > 0) reqRaw.withWaitTimeSeconds(waitTimeInSeconds) else reqRaw
            val req2 = req1.withAttributeNames(QueueAttributeName.All)
            val req = req2.withMessageAttributeNames(QueueAttributeName.All.name)
            val msgs = _sqs.receiveMessage(req).messages
            if (msgs.isNotEmpty() && msgs.size > 0) {
                val results = mutableListOf<Any>()
                for (ndx in 0..msgs.size - 1) {
                    val msg = msgs[ndx]
                    results += msg
                }
                results.toList()
            } else
                listOf()
        })
        return results ?: listOf()
    }

    /** Send a message using either a simple string or a map
     * contains the message data and attributes
     *
     * @param msg: String message, or map containing the fields "message", and "atts"
     */
    override fun send(msg: Any, tagName: String, tagValue: String): ResultEx<String> {
        val msgResult = when (msg) {
            is String -> {
                // Send the message, any message that fails will get caught
                // and the onError method is called for that message
                executeResult<String>(SOURCE, "send", data = "", call = { ->
                    val message = msg as String
                    val req = if (!tagName.isNullOrEmpty()) {
                        val finalTagValue = if (tagValue.isNullOrEmpty()) "" else tagValue
                        val req = SendMessageRequest(_queueUrl, message)
                                .addMessageAttributesEntry(tagName, MessageAttributeValue()
                                        .withDataType("String").withStringValue(finalTagValue))
                        req
                    } else {
                        SendMessageRequest(_queueUrl, message)
                    }
                    val result = _sqs.sendMessage(req)
                    result.messageId
                })
            }
            is Map<*, *> -> {
                val map = msg as Map<String, Any>
                val message = getOrDefault(map, "message", "") as String
                val atts = getOrDefault(map, "attributes", mapOf<String, Any>()) as Map<String, Any>
                send(message, atts)
            }
            else -> {
                Failure(Exception("Unknown message type"), msg = "unknown message type")
            }
        }
        return msgResult
    }

    /**Sends the message with the attributes supplied to the queue
     *
     * @param message : The message to send
     * @param attributes : Additional attributes to put into the message
     */
    override fun send(message: String, attributes: Map<String, Any>): ResultEx<String> {
        // Send the message, any message that fails will get caught
        // and the onError method is called for that message
        return executeResult<String>(SOURCE, "send", data = message, call = { ->
            val req = SendMessageRequest(_queueUrl, message)

            // Add the attributes
            req.withMessageAttributes(attributes.map { it ->
                Pair(it.key, MessageAttributeValue().withDataType("String").withStringValue(it.value.toString()))
            }.toMap())

            val result = _sqs.sendMessage(req)
            result.messageId
        })
    }

    override fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): ResultEx<String> {
        val path = Uris.interpret(fileNameLocal)
        return path?.let { pathLocal ->
            val content = File(pathLocal).readText()
            send(content, tagName, tagValue)
        } ?: Failure(IOException("Invalid file path: $fileNameLocal"),
                msg = "Invalid file path: $fileNameLocal")
    }

    /** Abandons the message supplied    *
     *
     * @param item : The message to abandon/delete
     */
    override fun abandon(item: Any?) {
        item?.let { i ->
            discard(i, "abandon")
        }
    }

    /** Completes the message by deleting it from the queue
     *
     * @param item : The message to complete
     */
    override fun complete(item: Any?) {
        item?.let { i ->
            discard(i, "complete")
        }
    }

    /** Completes the message by deleting it from the queue
     *
     * @param items : The messages to complete
     */
    override fun completeAll(items: List<Any>?) {
        items?.let { all ->
            all.forEach { item -> discard(item, "complete") }
        }
    }

    override fun getMessageBody(msgItem: Any?): String {
        return getMessageItemProperty(msgItem, { item -> item.body })
    }

    override fun getMessageTag(msgItem: Any?, tagName: String): String {
        return getMessageItemProperty(msgItem, { sqsMsg ->
            val atts = sqsMsg.messageAttributes
            if (atts.isEmpty() || !atts.containsKey(tagName))
                ""
            else {
                val tagVal = atts.get(tagName)
                tagVal?.stringValue ?: ""
            }
        })
    }

    private fun getOrDefault(map: Map<String, Any>, key: String, defaultVal: Any): Any {
        return map.getOrDefault(key, defaultVal)
    }

    private fun discard(item: Any, action: String) {
        when (item) {
            is Message -> {
                execute(SOURCE, action, data = item, call = { ->
                    val message = item as Message
                    val msgHandle = message.receiptHandle

                    _sqs.deleteMessage(DeleteMessageRequest(_queueUrl, msgHandle))
                })
            }
            else -> {
                TODO.IMPLEMENT("AWS", "Provide some callback/notification mechanism")
            }
        }
    }

    fun getMessageItemProperty(msgItem: Any?, callback: (Message) -> String): String {
        return msgItem?.let { item ->
            when (item) {
                is Message -> callback(item)
                else -> ""
            }
        } ?: ""
    }

    override fun toString(item: Any?): String {
        return when (item) {
            is Message -> getMessageBody(item)
            else -> item?.toString() ?: ""
        }
    }
}
