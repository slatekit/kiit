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

    // Check 1: Default to none
    val keyCheck = inputs.fold[Option[String]](None)( inp => {

      // Check 2: Key exists in request ?
      if( inp.contains(inputName) ) {
        val key = inp(inputName).toString()

        // Check 3: Key is non-empty ?
        if(Strings.isNullOrEmpty(key)) {
          None
        }
        else {
          // Check 4: CHeck if valid key
          if(keys.contains(key))
            Some(key)
          else
            None
        }
      }
      else
        None
    })

    keyCheck match {
      case None            => unAuthorized(msg = Some("Api Key not provided or invalid"))
      case s: Some[String] => validateKey(s.get, keys, actionRoles, parentRoles)
      case _               => unAuthorized(msg = Some("Api key not provided or invalid"))
    }
  }


  /**
   * matches the expected roles with the actual roles
   * @param expectedRole : "dev,ops,admin"
   * @param actualRoles  : Map of actual roles the user has.
   * @return
   */
  def matchRoles(expectedRole:String, actualRoles:Map[String,String]): Result[Boolean] = {

    // 1. No roles ?
    val anyRoles = Option(actualRoles).fold(false)( roles => roles.nonEmpty)
    if(!anyRoles) {
      unAuthorized()
    }
    // 2. Any role "*"
    else if(Strings.isMatch(expectedRole, AuthConstants.RoleAny)){
      ok()
    }
    else {
      // 3. Get all roles "dev,moderator,admin"
      val expectedRoles = Strings.split(expectedRole, ',')

      // 4. Now compare
      val matches = expectedRoles.toList.filter( role => actualRoles.contains(role))
      if(matches.nonEmpty )
        ok()
      else
        unAuthorized()
    }
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
        parentValue
      }
      else
        primaryValue
    }
    // Parent!
    else if(!Strings.isNullOrEmpty(parentValue)){
      parentValue
    }
    else
      ""
  }


  /**
   * Converts api keys supplied to a listmap ( list + map ) of Api Keys using the api.key as key
   * @param keys
   * @return
   */
  def convertKeys(keys:Option[List[ApiKey]]):ListMap[String,ApiKey] = {
    keys.fold(new ListMap[String,ApiKey]())( all => {

      val lookup = new ListMap[String,ApiKey]()
      for(key <- all) {
        lookup.add(key.key, key)
      }
      lookup
    })
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


  private def validateKey(key:String, keys:ListMap[String, ApiKey],
                          actionRoles:String, parentRoles:String):Result[Boolean] = {

    // Now ensure that key contains roles matching one provided.
    val apiKey = keys(key)

    // "Roles" could refer to "@parent" so get the final role(s)
    val expectedRole = getReferencedValue(actionRoles, parentRoles)

    // Now match the roles.
    matchRoles(expectedRole, apiKey.rolesLookup)
  }
}
