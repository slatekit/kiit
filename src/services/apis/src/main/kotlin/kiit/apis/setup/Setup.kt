package kiit.apis.setup

import kiit.apis.SetupType
import kiit.apis.routes.*
import kotlin.reflect.KClass
import kiit.utils.naming.Namer
import kiit.meta.Reflector


data class LoadOptions(
    val klass: KClass<*>,
    val declared: Boolean = true,
    val singleton: Any? = null,
    val setup: SetupType = SetupType.Methods
)


class Loader(val namer: Namer?, val options: LoadOptions? = null)  {
    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/members )
     *
     */
    fun code(cls: KClass<*>, instance: Any): Pair<Api, List<RouteMapping>> {
        val api = toApi(cls, namer)
        val area = Area(api.area)

        // Get all the actions using the @ApiAction
        // Get all the methods with the apiAction annotation
        val matches = Reflector.getAnnotatedMembers<kiit.apis.Action>(cls, kiit.apis.Action::class, true)

        // Convert to RouteMapping ( route -> handler )
        val mappings: List<RouteMapping> = matches.map { item ->
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
        return Pair(api, mappings)
    }


    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/members )
     *
     */
    fun config(cls: KClass<*>, instance: Any): Pair<Api, List<RouteMapping>> {
        return Pair(Api(", "), listOf())
    }
}
