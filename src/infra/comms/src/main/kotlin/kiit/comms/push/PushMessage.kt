/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.comms.push

/**
 * @param to : Metadata about the message
 * @param pushType : Information about the sender
 * @param data : The "data" part of the message. in android the "data" section
 * @param alert : The "alert" part of the message. in android the "notification" section
 */
data class PushMessage(
    val to: List<String>,
    val pushType: PushType = PushType.Alert,
    val payload: String = "",
    val alert: Notification? = null
) {

    val isMultiDelivery: Boolean = to.size > 1
    fun isAlert(): Boolean = pushType == PushType.Alert
    fun isData(): Boolean = pushType == PushType.Data
    fun isBoth(): Boolean = pushType == PushType.Both
    fun isOther(): Boolean = !isAlert() && !isData() && !isBoth()
}
