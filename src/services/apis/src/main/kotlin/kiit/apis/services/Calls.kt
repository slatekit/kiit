/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.apis.services

import kiit.apis.ApiRequest
import kiit.apis.core.Target
import kiit.apis.routes.Action
import kiit.apis.routes.Call
import kiit.apis.routes.MethodExecutor
import kiit.apis.routes.RouteMapping
import kiit.common.values.Inputs
import kiit.requests.Request
import kiit.meta.KTypes
import kiit.results.*
import kiit.results.builders.Outcomes
import kotlin.reflect.KClass

object Calls {

    /**
     * https://stackoverflow.com/questions/47654537/how-to-run-suspend-method-via-reflection
     */
    suspend fun callMethod(cls: KClass<*>, inst: Any, name: String, args: Array<Any?>): Any? {
        val mem = cls.members.find { m -> m.name == name }
        val params = arrayOf(inst, *args)
        val result = mem?.let {
            if(it.isSuspend) {
                kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn { cont ->
                    it.call(*params, cont)
                }
            } else {
                it.call(*params)
            }
        }
        return result
    }

    /**
     * whether or not the api call represented by the area.api.action exists. e.g. "app.users.invite"
     * and the parameters are valid.
     *
     * @param req : the command input
     * @return
     */
    fun validateCall(
        request: ApiRequest,
        fetcher: (Request) -> Outcome<Target>,
        allowSingleDefaultParam: Boolean = false
    ): Outcome<RouteMapping> {
        val req = request.request
        val fullName = req.fullName
        val args = req.data
        val apiRefCheck = request.host.get(req.verb, req.area, req.name, req.action)
        return apiRefCheck?.let { check ->
            val target = request.target!!
            val action = target.route.action
            val executor = target.handler as MethodExecutor
            val call = executor.call

            // 1 param with default argument.
            val res = if (allowSingleDefaultParam && call.isSingleDefaultedArg() && args.size() == 0) {
                Outcomes.success(target)
            }
            // Param: Raw ApiCmd itself!
            else if (call.isSingleArg() && call.paramsUser.isEmpty()) {
                Outcomes.success(target)
            }
            // Data - check args needed
            else if (!allowSingleDefaultParam && call.hasArgs && args.size() == 0)
                Outcomes.invalid("bad request : $fullName: inputs not supplied")

            // Data - ensure matching args
            else if (call.hasArgs) {
                val argCheck = validateArgs(request, action, call, args)
                val result = argCheck.map { target }
                result
            } else
                Outcomes.success(target)
            res
        } ?: Outcomes.errored("Unable to find action")
    }

    private fun validateArgs(request: ApiRequest, action: Action, call: Call, args: Inputs): Outcome<Boolean> {
        // Check each parameter to api call
        val errors = (0 until call.paramsUser.size).map { ndx ->
            val param = call.paramsUser[ndx]
            val name = param.name ?: ""
            val exists = when(param.type) {
                KTypes.KDocType -> {
                    // NOTE: For FILES:
                    // The Reading of the multi-part can be done only one time for
                    // KTor web server. This means if we load the file/doc to check
                    // for its existence then we can not read it again during loading
                    // of the values later on.
                    // One way to address this is to load / check it here and cache it.
                    // However, it could be a large file, so this is questionable.
                    // For now, we are not checking for supplied files here and instead
                    // just attempting to load them later on.
                    true
                }
                else -> args.containsKey(name)
            }
            when(exists) {
                false -> Err.on(name, "", "Missing")
                true  -> null
            }
        }
        val failures = errors.filterNotNull()
        // Any errors ?
        return if (failures.isNotEmpty()) {
            Outcomes.invalid(Err.ErrorList(failures, "Invalid request"))
        } else {
            Outcomes.success(true)
        }
    }
}
