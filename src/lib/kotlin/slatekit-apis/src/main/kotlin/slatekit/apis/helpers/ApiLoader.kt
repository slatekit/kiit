package slatekit.apis.helpers

import slatekit.apis.*
import slatekit.apis.core.*
import slatekit.apis.setup.Setup
import slatekit.apis.core.Api
import slatekit.apis.core.Protocols
import slatekit.apis.loader.AnnoLoader
import slatekit.apis.loader.MethodLoader
import slatekit.apis.loader.toApi
import slatekit.apis.loader.toLookup
import slatekit.common.naming.Namer
import kotlin.reflect.KClass

object ApiLoader {

    fun loadAll(rawApis: List<slatekit.apis.core.Api>, namer: Namer? = null): Lookup<Area> {
        return toLookup(rawApis, namer)
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

        val api = toApi(cls, area, name, desc ?: "", local, roles, access, auth, verb, protocol, singleton)
        return loadWithMeta(api, namer)
    }


    /**
     * Loads an api using the explicitly supplied API setup
     *
     * @param api : The API setup
     * @param namer: The naming convention
     */
    fun loadWithMeta(api: slatekit.apis.core.Api, namer: Namer?): Api {
        val loader = MethodLoader(api)
        // Get all the actions using the @ApiAction
        val actions = loader.loadActions(api, api.declaredOnly, namer)
        return api.copy(actions = Lookup(actions, { t -> t.name }))
    }


    private fun loadApiFromSetup(api: Api, namer: Namer?): Api {

        // If no actions, that means it was the raw input
        // during setup, so we have to load the api methods
        // from either annotations or from public methods
        return if (api.actions.size == 0) {
            if (api.setup == Setup.Annotated) {
                val apiAnnotated = AnnoLoader(api.cls).loadApi(namer)
                val area = name(apiAnnotated.area, namer)
                val name = name(apiAnnotated.name, namer)
                apiAnnotated.copy(area = area, name = name, singleton = api.singleton)
            } else { // if(api.setup == PublicMethods){
                val area = name(api.area, namer)
                val name = name(api.name, namer)
                val actions = MethodLoader(api).loadActions(api, api.declaredOnly, namer)
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
