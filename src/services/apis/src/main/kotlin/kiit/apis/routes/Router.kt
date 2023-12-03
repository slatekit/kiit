package kiit.apis.routes

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
    val areas: List<VersionAreas>,
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
    fun check(path: String): Boolean {
        val parts = path.split('.')
        return when (parts.size) {
            0 -> false
            1 -> contains(parts[0]) || contains("", parts[0])
            2 -> contains(parts[0], parts[1]) || contains("", parts[0], parts[1])
            3 -> contains(parts[0], parts[1], parts[2])
            else -> false
        }
    }

    /**
     * Whether there is an area w/ the supplied name.
     */
    fun contains(area: String, globalVersion:String = "0"): Boolean {
        if (area.isEmpty()) return false
        val match = areas.firstOrNull { it.version == globalVersion && it.get(area) != null }
        return match != null
    }

    /**
     * Whether there is an api in the area supplied
     * @param area    : The name of the area e.g. "accounts"
     * @param api     : The name of the api  e.g. "signup"
     * @param globalVersion : The global version of entire api set to check for
     * @param version : The version to check for e.g. "1.1" indicates api:version=1, action:version = 1
     */
    fun contains(area: String, api: String, globalVersion: String = "0", version:String? = null): Boolean {
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
    fun contains(area: String, api: String, action: String, globalVersion: String = "0", version:String? = null): Boolean {
        return action(area, api, action, globalVersion, version) != null
    }

    /**
     * Gets the API model associated with the area.name
     */
    fun api(area: String, api: String, globalVersion: String = "0", version: String? = null): ApiActions? {
        if (globalVersion.isEmpty()) return null
        if (area.isEmpty()) return null
        if (api.isEmpty()) return null
        val info = when (version) {
            null -> Pair("0:${area}", "0:${api}")
            else -> {
                val parts = version.split(".")
                val areaVersion = parts[0]
                val apiVersion = parts[1]
                Pair("${areaVersion}:${area}", "${apiVersion}:${api}")
            }
        }

        val areaLookup = areas.get(info.first) ?: return null
        return areaLookup.get(info.second)
    }

    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun action(area: String, api: String, action: String, globalVersion: String = "0", version: String?): RouteMapping? {
        if (globalVersion.isEmpty()) return null
        if (area.isEmpty()) return null
        if (api.isEmpty()) return null
        if (action.isEmpty()) return null
        val info = when (version) {
            null -> Pair("0:${area}", "0:${api}")
            else -> {
                val parts = version.split(".")
                val areaVersion = parts[0]
                val apiVersion = parts[1]
                Pair("${areaVersion}:${area}", "${apiVersion}:${api}")
            }
        }

        val areaLookup = areas.get(info.first) ?: return null
        val apiLookup = areaLookup.get(info.second) ?: return null
        return apiLookup.get(info.third)
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
