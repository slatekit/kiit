package slatekit.apis.codegen

import slatekit.apis.*
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.common.*
import slatekit.common.auth.Roles
import slatekit.common.requests.Request
import slatekit.results.Notice

/**
 * slate.codegen.toJava   -templatesFolder="user://git/slatekit/scripts/templates/codegen/java"       -outputFolder="user://dev/temp/codegen/java"  -packageName="blendlife" -classFile="" -methodFile="" -modelFile=""
 * slate.codegen.toJava   -templatesFolder="user://dev/tmp/slatekit/scripts/templates/codegen/java"   -outputFolder="user://dev/tmp/codegen/java"   -packageName="blendlife" -classFile="" -methodFile="" -modelFile=""
 * slate.codegen.toKotlin -templatesFolder="user://dev/tmp/slatekit/scripts/templates/codegen/kotlin" -outputFolder="user://dev/tmp/codegen/kotlin" -packageName="blendlife" -classFile="" -methodFile="" -modelFile=""
 * slate.codegen.toJava   -templatesFolder="user://git/slatekit/scripts/templates/codegen/java"       -outputFolder="user://dev/temp/codegen/java"  -packageName="blendlife" -classFile="" -methodFile="" -modelFile=""
 */
@Api(area = "slate", name = "codegen", desc = "client code generator",
        auth = AuthModes.apiKey, roles = Roles.all, verb = Verbs.auto, protocol = Protocols.all)
class CodeGenApi : ApiHostAware {

    private var host: ApiHost? = null

    @Ignore
    override fun setApiHost(host: ApiHost) {
        this.host = host
    }

    @ApiAction(name = "", desc = "generates client code in Kotlin", roles = "@parent", verb = "post", protocol = "*")
    fun toKotlin(
            req: Request,
            templatesFolder: String,
            outputFolder: String,
            packageName: String,
            classFile: String = "",
            methodFile: String = "",
            modelFile: String = ""
    ): Notice<String> {
        return generate(req, templatesFolder, outputFolder, packageName, classFile, methodFile, modelFile, "kotlin", "kt")
    }

    @ApiAction(name = "", desc = "generates client code in Swift", roles = "@parent", verb = "post", protocol = "*")
    fun toSwift(
            req: Request,
            templatesFolder: String,
            outputFolder: String,
            packageName: String,
            classFile: String = "",
            methodFile: String = "",
            modelFile: String = ""
    ): Notice<String> {
        return generate(req, templatesFolder, outputFolder, packageName, classFile, methodFile, modelFile, "swift", "swift")
    }

    @ApiAction(name = "", desc = "generates client code in Java", roles = "@parent", verb = "post", protocol = "*")
    fun toJava(
            req: Request,
            templatesFolder: String,
            outputFolder: String,
            packageName: String,
            classFile: String = "",
            methodFile: String = "",
            modelFile: String = ""
    ): Notice<String> {
        return generate(req, templatesFolder, outputFolder, packageName, classFile, methodFile, modelFile, "java", "java")
    }

    @ApiAction(name = "", desc = "generates client code in javascript", roles = "@parent", verb = "post", protocol = "*")
    fun toJS(
            req: Request,
            templatesFolder: String,
            outputFolder: String,
            packageName: String,
            classFile: String = "",
            methodFile: String = "",
            modelFile: String = ""
    ): Notice<String> {
        return generate(req, templatesFolder, outputFolder, packageName, classFile, methodFile, modelFile, "js", "js")
    }

    private fun generate(
            req: Request,
            templatesFolder: String,
            outputFolder: String,
            packageName: String,
            classFile: String = "",
            methodFile: String = "",
            modelFile: String = "",
            lang: String,
            extension: String
    ): Notice<String> {

        val result = this.host?.let { host ->
            val settings = CodeGenSettings(
                    host,
                    req,
                    templatesFolder,
                    outputFolder,
                    packageName,
                    classFile.orElse("api.$extension"),
                    methodFile.orElse("method.$extension"),
                    modelFile.orElse("model.$extension"),
                    lang,
                    extension
            )
            val gen = when (lang) {
                "kotlin" -> CodeGenKotlin(settings)
                "swift" -> CodeGenSwift(settings)
                "java" -> CodeGenJava(settings)
                "js" -> CodeGenJS(settings)
                else -> CodeGenJava(settings)
            }
            gen.generate(req)
            slatekit.results.Success("")
        } ?: slatekit.results.Failure("Api Container has not been set")
        return result
    }
}
