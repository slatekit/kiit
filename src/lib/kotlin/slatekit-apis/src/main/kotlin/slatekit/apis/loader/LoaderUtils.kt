package slatekit.apis.loader

import slatekit.apis.*
import slatekit.apis.core.*
import slatekit.apis.core.Action
import slatekit.apis.core.Protocols
import slatekit.apis.helpers.ApiLoader
import slatekit.apis.setup.Setup
import slatekit.common.naming.Namer
import slatekit.common.nonEmptyOrDefault
import slatekit.meta.Reflector
import kotlin.reflect.KCallable
import kotlin.reflect.KClass


fun toVerb(name: String?): Verb {
    return when(name) {
        null -> Verb.Auto
        else -> {
            val nameToCheck = name.toLowerCase()
            val verb = when {
                nameToCheck.startsWith(Verbs.Read)   -> Verb.Read
                nameToCheck.startsWith(Verbs.Delete) -> Verb.Delete
                nameToCheck.startsWith(Verbs.Patch)  -> Verb.Patch
                nameToCheck.startsWith(Verbs.Create) -> Verb.Post
                nameToCheck.startsWith(Verbs.Update) -> Verb.Put
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
fun toApi(cls: KClass<*>, instance:Any?, namer: Namer?): slatekit.apis.core.Api {

    // get the @Api annotation on the class
    val anno = Reflector.getAnnotationForClassOpt<slatekit.apis.Api>(cls, slatekit.apis.Api::class)!!
    val api = slatekit.apis.core.Api(
            cls,
            anno.area,
            anno.name,
            anno.desc,
            Roles(anno.roles.toList()),
            Access.parse(anno.access),
            AuthMode.parse(anno.auth),
            Protocols(anno.protocols.toList().map { Protocol.parse(it) }),
            Verb.parse(anno.verb),
            false,
            instance,
            Setup.Annotated
    )
    return api
}



fun toApi(cls: KClass<*>,
          area: String,
          name: String,
          desc: String?,
          local: Boolean = true,
          roles: Roles = Roles.empty,
          access: Access = Access.Public,
          auth: AuthMode = AuthMode.Keyed,
          verb: Verb = Verb.Auto,
          protocol: Protocols = Protocols.all,
          singleton: Boolean = false ) : slatekit.apis.core.Api {
    // Create initial temporary api
    // with all settings that can be used for override values
    val api = slatekit.apis.core.Api(cls, area, name, desc
            ?: "", roles, access, auth, protocol, verb, local, singleton)
    return api
}



fun toAction(member: KCallable<*>, api: slatekit.apis.core.Api, apiAction: slatekit.apis.Action?, namer: Namer?): Action {

    val methodName = member.name
    val actionNameRaw = apiAction?.name.nonEmptyOrDefault(methodName)
    val actionName = namer?.rename(actionNameRaw) ?: actionNameRaw
    val actionDesc = apiAction?.desc ?: ""
    val actionTags = apiAction?.tags?.toList() ?: listOf()

    // Default these from api if empty
    val actionRoles = Roles.of(apiAction?.roles ?: arrayOf()).orElse(api.roles)
    val actionProtocol = Protocols.of(apiAction?.protocols ?: arrayOf()).orElse(api.protocols)
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
            actionRoles,
            api.access,
            api.auth,
            actionProtocol,
            actionVerb,
            actionTags
    )
}


fun toLookup(rawApis: List<slatekit.apis.core.Api>, namer: Namer? = null): Lookup<Area> {

    // Get the apis with actions loaded from either
    // annotations or from public methods.
    val apis = rawApis.map { it -> ApiLoader.loadApiFromSetup(it, namer) }

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