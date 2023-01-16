/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.cli

data class CliSettings(
    val argPrefix: String = "-",
    val argSeparator: String = "=",
    val enableLogging: Boolean = false,
    val enableOutput: Boolean = false,
    val enableStartLog: Boolean = false
)
