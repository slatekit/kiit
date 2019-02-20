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

import slatekit.common.TODO
import slatekit.common.Failure
import slatekit.common.IO
import slatekit.common.ResultMsg
import slatekit.common.Success
import slatekit.common.conf.Conf
import slatekit.common.http.*
import slatekit.common.log.Logs
import slatekit.common.results.ResultFuncs
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Google FCM ( Fire base Cloud Messaging ) Service.
 * Provides a simple interface to call FCM for push notifications.
 *
 * SEE: https://firebase.google.com/docs/cloud-messaging/android/receive
 *
 *
 * URLS:
 * 1. https://fcm.googleapis.com/fcm/send      ( for modern FCM http requests )
 * 2. https://gcm-http.googleapis.com/gcm/send ( for older  GCM http requests )
 *    GCM will be deprecated on April, 2019

 *
 * MESSAGE TYPES:
 *
 *  1. Notification message
 *   {
 *       "to": "{{REG_ID_HERE}}",
 *       "notification": {
 *           "click_action" : ".MainActivity",
 *           "title" : "title v1",
 *           "text": "data v1!",
 *           "icon": "ic_launcher"
 *       }
 *   }
 *
 *   2. Data Message
 *   {
 *       "to": "{{REG_ID_HERE}}",
 *       "data": {
 *           "title" : "title v1",
 *           "text": "data v1!",
 *           "icon": "ic_launcher"
 *       }
 *   }
 *
 *   3. Notification And Data message
 *   {
 *       "to": "{{REG_ID_HERE}}",
 *       "notification": {
 *           "click_action" : ".MainActivity",
 *           "title" : "title v1",
 *           "text": "data v1!",
 *           "icon": "ic_launcher"
 *       },
 *       "data": {
 *           "title" : "title v1",
 *           "text": "data v1!",
 *           "icon": "ic_launcher"
 *       }
 *   }
 *
 *  NOTES:
 *  1. https://stackoverflow.com/questions/37711082/how-to-handle-notification-when-app-in-background-in-firebase/44150822#44150822
 *  2. https://stackoverflow.com/questions/37711082/how-to-handle-notification-when-app-in-background-in-firebase/42279260#42279260
 */
open class MessageServiceGoogle(
        _key: String,
        val config: Conf,
        val logs: Logs,
        val executor: ExecutorService ? = null,
        private val call: IO<HttpRequest, ResultMsg<Boolean>>? = null
) :
    MessageServiceBase() {

    private val _settings = MessageSettings("", _key, "")
    private val _baseUrl = config.getStringOrElse("android.sendUrl", fcmUrl)
    private val _sendNotifications = config.getBoolOrElse("android.sendNotifications", true)
    private val _logger = logs.getLogger(this.javaClass)

    /**
     * Sends a push notification to Android using the data from the Message supplied.
     */
    override fun send(msg: Message): ResultMsg<Boolean> {
        val req = buildRequest(msg)

        return if (_sendNotifications) {
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
     * @note : implement in derived class that can actually send the message
     */
    override fun sendAsync(msg: Message): Future<ResultMsg<Boolean>> {

        TODO.IMPLEMENT("ASYNC", "Figure out an async Http library to use or look at Kotlin CoRoutines")
        return exec.submit(Callable {
            send(msg)
        })
    }

    /**
     * Builds the Message to send as Push notification as an immutable HTTP Request
     * when the app is either in the background/killed, it must
     * have a notification object along w/ the data object.
     *
     * See links:
     * https://stackoverflow.com/questions/37711082/how-to-handle-notification-when-app-in-background-in-firebase/44150822#44150822
     * https://stackoverflow.com/questions/37711082/how-to-handle-notification-when-app-in-background-in-firebase/42279260#42279260
     * https://firebase.google.com/docs/cloud-messaging/android/receive
     */
    protected fun buildRequest(msg: Message): HttpRequest {

        // 1. Build "to" field
        // This correctly based on if sending to multiple devices
        // 1  = "to" : "regid1"
        // 2+ = "registration_ids" : ["regid1", "regid2" ]
        val ids = msg.to.joinToString(",") { "\"" + it + "\"" }
        val to = if (msg.isMultiDelivery) "\"registration_ids\"" else "\"to\""
        val recipient = if (msg.isMultiDelivery) "[$ids]" else ids
        val alert = msg.alert?.let { buildAlert(it) } ?: ""

        // 2. Build the content
        // This depends on if your sending a "notification" | "data" message or both.
        // Notifications only showup in the notification area on android.
        // Data messages will be handled in the app.
        // Use both for when an app is closed/backgrounded.
        val content = when (msg.messageType) {
            is MessageTypeData -> "{$to:$recipient, \"data\":${msg.payload}}"
            is MessageTypeAlert -> "{$to:$recipient, \"notification\":$alert}"
            is MessageTypeBoth -> "{$to:$recipient, \"notification\":$alert, \"data\":${msg.payload}}"
            else -> "{$to:$recipient, \"notification\":$alert}"
        }

        // 3. Build immutable http request.
        // Note: For now this is using the simple slatekit httprequest and
        // synchronous http call. Later on this will be converted to one of
        // a. the java async http libraries
        // b. apache commons http async
        // c. kotlin http async libraries
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
    private fun sendSync(req: HttpRequest): ResultMsg<Boolean> {
        val res = HttpClient.post(req)
        val result = if (res.is2xx) ResultFuncs.success(true, msg = res.result?.toString() ?: "")
        else Failure("error sending sms to ${req.url}")
        return result
    }

    private fun buildAlert(alert: Notification): String {
        return """{
            "click_action" : "${alert.click_action}",
            "title" : "${alert.title.replace("\"", "\\\"")}",
            "text": "${alert.text.replace("\"", "\\\"")}",
            "icon": "${alert.icon.replace("\"", "\\\"")}"
        }"""
    }

    companion object {

        @JvmStatic
        val fcmUrl = "https://fcm.googleapis.com/fcm/send"
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
