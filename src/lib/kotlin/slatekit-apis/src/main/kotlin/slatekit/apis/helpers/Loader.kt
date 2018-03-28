package slatekit.apis.helpers

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.ApiRegAction
import slatekit.common.Ignore
import slatekit.common.nonEmptyOrDefault
import slatekit.meta.Reflector
import kotlin.reflect.KClass

object Loader {

    fun fromAnnotations(clsType: KClass<*>): ApiLookup {

        // 1. get the annotation on the class
        val apiAnnoRaw = Reflector.getAnnotationForClassOpt<Api>(clsType, Api::class)

        // 2. Create a copy of the final annotation taking into account the overrides.
        val apiAnno = apiAnnoRaw?.let { apiAnno ->
            ApiHelper.buildApiInfo(apiAnno, reg)
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
                val callReflect = ApiRegAction(apiAnno, member, actionName, apiActionAnno?.desc ?: "", actionRoles, actionVerb, actionProtocol)
                endpointLookup.update(actionName, callReflect)

                // add the api to the class lookup
                _apisToClasses["$apiArea.$apiName"] = apiAnno
            }
        }
        // if singleton and api host aware, set the host
        setApiHost(reg.singleton)
    }
}