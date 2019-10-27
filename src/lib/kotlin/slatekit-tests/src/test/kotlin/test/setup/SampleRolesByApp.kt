package test.setup

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.common.auth.Roles


@Api(area = "samples", name = "rolesapp", desc = "sample to test security", roles= ["admin"], auth = AuthModes.Token)
class SampleRolesByApp {

    @Action(desc = "no roles allows access by anyone")
    fun rolesNone(code:Int, tag:String): String {
        return "rolesNone $code $tag"
    }


    @Action(desc = "* roles allows access by any authenticated in user", roles= [ Roles.all])
    fun rolesAny(code:Int, tag:String): String {
        return "rolesAny $code $tag"
    }


    @Action(desc = "allows access by specific role", roles= ["dev"])
    fun rolesSpecific(code:Int, tag:String): String  {
        return "rolesSpecific $code $tag"
    }


    @Action(desc = "@parent refers to its parent role", roles= [Roles.parent])
    fun rolesParent(code:Int, tag:String): String {
        return "rolesParent $code $tag"
    }
}
