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

package slatekit.apis

/**
 * Annotation to describe a parameter to an api action.
 * NOT CURRENTLY USED - Will be in upcoming versions.
 *
 * @param name : name of the argument
 * @param desc : description of argument
 * @param required : whether its required or not
 * @param defaultVal: 
 * @param eg
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class ApiArg(
    val name: String = "",
    val desc: String = "",
    val required: Boolean = true,
    val defaultVal: String = "",
    val eg: String = ""
)