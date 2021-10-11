package slatekit.apis.setup

import slatekit.apis.routes.Action
import slatekit.apis.routes.Api
import slatekit.utils.naming.Namer

interface Loader {

    /**
     * Loads an Api and builds all its actions using the provided class info and naming convention
     */
    fun api(namer: Namer?): Api

    /**
     * Loads all the actions on the API
     */
    fun actions(api: Api, local: Boolean, namer: Namer?): List<Action>
}
