package kiit.apis.routes

import kiit.apis.ApiConstants
import kiit.utils.naming.Namer

/**
 * Interface for the router.
 * This provides support for versioning at
 * 1. Area/Global  versioning : e.g. /v1/{area}/{api} v1 of area ( with many apis )
 * 2. API/Resource versioning : e.g. /{area}/v1/{api} v1 of api
 * 3. Action level versioning : e.g. /{area}/v1/{api} v2 of action ( via a header )
 */
interface Router {
    fun containsArea(area: String): Boolean

    fun containsApi(area: String, api: String, version:String? = null): Boolean {
        return containsApi(area, api, version ?: ApiConstants.zero)
    }

    fun containsApi(area: String, api: String, version:String): Boolean

    fun containsAction(verb: String, area: String, api: String, action: String): Boolean {
        return containsAction(verb, area, api, ApiConstants.zero, action, ApiConstants.zero)
    }
    fun containsAction(verb: String, area: String, api: String, apiVersion:String, action: String, actionVersion:String): Boolean
//    fun getArea(area: String, globalVersion: String = ApiConstants.zero): Area
//    fun getApi(area: String, globalVersion: String = ApiConstants.zero, api: String, version: String? = null): ApiActions?
//    fun getAction(verb:String, area: String, globalVersion: String = ApiConstants.zero, api: String, action: String, version: String? = null): Route?
}

/**
 * The top most level qualifier in the Universal Routing Structure
 * Essentially the root of the Routing tree
 * e.g.
 *
 * Format :  {area}.{api}.{action}
 * Routes :
 *          { Area A }
 *              - { v1 / API 1 }
 *                  - { Action a - v1 }
 *                  - { Action a - v2 }
 *                  - { Action b - v1 }
 *
 *         { Area B }
 *              - { v1 / API 2 }
 *                  - { Action a - v1 }
 *                  - { Action a - v2 }
 *                  - { Action b - v1 }
*/
data class DefaultRouter(
    val versions: List<VersionAreas>,
    val namer: Namer? = null,
    val onInstanceCreated: ((Any?) -> Unit)? = null
) : Router {

    init {
        onInstanceCreated?.let {
            //visitApis({ area, api -> onInstanceCreated.invoke(api.singleton) })
        }
    }

    /**
     * Whether there is an area w/ the supplied name.
     */
    override fun containsArea(area: String): Boolean {
        if (area.isEmpty()) return false
        val match = versions.firstOrNull { it.version == ApiConstants.zero }
        return match?.let { it.get(area) != null } ?: false
    }

    /**
     * Whether there is an api in the area supplied
     * @param area    : The name of the area e.g. "accounts"
     * @param api     : The name of the api  e.g. "signup"
     * @param version : The version to check for e.g. "1.1" indicates api:version=1, action:version = 1
     */
    override fun containsApi(area: String, api: String, version:String): Boolean {
        return getApi(area, api, version) != null
    }

    /**
     * Whether there is an action in the area/api supplied
     * @param area    : The name of the area e.g. "accounts"
     * @param api     : The name of the api  e.g. "signup"
     * @param apiVersion : The global version of entire api set to check for
     * @param action  : The name of the action  e.g. "login"
     * @param actionVersion : The version to check for e.g. "1.1" indicates api:version=1, action:version = 1
     */
    override fun containsAction(verb: String, area: String, api: String, apiVersion:String, action: String, actionVersion:String): Boolean {
        return getAction(verb, area, api, apiVersion, action, actionVersion) != null
    }

    /**
     * Gets the API model associated with the area.name
     */
    fun getApi(area: String, api: String, version: String? = null): Actions? {
        if (area.isEmpty()) return null
        if (api.isEmpty()) return null
        val info = when (version) {
            null -> Pair("0:${api}", ApiConstants.zero)
            else -> {
                val parts = version.split(".")
                val apiVersion = parts[0]
                val actionVersion = parts[1]
                Pair("${apiVersion}:${api}", "${actionVersion}:")
            }
        }

        val versionLookup = versions.firstOrNull { it.version == ApiConstants.zero } ?: return null
        val areaLookup = versionLookup.get(area) ?: return null
        val actionLookup = areaLookup.get(info.first)
        return actionLookup
    }

    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param getAction
     * @return
     */
    fun getAction(verb:String, area: String, api: String, apiVersion: String, action: String, actionVersion:String): Route? {
        val mapping = actionInternal(verb, area, api,  apiVersion, action, actionVersion)
        return when {
            mapping != null && (mapping.handler is MethodExecutor) -> mapping
            mapping != null && (mapping.handler is RouteForwarder) -> {
                val rf = mapping.handler as RouteForwarder
                val redirect = actionInternal(rf.verb.name, rf.area.name, rf.api.name,  apiVersion,rf.action.name, actionVersion)
                redirect
            }
            else -> mapping
        }
    }

    private fun actionInternal(verb:String, area: String, api: String, apiVersion: String, action: String, actionVersion: String): Route? {
        if (area.isEmpty()) return null
        if (api.isEmpty()) return null
        val info = when (actionVersion) {
            null -> Pair(ApiConstants.zero, ApiConstants.zero)
            else -> {
                val parts = apiVersion.split(".")
                val apiVersion = parts[0]
                val actionVersion = parts[1]
                Pair(apiVersion, actionVersion)
            }
        }
        val apiLookup = getApi(area, api, apiVersion)
        val actionName = "${verb}.${info.second}:${action}"
        val mapping = apiLookup?.get(actionName)
        return mapping
    }

    fun visitApis(visitor: (Area, Actions) -> Unit) {

//        // 1. Each top level area in the system
//        // e.g. {area}/{api}/{action}
//        this.areas.items.forEach { area ->
//
//            area.items.forEach { api ->
//                visitor(area.area, api)
//            }
//        }
    }

    fun visitActions(visitor: (Area, Api, Action) -> Unit) {

//        // 1. Each top level area in the system
//        // e.g. {area}/{api}/{action}
//        this.areas.items.forEach { area ->
//
//            area.items.forEach { api ->
//
//                api.items.forEach { action ->
//                    visitor(area.area, api.api, action.route.action)
//                }
//            }
//        }
    }
}
