package kiit.apis.setup

import kiit.apis.routes.Api
import kiit.apis.routes.Area
import kiit.apis.routes.RouteMapping

interface Loader {

    /**
     * Loads an Api and builds all its actions using the provided class info and naming convention
     */
    fun api(): Pair<Api, List<RouteMapping>>

    /**
     * Loads all the actions on the API
     */
    fun actions(area: Area, api: Api): List<RouteMapping>
}
