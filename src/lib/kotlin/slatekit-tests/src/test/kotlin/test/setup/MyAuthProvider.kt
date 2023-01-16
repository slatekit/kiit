/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */
package test.setup

import kiit.apis.support.Authenticator
import kiit.common.info.ApiKey
import kiit.requests.Request


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
