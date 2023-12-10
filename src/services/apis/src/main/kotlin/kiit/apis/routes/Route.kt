package kiit.apis.routes

import kiit.apis.Verb

data class Route(val area:Area, val api:Api, val action: Action)
data class RouteMapping(val route: Route, val handler: RouteHandler)

interface RouteHandler
class MethodExecutor(val call: Call) : RouteHandler
class RouteForwarder(val globalVersion: String, val verb: Verb, val area:Area, val api:Versioned, val action:Versioned) : RouteHandler
