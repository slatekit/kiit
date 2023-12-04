package kiit.apis.setup

import kiit.apis.SetupType
import kiit.apis.routes.*
import kiit.meta.Reflector
import kiit.utils.naming.Namer
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

class Loader(val namer: Namer?)  {
    fun routes(version:String, setup:List<ApiSetup>) : VersionAreas {
        val actions = setup.map {
            when(it.setup) {
                SetupType.Annotated -> code(it.klass, it.singleton!!)
                SetupType.Config -> config(it.klass, it.singleton!!, it.content)
            }
        }
        val areaNames = actions.map { Area(it.api.area) }.distinctBy { it.fullname }
        val apis = areaNames.map { area -> AreaApis(area, actions.filter { it.api.area == area.name }) }
        val areas = VersionAreas(version, apis)
        return areas
    }


    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/members )
     *
     */
    fun code(cls: KClass<*>, instance: Any): ApiActions {
        val loader = AnnoLoader(cls, instance, namer)
        val api = loader.loadApi()
        val area = Area(api.area)

        // Get all the actions using the @ApiAction
        // Get all the methods with the apiAction annotation
        val rawMatches = Reflector.getAnnotatedMembers<kiit.apis.Action>(cls, kiit.apis.Action::class, true)
        val matches = rawMatches.filter { it.first.visibility != null && it.first.visibility!! == KVisibility.PUBLIC }

        // Convert to RouteMapping ( route -> handler )
        val mappings: List<RouteMapping> = matches.map { item ->

            val action = loader.toAction(item.first, api, item.second, namer)

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


    fun config(cls: KClass<*>, instance: Any, json:String = ""): ApiActions {
        val parser = JSONParser()
        val doc = parser.parse(json) as JSONObject
        val conf = ConfigLoader(cls, instance)
        // Load root "api" attributes ( similar to @Api )
        val api = conf.loadApi(doc)

        // Get all public members
        val methods = Reflector.getMembers(cls,true, false, KVisibility.PUBLIC)
        val methodMap = methods.map { it.name to it }.toMap()

        // Load actions from the "actions" child
        val actions = conf.loadActions(Area(api.area), api, methodMap, doc)
        return ApiActions(api, actions)
    }
}
