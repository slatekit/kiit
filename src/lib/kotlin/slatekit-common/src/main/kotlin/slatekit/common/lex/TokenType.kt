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

package slatekit.common.lex

object TokenType {

    val None = 0
    val Ident = 1
    val String = 2
    val Number = 3
    val Boolean = 4
    val NonAlphaNum = 5
    val NewLine = 6
    val End = 7
    val ParamRef = 8
    val Interpolated = 9
    val Comment = 10
}
