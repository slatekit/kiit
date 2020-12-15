package slatekit.apis.setup

import kotlin.reflect.KVisibility
import slatekit.apis.routes.Lookup
import slatekit.apis.routes.Action
import slatekit.apis.routes.Api
import slatekit.common.naming.Namer
import slatekit.meta.Reflector

class Methods(val api: Api) : Loader {
    /**
     * Loads an api using class and method annotations e.g. @Api on class and @ApiAction on members.
     * NOTE: This allows all the API setup to be in 1 place ( in the class/memebers )
     *
     * @param cls : The class representing the API
     * @param namer: The naming convention
     */
    override fun api(namer: Namer?): Api {
        // Get all the actions using the @ApiAction
        val actions = actions(api, api.declaredOnly, namer)
        return api.copy(actions = Lookup(actions, { t -> t.name }))
    }

    override fun actions(api: Api, local: Boolean, namer: Namer?): List<Action> {
        val members = Reflector.getMembers(api.klass, local, true, KVisibility.PUBLIC)
        val actions: List<Action> = members.map { member -> toAction(member, api, null, namer) }
        return actions
    }
}
