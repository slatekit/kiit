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
package test.setup

import slatekit.apis.svcs.Authenticator
import slatekit.common.info.ApiKey
import slatekit.common.requests.Request


// =======================================================================================
// AUTH PROVIDER: Implement your own custom authentication/permission provider.
// NOTE: You can also use own pre-built authorization provider which has
// support for users, roles, permissions
// =======================================================================================
class MyAuthProvider(val user:String, val roles:String, keys:List<ApiKey>?) : Authenticator(keys ?: listOf())
{

  override fun getUserRoles(cmd: Request):String{
    return roles
  }
}
