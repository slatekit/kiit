/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.apis.core

import slatekit.apis.ApiRequest
import slatekit.apis.hooks.Targets
import slatekit.common.*
import slatekit.common.requests.Request
import slatekit.common.requests.RequestSupport
import slatekit.meta.KTypes
import slatekit.results.*
import slatekit.results.builders.Notices
import slatekit.results.builders.Outcomes
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend

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
    suspend fun validateCall(
        request: ApiRequest,
        fetcher: (Request) -> Notice<Target>,
        allowSingleDefaultParam: Boolean = false
    ): Outcome<Target> {
        val req = request.request
        val fullName = req.fullName
        val args = req.data
        val apiRefCheck = Targets().process(Outcomes.of(request))

        return when (apiRefCheck) {
            is Failure -> Outcomes.invalid("bad request : $fullName: inputs not supplied")
            is Success -> {
                val apiRef = apiRefCheck.value
                val target = apiRef.target!!
                val action = target.action

                // 1 param with default argument.
                if (allowSingleDefaultParam && action.isSingleDefaultedArg() && args.size() == 0) {
                    Outcomes.success(target)
                }
                // Param: Raw ApiCmd itself!
                else if (action.isSingleArg() && action.paramsUser.isEmpty()) {
                    Outcomes.success(target)
                }
                // Data - check args needed
                else if (!allowSingleDefaultParam && action.hasArgs && args.size() == 0)
                    Outcomes.invalid("bad request : $fullName: inputs not supplied")

                // Data - ensure matching args
                else if (action.hasArgs) {
                    val argCheck = validateArgs(request, action, args)
                    val result = argCheck.map { target }
                    result
                } else
                    Outcomes.success(target)
            }
        }
    }

    private fun validateArgs(request: ApiRequest, action: Action, args: Inputs): Outcome<Boolean> {
        // Check each parameter to api call
        val errors = (0 until action.paramsUser.size).map { ndx ->
            val param = action.paramsUser[ndx]
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
            Outcomes.invalid(ErrorList(failures, "Invalid request"))
        } else {
            Outcomes.success(true)
        }
    }
}
