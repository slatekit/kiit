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

import org.json.simple.JSONObject
import slatekit.common.DateTime


/**
 * @param meta      : Metadata about the message
 * @param sender    : Information about the sender
 * @param recipient : Information about the recipient
 * @param content   : Metadata about the content / hints
 * @param data      : The actual dynamic payload of the message
 */
data class Message(
    val to: List<String>,
    val messageType:MessageType = MessageTypeData,
    val payload: String = ""
) {

    fun isAlert(): Boolean = messageType == MessageTypeAlert
    fun isData(): Boolean = messageType == MessageTypeData

}

