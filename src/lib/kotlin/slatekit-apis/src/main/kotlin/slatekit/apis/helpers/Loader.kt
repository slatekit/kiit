package slatekit.apis.helpers

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.ApiReg
import slatekit.apis.ApiRegAction
import slatekit.apis.core.Actions
import slatekit.common.Ignore
import slatekit.common.Namer
import slatekit.common.nonEmptyOrDefault
import slatekit.meta.Reflector
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

object Loader {

    fun fromAnnotations(clsType: KClass<*>, namer: Namer?): Actions {

        // 1. get the annotation on the class
        val apiAnno = Reflector.getAnnotationForClassOpt<Api>(clsType, Api::class)!!

        // 2. get the name of the api and its area using the naming convention
        val apiName = namer?.name(apiAnno.name)?.text ?: apiAnno.name
        val apiArea = namer?.name(apiAnno.area.nonEmptyOrDefault(""))?.text ?: apiAnno.area.nonEmptyOrDefault("")

        // 3. create the api registration component
        val reg = ApiReg(
            clsType, apiAnno.area,
            apiAnno.name,
            apiAnno.desc,
            apiAnno.roles,
            apiAnno.auth,
            apiAnno.verb,
            apiAnno.protocol,
            false,
            null
        )
        val actions = loadActions(reg, namer)
        return Actions(reg, actions)
    }


    fun loadActions(reg:ApiReg, namer:Namer?): List<Pair<String,ApiRegAction>> {

        // 1. get all the methods with the apiAction annotation
        val rawMatches = Reflector.getAnnotatedMembersOpt<ApiAction>(reg.cls, ApiAction::class, reg.declaredOnly)
        val rawIgnores = Reflector.getAnnotatedMembersOpt<Ignore>(reg.cls, Ignore::class, reg.declaredOnly)
        val rawIgnoresLookup = rawIgnores.filter { it.second != null }.map { it -> Pair(it.first.name, true) }.toMap()

        // 2. Filter out builtin methods
        val matches = rawMatches.filter { mem ->
            mem.first.name != "equals" && mem.first.name != "hashCode" && mem.first.name != "toString"
        }

        // 3. Convert to ApiRegAction
        val routes:List<Pair<String,ApiRegAction>?> = matches.map { item ->

            // a) The member
            val member = item.first

            // Ensure it does not have an Ignore annotation
            if (rawIgnoresLookup.containsKey(member.name)) {
                val ignored = member.name
                null
            } else {
                buildAction(item.first, reg, item.second, namer)
            }
        }

        // 4. Filter out ignored ones.
        val finalRoutes = routes.filterNotNull()
        return finalRoutes
    }


    fun buildAction(member:KCallable<*>, reg:ApiReg, apiAction:ApiAction?, namer:Namer?): Pair<String,ApiRegAction> {

        val methodName = member.name
        val actionNameRaw = apiAction?.name.nonEmptyOrDefault(methodName)
        val actionRoles = apiAction?.roles ?: reg.roles
        val actionVerb = apiAction?.verb ?: reg.verb
        val actionProtocol = apiAction?.protocol ?: reg.protocol
        val actionName = namer?.name(actionNameRaw)?.text ?: actionNameRaw
        val actionDesc = apiAction?.desc ?: ""
        val action = ApiRegAction(
            reg,
            member,
            actionName,
            actionDesc,
            actionRoles,
            actionVerb,
            actionProtocol
        )
        return Pair(actionName, action)
    }
}
