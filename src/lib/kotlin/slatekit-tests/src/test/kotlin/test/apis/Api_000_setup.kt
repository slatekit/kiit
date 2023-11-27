package test.apis

import kiit.results.*
import kiit.apis.Access
import kiit.apis.ApiRequest
import kiit.apis.AuthMode
import kiit.apis.Verb
import kiit.apis.core.Roles
import kiit.apis.core.Sources
import kiit.meta.kClass
import kiit.results.builders.Outcomes
import kotlin.reflect.KCallable
import kotlin.reflect.KClass


data class Api2(
    val area: String = "",
    val name: String = "",
    val desc: String = "",
    val auth: AuthMode = AuthMode.None,
    val roles: Roles = Roles.empty,
    val access: Access = Access.Public,
    val sources: Sources = Sources.all,
    val verb: Verb = Verb.Auto,
    val version: String = "",
    val tags: List<String> = listOf()
)

data class Action2(
    val name: String = "",
    val desc: String = "",
    val auth: AuthMode = AuthMode.Parent,
    val roles: Roles = Roles.empty,
    val access: Access = Access.Public,
    val sources: Sources = Sources.all,
    val verb: Verb = Verb.Post,
    val version:String = "",
    val tags: List<String> = listOf()
)

class ApiContext {

}

data class Route(val area:Version,
                 val api:Version,
                 val action: Version,
)


data class Version(val name:String, val version:String = "")

class Router {
    private var routes:MutableMap<String, Route> = mutableMapOf()

    fun on(area:String,
           api:String,
           action:String,
           desc: String = "",
           auth: AuthMode = AuthMode.Parent,
           roles: Roles = Roles.empty,
           access: Access = Access.Public,
           sources: Sources = Sources.all,
           verb: Verb = Verb.Post,
           tags: List<String> = listOf(),
           executor: RouteExecutor) {
        this.on(Version(area), Version(api), Version(action), executor)
    }

    fun on(api:Api2, action:Action2, executor: RouteExecutor) {

    }

    fun on(area:String, api:String, action:String, executor: RouteExecutor) {
        val route = Route(Version(area, "1"), Version(api, "1"), Version(action, "1"))
    }

    fun on(area:Version, api:Version, action:Version, executor: RouteExecutor) {
        val route = Route(area, api, action)
    }
}

interface RouteExecutor {
    suspend fun handle(context: ApiContext, request: ApiRequest, route: Route) : Outcome<*>
}


class MethodExecutor(val alias:String, val callable: KCallable<*>) : RouteExecutor {
    override suspend fun handle(context: ApiContext, request: ApiRequest, route: Route): Outcome<*> {
        return Outcomes.success("")
    }
}



class MethodForwarder(val area:Version, val api:Version, val action:Version) : RouteExecutor {

    constructor(area:String, api:String, action:String)
            : this(Version(area, "1"), Version(api, "1"), Version("action", "1"))

    override suspend fun handle(context: ApiContext, request: ApiRequest, route: Route): Outcome<*> {
        return Outcomes.success("")
    }
}


class ClassExecutors {
    fun add(alias:String, instance: Any) {
        val type: KClass<*> = instance.kClass
    }
}


class SimpleCalsApi {
    fun add(a:Int, b:Int) : Int = a + b
    fun sub(a:Int, b:Int) : Int = a - b
}