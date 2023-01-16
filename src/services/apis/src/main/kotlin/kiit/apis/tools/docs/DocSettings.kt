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

package kiit.apis.tools.docs

data class DocSettings(
    val maxLengthApi: Int = 0,
    val maxLengthAction: Int = 0,
    val maxLengthArg: Int = 0,
    val enableDetailMode: Boolean = false
)
