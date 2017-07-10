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

import slatekit.apis.ApiConstants.AuthModeAppKey
import slatekit.apis.ApiConstants.AuthModeAppRole
import slatekit.apis.ApiConstants.AuthModeKeyRole
import slatekit.apis.support.ApiHelper.getReferencedValue
import slatekit.common.ApiKey
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.auth.AuthFuncs.convertKeys
import slatekit.common.auth.AuthFuncs.isKeyValid
import slatekit.common.auth.AuthFuncs.matchRoles
import slatekit.common.splitToMapWithPairs
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.unAuthorized


/**
 * Class used to authenticate an api with support for 3 modes:
 * 1. app-key : user needs to supply an api-key to authenticate
 * 2. app-role: application specific role mode ( will validate against roles )
 * 3. key-role: dual authentication mode that will validate key + role.
 *
 * Need to initialize with api-keys
 */
open class Auth(
        protected val keys: List<ApiKey>?,
        private val callback: ((String, Request, String, String) -> Result<Boolean>)? = null,
        protected val headerApiKeyName: String = "api-key") {

    private val _keyLookup = convertKeys(keys ?: listOf())


    /**
     * whether or not the authorization is valid for the mode, roles supplied.
     *
     * @param mode
     * @param roles
     * @param roleParents
     * @return
     */
    open fun isAuthorized(cmd: Request, mode: String, roles: String, roleParents: String): Result<Boolean> {
        // CASE 1: no roles ? authorization not applicable
        return if (roles.isNullOrEmpty())
            ok()

        // CASE 2: Guest
        else if (roles == "?")
            ok()

        // CASE 3: App Roles + Key Roles mode
        else if (AuthModeKeyRole == mode) {
            isKeyRoleValid(cmd, roles, roleParents)
        }
        // CASE 4: App-Role mode
        else if (AuthModeAppRole == mode) {
            isAppRoleValid(cmd, roles, roleParents)
        }
        // CASE 5: api-key + role
        else if (AuthModeAppKey == mode) {
            val keyResult = isKeyRoleValid(cmd, roles, roleParents)
            if (!keyResult.success) {
                keyResult
            }
            else {
                isAppRoleValid(cmd, roles, roleParents)
            }
        }
        else
            unAuthorized()
    }


    open fun isKeyRoleValid(cmd: Request, actionRoles: String, parentRoles: String): Result<Boolean> {

        // Validate using the callback if supplied,
        // otherwise use built-in key check
        return callback?.let { call ->
            call(AuthModeAppKey, cmd, actionRoles, parentRoles)
        } ?: isKeyValid(cmd.opts, _keyLookup, headerApiKeyName, actionRoles, parentRoles)
    }


    open fun isAppRoleValid(cmd: Request, actionRoles: String, parentRoles: String): Result<Boolean> {

        return if (callback != null) {
            callback!!(AuthModeAppRole, cmd, actionRoles, parentRoles)
        }
        else {
            // Get the expected role from either action or possible reference to parent
            val expectedRoles = getReferencedValue(actionRoles, parentRoles)

            // Get the user roles
            val actualRole = getUserRoles(cmd)
            val actualRoles = actualRole.splitToMapWithPairs(',')

            // Now match.
            matchRoles(expectedRoles, actualRoles)
        }
    }


    protected open fun getUserRoles(cmd: Request): String = ""
}
