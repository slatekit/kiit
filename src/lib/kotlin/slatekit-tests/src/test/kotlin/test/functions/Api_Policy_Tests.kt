package test.functions

import kotlinx.coroutines.runBlocking
import org.junit.Test
import slatekit.functions.middleware.After
import slatekit.functions.middleware.Before
import slatekit.functions.middleware.Filter
import slatekit.functions.middleware.Handler
import slatekit.meta.kClass
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.results.flatMap
import kotlin.reflect.KCallable

class Api_Policy_Tests {


    @Test
    fun test() {
        val host = ApiHost(listOf(MyAPI1(), MyAPI2()))
        runBlocking {
            val result1 = host.call(ApiRequest("myapi1", "add", mapOf("curr" to 1)))
            println(result1)
            println("\n")

            val result2 = host.call(ApiRequest("myapi1", "add", mapOf("curr" to 1, "count" to 2)))
            println(result2)
            println("\n")

            val result3 = host.call(ApiRequest("myapi2", "sub", mapOf("curr" to 1)))
            println(result3)
        }
    }

}


interface Api {
    val api: String
}


data class ApiRequest(val api: String, val action: String, val args: Map<String, Any>)
data class ApiCall(val req: ApiRequest, val api: Api, val action: KCallable<*>, val args: List<Any?>)
typealias  ApiResult = Any?


open class MyAPI1 : Api, Filter<ApiRequest>, Before<ApiRequest>, After<ApiRequest, ApiResult>, slatekit.functions.middleware.Error<ApiRequest, ApiResult> {

    override val api: String = "myapi1"


    override suspend fun onBefore(req: ApiRequest) {
        println("before: ${req.api}.${req.action}")
    }


    override suspend fun onFilter(req: ApiRequest): Outcome<Boolean> {
        println("filter: ${req.api}.${req.action} args=${req.args.size}")
        return if(req.args.size > 1) {
            Outcomes.invalid("Invalid number of inputs, expected 1 input, got ${req.args.size}")
        } else {
            Outcomes.success(true)
        }
    }


    override suspend fun onAfter(req: ApiRequest, res: Outcome<ApiResult>) {
        println("after : success=${res.success}, code=${res.code}")
    }


    override suspend fun onError(req: ApiRequest, res: Outcome<ApiResult>) {
        println("error : success=${res.success}, code=${res.code}")
    }



    fun add(curr: Int): Int = curr + 1

}

class MyAPI2 : MyAPI1(), Filter<ApiRequest>, Before<ApiRequest>, After<ApiRequest, ApiResult>, slatekit.functions.middleware.Error<ApiRequest, ApiResult>, Handler<ApiRequest, ApiResult> {

    override val api: String = "myapi2"

    override suspend fun handle(req: ApiRequest, op: (ApiRequest) -> Outcome<ApiResult>): Outcome<ApiResult> {
        println("handle: ${req.api}.${req.action}")
        return op(req)
    }


    fun sub(curr: Int): Int = curr - 1
}


class ApiHost(val apis: List<Api>) {


    suspend fun call(api: String, action: String, args: Map<String, Any>): Outcome<*> {
        val req = ApiRequest(api, action, args)
        return call(req)
    }


    suspend fun call(req: ApiRequest): Outcome<*> {
        val api = apis.first { it.api == req.api }
        val action = api.kClass.members.first { it.name == req.action }
        val arg = req.args.keys.map { req.args[it] }.first()
        return exec(ApiCall(req, api, action, listOf(arg)))
    }


    suspend fun exec(call: ApiCall): Outcome<*> {
        val api = call.api
        val action = call.action
        val arg = call.args.first()
        val result = impose(call) {
            Outcomes.of(action.call(api, arg))
        }
        return result
    }


    suspend fun impose(call: ApiCall, op: (ApiCall) -> Outcome<*>): Outcome<*> {
        val api = call.api
        val req = call.req

        // Hook: Before
        if (api is Before<*>) {
            (api as Before<ApiRequest>).onBefore(req)
        }

        // Filter
        val filter:Outcome<Boolean> = when(api) {
            is Filter<*> -> (api as Filter<Any>).onFilter(req)
            else         -> Outcomes.success(true)
        }

        // Exec
        val result: Outcome<Any?> = filter.flatMap {
            if(api is Handler<*, *>) {
                (api as Handler<ApiRequest, Any?>).handle(req) {
                    op(call)
                }
            } else {
                op(call)
            }
        }

        // Hook: After
        if (api is After<*, *>) {
            (api as After<ApiRequest, Any?>).onAfter(req, result)
        }

        // Hook: Error
        if(!result.success && api is slatekit.functions.middleware.Error<*, *>){
            (api as slatekit.functions.middleware.Error<ApiResult, Any?>).onError(req, result)
        }
        return result
    }
}