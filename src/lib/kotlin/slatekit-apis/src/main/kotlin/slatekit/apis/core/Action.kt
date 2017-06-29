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

package slatekit.apis.core

import slatekit.apis.ApiAction
import slatekit.apis.support.ApiInfo
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter


/**
 * Represents full information about the API Action.
 * This include the metadata and reflection info needed to call the method on the class/ApiBase
 * NOTE: Scala based annotations and reflection are used to get the parameter lists
 *
 * @param name      :  The name of the api action / method name to call
 * @param api       :  The Api annotation put on the method indicating its an api with roles/permissions
 * @param action    :  The ApiAction annotation put on the method indicating its an api action with roles/permissions
 * @param mirror    :  The Scala scala.lang.runtime.universe.MethodMirror to call a method dynamically
 * @param hasArgs   :  Whether the api action has any arguments / parameters ( convenience flag )
 * @param paramList :  A list of the parameters on the method ( name, typename, typeSymbol, position )
 */
data class Action(
        val name: String,
        val api: ApiInfo,
        val action: ApiAction,
        val member: KCallable<*>,
        val hasArgs: Boolean
) {

    val paramList: List<KParameter> = if (member.parameters.size == 1) listOf<KParameter>() else member.parameters.subList(1, member.parameters.size)

    fun isSingleDefaultedArg(): Boolean {
        return if (!hasArgs || paramList.size > 1) {
            false
        }
        else {
            paramList[0].isOptional
        }
    }


    fun isSingleArg(): Boolean = hasArgs && paramList.size == 1
}
