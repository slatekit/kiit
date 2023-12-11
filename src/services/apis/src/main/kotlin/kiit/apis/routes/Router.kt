package kiit.apis.routes

import kiit.apis.ApiConstants
import kiit.utils.naming.Namer

/**
 * The top most level qualifier in the Universal Routing Structure
 * Essentially the root of the Routing tree
 * e.g.
 *
 * Format :  {area}.{api}.{action}
 * Routes :
 *          { Area A }
 *              - { API X - v1 }
 *                  - { Action a - v1 }
 *                  - { Action a - v2 }
 *                  - { Action b - v1 }
 *
 *         { Area B }
 *              - { API Y - v1 }
 *                  - { Action a - v1 }
 *                  - { Action a - v2 }
 *                  - { Action b - v1 }
*/
data class Router(
    val versions: List<VersionAreas>,
    val namer: Namer? = null,
    val onInstanceCreated: ((Any?) -> Unit)? = null
) {

    init {
        onInstanceCreated?.let {
            //visitApis({ area, api -> onInstanceCreated.invoke(api.singleton) })
        }
    }

    /**
     * gets the api info associated with the request
     * @param cmd
     * @return
     */
    fun check(verb:String, path: String): Boolean {
        val parts = path.split('.')
        return when (parts.size) {
            0 -> false
            1 -> containsArea(parts[0]) || containsArea("", parts[0])
            2 -> containsApi(parts[0], parts[1]) || containsApi("", parts[0], parts[1])
            3 -> containsAction(verb, parts[0], parts[1], parts[2])
            else -> false
        }
    }

    /**
     * Whether there is an area w/ the supplied name.
     */
    fun containsArea(area: String, globalVersion:String = ApiConstants.zero): Boolean {
        if (area.isEmpty()) return false
        val match = versions.firstOrNull { it.version == globalVersion }
        return match?.let { it.get(area) != null } ?: false
    }

    /**
     * Whether there is an api in the area supplied
     * @param area    : The name of the area e.g. "accounts"
     * @param api     : The name of the api  e.g. "signup"
     * @param globalVersion : The global version of entire api set to check for
     * @param version : The version to check for e.g. "1.1" indicates api:version=1, action:version = 1
     */
    fun containsApi(area: String, api: String, globalVersion: String = ApiConstants.zero, version:String? = null): Boolean {
        return api(area, api, globalVersion, version) != null
    }

    /**
     * Whether there is an action in the area/api supplied
     * @param area    : The name of the area e.g. "accounts"
     * @param api     : The name of the api  e.g. "signup"
     * @param action  : The name of the action  e.g. "login"
     * @param globalVersion : The global version of entire api set to check for
     * @param version : The version to check for e.g. "1.1" indicates api:version=1, action:version = 1
     */
    fun containsAction(verb: String, area: String, api: String, action: String, globalVersion: String = ApiConstants.zero, version:String? = null): Boolean {
        return action(verb, area, api, action, globalVersion, version) != null
    }

    /**
     * Gets the API model associated with the area.name
     */
    fun api(area: String, api: String, globalVersion: String = "0", version: String? = null): ApiActions? {
        if (globalVersion.isEmpty()) return null
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

        val versionLookup = versions.firstOrNull { it.version == globalVersion } ?: return null
        val areaLookup = versionLookup.get(area) ?: return null
        val actionLookup = areaLookup.get(info.first)
        return actionLookup
    }

    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun action(verb:String, area: String, api: String, action: String, globalVersion: String = ApiConstants.zero, version: String? = null): Route? {
        val mapping = actionInternal(verb, area, api, action, globalVersion, version)
        return when {
            mapping != null && (mapping.handler is MethodExecutor) -> mapping
            mapping != null && (mapping.handler is RouteForwarder) -> {
                val rf = mapping.handler as RouteForwarder
                val redirect = actionInternal(rf.verb.name, rf.area.name, rf.api.name, rf.action.name, rf.globalVersion, null)
                redirect
            }
            else -> mapping
        }
    }

    private fun actionInternal(verb:String, area: String, api: String, action: String, globalVersion: String = ApiConstants.zero, version: String?): Route? {
        if (globalVersion.isEmpty()) return null
        if (area.isEmpty()) return null
        if (api.isEmpty()) return null
        val info = when (version) {
            null -> Pair(ApiConstants.zero, ApiConstants.zero)
            else -> {
                val parts = version.split(".")
                val apiVersion = parts[0]
                val actionVersion = parts[1]
                Pair(apiVersion, actionVersion)
            }
        }
        val apiLookup = api(area, api, globalVersion, version)
        val actionName = "${verb}.${info.second}:${action}"
        val mapping = apiLookup?.get(actionName)
        return mapping
    }

    fun visitApis(visitor: (Area, ApiActions) -> Unit) {

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


    companion object {

    }
}
