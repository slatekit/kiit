package slatekit.setup

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.common.Context
import slatekit.results.Try


@Api(area = "slate", name = "setup", desc= "new project setup",
        auth = AuthModes.apiKey, roles = "", verb = Verbs.auto, protocol = Protocols.cli)
class SetupApi(context: Context) {

    val service = SetupService(context)


    @ApiAction(desc= "generates a new app project")
    fun app(root:String, output:String): Try<String> {
        return service.app(root, output)
    }


    @ApiAction(desc= "generates a new cli project")
    fun cli(root:String, output:String): Try<String> {
        return service.cli(root, output)
    }


    @ApiAction(desc= "generates a new server project")
    fun server(root:String, output:String): Try<String> {
        return service.server(root, output)
    }
}
