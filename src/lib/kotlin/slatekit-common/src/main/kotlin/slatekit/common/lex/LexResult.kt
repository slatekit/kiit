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

import slatekit.common.newline

/**
 * Result of lexical parsing. This could represent all the tokens or just a subset
 * @param success : whether or not the lex parse was valid
 * @param message : a message if there were errors
 * @param tokens : the tokens parsed
 * @param total : the total number of tokens parsed
 * @param isSubset : whether this represents a subset of tokens or all tokens
 * @param ex : exception if failure
 */
data class LexResult(
    val success: Boolean,
    val message: String,
    val tokens: List<Token>,
    val total: Int,
    val isSubset: Boolean,
    val ex: Exception?
) {
    fun toStringDetail(): String {
        val text = "$success, $message, total=$total, isSubset=$isSubset"
        val tokenDetail = tokens.fold("", { s, t -> s + t.toStringDetail() + newline })
        return text + newline + tokenDetail
    }
}
