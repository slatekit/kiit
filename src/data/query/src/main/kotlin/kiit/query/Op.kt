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


