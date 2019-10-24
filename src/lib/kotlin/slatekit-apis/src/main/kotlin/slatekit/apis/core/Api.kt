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

import slatekit.apis.setup.Access
import slatekit.apis.setup.Protocol
import slatekit.apis.setup.Verb
import slatekit.meta.kClass
import kotlin.reflect.KClass

/**
 * Represents an API in Slate Kit which is a reference to a regular Class
 *
 *  NOTE: API routes are considered Universal APIs and are organized into 3 parts in the route.
 *  e.g. area / api  / action
 *       app  / user / invite
 *
 * @param area : the top level area/category of the api "sys", "app", "ops"
 * @param name : the name of the api "users"
 * @param desc : description of the api
 * @param roles : the roles allowed to access this api ( "admin", "ops" )
 * @param auth : the authorization mode ( "app-key" | "app-roles", "key-roles" )
 * @param verb : the verb ( "get", "post", "cli", "*" )
 * @param protocol : the platforms this is accessible to ( "web" | "cli" | "*" )
 * @param actions : the collection of actions / methods on this API.
 */
data class Api(
        val cls: KClass<*>,
        val area: String = "",
        val name: String = "",
        val desc: String = "",
        val roles: String = "",
        val auth: String = "",
        val verb: Verb = Verb.Auto,
        val protocol: Protocol = Protocol.All,
        val access: Access = Access.Public,
        val declaredOnly: Boolean = true,
        val singleton: Any? = null,
        val setup: Setup = PublicMethods,
        val actions: Lookup<Action> = Lookup(listOf(), { t -> t.name })
) {
    constructor(
        instance: Any,
        area: String = "",
        name: String = "",
        desc: String = "",
        roles: String = "",
        auth: String = "",
        verb: String = "*",
        protocol: String = "",
        declaredOnly: Boolean = true,
        setup: Setup = PublicMethods
    ) : this(instance.kClass, area, name, desc, roles, auth, verb, protocol, declaredOnly, instance, setup)
}
