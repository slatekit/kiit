package slatekit.apis.helpers

import slatekit.apis.ApiAction
import slatekit.apis.core.Action
import slatekit.apis.core.Api
import slatekit.apis.core.Area
import slatekit.apis.core.Lookup
import slatekit.common.Ignore
import slatekit.common.Namer
import slatekit.common.nonEmptyOrDefault
import slatekit.meta.Reflector
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

object ApiLoader {

    fun loadAll(apis:List<slatekit.apis.core.Api>, namer:Namer?): Lookup<Area> {
        return Lookup(listOf(), { a -> a.name } )
    }


    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/memebers )
     *
     * @param cls  : The class representing the API
     * @param namer: The naming convention
     */
    fun load( cls: KClass<*>, namer:Namer?): Api {
        // get the @Api annotation on the class
        val annotation = Reflector.getAnnotationForClassOpt<slatekit.apis.Api>(cls, slatekit.apis.Api::class)!!
        val api = slatekit.apis.core.Api(
                cls,
                annotation.area,
                annotation.name,
                annotation.desc,
                annotation.roles,
                annotation.auth,
                annotation.verb,
                annotation.protocol,
                false,
                null
        )

        // Get all the actions using the @ApiAction
        val actions = loadActionsFromAnnotations(api, namer)
        return api.copy(actions = Lookup(actions, { t -> t.name } ) )
    }


    /**
     * Loads an api using purely just the class with explicitly supplied metadata
     * This does NOT need any annotations on the class / members and assumes and expects
     * the class to be a fairly PURE Kotlin class/object. This also expects that the
     * member actions use the same values for ( roles, protocol etc ) as the ones supplied.
     * NOTE: Use this member for obtaining very low to 0 vendor lock-in with Slate Kit as
     * you basically use plain Kotlin Objects
     *
     * @param cls  : The class representing the API
     * @param namer: The naming convention
     *
     */
    fun loadPure(cls  : KClass<*>,
              area : String,
              name : String,
              desc : String?,
              local: Boolean = true,
              roles: String  = "",
              auth : String  = "",
              verb : String  = "",
              protocol: String = "*",
              singleton:Boolean = false,
              namer: Namer? = null): Api {

        // Create initial temporary api
        // with all settings that can be used for override values
        val api = Api(cls, area, name, desc ?: "", roles, auth, verb, protocol, local, singleton)
        return loadApi(api, namer)
    }


    /**
     * Loads an api using the explicitly supplied API setup
     *
     * @param api  : The API setup
     * @param namer: The naming convention
     */
    fun loadApi(api:slatekit.apis.core.Api, namer:Namer?): Api {
        // Get all the actions using the @ApiAction
        val actions = loadActions(api, api.declaredOnly, namer)
        return api.copy(actions = Lookup(actions, { t -> t.name } ) )
    }


    fun loadActions(api: slatekit.apis.core.Api, local:Boolean, namer:Namer?): List<Action> {
        val members = Reflector.getMembers(api.cls, local, true)
        val actions:List<Action> = members.map { member -> buildAction(member, api, null, namer) }
        return actions
    }


    private fun loadActionsFromAnnotations(api: slatekit.apis.core.Api, namer:Namer?): List<Action> {

        // 1. get all the methods with the apiAction annotation
        val rawMatches = Reflector.getAnnotatedMembersOpt<ApiAction>(api.cls, ApiAction::class, api.declaredOnly)
        val rawIgnores = Reflector.getAnnotatedMembersOpt<Ignore>(api.cls, Ignore::class, api.declaredOnly)
        val rawIgnoresLookup = rawIgnores.filter { it.second != null }.map { it -> Pair(it.first.name, true) }.toMap()

        // 2. Filter out builtin methods
        val matches = rawMatches.filter{ it -> Reflector.isBuiltIn(it.first) }

        // 3. Convert to Action
        val actions:List<Action?> = matches.map { item ->

            // a) The member
            val member = item.first

            // Ensure it does not have an Ignore annotation
            if (rawIgnoresLookup.containsKey(member.name)) {
                val ignored = member.name
                null
            } else {
                buildAction(item.first, api, item.second, namer)
            }
        }

        // 4. Filter out ignored ones.
        return actions.filterNotNull()
    }


    private fun buildAction(member:KCallable<*>, api: slatekit.apis.core.Api, apiAction:ApiAction?, namer:Namer?): Action {

        val methodName = member.name
        val actionNameRaw = apiAction?.name.nonEmptyOrDefault(methodName)
        val actionRoles = apiAction?.roles ?: api.roles
        val actionVerb = apiAction?.verb ?: api.verb
        val actionProtocol = apiAction?.protocol ?: api.protocol
        val actionName = namer?.name(actionNameRaw)?.text ?: actionNameRaw
        val actionDesc = apiAction?.desc ?: ""
        return Action(
                member,
                actionName,
                actionDesc,
                actionRoles,
                actionVerb,
                actionProtocol
        )
    }
}
