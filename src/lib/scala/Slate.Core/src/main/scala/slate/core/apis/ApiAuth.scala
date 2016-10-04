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
import slate.common.{ListMap, Result, Strings, ApiKey}

/**
 * Class used to authenticate an api with support for 3 modes:
 * 1. app-key : user needs to supply an api-key to authenticate
 * 2. app-role: application specific role mode ( will validate against roles )
 * 3. key-role: dual authentication mode that will validate key + role.
 *
 * Need to initialize with api-keys
 */
class ApiAuth(private val keys:List[ApiKey],
              private val callback:Option[(ApiCmd, String, String ) => Result[Boolean]],
              private val headerApiKeyName:String = "api-key")
  extends ResultSupportIn {

  private val _keyLookup = init(keys)


  /**
   * whether or not the authorization is valid for the mode, roles supplied.
    *
    * @param mode
   * @param roles
   * @param roleParents
   * @return
   */
  def isAuthorized(cmd:ApiCmd, mode:String, roles:String, roleParents:String):Result[Boolean] =
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


  def isKeyRoleValid(cmd:ApiCmd, actionRoles:String, parentRoles:String):Result[Boolean] = {

    // No headers!
    if(!cmd.opts.isDefined) {
      return unAuthorized(Some("api key not provided"))
    }

    // Key not in header!
    if(!cmd.opts.get.contains(headerApiKeyName)) {
      return unAuthorized(Some("api key not provided"))
    }

    val key = cmd.opts.get(headerApiKeyName).toString()

    // Empty key!
    if(Strings.isNullOrEmpty(key)) {
      return unAuthorized(Some("api key not provided"))
    }

    // Unknown key!
    if(!_keyLookup.contains(key)){
      return unAuthorized(Some("api key not valid"))
    }

    // Now ensure that key contains roles matching one provided.
    val apiKey = _keyLookup(key)

    // "Roles" could refer to "@parent" so get the final role(s)
    val expectedRole = ApiHelper.getReferencedValue(actionRoles, parentRoles)

    // Now match the roles.
    matchRoles(expectedRole, apiKey.rolesLookup)
  }


  def isAppRoleValid(cmd:ApiCmd, actionRoles:String, parentRoles:String):Result[Boolean] = {

    // Check 1: Callback supplied ( so as to avoid subclassing this class )
    if(callback.isDefined) {
      return callback.map(c => c(cmd, actionRoles, parentRoles)).getOrElse(unAuthorized())
    }

    // Get the expected role from either action or possible reference to parent
    val expectedRoles = ApiHelper.getReferencedValue(actionRoles, parentRoles)

    // Get the user roles
    val actualRole = getUserRoles(cmd)
    val actualRoles = Strings.splitToMap(actualRole, ',')

    // Now match.
    matchRoles(expectedRoles, actualRoles)
  }


  protected def getUserRoles(cmd:ApiCmd):String = {
    ""
  }


  /**
   * matches the expected roles with the actual roles
   * @param expectedRole : "dev,ops,admin"
   * @param actualRoles  : Map of actual roles the user has.
   * @return
   */
  protected def matchRoles(expectedRole:String, actualRoles:Map[String,String]): Result[Boolean] = {

    // 1. No roles ?
    if(actualRoles == null || actualRoles.size == 0) {
      return unAuthorized()
    }

    // 2. Any role "*"
    if(Strings.isMatch(expectedRole, ApiConstants.RoleAny)){
      return ok()
    }

    // 3. Get all roles "dev,moderator,admin"
    val expectedRoles = Strings.split(expectedRole, ',')

    // 4. Now compare
    for(role <- expectedRoles ){
      if(actualRoles.contains(role)) {
        return ok()
      }
    }
    unAuthorized()
  }


  private def init(keys:List[ApiKey]):ListMap[String,ApiKey] = {
    val lookup = new ListMap[String,ApiKey]()
    if(keys == null)
      return lookup
    for(key <- keys) {
      val rolesLookup = Strings.splitToMap(key.roles, ',', true)
      lookup.add(key.key, new ApiKey(key.name, key.key, key.roles, rolesLookup))
    }
    lookup
  }
}
