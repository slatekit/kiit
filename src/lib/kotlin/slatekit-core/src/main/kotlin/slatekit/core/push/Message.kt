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



/**
 * @param to          : Metadata about the message
 * @param messageType : Information about the sender
 * @param data        : The "data" part of the message. in android the "data" section
 * @param alert       : The "alert" part of the message. in android the "notification" section
 */
data class Message(
    val to: List<String>,
    val messageType:MessageType = MessageTypeData,
    val payload: String = "",
    val alert: Notification? = null
) {

    val isMultiDelivery:Boolean = to.size > 1
    fun isAlert(): Boolean = messageType == MessageTypeAlert
    fun isData(): Boolean = messageType == MessageTypeData
    fun isBoth(): Boolean = messageType == MessageTypeBoth

}

