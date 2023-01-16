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

/**
 * Represents a single token during lexical parsing
 * @param text : The raw text of the token
 * @param tValue : The converted value of the text
 * @param tType : The type of the token
 * @param line : The line number of the token
 * @param charPos : The starting char position in the line
 * @param index : The index of the token on the line
 */
data class Token(
    val text: String,
    val tValue: Any,
    val tType: Int,
    val line: Int,
    val charPos: Int,
    val index: Int
) {
    fun toStringDetail(): String = "$tType, line=$line, charPos=$charPos, index=$index $text"

    companion object {

        @JvmStatic
        val none = Token("", "", TokenType.None, -1, -1, -1)
    }
}