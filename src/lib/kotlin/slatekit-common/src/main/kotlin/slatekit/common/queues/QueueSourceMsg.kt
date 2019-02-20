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

package slatekit.common.queues

interface QueueSourceMsg<T> : QueueSource<T> {

    /**
     * Get the message body of the entry
     */
    fun getMessageBody(msgItem: T?): String


    /**
     * Gets the message tag/attribute from metadata
     */
    fun getMessageTag(msgItem: T?, tagName: String): String
}
