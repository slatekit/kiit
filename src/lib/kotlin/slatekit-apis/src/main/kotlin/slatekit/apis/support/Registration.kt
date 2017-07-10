/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.apis.support

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.ApiBase
import slatekit.apis.ApiReg
import slatekit.apis.core.Action
import slatekit.apis.core.Apis
import slatekit.common.*

/**
 * Created by kishorereddy on 6/12/17.
 */

/**
 * Emphermeral class to register the apis into a ListMap which is ultimately
 * returned from there and used by the ApiContainer.
 */
class Areas {

    /**
     *  ListMap will eventually contain all the areas by name.
     *  This will be returned from the registration
     *  e.g.
     *      - app   : Apis
     *      - sys   : Apis
     *      - admin : Apis
     */
    private var _areas = mutableMapOf<String, ListMap<String, ApiBase>>()
    private val _areaApis = mutableMapOf<String, MutableMap<String, ApiInfo>>()


    /**
     * The names of the areas
     */
    fun keys(): List<String> = _areas.keys.toList()


    /**
     * Whether there is an area w/ the supplied name.
     */
    fun contains(key: String): Boolean = _areas.contains(key)


    /**
     * Gets the area
     */
    operator fun get(key: String): ListMap<String, ApiBase>? = _areas[key]


    fun getApiInfo(area: String, name: String): ApiInfo? {
        return _areaApis[area]?.let { it[name] }
    }


    /**
     * registers all the apis.
     */
    fun registerAll(apis: List<ApiReg>? = null): Areas {
        apis?.forEach { register(it) }
        return this
    }


    /**
     * register via the ApiReg component
     */
    fun register(reg: ApiReg): Unit {
        register(reg.api, reg.declaredOnly, reg.roles, reg.auth, reg.protocol)
    }


    /**
     * register via the ApiReg component
     */
    fun register(api: ApiBase): Unit {
        register(api, true, null, null, "*")
    }


    /**
     * converts this to a list map of areas to apis
     */
    fun asListMap(): ListMap<String, Apis> {
        return ListMap(_areas.map { it -> Pair(it.key, Apis(it.value.entries())) })
    }


    /**
     * registers an api for dynamic calls
     */
    fun register(api: ApiBase,
                 declaredOnly: Boolean = true,
                 roles: String? = null,
                 auth: String? = null,
                 protocol: String? = "*"): Unit {
        val clsType = api.kClass

        // 1. get the annotation on the class
        val apiAnnoRaw = Reflector.getAnnotationForClass<Api>(clsType, Api::class)

        // 2. Create a copy of the final annotation taking into account the overrides.
        val apiAnno = ApiHelper.buildApiInfo(apiAnnoRaw, roles, auth, protocol)

        // 3. get the name of the api and its area ( category of apis )
        val apiName = apiAnno.name
        val apiArea = apiAnno.area.nonEmptyOrDefault("")

        // 4. get the lookup containing all the apis in a specific area
        var apiLookup = getLookup(apiArea)

        // 5. add api name to area
        apiLookup = apiLookup.add(apiName, api)
        _areas[apiArea] = apiLookup
        _areaApis[apiArea]?.let { it[apiName] = apiAnno }

        // 6. get all the methods with the apiAction annotation
        val matches = Reflector.getAnnotatedMembers<ApiAction>(clsType, ApiAction::class)
        matches.forEach { item ->

            // a) The member
            val member = item.first

            // b) Get the name of the action or default to method name
            val methodName = member.name

            // c) Annotation
            val apiActionAnno = item.second
            val actionName = apiActionAnno.name.nonEmptyOrDefault(methodName)

            // d) Get the parameters to easily check/validate params later
            val parameters = member.parameters

            // Add the action name and link it to the method + annotation
            val anyParameters = parameters.isNotEmpty() && parameters.size > 1
            val callReflect = Action(actionName, apiAnno, apiActionAnno, member, anyParameters)
            api.update(actionName, callReflect)
        }
    }


    /**
     * gets or creates a lookup that stores all the apis in a specific area
     * in the 3 part route system area/api/action.
     * @param area
     * @return
     */
    fun getLookup(area: String): ListMap<String, ApiBase> {
        val result = if (_areas.contains(area)) {
            _areas[area]!!
        }
        else {
            val lookup = ListMap<String, ApiBase>()
            _areas[area] = lookup
            _areaApis[area] = mutableMapOf<String, ApiInfo>()
            lookup
        }
        return result
    }
}