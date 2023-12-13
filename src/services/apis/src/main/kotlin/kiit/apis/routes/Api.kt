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

import kiit.apis.*
import kiit.apis.SetupType
import kiit.apis.core.Roles
import kiit.apis.core.Sources
import kotlin.reflect.KClass


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
 * From the example above, this represents the Api "signup" and it's mapped class.

 * @param area : the top level area/category of the api "account", "alerts"
 * @param name : the name of the api "users"
 * @param desc : description of the api
 * @param roles : the roles allowed to access this api ( "admin", "ops" )
 * @param auth : the authorization mode ( "app-key" | "app-roles", "key-roles" )
 * @param verb : the verb ( "get", "post", "cli", "*" )
 * @param version : the version of this api e.g. "1", "2"
 * @param sources : the platforms this is accessible to ( "web" | "cli" | "*" )
 * @param actions : the collection of actions / methods on this API.
 */
data class Api(
    val area: String = "",
    val name: String = "",
    val desc: String = "",
    val auth: AuthMode = AuthMode.None,
    val roles: Roles = Roles.empty,
    val access: Access = Access.Public,
    val sources: Sources = Sources.all,
    val verb: Verb = Verb.Auto,
    val version:String = "",
    val policies: List<String> = listOf(),
    val tags: List<String> = listOf()
)
