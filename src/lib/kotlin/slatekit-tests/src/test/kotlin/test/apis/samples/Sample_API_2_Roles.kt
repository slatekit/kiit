/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 * 
 *  </kiit_header>
 */

package test.apis.samples

import kiit.apis.*
import kiit.common.*
import kiit.apis.core.Roles
import kiit.results.Outcome
import kiit.results.Success


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
