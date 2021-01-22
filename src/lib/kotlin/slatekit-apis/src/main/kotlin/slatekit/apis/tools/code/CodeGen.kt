package slatekit.apis.tools.code

import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberFunctions
import slatekit.apis.Verbs
import slatekit.apis.routes.Action
import slatekit.apis.routes.Api
import slatekit.apis.tools.code.builders.CodeBuilder
import slatekit.common.*
import slatekit.common.ext.orElse
import slatekit.common.io.Files
import slatekit.common.io.Uris
import slatekit.common.requests.Request
import slatekit.common.utils.Props

class CodeGen(val settings: CodeGenSettings, val builder:CodeBuilder) {

    /**
     * Represents the 3 code templates ( api.kt, method.kt, model.kt (DTO) )
     */
    val templates = Templates.load(settings.templatesFolder, settings.lang)

    /**
     * @command="codegen" -lang="java" -package="blendlife.api" -pathToTemplates="C:\Dev\github\blend-server\scripts\templates\codegen"
     */
    fun generate(req: Request) {
        val log = this.settings.host.ctx.logs.getLogger("slate")
        val dirs = this.settings.host.ctx.dirs

        // Check if templates folder exists
        val templatesFolderPath = Uris.interpret(settings.templatesFolder)
        val templatesFolder = File(templatesFolderPath)
        if (!templatesFolder.exists()) {
            log.error("Templates folder: ${templatesFolder.absolutePath} does not exist")
            return
        }

        val outputFolderPathRaw = this.settings.outputFolder.orElse(dirs?.pathToOutputs ?: Props.tmpDir)
        val outputFolderPath = Uris.interpret(outputFolderPathRaw)
        val dateFolder = Files.folderNameByDate()

        // ~/.slatekit/tools/apis/output/
        val codeGenDirs = Directories(File(outputFolderPath ?: ""))
        codeGenDirs.create(log)
        codeGenDirs.log(log)

        // Collection of all custom types
        val routes = this.settings.host.routes
        val rules = CodeRules(this.settings)
        val allApis = routes.areas.items.map { it.apis.items }.flatten()
        val apis = allApis.filter { rules.isValidApi(it) }

        apis.forEach { api ->
            try {
                println("API: " + api.area + "." + api.name)

                // Get only the declared members in the api/class
                val declaredMembers = api.klass.declaredMemberFunctions
                val declaredMemberLookup = declaredMembers.map { func -> (func.name to true) }.toMap()

                // Get all the actions on the api
                val methodsBuffer = StringBuilder()

                // Iterate over all the api actions
                api.actions.items.forEach { action ->

                    log.info("Processing : " + api.area + "/" + api.name + "/" + action.name)

                    // Ok to generate ?
                    if (rules.isValidAction(api, action, declaredMemberLookup)) {
                        // Generate code here.
                        val methodInfo = generateMethod(api, action)
                        log.info("generating method for: " + api.area + "/" + api.name + "/" + action.name)
                        methodsBuffer.append(methodInfo)
                        methodsBuffer.append(newline)
                    }
                }

                // Get unique types
                // Iterate over all the api actions
                if(this.settings.createDtos) {
                    api.actions.items.map { action ->
                        println(action.member.name)
                        generateModelFromType(action.paramsUser.map { it.type }, codeGenDirs.modelFolder)
                        try {
                            generateModelFromType(listOf(action.member.returnType), codeGenDirs.modelFolder)
                        } catch (ex: Exception) {
                            log.error("Error trying to generate types from return type:" + action.member.name + ", " + action.member.returnType.classifier?.toString())
                        }
                    }
                }

                // Generate file.
                generateApi(req, api, codeGenDirs.apiFolder, methodsBuffer.toString())
            } catch (ex: Exception) {
                log.error("Error inspecting and generating code for: ${api.area}.${api.name}")
                throw ex
            }
        }
    }

    /**
     * Collect all variables for the API
     */
    private fun collect(api: Api):Map<String, String>{
        return mapOf(
            "className" to  api.name.pascalCase(),
            "packageName" to  settings.packageName,
            "about" to  "Client side API for " + api.name.pascalCase(),
            "description" to  api.desc,
            "route" to  api.area + "/" + api.name,
            "version" to  "1.0.0"
        )
    }


    /**
     * Collect all variables for Action
     */
    private fun collect(api: Api, action: Action):Map<String, String>{
        val typeInfo = builder.buildTypeName(action.member.returnType)
        val verb = action.verb
        return mapOf(
            "route" to api.area + "/" + api.name + "/" + action.name,
            "verb" to verb.name,
            "methodName" to action.name,
            "methodDesc" to action.desc,
            "methodParams" to builder.buildArgs(action),
            "methodReturnType" to typeInfo.returnType(),
            "queryParams" to builder.buildQueryParams(action),
            "postDataDecl" to if (verb.name == Verbs.GET) "" else builder.mapTypeDecl,
            "postDataVars" to builder.buildDataParams(action),
            "postDataParam" to if (verb.name == Verbs.GET) "" else "postData,",
            "converterClass" to typeInfo.converterTypeName(),
            "parameterizedClassNames" to typeInfo.parameterizedNames,
            "parameterizedClassTypes" to typeInfo.parameterizedTypes(builder.buildTypeLoader())
        )
    }

    private fun generateModelFromType(types: List<KType>, modelFolder: File) {
        types.map { builder.buildTypeName(it) }
                .filter { it.isApplicableForCodeGen() }
                .forEach { typeInfo -> generateDTO(modelFolder, typeInfo.targetType) }
    }

    private fun generateApi(req: Request, api: Api, folder: File, methods: String) {
        val apiVars = collect(api).plus("methods" to methods)
        val apiName = api.name.pascalCase() + settings.templateClassSuffix
        templates.api.generate(apiVars, folder.absolutePath, apiName, settings.lang)
    }

    private fun generateMethod(api: Api, action: Action): String {
        val info = collect(api, action)
        val rawTemplate = templates.method.raw
        val finalTemplate = info.entries.fold(rawTemplate) { acc, entry ->
            acc.replace("@{${entry.key}}", entry.value)
        }
        return finalTemplate
    }

    private fun generateDTO(folder: File, cls: KClass<*>): String {
        val info = builder.buildModelInfo(cls)
        val rawTemplate = templates.dto.raw
        val template = rawTemplate
                .replace("@{packageName}", settings.packageName)
                .replace("@{className}", cls.simpleName ?: "")
                .replace("@{properties}", info)
        val file = File(folder, cls.simpleName?.pascalCase() + ".${settings.lang.ext}")
        file.writeText(template)
        return file.absolutePath
    }
}
