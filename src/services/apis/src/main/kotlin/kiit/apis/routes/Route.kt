package kiit.apis.routes

import kiit.apis.ApiRequest
import kiit.apis.ApiServer
import kiit.results.Outcome
import kiit.results.builders.Outcomes


data class ApiContext(val server:ApiServer)
data class Route(val area:Area, val api:Api, val action: Action)
data class RouteMapping(val route: Route, val handler: RouteHandler)


interface RouteHandler {
    suspend fun handle(ctx: ApiContext, request: ApiRequest, route: Route) : Outcome<*>
}


class MethodExecutor(val call: Call) : RouteHandler {
    override suspend fun handle(ctx: ApiContext, request: ApiRequest, route: Route): Outcome<*> {
        return Outcomes.success("")
    }
}


class RouteForwarder(val area:Area, val api:Versioned, val action:Versioned) : RouteHandler {
    override suspend fun handle(ctx: ApiContext, request: ApiRequest, route: Route): Outcome<*> {
        return Outcomes.success("")
    }
}
