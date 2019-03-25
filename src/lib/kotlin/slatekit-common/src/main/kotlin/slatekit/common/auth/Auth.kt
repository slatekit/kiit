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

package slatekit.common.auth

/**
 * Provides a reasonable mechanism for ONLY inspecting an authenticated or non-authenticated user
 *
 * NOTES:
 * 1. This component does NOT handle any actual login/logout/authorization features.
 * 2. This set of classes are only used to inspect information about a user.
 * 3. Since authorization is a fairly complex feature with implementations such as
 *    OAuth, Social Auth, Slate Kit has purposely left out the Authentication to more reliable
 *    libraries and frameworks.
 * 4. The SlateKit.Api component, while supporting basic api "Keys" based authentication,
 *    and a roles based authentication, it leaves the login/logout and actual generating
 *    of tokens to libraries such as OAuth.
 *
 *
 * @param isAuthenticated
 * @param userInfo
 * @param rolesDelimited
 */
open class Auth(val isAuthenticated: Boolean, val user: User?, rolesDelimited: String) {

    private val roles = AuthFuncs.convertRoles(rolesDelimited)

    /**
     * matches the user to the one supplied.
     * @param user
     * @return
     */
    fun isUser(user: User?): Boolean = user?.isMatch(this.user ?: AuthFuncs.guest) ?: false

    /**
     * whether or not the user in the role supplied.
     * @param role
     * @return
     */
    fun isInRole(role: String): Boolean = roles.containsKey(role)

    /**
     * whether or not the users phone is verified
     * @return
     */
    val isPhoneVerified: Boolean get() = user?.isPhoneVerified ?: false

    /**
     * whether or not the users email is verified
     * @return
     */
    val isEmailVerified: Boolean get() = user?.isEmailVerified ?: false

    /**
     * The user id
     * @return
     */
    val userId: String get() = user?.id ?: ""
}
