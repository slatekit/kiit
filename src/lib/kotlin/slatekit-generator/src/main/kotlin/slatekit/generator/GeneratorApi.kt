package slatekit.generator

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.common.Context
import slatekit.common.Sources
import slatekit.common.auth.Roles
import slatekit.results.Try
import java.io.File


@Api(area = "slatekit", name = "new", desc = "generator for new projects",
        auth = AuthModes.Keyed, roles = [Roles.none], verb = Verbs.Auto, sources = [Sources.CLI])
class GeneratorApi(val context: Context, val service: GeneratorService) {

    /**
     * slatekit new app -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     */
    @Action(desc = "generates a new app project")
    fun app(name: String, packageName: String): Try<String> {
        return generate("slatekit/app", name, packageName)
    }


    /**
     * slatekit new api -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     */
    @Action(desc = "generates a new api project")
    fun api(name: String, packageName: String): Try<String> {
        return generate("slatekit/api", name, packageName)
    }


    /**
     * slatekit new cli -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     */
    @Action(desc = "generates a new cli project")
    fun cli(name: String, packageName: String): Try<String> {
        return generate("slatekit/cli", name, packageName)
    }


    /**
     * slatekit new job -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     */
    @Action(desc = "generates a new background job project")
    fun job(name: String, packageName: String): Try<String> {
        return generate("slatekit/job", name, packageName)
    }


    /**
     * slatekit new lib -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     */
    @Action(desc= "generates a new library project")
    fun lib(name:String, packageName:String): Try<String> {
        return generate("slatekit/lib", name, packageName)
    }


    /**
     * slatekit new orm -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     */
    @Action(desc= "generates a new orm ( domain driven entities ) project")
    fun orm(name:String, packageName:String): Try<String> {
        return generate("slatekit/orm", name, packageName)
    }


    /**
     * @param templateName: The name of the template to use e.g. "_slatekit/app"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     */
    private fun generate(templateName: String, name: String, packageName: String): Try<String> {
        val targetPath = slatekit.common.io.Files.createAtUserDir(listOf("gen", name))
        val templateDirPath = context.cfg.getString("templates.dir")
        val template = Templates.load(templateDirPath, templateName)
        //val parentDir = File(templateDirPath, templateName.split("/")[0])
        val rootDir = File(templateDirPath)
        val ctx = GeneratorContext(rootDir, name, "", packageName, "company", targetPath.absolutePath, CredentialMode.EnvVars)
        return service.generate(ctx, template)
    }
}
