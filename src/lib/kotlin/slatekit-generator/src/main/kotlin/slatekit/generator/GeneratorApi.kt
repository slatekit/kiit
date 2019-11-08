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
     * @param area: The area/company/group associated with this generated item 9 e.g. company name | department name
     * @param destination: The destination folder to generate this
     */
    @Action(desc = "generates a new app project")
    fun app(name: String, packageName: String): Try<String> {
        val dir = System.getProperty("user.dir")
        return generate("slatekit/app", name, packageName, "company", dir)
    }


    /**
     * slatekit new api -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     * @param area: The area/company/group associated with this generated item 9 e.g. company name | department name
     * @param destination: The destination folder to generate this
     */
    @Action(desc = "generates a new api project")
    fun api(name: String, packageName: String, area: String, destination: String): Try<String> {
        return generate("slatekit/api", name, packageName, area, destination)
    }


    /**
     * slatekit new cli -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     * @param area: The area/company/group associated with this generated item 9 e.g. company name | department name
     * @param destination: The destination folder to generate this
     */
    @Action(desc = "generates a new cli project")
    fun cli(name: String, packageName: String, area: String, destination: String): Try<String> {
        return generate("slatekit/cli", name, packageName, area, destination)
    }


    /**
     * slatekit new job -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     * @param area: The area/company/group associated with this generated item 9 e.g. company name | department name
     * @param destination: The destination folder to generate this
     */
    @Action(desc = "generates a new background job project")
    fun job(name: String, packageName: String, area: String, destination: String): Try<String> {
        return generate("slatekit/job", name, packageName, area, destination)
    }


    /**
     * slatekit new lib -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     * @param area: The area/company/group associated with this generated item 9 e.g. company name | department name
     * @param destination: The destination folder to generate this
     */
    @Action(desc= "generates a new library project")
    fun lib(name:String, packageName:String, area:String, destination:String): Try<String> {
        return generate("slatekit/lib", name, packageName, area, destination)
    }


    /**
     * slatekit new orm -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/slatekit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     * @param area: The area/company/group associated with this generated item 9 e.g. company name | department name
     * @param destination: The destination folder to generate this
     */
    @Action(desc= "generates a new orm ( domain driven entities ) project")
    fun orm(name:String, packageName:String, area:String, destination:String): Try<String> {
        return generate("slatekit/orm", name, packageName, area, destination)
    }


    /**
     * @param templateName: The name of the template to use e.g. "_slatekit/app"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     * @param area: The area/company/group associated with this generated item 9 e.g. company name | department name
     * @param destination: The destination folder to generate this
     */
    private fun generate(templateName: String, name: String, packageName: String, area: String, destination: String): Try<String> {
        val templateDirPath = context.cfg.getString("templates.dir")
        val template = Templates.load(templateDirPath, templateName)
        //val parentDir = File(templateDirPath, templateName.split("/")[0])
        val rootDir = File(templateDirPath)
        val ctx = GeneratorContext(rootDir, name, "", packageName, area, destination, CredentialMode.EnvVars)
        return service.generate(ctx, template)
    }
}
