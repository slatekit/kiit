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


class AuthBase(val isAuthenticated:Boolean, userInfo:User, roles:String) {

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
    else if(!_roles.contains(role))
      false
    else
      true
  }


  /**
   * whether or not the users phone is verified
   * @return
   */
  def isPhoneVerified:Boolean = {
    _user.fold( false)( _.isPhoneVerified )
  }


  /**
   * whether or not the users email is verified
   * @return
   */
  def isEmailVerified:Boolean = {
    _user.fold( false )( _.isEmailVerified )
  }


  /**
   * The user id
   * @return
   */
  def userId:String = {
    _user.fold( Strings.empty )( _.id )
  }


  /**
   * The user info
   * @return
   */
  def user :Option[User] = {
    _user
  }
}
