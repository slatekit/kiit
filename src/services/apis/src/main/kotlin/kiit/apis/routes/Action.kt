/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
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
 * @param member  : The callable method associated w/ the action
 * @param name    : Name of action which may have a different name than method due to conventions
 * @param desc    : Description of the action
 * @param auth    : Authentication mode @see[AuthMode] for the action/method
 * @param roles   : Roles allowed to call this action
 * @param verb    : Get/Post verb for Http enabled source
 * @param version : the version of this api e.g. "1", "2"
 * @param sources : Protocol associated with the action.
 */
data class Action(
    val name: String = "",
    val desc: String = "",
    val auth: AuthMode = AuthMode.Parent,
    val roles: Roles = Roles.empty,
    val access: Access = Access.Public,
    val sources: Sources = Sources.all,
    val verb: Verb = Verb.Auto,
    val version:String = "",
    val tags: List<String> = listOf()
)
