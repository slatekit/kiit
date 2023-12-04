package kiit.apis.setup

import kiit.apis.Access
import kiit.apis.AuthMode
import kiit.apis.Verb
import kiit.apis.core.Roles
import kiit.apis.core.Sources
import kiit.apis.routes.*
import kiit.common.Source
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import kotlin.reflect.KCallable
import kotlin.reflect.KClass


/**
 * Loads an api using JSON configuration.
 * {
 *     "area"     : "spaces",
 *     "name"     : "manage",
 *     "desc"     : "rpc calls for managing spaces",
 *     "auth"     : "token",
 *     "roles"    : ["user"],
 *     "verb"     : "auto",
 *     "access"   : "public",
 *     "sources"  : ["all"],
 *     "version"  : "0",
 *     "tags"     : [],
 *     "actions"  : [
 *         {
 *             "execute"  : { "type": "Method"  , "target": "create" }
 *             "execute"  : { "type": "Redirect", "target": "create" }
 *         },
 *         {
 *              "name"     : "create",
 *              "desc"     : "Description here",
 *              "auth"     : "@parent",
 *              "roles"    : [],
 *              "verb"     : "Post",
 *              "access"   : "public",
 *              "sources"  : ["all"],
 *              "version"  : "1",
 *              "tags"     : [],
 *              "handler"  : { "type": "MethodExecutor", "target": { "method": "create" } },
 *              "handler"  : { "type": "RouteForwarder", "target": { "globalVersion": "1", "path": "spaces/manage/create", "verb": "Post" } }
 *          }
 *     ]
 * }
 *
 */
class ConfigLoader(val cls: KClass<*>, val instance: Any) {

    fun loadApi(doc: JSONObject) : Api {
        val area = doc.get("area") as String
        val name = doc.get("name") as String
        val desc = doc.get("desc") as String
        val auth = doc.get("auth") as String
        val roles = toList(doc.get("roles") as JSONArray)
        val verb = doc.get("verb") as String
        val access = doc.get("access") as String
        val sources = toList(doc.get("sources") as JSONArray).map { Source.parse(it) }
        val version = doc.get("version") as String
        val tags = toList(doc.get("tags") as JSONArray)
        val api = Api(
            area,
            name,
            desc ?: "",
            AuthMode.parse(auth),
            Roles(roles),
            Access.parse(access),
            Sources(sources),
            Verb.parse(verb),
            version,
            tags
        )
        return api
    }

    fun loadActions(area: Area, api: Api, methods: Map<String, KCallable<*>>, doc: JSONObject): List<RouteMapping> {
        val actions = doc.get("actions") as JSONArray
        val apiActions = actions.map { action ->
            val actionJson = action as JSONObject
            val executor = actionJson.get("execute") as JSONObject
            val type = executor.get("type") as String
            val route:RouteMapping? = when (type) {
                EXECUTOR_TYPE_METHOD   -> {
                    val handler = buildExecutor(executor, methods)
                    val action = loadAction(api, action as JSONObject, handler!!)
                    val route = buildRoute(area, api, action, handler)
                    route
                }
                EXECUTOR_TYPE_REDIRECT -> {
                    val handler = buildRedirect(executor, methods)
                    val action = loadAction(api, action as JSONObject, handler!!)
                    val route = buildRoute(area, api, action, handler)
                    route
                }
                else -> null
            }
            route
        }
        return apiActions.filterNotNull()
    }


    private fun loadAction(api: Api, doc: JSONObject, handler: RouteHandler): Action {
        // From config
        val name = doc.get("name") as String?
        val desc = doc.get("desc") as String?
        val auth = doc.get("auth") as String?
        val roles = toList(doc.get("roles") as JSONArray?)
        val verb = doc.get("verb") as String?
        val access = doc.get("access") as String?
        val sources = toList(doc.get("sources") as JSONArray?)
        val version = doc.get("version") as String?
        val tags = toList(doc.get("tags") as JSONArray?)

        // Override with api ( if null or reference to parent via "@parent"
        val actionAuth = References.auth(api.auth, auth)
        val actionRoles = References.roles(api.roles, roles.toTypedArray())
        val actionAccess = References.access(api.access, access)
        val actionSources = References.sources(api.sources, sources.toTypedArray())
        val actionVersion = References.version(api.version, version)

        // To determine the verb automatically if not supplied.
        val isExecutor = handler is MethodExecutor
        val method = if(isExecutor) (handler as MethodExecutor).call.member.name else ""
        val actionVerb = References.verb(api.verb, verb, name ?: method)
        val actionName = when {
            name.isNullOrEmpty() -> if(isExecutor) method else ""
            else -> name
        }
        // Action to be used in route
        val action = Action(
            actionName,
            desc ?: "",
            actionAuth,
            actionRoles,
            actionAccess,
            actionSources,
            actionVerb,
            actionVersion,
            tags
        )
        return action
    }


    /**
     * "execute"  : { "type": "Method"  , "target": "create" }
     */
    private fun buildExecutor(json: JSONObject, methods: Map<String, KCallable<*>>): MethodExecutor? {
        val target = json.get("target") as String
        val method = methods.get(target)
        return method?.let {
            // Reflection based KCallable
            val call = Call(cls, method, instance)

            // Type of route handler
            val handler = MethodExecutor(call)
            handler
        }
    }


    /**
     * "execute"  : { "type": "Method"  , "target": "create" }
     */
    private fun buildRedirect(json: JSONObject, methods: Map<String, KCallable<*>>): RouteForwarder? {
        val target = json.get("target") as String
        val method = methods.get(target)
        return null
    }


    private fun buildRoute(area: Area, api: Api, action: Action, handler: RouteHandler): RouteMapping {
        // area/api/action objects ( with version info )
        val route = Route(area, api, action)

        // Final mapping of route -> handler
        return RouteMapping(route, handler)
    }

    fun toList(items:JSONArray?) : List<String> {
        return items?.map { it as String } ?: listOf()
    }

    companion object {
        const val EXECUTOR_TYPE_METHOD   = "method"
        const val EXECUTOR_TYPE_REDIRECT = "redirect"
    }
}
