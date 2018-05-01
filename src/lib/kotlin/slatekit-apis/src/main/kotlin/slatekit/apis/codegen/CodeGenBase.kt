package slatekit.apis.codegen

import slatekit.apis.core.Api
import slatekit.apis.core.Action
import slatekit.apis.helpers.ApiHelper
import slatekit.common.*
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberFunctions

abstract class CodeGenBase(val settings: CodeGenSettings) {


    open val basicTypes = mapOf<KType, TypeInfo>()


    /** Represents type information for the purspose of code generation.
     * @param isBasicType          : whether this is a number, boolean, String
     * @param isCollection         : whether this is a list or map
     * @param targetParameterType  : The target type name used as a parameter input: e.g. "int", "double"
     * @param targetReturnType     : The target type name used as a return type. For parsing/generic purposes,
     *                               this is the corresponding object type for a type. e.g. Integer for int.
     *                               e.g. "Integer", "Double"
     * @param containerType        : For collections, the Kotlin class representing the container type. e.g. "List::class", "Map::class"
     * @param dataType             : The Kotlin class representing the data type.
     * @param conversionType       : The Kotlin
     */
    data class TypeInfo(val isBasicType:Boolean,
                        val isCollection:Boolean,
                        val targetParameterType:String,
                        val targetReturnType:String,
                        val containerType:KClass<*>,
                        val dataType:KClass<*>,
                        val conversionType:String,
                        val keyType:KClass<*>? = null) {

        fun isList():Boolean = containerType == List::class
        fun isMap():Boolean = containerType == Map::class
        fun isObject():Boolean = !isBasicType && !isCollection
        fun isPair():Boolean = isObject() && dataType.simpleName?.startsWith("Pair") ?: false
    }


    open val templateClassSuffix = "Api"


    open fun templateClass():String {
        val content = getContent(settings.templatesFolder, settings.classFile)
        return content
    }


    open fun templateModel():String = getContent(settings.templatesFolder, settings.modelFile)


    open fun templateMethod():String = getContent(settings.templatesFolder, settings.methodFile)


    /**
     * @command="codegen" -lang="java" -package="blendlife.api" -pathToTemplates="C:\Dev\github\blend-server\scripts\templates\codegen"
     *  -nameOfTemplateClass="codegen-java-api.txt"
     *  -nameOfTemplateMethod="codegen-java-method.txt"
     *  -nameOfTemplateModel="codegen-java-model.txt"
     */
    fun generate(req:Request):Unit {
        val dirs = this.settings.host.ctx.dirs
        val outputFolderPathRaw = this.settings.outputFolder.orElse( dirs?.pathToOutputs ?: Props.tmpDir)
        val outputFolderPath = Uris.interpret(outputFolderPathRaw)
        val dateFolder = Files.folderNameByDate()

        // C:\Users\kv\blendlife-kotlin\core\blend.cli\output\2017-11-08
        val outputFolder = File(outputFolderPath)
        val targetFolder = File(outputFolder, dateFolder)
        val apiFolder = File(targetFolder, "api")
        val modelFolder = File(targetFolder, "dto")
        outputFolder.mkdir()
        targetFolder.mkdir()
        apiFolder.mkdir()
        modelFolder.mkdir()
        val log = this.settings.host.ctx.logs.getLogger()
        log.info("Target folder: " + targetFolder.absolutePath)

        // Collection of all custom types
        this.settings.host.routes.visitApis({ _, api  ->

            try {
                if(ApiHelper.isWebProtocol(api.protocol, "")) {
                    println("API: " + api.area + "." + api.name)

                    // Get only the declared members in the api/class
                    val declaredMembers = api.cls.declaredMemberFunctions
                    val declaredMemberLookup = declaredMembers.map { func -> (func.name to true) }.toMap()

                    // Get all the actions on the api
                    val buff = StringBuilder()

                    // Iterate over all the api actions
                    api.actions.items.forEach { action ->

                        // Ok to generate ?
                        if (canGenerate(api, action, declaredMemberLookup)) {

                            // Generate code here.
                            val methodInfo = genMethod(api, action)
                            log.info("generating method for: " + api.area + "/" + api.name + "/" + action.name)
                            buff.append(methodInfo)
                            buff.append(newline)
                        }
                    }

                    // Get unique types
                    // Iterate over all the api actions
                    api.actions.items.map { action ->
                        println(action.member.name)
                        val customTypes = action.paramsUser.map { p -> buildTypeName(p.type) }
                                .filter {
                                    !it.isBasicType
                                            && !it.isCollection
                                            && it.dataType != Request::class
                                            && it.dataType != Any::class
                                            && it.dataType != Context::class
                                }
                        customTypes.forEach { typeInfo ->
                            val cls = typeInfo.dataType
                            genModel(modelFolder, cls)
                        }
                    }

                    // Generate file.
                    genClientApi(req, api, apiFolder, buff.toString())
                }
            }
            catch(ex:Exception){
                log.error("Error inspecting and generating code for: ${api.area}.${api.name}")
                throw ex
            }
        })
    }


