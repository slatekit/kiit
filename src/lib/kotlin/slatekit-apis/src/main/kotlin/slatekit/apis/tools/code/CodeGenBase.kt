package slatekit.apis.tools.code

import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberFunctions
import slatekit.apis.Verbs
import slatekit.apis.core.Action
import slatekit.apis.core.Api
import slatekit.common.*
import slatekit.common.ext.orElse
import slatekit.common.io.Files
import slatekit.common.io.Uris
import slatekit.common.requests.Request
import slatekit.common.utils.Props
import slatekit.meta.KTypes
import slatekit.meta.Reflector

abstract class CodeGenBase(val settings: CodeGenSettings) {

    /**
     * Basic type info for type conversion
     */
    open val basicTypes = mapOf<KType, TypeInfo>()

    /**
     * Represents the 3 code templates ( api.kt, method.kt, model.kt (DTO) )
     */
    val templates = CodeGenTemplates.load(settings.templatesFolder, settings.lang)

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

        // C:\Users\kv\blendlife-kotlin\core\blend.cli\output\
        val codeGenDirs = CodeGenDirs(File(outputFolderPath ?: ""))
        codeGenDirs.create(log)
        codeGenDirs.log(log)

        // Collection of all custom types
        val routes = this.settings.host.routes
        val rules = CodeGenRules(this.settings)
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

