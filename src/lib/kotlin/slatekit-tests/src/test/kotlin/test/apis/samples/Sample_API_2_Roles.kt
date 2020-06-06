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

package test.apis.samples

import slatekit.apis.*
import slatekit.common.*
import slatekit.common.auth.Roles
import slatekit.results.Outcome
import slatekit.results.Success


@Api(area = "app", name = "rolesTest", desc = "api to access and manage users 3", roles= ["admin"], auth = AuthModes.TOKEN, verb = Verbs.AUTO, sources = [Sources.ALL])
class Sample_API_2_Roles {

  @Action(desc = "", roles= [Roles.NONE])
  fun rolesNone(code:Int, tag:String): Outcome<String> {
    return Success("rolesNone", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles=[Roles.ALL])
  fun rolesAny(code:Int, tag:String): Outcome<String> {
    return Success("rolesAny", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles= ["dev"])
  fun rolesSpecific(code:Int, tag:String): Outcome<String>  {
    return Success("rolesSpecific", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles= [Roles.PARENT])
  fun rolesParent(code:Int, tag:String): Outcome<String> {
    return Success("rolesParent", msg ="${code} ${tag}")
  }


  @Action(desc = "", auth = AuthModes.KEYED, roles=[Roles.ALL])
  fun authOverride(code:Int, tag:String): Outcome<String> {
    return Success("authOverride", msg ="${code} ${tag}")
  }
}
