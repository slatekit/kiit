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


import slatekit.meta.kClass
import kotlin.reflect.KClass



/**
 * Annotation to designate a class as an API
 *
 *  NOTE: Apis in SlateKit are organized into 3 parts in the route.
 *  e.g. area / api  / action
 *       app  / user / invite
 *
 * @param area   : the top level area/category of the api "sys", "app", "ops"
 * @param name   : the name of the api "users"
 * @param desc   : description of the api
 * @param roles  : the roles allowed to access this api ( "admin", "ops" )
 * @param auth   : the authorization mode ( "app-key" | "app-roles", "key-roles" )
 * @param verb   : the verb ( "get", "post", "cli", "*" )
 * @param protocol : the platforms this is accessible to ( "web" | "cli" | "*" )
 */
data class ApiReg(val cls: KClass<*>,
                  val area : String  = "",
                  val name : String  = "",
                  val desc : String  = "",
                  val roles: String  = "",
                  val auth : String  = "",
                  val verb : String  = "*",
                  val protocol: String = "*",
                  val declaredOnly: Boolean = true,
                  val singleton:Any ? = null
                  )
{
    constructor(
                instance: Any,
                area : String  = "",
                name : String  = "",
                desc : String  = "",
                roles: String  = "",
                auth : String  = "",
                verb : String  = "*",
                protocol: String = "*",
                declaredOnly: Boolean = true) : this(instance.kClass, area, name, desc, roles, auth, verb, protocol, declaredOnly, instance)
}