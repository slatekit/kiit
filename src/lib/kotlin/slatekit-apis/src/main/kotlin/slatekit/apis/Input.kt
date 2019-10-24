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
 * @param name      : name of the argument
 * @param desc      : description of argument
 * @param length    : max length of value e.g. 200
 * @param format    : format of the value e.g.
 * @param examples  : list of example values
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Input(
    val name     : String = "",
    val desc     : String = "",
    val required : Boolean = true,
    val length   : String = "",
    val defaults : String = "",
    val format   : String = "",
    val examples : Array<String> = []
)