    fun genClientApi(req: Request, apiReg: Api, folder: File, methods:String):Unit {
        val packageName = settings.packageName
        val rawTemplate = this.templateClass()
        val template = rawTemplate
                .replace("@{className}"  , apiReg.name.pascalCase())
                .replace("@{packageName}", packageName)
                .replace("@{about}"      , "Client side API for " + apiReg.name.pascalCase())
                .replace("@{description}", apiReg.desc)
                .replace("@{route}"      , apiReg.area + "/" + apiReg.name)
                .replace("@{version}"    , req.data.getStringOrElse("version", "1.0.0"))
                .replace("@{methods}"    , methods)

        File(folder, apiReg.name.pascalCase() + templateClassSuffix + ".${settings.extension}").writeText(template)
    }


    fun genMethod(api: Api, action: Action): String {
        val info = buildMethodInfo(api, action)
        val rawTemplate = this.templateMethod()
        val finalTemplate = info.entries.fold( rawTemplate, { acc, entry ->
            acc.replace("@{${entry.key}}", entry.value)
        })
        return finalTemplate
    }


    fun genModel(folder:File, cls: KClass<*>): String {
        val info = buildModelInfo(cls)
        val rawTemplate = this.templateModel()
        val template = rawTemplate
                .replace("@{packageName}", settings.packageName)
                .replace("@{className}"  , cls.simpleName ?: "")
                .replace("@{properties}" , info)
        val file = File(folder, cls.simpleName?.pascalCase() + ".${settings.extension}")
        file.writeText(template)
        return file.absolutePath
    }


    fun buildMethodInfo(api:slatekit.apis.core.Api, reg: Action):Map<String,String> {
        val typeInfo = buildTypeName(reg.member.returnType)
        val verb = buildVerb(reg.name)
        return mapOf(
            "route"                 to  api.area + "/" + api.name + "/" + reg.name,
            "verb"                  to  verb,
            "methodName"            to  reg.name,
            "methodDesc"            to  reg.desc,
            "methodParams"          to  buildArgs(reg),
            "methodReturnType"      to  typeInfo.targetReturnType,
            "queryParams"           to  buildQueryParams(reg),
            "postDataDecl"          to  if(verb == "get") "" else "HashMap<String, Object> postData = new HashMap<>();",
            "postDataVars"          to  buildDataParams(reg),
            "postDataParam"         to  if(verb == "get") "" else "postData,",
            "converterTypes"        to  typeInfo.conversionType,
            "converterClass"        to  getConverterTypeName(typeInfo)
        )
    }


    fun getConverterTypeName(info:TypeInfo):String {
        return if(info.isCollection)
            if(info.isList())
                "List"
            else
                "Map"
        else
            if ( info.isPair())
                "Pair"
            else
                "Single"
    }


