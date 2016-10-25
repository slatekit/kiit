/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.core.auth

import slate.common.results.ResultSupportIn
import slate.common._

/**
 * This class is intended for desktop apps where only 1 person can be logged in at a time.
 * Because of this reason, this class provides static access to the auth implementation (_auth)
 * and represents more of a provider pattern.
 * NOTE: do not use this for web apps
 */
object AuthFuncs extends ResultSupportIn {

  val guest = new User(id = "guest")

  /**
   * determines whether or not there is a valid api key in the inputs (Map like collection) supplied
   * @param inputs      : The inputs ( abstracted Map-like collection )
   * @param keys        : The list of ApiKey available
   * @param inputName   : The name of the key in the inputs containing the ApiKey.key
   *                      NOTES:
   *                      1. This is like the "Authorization" header in http.
   *                      2. In fact, since the HttpRequest is abstracted out via ApiCmd
   *                         in the Api feature ( Protocol Independent APIs ), the inputName
   *                         should be "Authorization" if the protocol is Http
   * @param actionRoles : The role required on the action being performed
   *                      NOTE: The key if present in inputs and matching one of ApiKeys in lookup
   *                            must have the role associated or this could be referencing "@parent"
   * @param parentRoles : The role of some parent ( This could be referenced in actionRoles )
   * @return
   */
  def isKeyValid(inputs:Option[Inputs],
                 keys:ListMap[String, ApiKey],
                 inputName:String,
                 actionRoles:String,
                 parentRoles:String):Result[Boolean] = {

    // No headers!
    if(!inputs.isDefined) {
      return unAuthorized(Some("api key not provided"))
    }

    // Key not in header!
    if(!inputs.get.contains(inputName)) {
      return unAuthorized(Some("api key not provided"))
    }

    val key = inputs.get(inputName).toString()

    // Empty key!
    if(Strings.isNullOrEmpty(key)) {
      return unAuthorized(Some("api key not provided"))
    }

    // Unknown key!
    if(!keys.contains(key)){
      return unAuthorized(Some("api key not valid"))
    }

    // Now ensure that key contains roles matching one provided.
    val apiKey = keys(key)

    // "Roles" could refer to "@parent" so get the final role(s)
    val expectedRole = getReferencedValue(actionRoles, parentRoles)

    // Now match the roles.
    matchRoles(expectedRole, apiKey.rolesLookup)
  }


  /**
   * matches the expected roles with the actual roles
   * @param expectedRole : "dev,ops,admin"
   * @param actualRoles  : Map of actual roles the user has.
   * @return
   */
  def matchRoles(expectedRole:String, actualRoles:Map[String,String]): Result[Boolean] = {

    // 1. No roles ?
    if(actualRoles == null || actualRoles.size == 0) {
      return unAuthorized()
    }

    // 2. Any role "*"
    if(Strings.isMatch(expectedRole, AuthConstants.RoleAny)){
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


  /**
   * gets the primary value supplied unless it references the parent value via "@parent"
   * @param primaryValue
   * @param parentValue
   * @return
   */
  def getReferencedValue(primaryValue:String, parentValue:String) : String = {

    // Role!
    if(!Strings.isNullOrEmpty(primaryValue) ){
      if(Strings.isMatch(primaryValue, AuthConstants.RoleParent)){
        return parentValue
      }
      return primaryValue
    }
    // Parent!
    if(!Strings.isNullOrEmpty(parentValue)){
      return parentValue
    }
    ""
  }


  /**
   * Converts api keys supplied to a listmap ( list + map ) of Api Keys using the api.key as key
   * @param keys
   * @return
   */
  def convertKeys(keys:List[ApiKey]):ListMap[String,ApiKey] = {
    val lookup = new ListMap[String,ApiKey]()
    if(keys == null)
      return lookup
    for(key <- keys) {
      val rolesLookup = Strings.splitToMap(key.roles, ',', true)
      lookup.add(key.key, new ApiKey(key.name, key.key, key.roles, rolesLookup))
    }
    lookup
  }



  /**
   * converts a comma delimited string of roles to an immutable map of role:String -> boolean:true
   * @param roles
   * @return
   */
  def convertRoles(roles:String): scala.collection.immutable.Map[String,Boolean] = {
    val rolesLookup = scala.collection.mutable.Map[String, Boolean]()

    val isEmpty = Some(!Strings.isNullOrEmpty(roles))

    isEmpty.fold(rolesLookup)( _ => {
      roles.split(',')
           .filter(p => !Strings.isNullOrEmpty(p))
           .foreach(role => rolesLookup(role) = true)
      rolesLookup
    })

    scala.collection.immutable.Map[String,Boolean](rolesLookup.toSeq : _*)
  }
}
