/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.apis

import slate.common.results.ResultSupportIn
import slate.common.{Result, Strings, ApiKey}
import slate.core.apis.ApiConstants._
import slate.core.auth.AuthFuncs._

/**
 * Class used to authenticate an api with support for 3 modes:
 * 1. app-key : user needs to supply an api-key to authenticate
 * 2. app-role: application specific role mode ( will validate against roles )
 * 3. key-role: dual authentication mode that will validate key + role.
 *
 * Need to initialize with api-keys
 */
class ApiAuth(private val keys:Option[List[ApiKey]],
              private val callback:Option[(String, Request, String, String ) => Result[Boolean]],
              private val headerApiKeyName:String = "api-key")
  extends ResultSupportIn {

  private val _keyLookup = convertKeys(keys)


  /**
   * whether or not the authorization is valid for the mode, roles supplied.
    *
    * @param mode
   * @param roles
   * @param roleParents
   * @return
   */
  def isAuthorized(cmd:Request, mode:String, roles:String, roleParents:String):Result[Boolean] =
  {
    // CASE 1: no roles ? authorization not applicable
    if (Strings.isNullOrEmpty(roles))
      return ok()

    // CASE 2: Guest
    if (roles == "?")
      return ok()

    // CASE 3: App Roles + Key Roles mode
    if (Strings.isMatch(AuthModeKeyRole, mode)) {
      return isKeyRoleValid(cmd, roles, roleParents)
    }
    // CASE 4: App-Role mode
    if (Strings.isMatch(AuthModeAppRole, mode)) {
      return isAppRoleValid(cmd, roles, roleParents)
    }
    // CASE 5: api-key + role
    if (Strings.isMatch(AuthModeAppKey, mode)) {
      val keyResult = isKeyRoleValid(cmd, roles, roleParents)
      if(!keyResult.success){
        return keyResult
      }
      val appResult = isAppRoleValid(cmd, roles, roleParents)
      return appResult
    }
    unAuthorized()
  }


  def isKeyRoleValid(cmd:Request, actionRoles:String, parentRoles:String):Result[Boolean] = {
    callback.fold[Result[Boolean]](
      isKeyValid(cmd.opts, _keyLookup, headerApiKeyName, actionRoles, parentRoles))( call => {
        call(AuthModeAppKey, cmd, actionRoles, parentRoles)
      })
  }


  def isAppRoleValid(cmd:Request, actionRoles:String, parentRoles:String):Result[Boolean] = {

    // Check 1: Callback supplied ( so as to avoid subclassing this class )
    if(callback.isDefined) {
      return callback.map(c => c(AuthModeAppRole, cmd, actionRoles, parentRoles))
        .getOrElse(unAuthorized())
    }

    // Get the expected role from either action or possible reference to parent
    val expectedRoles = getReferencedValue(actionRoles, parentRoles)

    // Get the user roles
    val actualRole = getUserRoles(cmd)
    val actualRoles = Strings.splitToMap(actualRole, ',')

    // Now match.
    matchRoles(expectedRoles, actualRoles)
  }


  protected def getUserRoles(cmd:Request):String = {
    ""
  }
}
