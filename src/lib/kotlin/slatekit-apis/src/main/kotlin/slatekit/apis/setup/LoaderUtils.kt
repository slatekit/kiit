package slatekit.apis.setup

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import slatekit.apis.*
import slatekit.apis.SetupType
import slatekit.apis.core.*
import slatekit.apis.routes.Action
import slatekit.apis.routes.Api
import slatekit.apis.core.Sources
import slatekit.apis.routes.Lookup
import slatekit.apis.routes.Area
import slatekit.common.Source
import slatekit.common.ext.orElse
import slatekit.common.naming.Namer
import slatekit.meta.Reflector

fun toVerb(name: String?): Verb {
    return when (name) {
        null -> Verb.Auto
        else -> {
            val nameToCheck = name.toLowerCase()
            val verb = when {
                nameToCheck.startsWith(Verbs.AUTO) -> Verb.Auto
                nameToCheck.startsWith(Verbs.GET)  -> Verb.Get
                nameToCheck.startsWith(Verbs.Delete) -> Verb.Delete
                nameToCheck.startsWith(Verbs.PATCH) -> Verb.Patch
                nameToCheck.startsWith(Verbs.CREATE) -> Verb.Post
                nameToCheck.startsWith(Verbs.UPDATE) -> Verb.Put
                else -> Verb.Post
            }
            verb
        }
    }
}

/**
 * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
 * NOTE: This allows all the API setup to be in 1 place ( in the class/memebers )
 *
 * @param cls : The class representing the API
 * @param namer: The naming convention
 */
fun toApi(cls: KClass<*>, instance: Any?, access: Access?, namer: Namer?): Api {

    // get the @Api annotation on the class
    val anno = Reflector.getAnnotationForClassOpt<slatekit.apis.Api>(cls, slatekit.apis.Api::class)!!
    val accessAnno = Access.parse(anno.access)
    val api = Api(
        cls,
        anno.area,
        anno.name,
        anno.desc,
        AuthMode.parse(anno.auth),
        Roles(anno.roles.toList()),
        accessAnno.min(access),
        Sources(anno.sources.toList().map { Source.parse(it) }),
        Verb.parse(anno.verb),
        false,
        instance,
        SetupType.Annotated
    )
    return api
}

fun toApi(
    cls: KClass<*>,
    area: String,
    name: String,
    desc: String?,
    local: Boolean = true,
    roles: Roles = Roles.empty,
    access: Access = Access.Public,
    auth: AuthMode = AuthMode.Keyed,
    verb: Verb = Verb.Auto,
    protocol: Sources = Sources.all,
    singleton: Boolean = false
): Api {
    // Create initial temporary api
    // with all settings that can be used for override values
    val api = Api(cls, area, name, desc
        ?: "", auth, roles, access, protocol, verb, local, singleton)
    return api
}

fun toAction(member: KCallable<*>, api: Api, apiAction: slatekit.apis.Action?, namer: Namer?): Action {

    val methodName = member.name
    val actionNameRaw = apiAction?.name.orElse(methodName)
    val actionName = namer?.rename(actionNameRaw) ?: actionNameRaw
    val actionDesc = apiAction?.desc ?: ""
    val actionTags = apiAction?.tags?.toList() ?: listOf()

    // Default these from api if empty
    val actionAuth = AuthMode.parse( apiAction?.auth?.orElse(api.auth.name) ?: AuthModes.PARENT).orElse(api.auth)
    val actionRoles = Roles.of(apiAction?.roles ?: arrayOf()).orElse(api.roles)
    val actionAccess = (apiAction?.access?.let{ Access.parse(it) } ?: api.access).orElse(api.access)
    val actionProtocol = Sources.of(apiAction?.sources ?: arrayOf()).orElse(api.sources)
    val rawVerb = toVerb(apiAction?.verb).orElse(api.verb)

    // Determine the actual verb
    val actionVerb = when (rawVerb) {
        is Verb.Auto -> toVerb(actionNameRaw)
        else -> rawVerb
    }
    return Action(
        member,
        actionName,
        actionDesc,
        actionAuth,
        actionRoles,
        actionAccess,
        actionProtocol,
        actionVerb,
        actionTags
    )
}

fun toLookup(rawApis: List<Api>, namer: Namer? = null): Lookup<Area> {

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

fun loadAll(rawApis: List<Api>, namer: Namer? = null): Lookup<Area> {
    return toLookup(rawApis, namer)
}

fun loadApiFromSetup(api: Api, namer: Namer?): Api {

    // If no actions, that means it was the raw input
    // during setup, so we have to load the api methods
    // from either annotations or from public methods
    return if (api.actions.size == 0) {
        if (api.setup == SetupType.Annotated) {
            val apiAnnotated = AnnoLoader(api.klass, api).loadApi(namer)
            val area = rename(apiAnnotated.area, namer)
            val name = rename(apiAnnotated.name, namer)
            apiAnnotated.copy(area = area, name = name, singleton = api.singleton)
        } else { // if(api.setup == PublicMethods){
            val area = rename(api.area, namer)
            val name = rename(api.name, namer)
            val actions = MethodLoader(api).loadActions(api, api.declaredOnly, namer)
            api.copy(area = area, name = name, actions = Lookup(actions, { t -> t.name }))
        }
    } else api
}

fun rename(text: String, namer: Namer?): String {
    // Rename the area if namer is supplied
    val area = namer?.rename(text) ?: text
    return area
}
