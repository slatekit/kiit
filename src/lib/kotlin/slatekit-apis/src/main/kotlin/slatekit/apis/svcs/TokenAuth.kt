package slatekit.apis.svcs

import slatekit.apis.ApiConstants
import slatekit.apis.core.Auth
import slatekit.common.ApiKey
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.auth.AuthFuncs
import slatekit.common.encrypt.Encryptor
import slatekit.common.results.ResultFuncs
import slatekit.common.splitToMapWithPairs


/**
 * Class used to authenticate an api with support for 3 modes:
 *
 * 1. api-key : user needs to supply an api-key to authenticate ( api-keys are not unique to users )
 * 2. token   : user needs to login to get an Authorization token ( the token is unique to a user )
 * 3. hybrid  : dual authentication mode that will validate key + token.
 *
 * @param keys        : The list of api keys supported ( which contain roles ).
 *                      These are used for actions where 1 ore more keys are used by many users
 * @param callback    : Callback used for handing the actual logic for validating an action
 */
open class TokenAuth(
        protected val keys: List<ApiKey>,
        protected val enc: Encryptor?,
        private val callback: ((String, Request, String) -> Result<Boolean>)? = null,
        private val headerKey: String = "api-key") : Auth {

    private val _keyLookup = AuthFuncs.convertKeys(keys )


    /**
     * whether or not the authorization is valid for the mode, roles supplied.
     *
     * @param req          : The api request object
     * @param authMode     : The auth mode for the api
     * @param rolesOnAction: The roles setup for the action on the api
     * @param rolesOnApi   : The roles setup for the api itself
     * @return
     */
    override fun isAuthorized(req: Request, authMode: String, rolesOnAction: String, rolesOnApi: String): Result<Boolean> {
        // 1. No roles or guest ?
        if (isRoleEmptyOrGuest(rolesOnAction))
            return ResultFuncs.ok()

        // 2. Get the actual role if the action references the parent via @parent
        val role = determineRole(rolesOnAction, rolesOnApi)

        // 3. Now determine roles based on api-key or role
        return when(authMode){
            ApiConstants.AuthModeKeyRole -> isKeyRoleValid(req, role)
            ApiConstants.AuthModeAppRole -> isTokenRoleValid(req, role)
            ApiConstants.AuthModeAppKey  -> isKeyTokenRoleValid(req, role)
            else                         -> ResultFuncs.unAuthorized()
        }
    }


    /**
     * Determines whether the request is authorized based on an api-key based role.
     * This expects a meta parameter named "api-key" containing a guid that matches
     * one of the keys this was initialized with.
     *
     * @sample: "api-key" = abc123
     *
     * NOTES:
     * 1. Meta parameter has key "api-key"
     * 2. "api-key" has value "abc123"
     * 3. The api key "abc123" maps internally to one of key in @see: keys
     * 4. The matched key has associated roles
     */
    open fun isKeyRoleValid(req: Request, role:String): Result<Boolean> {

        // Validate using the callback if supplied,
        // otherwise use built-in key check
        return callback?.let { call ->
            call(ApiConstants.AuthModeAppKey, req, role)
        } ?: AuthFuncs.isKeyValid(req.meta, _keyLookup, headerKey, role)
    }


    /**
     * Determines whether the request is authorized based on a token.
     * This expects a meta parameter named "Authorization" containing a string token
     *
     * NOTES:
     * 1. Meta parameter has key "Authorization"
     * 2. Authorization" has value "abc123"
     * 3. The token "abc123" maps internally to a user
     * 4. We look up the user identified by the token and get their roles
     */
    open fun isTokenRoleValid(req: Request, role: String): Result<Boolean> {

        return if (callback != null) {
            callback.invoke(ApiConstants.AuthModeAppRole, req, role)
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
    private fun isKeyTokenRoleValid(req:Request, roles:String): Result<Boolean> {
        val keyResult = isKeyRoleValid(req, roles)
        if(!keyResult.success) return keyResult
        return isTokenRoleValid(req, roles)
    }
}
