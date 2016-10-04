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
package slate.ext.users

import slate.common.Strings

object UserHelper {

 def parseUserId(id:String):UserId =
  {
    if(id == null || id == "") {
      return UserId.empty
    }
    val tokens = Strings.split(id, ',')
    new UserId(tokens(0).toInt, tokens(1).toLong, tokens(2), tokens(3), tokens(4))
  }

}
