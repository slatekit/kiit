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

package kiit.common.lex

object TokenType {
    const val None = 0
    const val Ident = 1
    const val String = 2
    const val Number = 3
    const val Boolean = 4
    const val NonAlphaNum = 5
    const val NewLine = 6
    const val End = 7
    const val ParamRef = 8
    const val Interpolated = 9
    const val Comment = 10
}
