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
 * Annotation used in conjunction with the Api annotation, to designate your method
 * as an api action that is exposed for use.
 *
 *  NOTE: Apis in SlateKit are organized into 3 parts in the route.
 *  e.g. area/api/action
 *       app/user/invite
 *
 * @param name : the name of the action, leave empty to use the method name this is applied to
 * @param desc : the description of the action
 * @param roles : the roles allowed ( use @parent to refer to parent Api anntoation roles )
 * @param verb : the verb ( "get", "post", "cli", "*" ) allowed.
 * @param protocols : the protocol ( "web, "cli", "*" ) required to access this action
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Action(
        val name: String = "",
        val desc: String = "",
        val roles: Array<String> = [],
        val verb: String = Verbs.Auto,
        val access: String = AccessLevel.Public,
        val protocols: Array<String> = [Protocols.All],
        val version: String = "1",
        val tags: Array<String> = []
)
