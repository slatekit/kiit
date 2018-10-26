package slatekit.apis.core

import slatekit.common.Context
import slatekit.common.naming.Namer
import slatekit.meta.Reflector
import kotlin.reflect.full.primaryConstructor


/**
 * The top most level qualifier in the Universal Routing Structure
 * Essentially the root of the Routing tree
 * e.g.
 *
 * Format :  {area}.{api}.{action}
 * Routes :
 *          { area_1 }
 *
 *              - { api_1 }
 *
 *                  - { action_a }
 *                  - { action_b }
 *
 *              - { api_2 }
 *
 *                  - { action_c }
 *                  - { action_d }
 *
 *         { area_2 }
 *
 *              - { api_1 }
 *
 *                  - { action_a }
 *                  - { action_b }
 *
 *              - { api_2 }
 *
 *                  - { action_c }
 *                  - { action_d }
*/
data class Routes(val areas: Lookup<Area>,
                  val namer: Namer? = null,
                  val onInstanceCreated: ((Any?) -> Unit )? = null) {

    init {
        onInstanceCreated?.let {
            visitApis({ area, api -> onInstanceCreated.invoke(api.singleton) })
        }
    }

    /**
     * Whether there is an area w/ the supplied name.
     */
    fun contains(area: String): Boolean = areas.contains(area)


    /**
     * Whether there is an api in the area supplied
     */
    fun contains(area: String, api:String): Boolean {
        return areas[area]?.apis?.contains(api) ?: false
    }


    /**
     * Whether there is an api in the area supplied
     */
    fun contains(area: String, api:String, action:String): Boolean {
        return areas[area]?.apis?.get(api)?.actions?.contains(action) ?: false
    }


    /**
     * Gets the API model associated with the area.name
     */
    fun api(area: String, name: String): Api? {
        return areas[area]?.apis?.get(name)
    }


    /**
     * gets an instance of the API for the corresponding area.name
     */
    fun instance(area:String, name:String, ctx: Context): Any? {
        val api = api(area, name)
        val instance = api?.let { info ->
            info.singleton ?: if (info.cls.primaryConstructor!!.parameters.isEmpty()) {
                Reflector.create<Any>(info.cls)
            } else {
                Reflector.createWithArgs<Any>(info.cls, arrayOf(ctx))
            }
        }
        onInstanceCreated?.invoke(instance)
        return instance
    }


    fun visitApis(visitor:(Area, Api) -> Unit) {

        // 1. Each top level area in the system
        // e.g. {area}/{api}/{action}
        this.areas.items.forEach { area ->

            area.apis.items.forEach { api ->
                visitor( area, api )
            }
        }
    }


    fun buildApiKey(area:String, name:String):String = "$area.$name"


    fun visitActions(visitor:(Area, Api, Action) -> Unit) {

        // 1. Each top level area in the system
        // e.g. {area}/{api}/{action}
        this.areas.items.forEach { area ->

            area.apis.items.forEach { api ->

                api.actions.items.forEach { action ->
                    visitor(area, api, action )
                }
            }
        }
    }
}
