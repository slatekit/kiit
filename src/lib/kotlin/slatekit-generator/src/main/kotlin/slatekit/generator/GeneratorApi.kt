package slatekit.generator

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.common.Context
import slatekit.common.Sources
import slatekit.common.auth.Roles
import slatekit.common.io.Uris
import slatekit.results.Try
import java.io.File


@Api(area = "slatekit", name = "new", desc = "generator for new projects",
        auth = AuthModes.KEYED, roles = [Roles.NONE], verb = Verbs.AUTO, sources = [Sources.CLI])
class GeneratorApi(val context: Context, val service: GeneratorService) {

    /**
     * slatekit new app -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param package: The package name of the generate item
     */
    @Action(desc = "generates a new app project")
    fun app(name: String, `package`: String): Try<String> {
        return generate("slatekit/app", name, `package`)
    }


    /**
     * slatekit new api -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param package: The package name of the generate item
     */
    @Action(desc = "generates a new api project")
    fun api(name: String, `package`: String): Try<String> {
        return generate("slatekit/api", name, `package`)
    }


    /**
     * slatekit new cli -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param package: The package name of the generate item
     */
    @Action(desc = "generates a new cli project")
    fun cli(name: String, `package`: String): Try<String> {
        return generate("slatekit/cli", name, `package`)
    }


    /**cli
     * slatekit new job -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param package: The package name of the generate item
     */
    @Action(desc = "generates a new background job project")
    fun job(name: String, `package`: String): Try<String> {
        return generate("slatekit/job", name, `package`)
    }


    /**
     * slatekit new lib -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param package: The package name of the generate item
     */
    @Action(desc= "generates a new library project")
    fun lib(name:String, `package`:String): Try<String> {
        return generate("slatekit/lib", name, `package`)
    }


    /**
     * slatekit new orm -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param package: The package name of the generate item
     */
    @Action(desc= "generates a new orm ( domain driven entities ) project")
    fun orm(name:String, `package`:String): Try<String> {
        return generate("slatekit/orm", name, `package`)
    }


    /**
     * @param templateName: The name of the template to use e.g. "_slatekit/app"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     */
    private fun generate(templateName: String, name: String, `package`: String): Try<String> {
        val templateDirPath = context.conf.getString("generation.source")
        val templateOutPath = context.conf.getString("generation.output")
        val rootDir = Uris.parse(templateDirPath).toFile()
        val genDir = Uris.parse(templateOutPath).toFile()
        val template = Templates.load(rootDir.toString(), templateName)
        val ctx = GeneratorContext(rootDir, genDir, name, "New app from template $templateName", `package`, "company", CredentialMode.EnvVars, service.settings)
        return service.generate(ctx, template)
    }
}
