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

import kotlin.reflect.KCallable
import kotlin.reflect.KParameter

data class ApiRegAction(
        val api     : ApiReg        ,
        val member  : KCallable<*>  ,
        val name    : String  = ""  ,
        val desc    : String  = ""  ,
        val roles   : String  = ""  ,
        val verb    : String  = "*" ,
        val protocol: String  = "*" ,
        val hasArgs : Boolean
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