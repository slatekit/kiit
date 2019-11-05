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
import slatekit.common.content.Doc
import slatekit.common.encrypt.EncDouble
import slatekit.common.encrypt.EncString
import slatekit.common.encrypt.EncInt
import slatekit.common.encrypt.EncLong
import slatekit.common.requests.Request
import slatekit.integration.common.ApiBaseEntity
import slatekit.entities.EntityService
import slatekit.integration.common.AppEntContext
import slatekit.results.Notice
import slatekit.results.Outcome
import slatekit.results.Success


@Api(area = "app", name = "rolesTest", desc = "api to access and manage users 3", roles= ["admin"], auth = AuthModes.Token, verb = Verbs.Auto, protocols = [Sources.All])
class Sample_API_2_Roles {

  @Action(desc = "", roles= [Roles.none])
  fun rolesNone(code:Int, tag:String): Outcome<String> {
    return Success("rolesNone", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles=[Roles.all])
  fun rolesAny(code:Int, tag:String): Outcome<String> {
    return Success("rolesAny", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles= ["dev"])
  fun rolesSpecific(code:Int, tag:String): Outcome<String>  {
    return Success("rolesSpecific", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles= [Roles.parent])
  fun rolesParent(code:Int, tag:String): Outcome<String> {
    return Success("rolesParent", msg ="${code} ${tag}")
  }
}
