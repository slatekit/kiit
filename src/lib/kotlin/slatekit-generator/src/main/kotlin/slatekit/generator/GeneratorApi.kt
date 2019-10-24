package slatekit.generator

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Protocols
import slatekit.apis.Verbs
import slatekit.common.Context
import slatekit.common.auth.Roles
import slatekit.results.Try


@Api(area = "slatekit", name = "setup", desc= "new project setup",
        auth = AuthModes.keyed, roles = Roles.none, verb = Verbs.Auto, protocols = Protocols.CLI)
class GeneratorApi(val context: Context, val service: GeneratorService) {

    // slatekit.setup.app -name="myapp1" -packageName="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
    @Action(desc= "generates a new app project")
    fun app(name:String, packageName:String, company:String, desc:String, destination:String): Try<String> {
        return service.generate(GeneratorContext(name, desc, packageName, company, destination, CredentialMode.EnvVars), Templates.app())
    }
}
