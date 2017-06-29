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

package slatekit.apis

/**
 * Used to indicate the Role(s) allowed at either the top-level api and/or action
 *
 * 1. Any    = "*"       : indicates any authenticated role
 * 2. Guest  = "?"       : indicates any one, including unauthenticated role
 * 3. Parent = "@parent" : indicates a reference to the role of the parent item
 *                         e.g. given: area/api/action
 *                                     app/users/invite
 *                         - action -> api
 *                         - api    -> area
 * 4. None   = "@none"   :
 *
 * @param name
 */
abstract class ApiRoleRef(val name: String)


/**
 * Used to setup an ApiContainer to allow any protocol
 */
object ApiRoleRefAny : ApiRoleRef(ApiConstants.RoleAny)


/**
 * Used to setup an ApiContainer to allow only the CLI ( command line interface ) protocol
 */
object ApiRoleRefGuest : ApiRoleRef(ApiConstants.RoleGuest)


/**
 * Used to setup an ApiContainer to allow only web/http protocol
 */
object ApiRoleRefParent : ApiRoleRef(ApiConstants.RoleParent)


/**
 * Used to setup an ApiContainer to allow only web/http protocol
 */
object ApiRoleRefNone : ApiRoleRef(ApiConstants.RoleNone)
