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

package kiit.utils.templates

/**
 * Represents either a plain text or variable in the template.
 * e.g.
 * "Hi @{user.name}, Welcome to @{startup.name}, please click @{verifyUrl} to verify your email."
 *
 * In the above, "Hi" is a part of type plain text
 * In the above, "user.name" is a part of type substitution
 *
 * @param text : The text represented. e.g. Plain text "Hi" or Variable "user.name"
 * @param subType : Whether this is plain text or represents a substitution variable
 * @param pos : The position of the text
 * @param length : The length of the text
 */
data class TemplatePart(val text: String, val subType: Short, val pos: Int, val length: Int)
