/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */
package test.setup

import slatekit.apis.support.Authenticator
import slatekit.common.info.ApiKey
import slatekit.requests.Request


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
