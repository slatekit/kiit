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

package kiit.common.args

/**
 * Created by kishorereddy on 5/25/17.
 */
data class ParsedItem(val action: String, val actions: List<String>, val pos: Int, val posLast: Int)

data class ParsedArgs(
    val named: Map<String, String>,
    val meta: Map<String, String>,
    val sys: Map<String, String>,
    val ndx: Int
)