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

package kiit.query

enum class Op(val text: String) {
    Eq("="),
    Neq("<>"),
    Gt(">"),
    Gte(">="),
    Lt("<"),
    Lte("<="),
    Is("is"),
    IsNot("is not"),
    In("in")
}



enum class Logic(val text: String) {
    And("and"),
    Or("or")
}


