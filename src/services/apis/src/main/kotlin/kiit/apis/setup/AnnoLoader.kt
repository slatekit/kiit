package kiit.apis.setup

import kiit.apis.Access
import kiit.apis.AuthMode
import kiit.apis.Verb
import kiit.apis.core.Roles
import kiit.apis.core.Sources
import kiit.apis.routes.Api
import kiit.common.Source
import kiit.common.ext.orElse
import kiit.meta.Reflector
import kiit.utils.naming.Namer
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

class AnnoLoader(val cls: KClass<*>, val instance: Any, val namer: Namer?) {

    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/memebers )
     *
     */
    fun loadApi(): Api {

        // get the @Api annotation on the class
        val anno = Reflector.getAnnotationForClassOpt<kiit.apis.Api>(cls, kiit.apis.Api::class)!!
        val accessAnno = Access.parse(anno.access)
        val areaName = namer?.rename(anno.area) ?: anno.area
        val apiName = namer?.rename(anno.name) ?: anno.name

        val api = kiit.apis.routes.Api(
            areaName,
            apiName,
            anno.desc,
            AuthMode.parse(anno.auth),
            Roles(anno.roles.toList()),
            accessAnno,
            Sources(anno.sources.toList().map { Source.parse(it) }),
            Verb.parse(anno.verb),
            anno.version,
            anno.tags.toList()
        )

        return api
    }

    fun toAction(member: KCallable<*>, api: kiit.apis.routes.Api, apiAction: kiit.apis.Action, namer: Namer?): kiit.apis.routes.Action {

        val methodName = member.name
        val actionNameRaw = apiAction?.name.orElse(methodName)
        val actionName = namer?.rename(actionNameRaw) ?: actionNameRaw
        val actionDesc = apiAction.desc ?: ""
        val actionTags = apiAction?.tags?.toList() ?: listOf()

        // Default these from api if empty
        val actionAuth = References.auth(api.auth, apiAction.auth)
        val actionRoles = References.roles(api.roles, apiAction.roles)
        val actionAccess = References.access(api.access, apiAction.access)
        val actionProtocol = References.sources(api.sources, apiAction.sources)
        val actionVersion = References.version(api.version, apiAction.version)
        val actionVerb = References.verb(api.verb, apiAction.verb, actionName)

        return kiit.apis.routes.Action(
            actionName,
            actionDesc,
            actionAuth,
            actionRoles,
            actionAccess,
            actionProtocol,
            actionVerb,
            actionVersion,
            actionTags.toList()
        )
    }
}
