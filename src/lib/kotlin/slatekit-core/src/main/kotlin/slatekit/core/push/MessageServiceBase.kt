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
package slatekit.core.push

import slatekit.results.Notice

abstract class MessageServiceBase {

    /**
     * Sends a message as a notification message
     *
     * @param to : device/group to send to
     * @param alert : the json data to put into the "notification" portion of the notification
     * @return
     */
    fun sendAlert(to: String, alert: Notification): Notice<Boolean> {
        return sendAlert(listOf(to), alert)
    }

    /**
     * Sends a message as a notification message
     *
     * @param to : device/group to send to
     * @param alert : the json data to put into the "notification" portion of the notification
     * @return
     */
    fun sendAlert(to: List<String>, alert: Notification): Notice<Boolean> {
        val message = Message(to, MessageTypeAlert, "", alert)
        return send(message)
    }

    /**
     * Sends a message as a message
     *
     * @param to : device/group to send to
     * @param data : the json data to put into the "data" portion of the notification
     * @return
     */
    fun sendData(to: String, data: String): Notice<Boolean> {
       return sendData(listOf(to), data)
    }

    /**
     * Sends a message as a data message
     *
     * @param to : device/group to send to
     * @param data : the json data to put into the "data" portion of the notification
     * @return
     */
    fun sendData(to: List<String>, data: String): Notice<Boolean> {
        val message = Message(to, MessageTypeData, data)
        return send(message)
    }

    /**
     * Sends a message with both data and notification sections
     *
     * @param to : device/group to send to
     * @param data : the json data to put into the "data" portion of the notification
     * @param alert : the json data to put into the "notification" portion of the notification
     * @return
     */
    fun sendAlertAndData(to: List<String>, data: String, alert: Notification): Notice<Boolean> {
        val message = Message(to, MessageTypeBoth, data, alert)
        return send(message)
    }

    /**
     * Sends the message
     *
     * @param msg : message to send
     * @return
     * @note : implement in derived class that can actually send the message
     */
    abstract fun send(msg: Message): Notice<Boolean>

    /**
     * Sends the message asynchronously
     *
     * @param msg : message to send
     * @return
     * @note : implement in derived class that can actually send the message
     */
    abstract fun sendAsync(msg: Message, callback:(Notice<Boolean>) -> Unit)
}
