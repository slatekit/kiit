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

package slatekit.apis.support


import slatekit.apis.Api
import slatekit.apis.ApiConstants
import slatekit.apis.core.Action
import slatekit.apis.core.Auth
import slatekit.apis.core.Call
import slatekit.common.*
import slatekit.common.encrypt.Encryptor
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.unAuthorized

object ApiHelper {
    val _call = Call()


    val _typeDefaults = mapOf(
            "String" to "",
            "Boolean" to false,
            "Int" to 0,
            "Long" to 0L,
            "Double" to 0.0,
            "DateTime" to DateTime.now()
    )


    /**
     * String match factoring in the wildcard "*"
     */
    fun isValidMatch(actual: String, expected: String): Boolean {
        return if (actual.isNullOrEmpty() || actual == "*")
            true
        else
            actual == expected
    }


    /**
     * Builds arguments supplied into the Inputs class which
     * is the base class for inputs in the request.
     */
    fun buildArgs(inputs: List<Pair<String, Any>>?): InputArgs {

        // fill args
        val rawArgs = inputs?.let { all -> all.toMap() } ?: mapOf()
        val args = InputArgs(rawArgs)
        return args
    }


    /**
     * builds the request
     */
    fun buildRequest(path: String,
                     inputs: List<Pair<String, Any>>?,
                     headers: List<Pair<String, Any>>?): Request {

        val tokens = path.split('.').toList()
        val args = buildArgs(inputs)
        val opts = buildArgs(headers)
        val apiCmd = Request(path, tokens, tokens[0], tokens[1], tokens[2], "get", args, opts)
        return apiCmd
    }


    /**
     *  Checks the action and api to ensure the current request (cmd) is authorizated to
     *  make the call
     */
    fun isAuthorizedForCall(cmd: Request, call: Action, auth: Auth?): Result<Boolean> {
        val noAuth = auth == null

        // CASE 1: No auth for action
        return if (noAuth && (call.action.roles == ApiConstants.RoleGuest || call.action.roles.isNullOrEmpty())) {
            ok()
        }
        // CASE 2: No auth for parent
        else if (noAuth && call.action.roles == ApiConstants.RoleParent
                && call.api.roles == ApiConstants.RoleGuest) {
            ok()
        }
        // CASE 3: No auth and action requires roles!
        else if (noAuth) {
            unAuthorized(msg = "Unable to authorize, authorization provider not set")
        }
        else {
            // auth-mode, action roles, api roles
            auth?.isAuthorized(cmd, call.api.auth, call.action.roles, call.api.roles)
                    ?: unAuthorized(msg = "Unable to authorize, authorization provider not set")
        }
    }


    fun getReferencedValue(primaryValue: String, parentValue: String): String {

        // Role!
        return if (!primaryValue.isNullOrEmpty()) {
            if (primaryValue == ApiConstants.RoleParent) {
                parentValue
            }
            else
                primaryValue
        }
        // Parent!
        else if (!parentValue.isNullOrEmpty()) {
            parentValue
        }
        else
            ""
    }


    fun fillArgs(callReflect: Action, cmd: Request, args: Inputs, allowLocalIO: Boolean = false,
                 enc: Encryptor? = null): Array<Any> {
        // Check 1: No args ?
        return if (!callReflect.hasArgs)
            arrayOf()
        // Check 2: 1 param with default and no args
        else if (callReflect.isSingleDefaultedArg() && args.size() == 0) {
            val argType = callReflect.paramList[0].type.toString()
            val defaultVal = if (_typeDefaults.contains(argType)) _typeDefaults[argType] else null
            arrayOf(defaultVal ?: "")
        }
        else {
            _call.fillArgsForMethod(callReflect.member, cmd, args, allowLocalIO, enc)
        }
    }


    /**
     * copies the annotation taking into account the overrides
     *
     * @param ano
     * @param roles
     * @param auth
     * @param protocol
     * @return
     */
    fun buildApiInfo(ano: Api,
                     roles: String? = null,
                     auth: String? = null,
                     protocol: String? = null): ApiInfo {

        val finalRoles = roles ?: ano.roles
        val finalAuth = auth ?: ano.auth
        val finalProtocol = protocol ?: ano.protocol
        return ApiInfo(ano.area, ano.name, ano.desc, finalRoles, finalAuth, ano.verb, finalProtocol)
    }
}
