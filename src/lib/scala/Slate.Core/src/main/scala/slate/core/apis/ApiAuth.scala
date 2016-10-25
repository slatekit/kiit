/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slate.core.apis

import slate.common.results.ResultSupportIn
import slate.common.{Result, Strings, ApiKey}
import slate.core.auth.AuthFuncs._

/**
 * Class used to authenticate an api with support for 3 modes:
 * 1. app-key : user needs to supply an api-key to authenticate
 * 2. app-role: application specific role mode ( will validate against roles )
 * 3. key-role: dual authentication mode that will validate key + role.
 *
 * Need to initialize with api-keys
 */
class ApiAuth(private val keys:List[ApiKey],
              private val callback:Option[(Request, String, String ) => Result[Boolean]],
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
    if (Strings.isMatch(ApiConstants.AuthModeKeyRole, mode)) {
      return isKeyRoleValid(cmd, roles, roleParents)
    }
    // CASE 4: App-Role mode
    if (Strings.isMatch(ApiConstants.AuthModeAppRole, mode)) {
      return isAppRoleValid(cmd, roles, roleParents)
    }
    // CASE 5: api-key + role
    if (Strings.isMatch(ApiConstants.AuthModeAppKey, mode)) {
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
    isKeyValid(cmd.opts, _keyLookup, headerApiKeyName, actionRoles, parentRoles)
  }


  def isAppRoleValid(cmd:Request, actionRoles:String, parentRoles:String):Result[Boolean] = {

    // Check 1: Callback supplied ( so as to avoid subclassing this class )
    if(callback.isDefined) {
      return callback.map(c => c(cmd, actionRoles, parentRoles)).getOrElse(unAuthorized())
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
