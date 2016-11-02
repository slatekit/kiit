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
package slate.test.common

import slate.common.{Strings, Result, ApiKey}
import slate.common.results.ResultSupportIn
import slate.core.apis.{Request, ApiAuth}

// =======================================================================================
// AUTH PROVIDER: Implement your own custom authentication/permission provider.
// NOTE: You can also use own pre-built authorization provider which has
// support for users, roles, permissions
// =======================================================================================
class MyAuthProvider(val user:String, val roles:String, keys:List[ApiKey]) extends ApiAuth(keys, None)
  with ResultSupportIn
{

  override protected def getUserRoles(cmd:Request):String = {
    roles
  }
}
