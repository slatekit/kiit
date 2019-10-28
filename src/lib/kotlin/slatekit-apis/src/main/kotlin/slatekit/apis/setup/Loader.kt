package slatekit.apis.setup

import slatekit.apis.core.Action
import slatekit.apis.core.Api
import slatekit.common.naming.Namer

interface Loader {

    /**
     * Loads an Api and builds all its actions using the provided class info and naming convention
     */
    fun loadApi(namer: Namer?): Api


    /**
     * Loads all the actions on the API
     */
    fun loadActions(api: Api, local: Boolean, namer: Namer?): List<Action>
}



