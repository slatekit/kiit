package kiit.apis.routes

import kotlin.reflect.full.primaryConstructor
import kiit.context.Context
import kiit.utils.naming.Namer
import kiit.meta.Reflector

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
data class Routes(
    val areas: AreaLookup,
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
    fun contains(area: String, version:String? = null): Boolean {
        if (area.isEmpty()) return false
        val name = version?.let { "${it}:${area}" } ?: area
        return areas.contains(name)
    }

    /**
     * Whether there is an api in the area supplied
     * @param area    : The name of the area e.g. "accounts"
     * @param api     : The name of the api  e.g. "signup"
     * @param version : The version to check for e.g. "1.1" indicates area:version=1, api:version = 1
     */
    fun contains(area: String, api: String, version:String? = null): Boolean {
        return api(area, api, version) != null
    }

    /**
     * Whether there is an action in the area/api supplied
     * @param area    : The name of the area e.g. "accounts"
     * @param api     : The name of the api  e.g. "signup"
     * @param action  : The name of the action  e.g. "login"
     * @param version : The version to check for e.g. "1.1" indicates area:version=1, api:version = 1
     */
    fun contains(area: String, api: String, action: String, version:String? = null): Boolean {
        return action(area, api, action, version) != null
    }

    /**
     * Gets the API model associated with the area.name
     */
    fun api(area: String, api: String, version: String? = null): ActionLookup? {
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
    fun action(area: String, api: String, action: String, version: String?): RouteMapping? {
        if (area.isEmpty()) return null
        if (api.isEmpty()) return null
        if (action.isEmpty()) return null
        val info = when (version) {
            null -> Triple("0:${area}", "0:${api}", "0:${action}")
            else -> {
                val parts = version.split(".")
                val areaVersion = parts[0]
                val apiVersion = parts[1]
                val actionVersion = parts[2]
                Triple("${areaVersion}:${area}", "${apiVersion}:${api}", "${actionVersion}:${action}")
            }
        }

        val areaLookup = areas.get(info.first) ?: return null
        val apiLookup = areaLookup.get(info.second) ?: return null
        return apiLookup.get(info.third)
    }

    fun visitApis(visitor: (Area, ActionLookup) -> Unit) {

        // 1. Each top level area in the system
        // e.g. {area}/{api}/{action}
        this.areas.items.forEach { area ->

            area.items.forEach { api ->
                visitor(area.area, api)
            }
        }
    }

    fun visitActions(visitor: (Area, Api, Action) -> Unit) {

        // 1. Each top level area in the system
        // e.g. {area}/{api}/{action}
        this.areas.items.forEach { area ->

            area.items.forEach { api ->

                api.items.forEach { action ->
                    visitor(area.area, api.api, action.route.action)
                }
            }
        }
    }


    companion object {

    }
}
