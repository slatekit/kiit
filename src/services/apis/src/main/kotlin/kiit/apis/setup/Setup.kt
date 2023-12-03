package kiit.apis.setup

import kiit.apis.SetupType
import kiit.apis.routes.*
import kotlin.reflect.KClass
import kiit.utils.naming.Namer
import kiit.meta.Reflector


data class ApiSetup(
    val klass: KClass<*>,
    val singleton: Any? = null,
    val setup: SetupType = SetupType.Annotated,
    val declared: Boolean = true
)


data class GlobalVersion(val version:String, val apis:List<ApiSetup>)


fun routes(versions: List<GlobalVersion>) : Router {
    return Router()
}

fun global(version:String, apis:List<ApiSetup>) : GlobalVersion = GlobalVersion(version, apis)

fun api(klass: KClass<*>, singleton: Any?, setup: SetupType = SetupType.Annotated, declared: Boolean = true)
    : ApiSetup = ApiSetup(klass, singleton, setup, declared)


class Loader(val namer: Namer?)  {
    fun routes(setup:List<ApiSetup>) : Router {
        val actions = setup.map {
            when(it.setup) {
                SetupType.Annotated -> code(it.klass, it.singleton!!)
                SetupType.Methods -> config(it.klass, it.singleton!!)
            }
        }
        val areaNames = actions.map { Area(it.api.area) }.distinctBy { it.fullname }
        val apis = areaNames.map { area -> AreaApis(area, actions.filter { it.api.area == area.name }) }
        val areas = kiit.apis.routes.VersionAreas(apis)
        return Router(areas, namer)
    }


    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/members )
     *
     */
    fun code(cls: KClass<*>, instance: Any): ApiActions {
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
        return ApiActions(api, mappings)
    }


    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/members )
     *
     */
    fun config(cls: KClass<*>, instance: Any): ApiActions {
        return ApiActions(Api(", "), listOf())
    }
}
