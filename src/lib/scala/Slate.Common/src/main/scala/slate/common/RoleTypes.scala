/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.common

object RoleTypes {

  def isGuestType(roles:String, parentRole:String):Boolean =
  {
    if (Strings.isNullOrEmpty(roles))
      return true

    if (roles == "?")
      return true

    if (roles == "@parent" ) {

      if (Strings.isNullOrEmpty(parentRole))
        return false

      if (parentRole == "?")
        return true
    }

    false
  }


  def isAuthType(roles:String, parentRole:String):Boolean =
  {
    if (isGuestType(roles, parentRole))
      return false

    if (roles == "*")
      return true

    if (roles == "@parent" ) {

      if (parentRole == "*")
        return true
    }

    true
  }
}
