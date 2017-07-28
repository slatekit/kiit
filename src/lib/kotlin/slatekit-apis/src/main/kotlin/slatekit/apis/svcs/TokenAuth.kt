package slatekit.apis.svcs

import slatekit.apis.ApiConstants
import slatekit.apis.core.Auth
import slatekit.common.ApiKey
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.auth.AuthFuncs
import slatekit.common.results.ResultFuncs
import slatekit.common.splitToMapWithPairs


/**
 * Class used to authenticate an api with support for 3 modes:
 * 1. app-key : user needs to supply an api-key to authenticate ( api-keys are not unique to users )
 * 2. app-role: user needs to login to get an Authorization token ( the token is unique to a user )
 * 3. key-role: dual authentication mode that will validate key + role.
 *
 * Need to initialize with api-keys
 */
open class TokenAuth(
        protected val keys: List<ApiKey>?,
        private val callback: ((String, Request, String) -> Result<Boolean>)? = null,
        protected val headerKey: String = "api-key") : Auth {

    private val _keyLookup = AuthFuncs.convertKeys(keys ?: listOf())


    /**
     * whether or not the authorization is valid for the mode, roles supplied.
     *
     * @param mode
     * @param roles
     * @param roleParents
     * @return
     */
    override fun isAuthorized(req: Request, mode: String, roles: String, roleParents: String): Result<Boolean> {
        // 1. No roles or guest ?
        if (isRoleEmptyOrGuest(roles))
            return ResultFuncs.ok()

        // 2. Get the actual role if the action references the parent via @parent
        val role = determineRole(roles, roleParents)

        // 3. Now determine roles based on api-key or role
        return when(mode){
            ApiConstants.AuthModeKeyRole -> isKeyRoleValid(req, role)
            ApiConstants.AuthModeAppRole -> isAppRoleValid(req, role)
            ApiConstants.AuthModeAppKey  -> isAppRoleKeyValid(req, role)
            else                         -> ResultFuncs.unAuthorized()
        }
    }


    /**
     * Determines whether the request is authorized based on an api-key based role.
     * This maps a header key to a token to roles
     * e.g e.g. api-key = abc123 = dev,qa,ops
     *
     * NOTES:
     * 1. Headers/Inputs has key "api-key"
     * 2. "api-key" has value "abc123"
     * 3. The token "abc123" maps internally to roles "dev,qa,ops"
     * 4. We check the role supplied to one of these roles
     */
    fun isKeyRoleValid(req: Request, role:String): Result<Boolean> {

        // Validate using the callback if supplied,
        // otherwise use built-in key check
        return callback?.let { call ->
            call(ApiConstants.AuthModeAppKey, req, role)
        } ?: AuthFuncs.isKeyValid(req.opts, _keyLookup, headerKey, role)
    }


    /**
     * Determines whether the request is authorized based on role.
     * This maps a header key to a token to roles
     * e.g e.g. Authorization = abc123 = dev,qa,ops
     *
     * NOTES:
     * 1. Headers/Inputs has key "Authorization"
     * 2. "Authorization" has value "abc123"
     * 3. The token "abc123" maps internally to roles "dev,qa,ops"
     * 4. We check the role supplied to one of these roles
     */
    fun isAppRoleValid(req: Request, role: String): Result<Boolean> {

        return if (callback != null) {
            callback!!(ApiConstants.AuthModeAppRole, req, role)
        }
        else {

            // Get the user roles
            val actualRole = getUserRoles(req)
            val actualRoles = actualRole.splitToMapWithPairs(',')

            // Now match.
            AuthFuncs.matchRoles(role, actualRoles)
        }
    }


    /**
     * performs both app role validation and key role validation.
     */
    fun isAppRoleKeyValid(req:Request, roles:String): Result<Boolean> {
        val keyResult = isKeyRoleValid(req, roles)
        if(!keyResult.success) return keyResult
        val appResult = isAppRoleValid(req, roles)
        return appResult
    }
}
