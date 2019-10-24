package slatekit.generator

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.setup.AuthModes
import slatekit.apis.setup.Protocols
import slatekit.apis.setup.Verbs
import slatekit.common.Context
import slatekit.common.auth.Roles
import slatekit.results.Try


@Api(area = "slatekit", name = "setup", desc= "new project setup",
        auth = AuthModes.keyed, roles = Roles.none, verb = Verbs.auto, protocol = Protocols.cli)
class GeneratorApi(val context: Context, val service: GeneratorService) {

    // slatekit.setup.app -name="myapp1" -packageName="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
    @Action(desc= "generates a new app project")
    fun app(name:String, packageName:String, company:String, desc:String, destination:String): Try<String> {
        return service.generate(GeneratorContext(name, desc, packageName, company, destination, CredentialMode.EnvVars), Templates.app())
    }
}
