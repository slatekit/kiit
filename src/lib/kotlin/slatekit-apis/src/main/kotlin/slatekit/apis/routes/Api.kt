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

package slatekit.apis.routes

import kotlin.reflect.KClass
import slatekit.apis.*
import slatekit.apis.SetupType
import slatekit.apis.core.Roles
import slatekit.apis.core.Sources
import slatekit.common.Source
import slatekit.meta.kClass

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
 * @param source : the platforms this is accessible to ( "web" | "cli" | "*" )
 * @param actions : the collection of actions / methods on this API.
 */
data class Api(
    val klass: KClass<*>,
    val area: String = "",
    val name: String = "",
    val desc: String = "",
    val auth: AuthMode = AuthMode.None,
    val roles: Roles = Roles.empty,
    val access: Access = Access.Public,
    val sources: Sources = Sources.all,
    val verb: Verb = Verb.Auto,
    val declaredOnly: Boolean = true,
    val singleton: Any? = null,
    val setup: SetupType = SetupType.Methods,
    val actions: Lookup<Action> = Lookup(listOf()) { t -> t.name }
) {

    val protocol = sources.all.first()

    constructor(
        instance: Any,
        area: String = "",
        name: String = "",
        desc: String = "",
        roles: List<String> = listOf(),
        access: Access = Access.Public,
        auth: AuthMode = AuthMode.Token,
        protocol: List<Source> = listOf(Source.All),
        verb: Verb = Verb.Auto,
        declaredOnly: Boolean = true,
        setup: SetupType = SetupType.Methods
    ) : this(instance.kClass, area, name, desc, auth, Roles(roles), access, Sources(protocol), verb, declaredOnly, instance, setup)

}
