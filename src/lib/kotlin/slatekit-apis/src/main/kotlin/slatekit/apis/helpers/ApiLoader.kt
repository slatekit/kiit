package slatekit.apis.helpers

import slatekit.apis.*
import slatekit.apis.core.*
import slatekit.apis.setup.Setup
import slatekit.apis.core.Action
import slatekit.apis.core.Api
import slatekit.apis.core.Protocols
import slatekit.common.Ignore
import slatekit.common.naming.Namer
import slatekit.common.nonEmptyOrDefault
import slatekit.meta.Reflector
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility

object ApiLoader {

    fun loadAll(rawApis: List<slatekit.apis.core.Api>, namer: Namer? = null): Lookup<Area> {

        // Get the apis with actions loaded from either
        // annotations or from public methods.
        val apis = rawApis.map { it -> loadApiFromSetup(it, namer) }

        // Routes: area.api.action
        // Get unique areas
        val areaNames = apis.map { it.area }.distinct()

        // Now get all the areas -> apis
        val areas = areaNames.map {
            val areaName = it
            val apisForArea = apis.filter { it.area == areaName }
            val area = Area(areaName, Lookup(apisForArea, { api -> api.name }))
            area
        }

        // List + Map of areas.
        return Lookup(areas, { area -> area.name })
    }

    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/memebers )
     *
     * @param cls : The class representing the API
     * @param namer: The naming convention
     */
    fun loadAnnotated(cls: KClass<*>, namer: Namer?): Api {
        // get the @Api annotation on the class
        val annotation = Reflector.getAnnotationForClassOpt<slatekit.apis.Api>(cls, slatekit.apis.Api::class)!!
        val api = slatekit.apis.core.Api(
                cls,
                annotation.area,
                annotation.name,
                annotation.desc,
                annotation.roles.toList(),
                Access.parse(annotation.access),
                AuthMode.parse(annotation.auth),
                annotation.protocols.toList().map { Protocol.parse(it) },
                Verb.parse(annotation.verb),
                false,
                Setup.Annotated
        )

        // Get all the actions using the @ApiAction
        val actions = loadActionsFromAnnotations(api, namer)
        return api.copy(actions = Lookup(actions, { t -> t.name }))
    }

    /**
     * Loads an api using purely just the class with explicitly supplied metadata
     * This does NOT need any annotations on the class / members and assumes and expects
     * the class to be a fairly PURE Kotlin class/object. This also expects that the
     * member actions use the same values for ( roles, protocol etc ) as the ones supplied.
     * NOTE: Use this member for obtaining very low to 0 vendor lock-in with Slate Kit as
     * you basically use plain Kotlin Objects
     *
     * @param cls : The class representing the API
     * @param namer: The naming convention
     *
     */
    fun loadPublic(
        cls: KClass<*>,
        area: String,
        name: String,
        desc: String?,
        local: Boolean = true,
        roles: Roles = Roles.empty,
        access: Access = Access.Public,
        auth: AuthMode = AuthMode.Keyed,
        verb: Verb = Verb.Auto,
        protocol: Protocols = Protocols.all,
        singleton: Boolean = false,
        namer: Namer? = null
    ): Api {

        // Create initial temporary api
        // with all settings that can be used for override values
        val api = Api(cls, area, name, desc ?: "", roles, access, auth, protocol, verb, local, singleton)
        return loadWithMeta(api, namer)
    }

    /**
     * Loads an api using the explicitly supplied API setup
     *
     * @param api : The API setup
     * @param namer: The naming convention
     */
    fun loadWithMeta(api: slatekit.apis.core.Api, namer: Namer?): Api {
        // Get all the actions using the @ApiAction
        val actions = loadActionsFromPublicMethods(api, api.declaredOnly, namer)
        return api.copy(actions = Lookup(actions, { t -> t.name }))
    }

    fun loadActionsFromPublicMethods(api: slatekit.apis.core.Api, local: Boolean, namer: Namer?): List<Action> {
        val members = Reflector.getMembers(api.cls, local, true, KVisibility.PUBLIC)
        val actions: List<Action> = members.map { member -> buildAction(member, api, null, namer) }
        return actions
    }

    private fun loadActionsFromAnnotations(api: slatekit.apis.core.Api, namer: Namer?): List<Action> {

        // 1. get all the methods with the apiAction annotation
        val rawMatches = Reflector.getAnnotatedMembers<Action>(api.cls, Action::class, api.declaredOnly)
        val rawIgnores = Reflector.getAnnotatedMembers<Ignore>(api.cls, Ignore::class, api.declaredOnly)
        val rawIgnoresLookup = rawIgnores.map { it -> Pair(it.first.name, true) }.toMap()

        // 2. Filter out builtin methods
        val matches = rawMatches.filter { it -> !Reflector.isBuiltIn(it.first) }

        // 3. Convert to Action
        val actions: List<Action?> = matches.map { item ->

            // a) The member
            val member = item.first

            // Ensure it does not have an Ignore annotation
            if (rawIgnoresLookup.containsKey(member.name)) {
                null
            } else {
                buildAction(item.first, api, item.second, namer)
            }
        }

        // 4. Filter out ignored ones.
        return actions.filterNotNull()
    }

    private fun buildAction(member: KCallable<*>, api: slatekit.apis.core.Api, apiAction: Action?, namer: Namer?): Action {

        val methodName = member.name
        val actionNameRaw = apiAction?.name.nonEmptyOrDefault(methodName)
        val actionName = namer?.rename(actionNameRaw) ?: actionNameRaw
        val actionDesc = apiAction?.desc ?: ""
        val actionTags = apiAction?.tags?.toList() ?: listOf()

        // Default these from api if empty
        val actionRoles = apiAction?.roles?.orElse(api.roles) ?: Roles.empty
        val actionProtocol = apiAction?.protocols?.orElse(api.protocols) ?: Protocols(listOf(Protocol.All))
        val rawVerb = apiAction?.verb?.orElse(api.verb) ?: Verb.Auto

        // Determine the actual verb
        val actionVerb = when (rawVerb) {
            is Verb.Auto -> determineVerb(actionNameRaw)
            else -> rawVerb
        }
        return Action(
                member,
                actionName,
                actionDesc,
                actionRoles,
                api.access,
                api.auth,
                actionProtocol,
                actionVerb,
                actionTags
        )
    }

    private fun determineVerb(name: String): Verb {
        val nameToCheck = name.toLowerCase()
        val verb = when {
            nameToCheck.startsWith(Verbs.Read) -> Verb.Read
            nameToCheck.startsWith(Verbs.Delete) -> Verb.Delete
            nameToCheck.startsWith(Verbs.Patch) -> Verb.Patch
            nameToCheck.startsWith("create") -> Verb.Post
            nameToCheck.startsWith("update") -> Verb.Put
            else -> Verb.Post
        }
        return verb
    }

    private fun loadApiFromSetup(api: Api, namer: Namer?): Api {

        // If no actions, that means it was the raw input
        // during setup, so we have to load the api methods
        // from either annotations or from public methods
        return if (api.actions.size == 0) {
            if (api.setup == Setup.Annotated) {
                val apiAnnotated = loadAnnotated(api.cls, namer)
                val area = name(apiAnnotated.area, namer)
                val name = name(apiAnnotated.name, namer)
                apiAnnotated.copy(area = area, name = name, singleton = api.singleton)
            } else { // if(api.setup == PublicMethods){
                val area = name(api.area, namer)
                val name = name(api.name, namer)
                val actions = loadActionsFromPublicMethods(api, api.declaredOnly, namer)
                api.copy(area = area, name = name, actions = Lookup(actions, { t -> t.name }))
            }
        } else api
    }

    private fun name(text: String, namer: Namer?): String {
        // Rename the area if namer is supplied
        val area = namer?.rename(text) ?: text
        return area
    }
}
