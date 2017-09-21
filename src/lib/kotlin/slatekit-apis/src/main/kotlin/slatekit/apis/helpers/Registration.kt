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

package slatekit.apis.helpers

import slatekit.apis.*
import slatekit.common.*
import slatekit.meta.Reflector
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Created by kishorereddy on 6/12/17.
 */

/**
 * Emphermeral class to register the apis into a ListMap which is ultimately
 * returned from there and used by the ApiContainer.
 */
class Areas(val namer:Namer?) {

    /**
     *  ListMap will eventually contain all the areas by name.
     *  This will be returned from the registration
     *  e.g.
     *      - app   : Apis
     *      - sys   : Apis
     *      - admin : Apis
     */
    private var _areas = mutableMapOf<String, ListMap<String, ApiLookup>>()
    private val _areaApis = mutableMapOf<String, MutableMap<String, ApiReg>>()
    private var _apisToClasses = mutableMapOf<String, ApiReg>()


    /**
     * The names of the areas
     */
    fun keys(): List<String> = _areas.keys.toList()


    /**
     * Whether there is an area w/ the supplied name.
     */
    fun contains(area: String): Boolean = _areas.contains(area)


    /**
     * Whether there is an api in the area supplied
     */
    fun contains(area: String, name:String): Boolean {
        return _areas[area]?.contains(name) ?: false
    }


    /**
     * Whether there is an area w/ the supplied name.
     */
    fun contains(area: String, name:String, action:String): Boolean =
            _areas[area]?.get(name)?.contains(action) ?: false

    /**
     * Gets the area
     */
    operator fun get(area: String): ListMap<String, ApiLookup>? = _areas[area]


    fun getApi(area:String, name:String): ApiReg {
        val key = buildApiKey(area, name)
        val reg = _apisToClasses[key]!!
        return reg
    }


    fun getApiInfo(area: String, name: String): ApiReg? {
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
        registerInternal(reg)
    }

    /**
     * registers an api for dynamic calls
     */
    fun register(cls  : KClass<*>,
                 area : String,
                 name : String,
                 desc : String?,
                 declaredOnly: Boolean = true,
                 roles: String  = "",
                 auth : String  = "",
                 verb : String  = "",
                 protocol: String = "*",
                 singleton:Boolean = false):Unit {
        registerInternal(ApiReg(cls, area, name, desc ?: "", roles, auth, verb, protocol, declaredOnly, singleton))
    }


    /**
     * registers an api for dynamic calls
     */
    fun registerInternal(reg: ApiReg): Unit {
        val clsType = reg.cls

        // 1. get the annotation on the class
        val apiAnnoRaw = Reflector.getAnnotationForClassOpt<Api>(clsType, Api::class)

        // 2. Create a copy of the final annotation taking into account the overrides.
        val hasAnnotations = apiAnnoRaw != null
        val apiAnno = apiAnnoRaw?.let { apiAnno ->
            ApiHelper.buildApiInfo(apiAnnoRaw, reg)
        } ?: ApiHelper.buildApiInfo(reg)

        // 3. get the name of the api and its area ( category of apis )
        val apiName = namer?.name(apiAnno.name)?.text ?: apiAnno.name
        val apiArea = namer?.name(apiAnno.area.nonEmptyOrDefault(""))?.text ?: apiAnno.area.nonEmptyOrDefault("")

        // 4. get the lookup containing all the apis in a specific area
        var apiLookup = getLookup(apiArea)

        // 5. add api name to area
        val endpointLookup = ApiLookup(apiAnno)
        apiLookup = apiLookup.add(apiName, endpointLookup)
        _areas[apiArea] = apiLookup
        _areaApis[apiArea]?.let { it[apiName] = apiAnno }

        // 6. get all the methods with the apiAction annotation
        val rawMatches = Reflector.getAnnotatedMembersOpt<ApiAction>(clsType, ApiAction::class, reg.declaredOnly)
        val rawIgnores = Reflector.getAnnotatedMembersOpt<Ignore>(clsType, Ignore::class, reg.declaredOnly)
        val rawIgnoresLookup = rawIgnores.filter { it.second != null }.map{ it -> Pair(it.first.name, true )}.toMap()

        val matches = rawMatches.filter { mem ->
            mem.first.name != "equals" && mem.first.name != "hashCode" && mem.first.name != "toString"
        }
        matches.forEach { item ->

            // a) The member
            val member = item.first

            // Ensure it does not have an Ignore annotation
            if(rawIgnoresLookup.containsKey(member.name)) {
                val ignored = member.name
            }
            else {
                // b) Get the name of the action or default to method name
                val methodName = member.name

                // c) Annotation
                val apiActionAnno = item.second
                val actionNameRaw = apiActionAnno?.name.nonEmptyOrDefault(methodName)
                val actionRoles = apiActionAnno?.roles ?: apiAnno.roles
                val actionVerb = apiActionAnno?.verb ?: apiAnno.verb
                val actionProtocol = apiActionAnno?.protocol ?: apiAnno.protocol
                val actionName = namer?.name(actionNameRaw)?.text ?: actionNameRaw

                // d) Get the parameters to easily check/validate params later
                val parameters = member.parameters

                // Add the action name and link it to the method + annotation
                val anyParameters = parameters.isNotEmpty() && parameters.size > 1
                val callReflect = ApiRegAction(apiAnno, member, actionName, apiActionAnno?.desc ?: "", actionRoles, actionVerb, actionProtocol, anyParameters)
                endpointLookup.update(actionName, callReflect)

                // add the api to the class lookup
                _apisToClasses["$apiArea.$apiName"] = apiAnno
            }
        }
    }


    fun buildApiKey(area:String, name:String):String = "$area.$name"


    fun getInstance(area:String, name:String, ctx: Context): Any {
        val key = buildApiKey(area, name)
        val reg = _apisToClasses[key]!!
        val instance = reg.singleton ?: if(reg.cls.primaryConstructor!!.parameters.isEmpty()) {
            Reflector.create<Any>(reg.cls)
        }
        else {
            Reflector.createWithArgs<Any>(reg.cls, arrayOf(ctx))
        }
        return instance
    }


    /**
     * gets or creates a lookup that stores all the apis in a specific area
     * in the 3 part route system area/api/action.
     * @param area
     * @return
     */
    private fun getLookup(area: String): ListMap<String, ApiLookup> {
        val result = if (_areas.contains(area)) {
            _areas[area]!!
        }
        else {
            val lookup = ListMap<String, ApiLookup>()
            _areas[area] = lookup
            _areaApis[area] = mutableMapOf<String, ApiReg>()
            lookup
        }
        return result
    }
}