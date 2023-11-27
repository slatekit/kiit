package kiit.apis.setup

import kiit.apis.routes.*
import kotlin.reflect.KClass
import kiit.utils.naming.Namer
import kiit.meta.Reflector

class AnnotationLoader(val cls: KClass<*>, val instance: Any, val namer: Namer?) : Loader {
    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/members )
     *
     */
    override fun api(): Pair<Api, List<RouteMapping>> {
        val api = toApi(cls, namer)
        val area = Area(api.area)

        // Get all the actions using the @ApiAction
        val mappings = actions(area, api)
        return Pair(api, mappings)
    }

    /**
     * Load all actions available in the API
     */
    override fun actions(area: Area, api: Api): List<RouteMapping> {

        // Get all the methods with the apiAction annotation
        val matches = Reflector.getAnnotatedMembers<kiit.apis.Action>(cls, kiit.apis.Action::class, true)

        // Convert to RouteMapping ( route -> handler )
        val actions: List<RouteMapping> = matches.map { item ->
            val action = toAction(item.first, api, item.second, namer)

            // area/api/action objects ( with version info )
            val route = Route(area, api, action)

            // Reflection based KCallable
            val call = Call(cls, item.first, instance)

            // Type of route handler
            val handler = MethodExecutor(call)

            // Final mapping of route -> handler
            RouteMapping(route, handler)
        }

        return actions
    }
}
