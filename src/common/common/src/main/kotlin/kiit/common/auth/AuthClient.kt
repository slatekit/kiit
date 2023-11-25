/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 * 
 *  </kiit_header>
 */

package kiit.common.auth

/**
 * Auth clent to be used on the mobile/client side.
 * This manages the storing, managing the id, access, refresh token and provides
 * access to the user profile data from the id token.
 */
interface AuthClient<T>  {
    /**
     * Gets the JWT id token for inspecting user profile info
     */
    fun identity(): TokenIdentity

    /**
     * Gets the JWT access token for making API calls.
     */
    fun access(): TokenAccess

    /**
     * Gets the Refresh token for refreshing access tokens
     */
    fun refresh(): TokenRefresh

    /**
     * Logs the user in ( using a persisted identity token )
     */
    fun login() : Boolean

    /**
     * Logs the user using the auth data ( id, access, refresh data ) obtained
     */
    fun login(data:AuthData) : Boolean

    /**
     * Logs the user using the auth data ( id, access, refresh data ) obtained and explicitly parsed User object
     */
    fun login(data:AuthData, user:T) : Boolean

    /**
     * Logs the user out and clears the persisted auth data
     */
    fun logout() : Boolean

    /**
     * Whether the current use is authenticated
     */
    fun isAuthenticated(): Boolean

    /**
     * Gets the user object T using the data from the id token
     */
    fun getUser():T

    /**
     * Performs any updates like refreshing access token
     */
    fun update()
}