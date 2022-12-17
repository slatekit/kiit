package slatekit.generator

import kiit.apis.Api
import kiit.apis.Action
import kiit.apis.AuthModes
import kiit.apis.Verbs
import slatekit.context.Context
import slatekit.common.Sources
import slatekit.common.auth.Roles
import slatekit.common.io.Uris
import slatekit.results.Try
import slatekit.results.builders.Tries
import slatekit.results.flatMap


@Api(area = "kiit", name = "new", desc = "generator for new projects",
        auth = AuthModes.KEYED, roles = [Roles.NONE], verb = Verbs.AUTO, sources = [Sources.CLI])
class GeneratorApi(val context: Context, val service: GeneratorService) {

    /**
     * kiit new app -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/kiit/myapp1"
     * @param name: The name of the generated item
     * @param packageName: The package name of the generate item
     */
    @Action(desc = "generates a new app project")
    fun app(name: String, packageName: String): Try<GeneratorResult> {
        return generate("kiit/app", name, packageName)
    }


    /**
     * kiit new api -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/kiit/myapp1"
     * @param name: Name of app/item
     * @param packageName: The package name of the generate item
     */
    @Action(desc = "generates a new api project")
    fun api(name: String, packageName: String): Try<GeneratorResult> {
        return generate("kiit/api", name, packageName)
    }


    /**
     * kiit new cli -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/kiit/myapp1"
     * @param name: Name of generated app/item
     * @param packageName: The package name of the generate item
     */
    @Action(desc = "generates a new cli project")
    fun cli(name: String, packageName: String): Try<GeneratorResult> {
        return generate("kiit/cli", name, packageName)
    }


    /**env
     * kiit new env -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/kiit/myapp1"
     * @param name: Name of generated app/item
     * @param packageName: The package name of the generate item
     */
    @Action(desc = "generates a new set of environment files")
    fun env(name: String, packageName: String): Try<GeneratorResult> {
        return generate("kiit/conf", name, packageName)
    }


    /**cli
     * kiit new job -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/kiit/myapp1"
     * @param name: Name of generated app/item
     * @param packageName: The package name of the generate item
     */
    @Action(desc = "generates a new background job project")
    fun job(name: String, packageName: String): Try<GeneratorResult> {
        return generate("kiit/job", name, packageName)
    }


    /**
     * kiit new lib -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/kiit/myapp1"
     * @param name: Name of generated app/item
     * @param packageName: The package name of the generate item
     */
    @Action(desc= "generates a new library project")
    fun lib(name:String, packageName:String): Try<GeneratorResult> {
        return generate("kiit/lib", name, packageName)
    }


    /**
     * kiit new orm -name="myapp1" -package="mycompany.myapp1" -desc="Sample app 1" -destination="~/dev/tests/kiit/myapp1"
     * @param name: Name of generated app/item
     * @param packageName: The package name of the generate item
     */
    @Action(desc= "generates a new orm ( domain driven entities ) project")
    fun orm(name:String, packageName:String): Try<GeneratorResult> {
        return generate("kiit/orm", name, packageName)
    }


    /**
     * @param templateName: The name of the template to use e.g. "_kiit/app"
     * @param name: Name of generated app/item
     * @param packageName: The package name of the generate item
     */
    private fun generate(templateName: String, name: String, packageName: String): Try<GeneratorResult> {
        val loadResult = Tries.of {
            val templateDirPath = service.conf.getString(Setup.KEY_GENERATION_SOURCE)
            val templateOutPath = service.conf.getString(Setup.KEY_GENERATION_OUTPUT)
            val rootDir = Uris.parse(templateDirPath).toFile()
            val genDir = Uris.parse(templateOutPath).toFile()
            val template = Templates.load(rootDir.toString(), templateName)
            Triple(rootDir, genDir, template)
        }
        val result = loadResult.flatMap {
            val rootDir = it.first
            val genDir = it.second
            val template = it.third
            val ctx = GeneratorContext(rootDir, genDir, name, "New app from template $templateName", packageName, "company", "apps", CredentialMode.EnvVars, service.settings)
            service.generate(ctx, template)
        }
        return result
    }
}
