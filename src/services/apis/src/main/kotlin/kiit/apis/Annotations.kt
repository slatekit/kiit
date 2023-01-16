package kiit.apis

import kiit.common.Sources

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
 * This Api annotations represents the "@Api(area = "accounts" ) portion in the example
 *
 * @param area : the top level area/category of the api "sys", "app", "ops"
 * @param name : the name of the api "users"
 * @param desc : description of the api
 * @param roles : the roles allowed to access this api ( "@admin", "@ops" )
 *                 in the event, the auth mode is "api-key", this is the name of the api-key
 * @param auth : the authorization mode ( "app-key" | "app-roles", "key-roles" )
 * @param verb : the verb ( "get", "post", "cli", "*" )
 * @param sources : the platforms this is accessible to ( "web" | "cli" | "*" )
 */
@Target(AnnotationTarget.CLASS)
annotation class Api(
    val area: String = "",
    val name: String = "",
    val desc: String = "",
    val auth: String = AuthModes.KEYED,
    val roles: Array<String> = [],
    val verb: String = Verbs.AUTO,
    val access: String = AccessLevel.PUBLIC,
    val sources: Array<String> = [Sources.ALL],
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
 * @param sources : the source ( "web, "cli", "*" ) required to access this action
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Action(
    val name: String = "",
    val desc: String = "",
    val auth: String = AuthModes.PARENT,
    val roles: Array<String> = [],
    val verb: String = Verbs.AUTO,
    val access: String = AccessLevel.PARENT,
    val sources: Array<String> = [Sources.ALL],
    val version: String = "1",
    val tags: Array<String> = []
)

/**
 * Annotation to describe a parameter to an api action.
 * NOT CURRENTLY USED - Will be in upcoming versions.
 *
 * @param name : name of the argument
 * @param desc : description of argument
 * @param length : max length of value e.g. 200
 * @param format : format of the value e.g.
 * @param examples : list of example values
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Input(
    val name: String = "",
    val desc: String = "",
    val required: Boolean = true,
    val length: String = "",
    val defaults: String = "",
    val format: String = "",
    val examples: Array<String> = []
)

/**
 * Annotation to reference external documentation API specs
 * @param path : optional path to identity the source of documentation
 * @param key : optional key to identity area within document
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Documented(val path: String = "", val key: String = "")


/**
 * Annotation used specify additional operations to perform on the action using middleware hooks.
 *
 * @param action : the name of the action, leave empty to use the method name this is applied to
 * @param at     : the description of the action
 * @param inputs : the roles allowed ( use @parent to refer to parent Api anntoation roles )
 * @param tags   : the verb ( "get", "post", "cli", "*" ) allowed.
 * @sample
 *          @Perform(action = "log"  , name="logger", at="after", inputs=["*"]                 , tags=["detail"])
 *          @Perform(action = "event", name="stream", at="after", inputs=["senderId", "userId"], tags=["detail"])
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Perform(
    val action: String,
    val name  : String = "",
    val at    : String = "",
    val inputs: Array<String> = [],
    val tags: Array<String> = []
)
