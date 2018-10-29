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
package slatekit.sampleapp.core.common

import slatekit.common.security.ApiKey

object AppApiKeys {

  /**
    * Example of using API keys for protected access to APIs    *
    * NOTE: API keys may be sufficient for local, internal network based access to APIs.
    * You should use some token/OAuth based approach in other situations.
    *
    * NOTE: These should be kept secret in some way, but shown here for sample purposes.
    *
    * @return
    */
  fun fetch():List<ApiKey> {
    return listOf(
      ApiKey("user" , "7BF84B28FC8A41BBA3FDFA48D2B462DA", "user"                    ),
      ApiKey("po"   , "0F66CD55079C42FF85C001846472343C", "user,po"                 ),
      ApiKey("qa"   , "EB7EB37764AD4411A1763E6A593992BD", "user,po,qa"              ),
      ApiKey("dev"  , "3E35584A8DE0460BB28D6E0D32FB4CFD", "user,po,qa,dev"          ),
      ApiKey("ops"  , "5020F4A237A443B4BEDC37D8A08588A3", "user,po,qa,dev,ops"      ),
      ApiKey("admin", "54B1817194C1450B886404C6BEA81673", "user,po,qa,dev,ops,admin")
    )
  }
}
