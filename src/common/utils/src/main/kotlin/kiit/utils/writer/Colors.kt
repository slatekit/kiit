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
package kiit.utils.writer

/**
 * Created by kishorereddy on 5/19/17.
 */

object Colors {

    val BLACK: String = "\u001B[30m"
    val RED: String = "\u001B[31m"
    val GREEN: String = "\u001B[32m"
    val YELLOW: String = "\u001B[33m"
    val BLUE: String = "\u001B[34m"
    val PURPLE: String = "\u001B[35m"
    val CYAN: String = "\u001B[36m"
    val WHITE: String = "\u001B[37m"
    val RESET: String = "\u001B[0m"

    fun defaults(): TextSettings = TextSettings(darkMode = true)
}