    fun buildVerb(name:String):String {
        val lcase = name.toLowerCase()
        return if(lcase.startsWith("get")) {
            "get"
        }
        else if(lcase.startsWith("create")){
            "post"
        }
        else if(lcase.startsWith("update")){
            "put"
        }
        else if(lcase.startsWith("delete")){
            "delete"
        }
        else {
            "post"
        }
    }


    /**
     * builds the name of the datatype for the target(Java) language.
     */
    /**
     * builds the name of the datatype for the target(Java) language.
     */
    open fun buildTypeName(tpe: KType): TypeInfo {
        return if(basicTypes.containsKey(tpe)) {
            basicTypes[tpe]!!
        }
        else  {
            val cls = tpe.classifier as KClass<*>
            if(cls == Result::class) {
                val genType = tpe.arguments[0].type!!
                val finalType = buildTypeName(genType)
                finalType
            }
            else if (cls.supertypes.contains(KTypes.KSmartStringType)){
                TypeInfo(true, false, "String"  , "String"  , KTypes.KSmartStringClass  , KTypes.KSmartStringClass, "String.class")
            }
            else if(cls == List::class){
                val listType = tpe.arguments[0].type!!
                val listCls = KTypes.getClassFromType(listType)
                val listTypeInfo = buildTypeName(listType)
                val typeSig = "List<" + listTypeInfo.targetReturnType + ">"
                TypeInfo(false, true, typeSig, typeSig, List::class, listCls, listTypeInfo.conversionType)
            }
            else if(cls == Map::class){
                val tpeKey = tpe.arguments[0].type!!
                val tpeVal = tpe.arguments[1].type!!
                //val clsKey = KTypes.getClassFromType(tpeKey)
                val clsVal = KTypes.getClassFromType(tpeVal)
                val keyTypeInfo = buildTypeName(tpeKey)
                val valTypeInfo = buildTypeName(tpeVal)
                val sig = "Map<" + keyTypeInfo.targetReturnType + "," + valTypeInfo.targetReturnType + ">"
                TypeInfo(false, true, sig, sig, Map::class, clsVal, "${keyTypeInfo.conversionType},${valTypeInfo.conversionType}")
            }
            else if(cls == Pair::class) {
                val tpeFirst = tpe.arguments[0].type!!
                val tpeSecond = tpe.arguments[1].type!!
                val firstTypeInfo = buildTypeName(tpeFirst)
                val secondTypeInfo = buildTypeName(tpeSecond)
                val sig = "Pair<" + firstTypeInfo.targetReturnType + "," + secondTypeInfo.targetReturnType + ">"
                TypeInfo(false, false, sig, sig, cls, cls, "${firstTypeInfo.conversionType},${secondTypeInfo.conversionType}")
            }
            else {
                val sig = cls.simpleName ?: ""
                TypeInfo(false, false, sig, sig, cls, cls, sig + ".class")
            }
        }
    }


    fun buildArgs(reg: Action): String {
        return reg.paramsUser.foldIndexed( "", { ndx:Int, acc:String, param:KParameter ->
            acc + ( if(ndx > 0 ) "\t\t" else "" ) + buildArg(param) + "," + newline
        })
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
    abstract fun buildArg(parameter:KParameter): String


    /**
     * builds all the properties/fields
     */
    abstract fun buildModelInfo(cls:KClass<*>): String


    open fun canGenerate(apiReg: Api, apiRegAction: Action, declaredMemberLookup:Map<String,Boolean>): Boolean {
        // Only include declared items
        val isDeclared = declaredMemberLookup.containsKey(apiRegAction.name)
        val isWebProtocol = ApiHelper.isWebProtocol(apiRegAction.protocol, apiReg.protocol)
        return (!this.settings.declaredMethodsOnly || isDeclared ) && isWebProtocol
    }

    companion object {

        fun getContent(folderPath:String, path:String): String {
            val pathToFile = folderPath + File.separator + path
            val content = Uris.readText(pathToFile) ?: ""
            return content
        }
    }
}
