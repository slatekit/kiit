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
import scala.collection.mutable.Map


class AuthBase {

  private var _user = Some(Auth.guest)
  private val _roles = Map[String, Boolean]()
  private var _isAuthenticated = false


  def isUser(user:Option[User]):Boolean = {
   user.fold( false )( _.isMatch(_user.getOrElse(Auth.guest) ) )
  }


  def isInRole(role:String ):Boolean = {
    if(_roles.isEmpty)
      return false
    if(!_roles.contains(role))
      return false

    true
  }


  def isAuthenticated:Boolean = {
    _isAuthenticated
  }


  def isPhoneVerified:Boolean = {
    _user.fold( false)( _.isPhoneVerified )
  }


  def isEmailVerified:Boolean = {
    _user.fold( false)( _.isEmailVerified )
  }


  def userId:String = {
    _user.fold( Strings.empty )( _.id )
  }


  def user :Option[User] = {
    _user
  }


  def init(isAuthenticated:Boolean, user:User, roles:String): AuthBase =
  {
    _isAuthenticated = isAuthenticated
    _user = Some(user)
    _roles.clear()

    if(!Strings.isNullOrEmpty(roles))
    {
      val tokens = roles.split(',')
      val filtered = tokens.filter( p => !Strings.isNullOrEmpty(p))
      filtered.foreach( role => _roles( role ) = true )
    }
    this
  }
}
