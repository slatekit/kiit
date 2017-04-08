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


import slate.common.Strings

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
 * @param roles
 */
class Auth(val isAuthenticated:Boolean, userInfo:User, roles:String) {

  private val _user = Option(userInfo)
  private val _roles = AuthFuncs.convertRoles(roles)


  /**
   * matches the user to the one supplied.
   * @param user
   * @return
   */
  def isUser(user:Option[User]):Boolean = {
   user.fold( false )( _.isMatch(_user.getOrElse(AuthFuncs.guest) ) )
  }


  /**
   * whether or not the user in the role supplied.
   * @param role
   * @return
   */
  def isInRole(role:String ):Boolean = {
    if(_roles.isEmpty)
      false
    else if(_roles.contains(role))
      true
    else
      false
  }


  /**
   * whether or not the users phone is verified
   * @return
   */
  def isPhoneVerified:Boolean = _user.fold( false)( _.isPhoneVerified )


  /**
   * whether or not the users email is verified
   * @return
   */
  def isEmailVerified:Boolean =  _user.fold( false )( _.isEmailVerified )


  /**
   * The user id
   * @return
   */
  def userId:String = _user.fold( Strings.empty )( _.id )


  /**
   * The user info
   * @return
   */
  def user :Option[User] = _user
}
