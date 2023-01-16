package kiit.apis.setup

import kiit.apis.routes.Action
import kiit.apis.routes.Api
import kiit.utils.naming.Namer

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