                    // Ok to generate ?
                    if (rules.isValidAction(api, action, declaredMemberLookup)) {
                        // Generate code here.
                        val methodInfo = genMethod(api, action)
                        log.info("generating method for: " + api.area + "/" + api.name + "/" + action.name)
                        methodsBuffer.append(methodInfo)
                        methodsBuffer.append(newline)
                    }
                }

                // Get unique types
                // Iterate over all the api actions
                api.actions.items.map { action ->
                    println(action.member.name)
                    generateModelFromType(action.paramsUser.map { it.type }, codeGenDirs.modelFolder)
                    try {
                        generateModelFromType(listOf(action.member.returnType), codeGenDirs.modelFolder)
                    } catch (ex: Exception) {
                        log.error("Error trying to generate types from return type:" + action.member.name + ", " + action.member.returnType.classifier?.toString())
                    }
                }

                // Generate file.
                genClientApi(req, api, codeGenDirs.apiFolder, methodsBuffer.toString())
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
        val typeInfo = buildTypeName(action.member.returnType)
        val verb = action.verb
        return mapOf(
            "route" to api.area + "/" + api.name + "/" + action.name,
            "verb" to verb.name,
            "methodName" to action.name,
            "methodDesc" to action.desc,
            "methodParams" to buildArgs(action),
            "methodReturnType" to typeInfo.targetReturnType,
            "queryParams" to buildQueryParams(action),
            "postDataDecl" to if (verb.name == Verbs.GET) "" else "HashMap<String, Object> postData = new HashMap<>();",
            "postDataVars" to buildDataParams(action),
            "postDataParam" to if (verb.name == Verbs.GET) "" else "postData,",
            "converterTypes" to typeInfo.conversionType,
            "converterClass" to typeInfo.converterTypeName()
        )
    }

    private fun generateModelFromType(types: List<KType>, modelFolder: File) {
        types.map { buildTypeName(it) }
                .filter { it.isApplicableForCodeGen() }
                .forEach { typeInfo -> genModel(modelFolder, typeInfo.dataType) }
    }

    private fun genClientApi(req: Request, api: Api, folder: File, methods: String) {
        val apiVars = collect(api)
        val apiName = api.name.pascalCase() + settings.templateClassSuffix
        templates.api.generate(apiVars, folder.absolutePath, apiName, settings.lang)
    }

    private fun genMethod(api: Api, action: Action): String {
        val info = collect(api, action)
        val rawTemplate = templates.method.raw
        val finalTemplate = info.entries.fold(rawTemplate) { acc, entry ->
            acc.replace("@{${entry.key}}", entry.value)
        }
        return finalTemplate
    }

    private fun genModel(folder: File, cls: KClass<*>): String {
        val info = buildModelInfo(cls)
        val rawTemplate = templates.dto.raw
        val template = rawTemplate
                .replace("@{packageName}", settings.packageName)
                .replace("@{className}", cls.simpleName ?: "")
                .replace("@{properties}", info)
        val file = File(folder, cls.simpleName?.pascalCase() + ".${settings.lang.ext}")
        file.writeText(template)
        return file.absolutePath
    }


    /**
     * builds the name of the datatype for the target(Java) language.
     */
    /**
     * builds the name of the datatype for the target(Java) language.
     */
    open fun buildTypeName(tpe: KType): TypeInfo {
        return if (basicTypes.containsKey(tpe)) {
            basicTypes[tpe]!!
        } else {
            val cls = tpe.classifier as KClass<*>
            if (Reflector.isSlateKitEnum(cls)) {
                buildTypeName(KTypes.KIntType)
            } else if (cls == slatekit.results.Result::class) {
                val genType = tpe.arguments[0].type!!
                val finalType = buildTypeName(genType)
                finalType
            } else if (cls.supertypes.contains(KTypes.KSmartValueType)) {
                TypeInfo(true, false, "String", "String", KTypes.KSmartValueClass, KTypes.KSmartValueClass, "String.class")
            } else if (cls == List::class) {
                val listType = tpe.arguments[0].type!!
                val listCls = KTypes.getClassFromType(listType)
                val listTypeInfo = buildTypeName(listType)
                val typeSig = "List<" + listTypeInfo.targetReturnType + ">"
                TypeInfo(false, true, typeSig, typeSig, List::class, listCls, listTypeInfo.conversionType)
            } else if (cls == Map::class) {
                val tpeKey = tpe.arguments[0].type!!
                val tpeVal = tpe.arguments[1].type!!
                // val clsKey = KTypes.getClassFromType(tpeKey)
                val clsVal = KTypes.getClassFromType(tpeVal)
                val keyTypeInfo = buildTypeName(tpeKey)
                val valTypeInfo = buildTypeName(tpeVal)
                val sig = "Map<" + keyTypeInfo.targetReturnType + "," + valTypeInfo.targetReturnType + ">"
                TypeInfo(false, true, sig, sig, Map::class, clsVal, "${keyTypeInfo.conversionType},${valTypeInfo.conversionType}")
            } else if (cls == Pair::class) {
                val tpeFirst = tpe.arguments[0].type!!
                val tpeSecond = tpe.arguments[1].type!!
                val firstTypeInfo = buildTypeName(tpeFirst)
                val secondTypeInfo = buildTypeName(tpeSecond)
                val sig = "Pair<" + firstTypeInfo.targetReturnType + "," + secondTypeInfo.targetReturnType + ">"
                TypeInfo(false, false, sig, sig, cls, cls, "${firstTypeInfo.conversionType},${secondTypeInfo.conversionType}")
            } else {
                val sig = cls.simpleName ?: ""
                TypeInfo(false, false, sig, sig, cls, cls, sig + ".class")
            }
        }
    }

    fun buildArgs(reg: Action): String {
        return reg.paramsUser.foldIndexed("") { ndx: Int, acc: String, param: KParameter ->
            acc + (if (ndx > 0) "\t\t" else "") + buildArg(param) + "," + newline
        }
    }

    /**
     * builds a string of parameters to put into the query string.
     * e.g. queryParams.put("id", id);
     */
    abstract fun buildQueryParams(reg: Action): String

    /**
     * builds a string of the parameters to put into the entity/body of request
     * e..g dataParams.put('id", id);
     */
    abstract fun buildDataParams(reg: Action): String

    /**
     * builds an individual argument to the method
     */
    abstract fun buildArg(parameter: KParameter): String

    /**
     * builds all the properties/fields
     */
    abstract fun buildModelInfo(cls: KClass<*>): String
}
