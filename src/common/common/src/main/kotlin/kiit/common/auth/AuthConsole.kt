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
 * This class is intended for console / desktop / batch apps where
 * only 1 person can be logged in at a time.
 */
class AuthConsole(isAuthenticated: Boolean, user: User, roles: String) : Auth(isAuthenticated, user, roles)
