/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Scala utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */

package slatekit.core.push

import slatekit.common.Failure
import slatekit.common.IO
import slatekit.common.ResultMsg
import slatekit.common.Success
import slatekit.common.conf.ConfigBase
import slatekit.common.http.*
import slatekit.common.log.Logger
import slatekit.common.log.Logs
import slatekit.common.results.ResultFuncs
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future


open class MessageServiceGoogle(_key: String,
                                val config:ConfigBase,
                                val logs:Logs,
                                val executor: ExecutorService ? = null,
                                private val call: IO<HttpRequest, ResultMsg<Boolean>>? = null) :
    MessageServiceBase() {

    private val _settings = MessageSettings("", _key, "")
    private val _baseUrl = config.getStringOrElse("android.sendUrl", "https://gcm-http.googleapis.com/gcm/send")
    private val _sendNotifications = config.getBoolOrElse("android.sendNotifications", true)
    private val _logger = logs.getLogger(this.javaClass)


    override fun send(msg: Message): ResultMsg<Boolean> {

        val req = buildRequest(msg)

        return if(_sendNotifications) {
            // Supplied an IO ? use that ( e.g. for custom sending )
            // Or use the default
            call?.let { call.run(req) } ?: sendSync(req)
        } else {
            _logger.warn("Push notification disabled for: ${req.url}: ${req.entity}")
            Success(true, msg = "Disabled")
        }
    }


    // TODO.IMPLEMENT("ASYNC", "Figure out an async Http library to use or look at Kotlin CoRoutines")
    private val exec = executor ?: Executors.newSingleThreadExecutor()


    /**
     * Sends the message asynchronously
     *
     * @param msg : message to send
     * @return
     * @note      : implement in derived class that can actually send the message
     */
    override fun sendAsync(msg: Message): Future<ResultMsg<Boolean>> {

        TODO.IMPLEMENT("ASYNC", "Figure out an async Http library to use or look at Kotlin CoRoutines")
        return exec.submit(Callable {
            send(msg)
        })
    }


    /**
     * Builds the Message to send as Push notification as an immutable HTTP Request
     */
    protected fun buildRequest(msg:Message): HttpRequest {

        val recipient = if(msg.to.size == 1) {
            "\"" + msg.to[0] + "\""
        } else {
            val ids = msg.to.joinToString(",")
            "[$ids]"
        }
        val content = when(msg.messageType) {
            is MessageTypeData -> "{ \"to\" : " + recipient + ", \"data\" : " + msg.payload + " }"
            else               -> "{ \"to\" : " + recipient + ", \"notification\" : " + msg.payload + " }"
        }

        // Build immutable http request.
        val req = HttpRequest(
            url = _baseUrl,
            method = HttpMethod.POST,
            params = null,
            headers = listOf(
                Pair("Content-Type", "application/json"),
                Pair("Authorization", "key=" + _settings.key)
            ),
            credentials = null,
            entity = content,
            connectTimeOut = HttpConstants.defaultConnectTimeOut,
            readTimeOut = HttpConstants.defaultReadTimeOut
        )
        return req
    }


    /**
     * Simple default for sending the request synchronously.
     * Clients should use the sendAsync method
     */
    protected fun sendSync(req: HttpRequest): ResultMsg<Boolean> {
        val res = HttpClient.post(req)
        val result = if (res.is2xx) ResultFuncs.success(true, msg = res.result?.toString() ?: "")
        else Failure("error sending sms to ${req.url}")
        return result
    }
}
/*
byte[] bytes = body.getBytes(UTF8);
HttpURLConnection conn = getConnection(url);
conn.setDoOutput(true);
conn.setUseCaches(false);
conn.setFixedLengthStreamingMode(bytes.length);
conn.setRequestMethod("POST");
conn.setRequestProperty("Content-Type", "application/json");
conn.setRequestProperty("Authorization", "key=" + key);
OutputStream out = conn.getOutputStream();
*/
