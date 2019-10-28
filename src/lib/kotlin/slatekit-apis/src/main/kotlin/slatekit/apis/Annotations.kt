package slatekit.apis



/**
 * Annotation to designate a class as an API
 *
 *  NOTE: Apis in SlateKit are organized into 3 parts in the route.
 *  e.g. area / api  / action
 *       app  / user / invite
 *
 * @param area : the top level area/category of the api "sys", "app", "ops"
 * @param name : the name of the api "users"
 * @param desc : description of the api
 * @param roles : the roles allowed to access this api ( "@admin", "@ops" )
 *                 in the event, the auth mode is "api-key", this is the name of the api-key
 * @param auth : the authorization mode ( "app-key" | "app-roles", "key-roles" )
 * @param verb : the verb ( "get", "post", "cli", "*" )
 * @param protocols : the platforms this is accessible to ( "web" | "cli" | "*" )
 */
@Target(AnnotationTarget.CLASS)
annotation class Api(
        val area: String = "",
        val name: String = "",
        val desc: String = "",
        val roles: Array<String> = [],
        val auth: String = AuthModes.Keyed,
        val verb: String = Verbs.Auto,
        val access: String = AccessLevel.Public,
        val protocols: Array<String> = [Protocols.All],
        val version: String = "1",
        val tags: Array<String> = []
)



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



/**
 * Annotation to describe a parameter to an api action.
 * NOT CURRENTLY USED - Will be in upcoming versions.
 *
 * @param name      : name of the argument
 * @param desc      : description of argument
 * @param length    : max length of value e.g. 200
 * @param format    : format of the value e.g.
 * @param examples  : list of example values
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Input(
        val name     : String = "",
        val desc     : String = "",
        val required : Boolean = true,
        val length   : String = "",
        val defaults : String = "",
        val format   : String = "",
        val examples : Array<String> = []
)



/**
 * Annotation to reference external documentation API specs
 * @param path : optional path to identity the source of documentation
 * @param key  : optional key to identity area within document
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Documented(val path:String = "", val key:String = "")
