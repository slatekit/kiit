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

import slatekit.common.Require.requireText
import slatekit.common.ResultMsg
import java.util.concurrent.Future

abstract class MessageServiceBase {

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


    /**
     * sends a message
     *
     * @param deviceId : destination device id
     * @param message  : message to send
     */
    fun send(deviceId: String, message: Message): ResultMsg<Boolean> {
        requireText(deviceId, "device id not provided")
        return send(message.copy(recipient = message.recipient.copy(device = deviceId)))
    }
}
