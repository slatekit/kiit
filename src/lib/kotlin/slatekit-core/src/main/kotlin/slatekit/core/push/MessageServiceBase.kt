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

import slatekit.common.ResultMsg
import java.util.concurrent.Future

abstract class MessageServiceBase {

    /**
     * Sends a message as a notification
     *
     * @param  to : device/group to send to
     * @return
     */
    fun sendAlert(to:String, payload:String): ResultMsg<Boolean> {
        return sendAlert(listOf(to), payload)
    }


    /**
     * Sends a message as a notification
     *
     * @param  to : device/group to send to
     * @return
     */
    fun sendAlert(to:List<String>, payload:String): ResultMsg<Boolean> {
        val message = Message(to, MessageTypeAlert, payload)
        return send(message)
    }


    /**
     * Sends a message as a notification
     *
     * @param  to : device/group to send to
     * @return
     */
    fun sendData(to:String, payload:String): ResultMsg<Boolean> {
       return sendData(listOf(to), payload)
    }


    /**
     * Sends a message as a notification
     *
     * @param  to : device/group to send to
     * @return
     */
    fun sendData(to:List<String>, payload:String): ResultMsg<Boolean> {
        val message = Message(to, MessageTypeData, payload)
        return send(message)
    }


    /**
     * Sends the message
     *
     * @param msg : message to send
     * @return
     * @note      : implement in derived class that can actually send the message
     */
    abstract fun send(msg: Message): ResultMsg<Boolean>


    /**
     * Sends the message asynchronously
     *
     * @param msg : message to send
     * @return
     * @note      : implement in derived class that can actually send the message
     */
    abstract fun sendAsync(msg: Message): Future<ResultMsg<Boolean>>

}
