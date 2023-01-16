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

package kiit.common.args

//import kiit.common.writer.*

/**
 *
 * @param alias : alias for the argument     ( -e   )
 * @param name : name of the argument       ( -env )
 * @param desc : description of argument
 * @param isRequired : whether arg is required
 * @param isCased : case sensitive
 * @param isDevOnly : used for development only
 * @param isInterpreted : if arg can be interpreted ( @date.today )
 * @param group : used to group args
 * @param tag : used to tag an arg
 * @param defaultVal : default value for the arg
 * @param example : example of an arg value
 * @param exampleMany : multiple examples of arg values
 */
data class Arg(
        val alias: String = "",
        val name: String = "",
        val desc: String = "",
        val dataType: String = "",
        val isRequired: Boolean = true,
        val isCased: Boolean = true,
        val isDevOnly: Boolean = false,
        val isInterpreted: Boolean = false,
        val group: String = "",
        val tag: String = "",
        val defaultVal: String = "",
        val example: String = "",
        val exampleMany: String = ""
)
