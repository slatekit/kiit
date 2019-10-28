package slatekit.apis.setup

import slatekit.apis.core.*
import slatekit.common.naming.Namer
import slatekit.meta.Reflector
import kotlin.reflect.KVisibility

class MethodLoader(val api:Api) : Loader {
    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/memebers )
     *
     * @param cls : The class representing the API
     * @param namer: The naming convention
     */
    override fun loadApi(namer: Namer?): Api {
        // Get all the actions using the @ApiAction
        val actions = loadActions(api, api.declaredOnly, namer)
        return api.copy(actions = Lookup(actions, { t -> t.name }))
    }


    override fun loadActions(api: slatekit.apis.core.Api, local:Boolean, namer: Namer?): List<Action> {
        val members = Reflector.getMembers(api.cls, local, true, KVisibility.PUBLIC)
        val actions: List<Action> = members.map { member -> toAction(member, api, null, namer) }
        return actions
    }
}