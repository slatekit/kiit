package kiit.apis.routes

import kiit.apis.ApiRequest
import kiit.apis.ApiServer
import kiit.apis.Verb
import kiit.apis.setup.GlobalVersion
import kiit.results.Outcome
import kiit.results.builders.Outcomes


data class ApiContext(val server:ApiServer)
data class Route(val area:Area, val api:Api, val action: Action)
data class RouteMapping(val route: Route, val handler: RouteHandler)


interface RouteHandler {
}


class MethodExecutor(val call: Call) : RouteHandler
class RouteForwarder(val globalVersion: String, val verb: Verb, val area:Area, val api:Versioned, val action:Versioned) : RouteHandler
