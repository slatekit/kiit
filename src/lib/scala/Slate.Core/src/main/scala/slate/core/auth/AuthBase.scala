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
      return false
    if(!_roles.contains(role))
      return false

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
