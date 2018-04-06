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
abstract class Role(val name: String)


/**
 * Used to setup an ApiContainer to allow any protocol
 */
object AnyRole : Role(ApiConstants.Any)


/**
 * Used to setup an ApiContainer to allow only the CLI ( command line interface ) protocol
 */
object GuestRole : Role(ApiConstants.Unknown)


/**
 * Used to setup an ApiContainer to allow only web/http protocol
 */
object ParentRole : Role(ApiConstants.Parent)


/**
 * Used to setup an ApiContainer to allow only web/http protocol
 */
object NoRole : Role(ApiConstants.None)
