package kiit.apis.routes

import kiit.apis.core.Target
import kotlin.reflect.full.primaryConstructor
import slatekit.context.Context
import slatekit.utils.naming.Namer
import slatekit.meta.Reflector
import slatekit.results.Codes
import slatekit.results.Err
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes

/**
 * The top most level qualifier in the Universal Routing Structure
 * Essentially the root of the Routing tree
 * e.g.
 *
 * Format :  {area}.{api}.{action}
 * Routes :
 *          { Area 1 }
 *              - { API 1 }
 *                  - { Action a }
 *                  - { Action b }
 *
 *              - { API 2 }
 *                  - { Action c }
 *                  - { Action d }
 *
 *         { Area 2 }
 *              - { API 1 }
 *                  - { Action a }
 *                  - { Action b }
 *
 *              - { API 2 }
 *                  - { Action c }
 *                  - { Action d }
*/
data class Routes(
    val areas: Lookup<Area>,
    val namer: Namer? = null,
    val onInstanceCreated: ((Any?) -> Unit)? = null
) {

    init {
        onInstanceCreated?.let {
            visitApis({ area, api -> onInstanceCreated.invoke(api.singleton) })
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
    fun contains(area: String): Boolean = areas.contains(area)

    /**
     * Whether there is an api in the area supplied
     */
    fun contains(area: String, api: String): Boolean {
        return areas[area]?.apis?.contains(api) ?: false
    }

    /**
     * Whether there is an api in the area supplied
     */
    fun contains(area: String, api: String, action: String): Boolean {
        return areas[area]?.apis?.get(api)?.actions?.contains(action) ?: false
    }

    /**
     * Gets the API model associated with the area.name
     */
    fun api(area: String, name: String): Api? {
        return areas[area]?.apis?.get(name)
    }

    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun api(area: String, name: String, action: String, ctx: Context): Outcome<Target> {
        if (area.isEmpty()) return Outcomes.invalid("area not supplied")
        if (name.isEmpty()) return Outcomes.invalid("api not supplied")
        if (action.isEmpty()) return Outcomes.invalid("action not supplied")
        if (!contains(area, name, action)) return Outcomes.invalid(Err.of("api route $area $name $action not found"), status = Codes.NOT_FOUND)

        val api = api(area, name)!!
        val act = api.actions[action]!!
        val instance = instance(area, name, ctx)
        return instance?.let { inst ->
            Success(Target(api, act, inst))
        } ?: Outcomes.errored("api route $area $name $action not found")
    }

    /**
     * gets an instance of the API for the corresponding area.name
     */
    fun instance(area: String, name: String, ctx: Context): Any? {
        val api = api(area, name)
        val instance = api?.let { info ->
            info.singleton ?: if (info.klass.primaryConstructor!!.parameters.isEmpty()) {
                Reflector.create<Any>(info.klass)
            } else {
                Reflector.createWithArgs<Any>(info.klass, arrayOf(ctx))
            }
        }
        onInstanceCreated?.invoke(instance)
        return instance
    }

    fun visitApis(visitor: (Area, Api) -> Unit) {

        // 1. Each top level area in the system
        // e.g. {area}/{api}/{action}
        this.areas.items.forEach { area ->

            area.apis.items.forEach { api ->
                visitor(area, api)
            }
        }
    }

    fun visitActions(visitor: (Area, Api, Action) -> Unit) {

        // 1. Each top level area in the system
        // e.g. {area}/{api}/{action}
        this.areas.items.forEach { area ->

            area.apis.items.forEach { api ->

                api.actions.items.forEach { action ->
                    visitor(area, api, action)
                }
            }
        }
    }
}
