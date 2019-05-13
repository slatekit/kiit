package slatekit.setup

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.common.Context
import slatekit.results.Try


@Api(area = "slatekit", name = "setup", desc= "new project setup",
        auth = AuthModes.apiKey, roles = "", verb = Verbs.auto, protocol = Protocols.cli)
class SetupApi(val context: Context, val service:SetupService) {


    @ApiAction(desc= "generates a new app project")
    fun app(name:String, packageName:String, desc:String, destination:String): Try<String> {
        return service.app(SetupContext(name, desc, packageName, destination))
    }
}
