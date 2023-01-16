/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 * 
  *  </kiit_header>
 */

package kiit.common.utils

/**
 * Supplements kotlin require guard method.
 */
object Require {

    /**
     * Ensures text is not empty, otherwise throws an IllegalArgumentException with the message
     *
     * @param text : The string to check
     * @param message : Message for the exception
     */
    fun requireText(text: String?, message: String) {
        require(!text.isNullOrEmpty()) { message }
    }

    /**
     * Ensures text exists in the list, otherwise throws an IllegalArgumentException with the message
     *
     * @param text : The string to check for
     * @param items : The list of items to search
     * @param message : Message for the exception
     */
    fun requireOneOf(text: String, items: List<String>, message: String) {
        require(items.contains(text)) { message }
    }

    /**
     * Ensures condition is true, otherwise throws an IllegalArgumentException with the message
     *
     * @param pos : The index position
     * @param size : The size to check against
     * @param message : Message for the exception
     */
    fun requireValidIndex(pos: Int, size: Int, message: String) {
        require(pos in 1..(size - 1)) { message }
    }
}
