package kiit.apis.tools.code

import kiit.apis.*
import kiit.apis.Verbs
import kiit.apis.setup.HostAware
import kiit.apis.tools.code.builders.JavaBuilder
import kiit.apis.tools.code.builders.KotlinBuilder
import kiit.common.*
import kiit.requests.Request
import kiit.results.Notice

/**
 * slatekit.codegen.toJava   -templatesFolder="user://git/slatekit/scripts/templates/codegen/java"       -outputFolder="user://dev/temp/codegen/java"  -packageName="myapp" -classFile="" -methodFile="" -modelFile=""
 * slatekit.codegen.toJava   -templatesFolder="user://dev/tmp/slatekit/scripts/templates/codegen/java"   -outputFolder="user://dev/tmp/codegen/java"   -packageName="myapp" -classFile="" -methodFile="" -modelFile=""
 * slatekit.codegen.toKotlin -templatesFolder="user://dev/tmp/slatekit/scripts/templates/codegen/kotlin" -outputFolder="user://dev/tmp/codegen/kotlin" -packageName="myapp" -classFile="" -methodFile="" -modelFile=""
 * slatekit.codegen.toJava   -templatesFolder="user://git/slatekit/scripts/templates/codegen/java"       -outputFolder="user://dev/temp/codegen/java"  -packageName="myapp" -classFile="" -methodFile="" -modelFile=""
 * slatekit.codegen.toKotlin -templatesFolder="usr://dev/tmp/slatekit/slatekit/scripts/templates/codegen/kotlin" -outputFolder="usr://dev/tmp/codegen/kotlin" -packageName="myapp"
 */
@Api(area = "kiit", name = "codegen", desc = "client code generator", verb = Verbs.AUTO, sources = [Sources.CLI])
class CodeGenApi : HostAware {

    private var host: ApiServer? = null

    @Ignore
    override fun setApiHost(host: ApiServer) {
        this.host = host
    }

    @Action(name = "", desc = "generates client code in Kotlin")
    fun toKotlin(req: Request, templatesFolder: String, outputFolder: String, packageName: String, createDtos:Boolean): Notice<String> {
        return generate(req, templatesFolder, outputFolder, packageName, Language.Kotlin, createDtos)
    }

    @Action(name = "", desc = "generates client code in Swift")
    fun toSwift(req: Request, templatesFolder: String, outputFolder: String, packageName: String, createDtos:Boolean): Notice<String> {
        return generate(req, templatesFolder, outputFolder, packageName, Language.Kotlin, createDtos)
    }

    @Action(name = "", desc = "generates client code in Java")
    fun toJava(req: Request, templatesFolder: String, outputFolder: String, packageName: String, createDtos:Boolean): Notice<String> {
        return generate(req, templatesFolder, outputFolder, packageName, Language.Kotlin, createDtos)
    }

    @Action(name = "", desc = "generates client code in javascript")
    fun toJS(req: Request, templatesFolder: String, outputFolder: String, packageName: String, createDtos:Boolean): Notice<String> {
        return generate(req, templatesFolder, outputFolder, packageName, Language.JS, createDtos)
    }

    private fun generate(
        req: Request,
        templatesFolder: String,
        outputFolder: String,
        packageName: String,
        lang: Language,
        createDtos:Boolean
    ): Notice<String> {

        val result = this.host?.let { host ->
            val settings = CodeGenSettings(
                host,
                req,
                templatesFolder,
                outputFolder,
                packageName,
                lang
            )
            val builder = when (lang) {
                Language.Kotlin -> KotlinBuilder(settings)
                Language.Java -> JavaBuilder(settings)
                else -> JavaBuilder(settings)
            }
            val generator = CodeGen(settings.copy(createDtos = createDtos), builder)
            generator.generate(req)
            kiit.results.Success("")
        } ?: kiit.results.Failure("Api Container has not been set")
        return result
    }
}
