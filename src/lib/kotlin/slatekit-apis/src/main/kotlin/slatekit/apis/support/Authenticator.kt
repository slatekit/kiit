package slatekit.apis.support

import slatekit.apis.AuthMode
import slatekit.apis.core.Auth
import slatekit.apis.core.Roles
import slatekit.common.auth.AuthFuncs
import slatekit.common.ext.splitToMapWithPairs
import slatekit.common.info.ApiKey
import slatekit.common.requests.Request
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

/**
 * Class used to authenticate an api with support for 3 modes:
 *
 * 1. api-key : user needs to supply an api-key to authenticate ( api-keys are not unique to users )
 * 2. token   : user needs to login to get an Authorization token ( the token is unique to a user )
 * 3. hybrid  : dual authentication mode that will validate key + token.
 *
 * @param keys : The list of api keys supported ( which contain roles ).
 *                      These are used for actions where 1 ore more keys are used by many users
 * @param callback : Callback used for handing the actual logic for validating an action
 */
open class Authenticator(
    protected val keys: List<ApiKey>,
    private val headerKey: String = "api-key"
) : Auth {

    override fun check(req: Request, authMode: AuthMode, roles: Roles): Outcome<Boolean> {
        return isAuthorized(req, authMode, roles).toOutcome()
    }

    private val keyLookup = AuthFuncs.convertKeys(keys)

    /**
     * whether or not the authorization is valid for the mode, roles supplied.
     *
     * @param req : The api request object
     * @param authMode : The auth mode for the api
     * @param rolesOnAction: The roles setup for the action on the api
     * @param rolesOnApi : The roles setup for the api itself
     * @return
     */
    override fun isAuthorized(
        req: Request,
        authMode: AuthMode,
        roles:Roles
    ): Outcome<Boolean> {
        return when {
            roles.isEmpty || roles.allowGuest -> Outcomes.success(true)
            authMode == AuthMode.Keyed        -> validateApiKey(req, roles)
            authMode == AuthMode.Token        -> validateToken(req, roles)
            else                              -> Outcomes.denied("Not authorized")
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
    open fun validateApiKey(req: Request, roles: Roles): Outcome<Boolean> {

        // Validate using the callback if supplied,
        // otherwise use built-in key check
        return AuthFuncs.isKeyValid(req.meta, keyLookup, headerKey, roles.delimited).toOutcome()
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
    open fun validateToken(req: Request, roles: Roles): Outcome<Boolean> {

        // Get the user roles
        val actualRole = getUserRoles(req)
        val actualRoles = actualRole.splitToMapWithPairs(',')

        // Now match.
        return AuthFuncs.matchRoles(roles.delimited, actualRoles).toOutcome()
    }
}
