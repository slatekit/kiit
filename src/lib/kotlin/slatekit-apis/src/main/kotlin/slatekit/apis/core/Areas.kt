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

package slatekit.apis.core

import slatekit.apis.*
import slatekit.apis.helpers.ApiHelper
import slatekit.apis.helpers.Loader
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
class Areas(val apiHost:ApiContainer, val namer:Namer?) {

    /**
     *  ListMap will eventually contain all the areas by name.
     *  This will be returned from the registration
     *  e.g.
     *      - app   : Apis
     *      - sys   : Apis
     *      - admin : Apis
     */
    private var _areas = mutableMapOf<String, ListMap<String, Actions>>()
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
    operator fun get(area: String): ListMap<String, Actions>? = _areas[area]


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
        val apiReg = apiAnnoRaw?.let { apiAnno ->
            ApiHelper.buildApiInfo(apiAnno, reg)
        } ?: ApiHelper.buildApiInfo(reg)

        // 3. get the name of the api and its area ( category of apis )
        val apiName = namer?.name(apiReg.name)?.text ?: apiReg.name
        val apiArea = namer?.name(apiReg.area.nonEmptyOrDefault(""))?.text ?: apiReg.area.nonEmptyOrDefault("")

        // 4. Get the actions
        val actionList = Loader.loadActions(apiReg, namer)

        // 5. add api name to area
        var apiLookup = getLookup(apiArea)
        val actions = Actions(apiReg, actionList)
        apiLookup = apiLookup.add(apiName, actions)
        _areas[apiArea] = apiLookup
        _areaApis[apiArea]?.let { it[apiName] = apiReg }

        // add the api to the class lookup
        _apisToClasses["$apiArea.$apiName"] = apiReg

        // if singleton and api host aware, set the host
        setApiHost(reg.singleton)
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
        setApiHost(instance)
        return instance
    }


    fun visitApis(visitor:(ApiReg, Actions) -> Unit):Unit {
        val areas = this.keys()

        // 1. Each top level area in the system
        // e.g. {area}/{api}/{action}
        for(area in areas )
        {
            // 2. Get lookup of apis in area
            val apis = this[area]
            apis?.let { areaApis ->

                // 3. All apis
                val all = areaApis.all()
                if (all.isNotEmpty()) {

                    // 4. Get all the api names
                    val keys = apis.keys()

                    keys.forEach { key ->

                        // 5. The actual api
                        val api = apis[key]

                        api?.let { api ->

                            val apiInfo = this.getApiInfo(area, key)
                            visitor(apiInfo!!, api)
                        }
                    }
                }
            }
        }
    }


    fun visitActions(visitor:(ApiReg, ApiRegAction) -> Unit):Unit {
        val areas = this.keys()

        // 1. Each top level area in the system
        // e.g. {area}/{api}/{action}
        for(area in areas )
        {
            // 2. Get lookup of apis in area
            val apis = this[area]
            apis?.let { areaApis ->

                // 3. All apis
                val all = areaApis.all()
                if (all.isNotEmpty()) {

                    // 4. Get all the api names
                    val keys = apis.keys()

                    keys.forEach { key ->

                        // 5. The actual api
                        val api = apis[key]

                        api?.let { api ->

                            // 6. Now get all the api actions
                            val apiInfo = this.getApiInfo(area, key)
                            api.actions().values().forEach{ action ->
                                //println(apiInfo?.area + "/" + apiInfo?.name + "/" + action.name)
                                visitor(apiInfo!!, action)
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * gets or creates a lookup that stores all the apis in a specific area
     * in the 3 part route system area/api/action.
     * @param area
     * @return
     */
    private fun getLookup(area: String): ListMap<String, Actions> {
        val result = if (_areas.contains(area)) {
            _areas[area]!!
        }
        else {
            val lookup = ListMap<String, Actions>()
            _areas[area] = lookup
            _areaApis[area] = mutableMapOf<String, ApiReg>()
            lookup
        }
        return result
    }


    private fun setApiHost(item:Any?):Unit {
        if(item is ApiHostAware) {
            item.setApiHost(apiHost)
        }
    }
}
