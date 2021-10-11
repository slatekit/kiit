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

package slatekit.utils.templates

/**
  * Represents a template that can be processed for variables/substitutions.
  * e.g. An email template such as :
  *
  * "Hi @{user.name}, Welcome to @{startup.name}, please click @{verifyUrl} to verify your email."
  *
  * @param name : The name of the template ( e.g. "welcome_email" )
  * @param content : The text content of the template
  * @param parsed : Whether this template has been parsed into its parts.
  * @param valid : Whether this template is valid ( after parsing )
  * @param status : Status message of the template ( if invalid )
  * @param group : Optional group this template belongs to ( for organizing templates )
  * @param path : Optional path of the template if coming from a file
  * @param parts : Optional path of the template if coming from a file
  */
data class Template(
    val name: String,
    val content: String,
    val parsed: Boolean = false,
    val valid: Boolean = false,
    val status: String? = null,
    val group: String? = null,
    val path: String? = null,
    val parts: List<TemplatePart>? = null
)
