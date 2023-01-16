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

package kiit.apis.routes

import kotlin.reflect.KCallable
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType
import kiit.apis.Access
import kiit.apis.AuthMode
import kiit.apis.Verb
import kiit.apis.core.Roles
import kiit.apis.core.Sources
import kiit.common.values.Metadata
import kiit.common.ext.tail
import kiit.requests.Request

/**
 * ================================================================
 * Universal Route =  {AREA}.{API}.{ACTION}
 * Route           =  accounts.signup.register
 * Web             =  POST https://{host}/api/accounts/signup/register
 * CLI             =  :> accounts.signup.register -email=".." -pswd=".."
 * Queue           =  JSON { path: "account.signup.register", meta: { }, data : { } }
 * Class           =
 *      @Api(area = "accounts", name = "signup", ...)
 *      class Signup {
 *          @Action(desc = "processes an request with 0 parameters")
 *          suspend fun register(email:String, pswd:String): Outcome<UUID> {
 *              // code...
 *          }
 *      }
 * ================================================================
 * From the example above, this represents the action "register" and it's mapped method.
 *
 * @param member : The callable method associated w/ the action
 * @param name : Name of action which may have a different name than method due to conventions
 * @param desc : Description of the action
 * @param auth : Authentication mode @see[AuthMode] for the action/method
 * @param roles : Roles allowed to call this action
 * @param verb : Get/Post verb for Http enabled source
 * @param source : Protocol associated with the action.
 */
data class Action(
    val member: KCallable<*>,
    val name: String = "",
    val desc: String = "",
    val auth: AuthMode = AuthMode.Parent,
    val roles: Roles = Roles.empty,
    val access: Access = Access.Public,
    val sources: Sources = Sources.all,
    val verb: Verb = Verb.Auto,
    val tags: List<String> = listOf()
) {
    val protocol = sources.all.first()

    /**
     * All the parameters of the function, this includes:
     *
     * 1. 0th instance parameter for kotlin
     * 2. a possible Request
     * 3. a possible Meta
     * 4. actual parameters for the method
     */
    private val paramsAll = member.parameters

    /**
     * All the function specific parameters WITHOUT references to
     * the following: These are the parameters that can be
     * discovered, documented, validated against
     *
     * 1. 0th instance parameter for kotlin
     * 2. a possible request/meta paramter
     */
    val paramsUser = filter(member.parameters)

    /**
     * All the parameters that can me mapped over for
     * populating during calls on the CLI / Web
     */
    val params = if (paramsAll.size <= 1) listOf() else paramsAll.tail()

    /**
     * Whether the action has any arguments.
     */
    val hasArgs = !params.isEmpty()

    fun isSingleDefaultedArg(): Boolean {
        return if (!hasArgs || params.size > 1) {
            false
        } else {
            paramsUser.isNotEmpty() && paramsUser[0].isOptional
        }
    }

    fun isSingleArg(): Boolean = hasArgs && params.size == 1

    companion object {

        @JvmStatic
        val TypeRequest = Request::class.createType()

        @JvmStatic
        val TypeMeta = Metadata::class.createType()

        @JvmStatic
        fun filter(args: List<KParameter>): List<KParameter> {
            if (args.isEmpty()) return args
            val finalArgs = args.tail().filter { arg ->
                val type = arg.type
                type != TypeRequest && arg.type != TypeMeta
            }.toList()
            return finalArgs
        }
    }
}
