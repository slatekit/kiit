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


/**
 * This class is intended for desktop apps where only 1 person can be logged in at a time.
 * Because of this reason, this class provides static access to the auth implementation (_auth)
 * and represents more of a provider pattern.
 * NOTE: do not use this for web apps
 */
object Auth {

  val guest = new User(id = "guest")

  var _auth:AuthBase = new AuthDesktop().init(false, guest, "")


  def isUser(user:Option[User]):Boolean = {
    _auth.isUser(user)
  }


  def isInRole(role:String ):Boolean = {
    _auth.isInRole(role)
  }


  def isAuthenticated:Boolean = {
    _auth.isAuthenticated
  }


  def isPhoneVerified:Boolean = {
    _auth.isPhoneVerified
  }


  def isEmailVerified:Boolean = {
    _auth.isEmailVerified
  }


  def userId:String = {
    _auth.userId
  }


  def user :User = {
    _auth.user.getOrElse( Auth.guest )
  }


  def init(isAuthenticated:Boolean, user:User, roles:String):Unit =
  {
    _auth = new AuthDesktop()
    _auth.init(isAuthenticated, user, roles)
  }
}
