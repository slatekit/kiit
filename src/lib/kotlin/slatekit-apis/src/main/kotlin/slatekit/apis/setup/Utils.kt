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
import slatekit.utils.naming.Namer
import slatekit.meta.Reflector

fun toVerb(name: String?): Verb {
    return when (name) {
        null -> Verb.Auto
        else -> {
            val nameToCheck = name.toLowerCase()
            val verb = when {
                nameToCheck.startsWith(Verbs.AUTO) -> Verb.Auto
                nameToCheck.startsWith(Verbs.GET)  -> Verb.Get
                nameToCheck.startsWith(Verbs.DELETE) -> Verb.Delete
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

/**
 * Creates a lookup object of all the APIs and Actions ensuring that only
 * the actions that are applicable based on the host source are loaded
 * e.g. hostSource = WEB, only actions where source = ALL || source = WEB will be loaded
 */
fun toLookup(rawApis: List<Api>, hostSource: Source, namer: Namer? = null): Lookup<Area> {

    // Get the apis with actions loaded from either
    // annotations or from public methods.
    val apis = rawApis.map { it -> loadApiFromSetup(it, hostSource, namer) }

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

fun loadAll(rawApis: List<Api>, hostSource: Source = Source.All, namer: Namer? = null): Lookup<Area> {
    return toLookup(rawApis, hostSource, namer)
}

fun loadApiFromSetup(api: Api, hostSource: Source, namer: Namer?): Api {
    // If no actions, that means it was the raw input
    // during setup, so we have to load the api methods
    // from either annotations or from public methods
    val finalized = if (api.actions.size == 0) {
        val loaded = if (api.setup == SetupType.Annotated) {
            val apiAnnotated = Annotations(api.klass, api).api(namer)
            val area = rename(apiAnnotated.area, namer)
            val name = rename(apiAnnotated.name, namer)
            apiAnnotated.copy(area = area, name = name, singleton = api.singleton)
        } else { // if(api.setup == PublicMethods){
            val area = rename(api.area, namer)
            val name = rename(api.name, namer)
            val actions = Methods(api).actions(api, api.declaredOnly, namer)
            api.copy(area = area, name = name, actions = Lookup(actions) { t -> t.name })
        }
        // Filter out actions that are only applicable for the host source
        val all = loaded.actions.items
        val actions = all.filter { validate(it, hostSource) }
        val filtered = loaded.copy( actions = Lookup(actions) { t -> t.name } )
        filtered
    } else api
    return finalized
}

fun validate(action: Action, hostSource: Source):Boolean {
    val include = when(hostSource) {
        Source.All -> true
        Source.CLI -> action.sources.isMatchOrAll(Source.CLI) || action.sources.isMatchOrAll(Source.Queue)
        Source.Web -> action.sources.isMatchOrAll(listOf(Source.Web, Source.API, Source.Queue))
        Source.API -> action.sources.isMatchOrAll(listOf(Source.Web, Source.API, Source.Queue))
        else       -> false
    }
    //println("filtering name=${action.name}, include=$include")
    return include
}

fun rename(text: String, namer: Namer?): String {
    // Rename the area if namer is supplied
    val area = namer?.rename(text) ?: text
    return area
}
