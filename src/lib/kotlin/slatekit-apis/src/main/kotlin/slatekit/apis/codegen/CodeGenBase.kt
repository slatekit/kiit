package slatekit.apis.codegen

import slatekit.apis.ApiContainer
import slatekit.apis.ApiReg
import slatekit.apis.ApiRegAction
import slatekit.apis.helpers.ApiHelper
import slatekit.apis.core.Actions
import slatekit.common.*
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberFunctions

open class CodeGenBase(val container:ApiContainer, val generateDeclaredMethodsOnly:Boolean = true) {


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


    open fun templateClass():String = ""


    open fun templateModel():String = ""


    open fun templateMethod():String = ""


    /**
     * @command="codegen" -lang="java" -package="blendlife.api" -pathToTemplates="C:\Dev\github\blend-server\scripts\templates\codegen"
     *  -nameOfTemplateClass="codegen-java-api.txt"
     *  -nameOfTemplateMethod="codegen-java-method.txt"
     *  -nameOfTemplateModel="codegen-java-model.txt"
     */
    fun generate(req:Request):Unit {
        val dirs = this.container.ctx.dirs
        val outputFolder = dirs?.pathToOutputs ?: Props.tmpDir
        val dateFolder = Files.folderNameByDate()

        // C:\Users\kv\blendlife-kotlin\core\blend.cli\output\2017-11-08
        val targetFolder = File(outputFolder, dateFolder)
        val apiFolder = File(targetFolder, "api")
        val modelFolder = File(targetFolder, "dto")
        targetFolder.mkdir()
        apiFolder.mkdir()
        modelFolder.mkdir()
        val log = this.container.ctx.log
        log.info("Target folder: " + targetFolder.absolutePath)

        // Collection of all custom types
        this.container.lookup().visitApis({ apiReg, apiLookup ->

            try {
                if(ApiHelper.isWebProtocol(apiReg.protocol, "")) {
                    println("API: " + apiReg.area + "." + apiReg.name)

                    // Get only the declared members in the api/class
                    val declaredMembers = apiReg.cls.declaredMemberFunctions
                    val declaredMemberLookup = declaredMembers.map { func -> (func.name to true) }.toMap()

                    // Get all the actions on the api
                    val buff = StringBuilder()

                    // Iterate over all the api actions
                    apiLookup.actions().values().forEach { apiRegAction ->

                        // Ok to generate ?
                        if (canGenerate(apiReg, apiRegAction, declaredMemberLookup)) {

                            // Generate code here.
                            val methodInfo = genMethod(req, apiReg, apiLookup, apiRegAction)
                            this.container.ctx.log.info("generating method for: " + apiReg.area + "/" + apiReg.name + "/" + apiRegAction.name)
                            buff.append(methodInfo)
                            buff.append(newline)
                        }
                    }

                    // Get unique types
                    // Iterate over all the api actions
                    val uniqueTypes = apiLookup.actions().values().map { apiRegAction ->
                        println(apiRegAction.member.name)
                        val customTypes = apiRegAction.paramsUser.map { p -> buildTypeName(p.type) }
                                .filter {
                                    !it.isBasicType
                                            && !it.isCollection
                                            && it.dataType != Request::class
                                            && it.dataType != Any::class
                                            && it.dataType != Context::class
                                }
                        customTypes.forEach { typeInfo ->
                            val cls = typeInfo.dataType
                            genModel(req, modelFolder, apiReg, apiLookup, apiRegAction, cls)
                        }
                    }

                    // Generate file.
                    genClientApi(req, apiReg, apiLookup, apiFolder, buff.toString())
                }
            }
            catch(ex:Exception){
                log.error("Error inspecting and generating code for: ${apiReg.area}.${apiReg.name}")
                throw ex
            }
        })
    }


    fun genClientApi(req: Request, apiReg: ApiReg, apiLookup: Actions, folder: File, methods:String):Unit {
        val rawPackageName = req.data?.getStringOpt("package")
        val packageName = rawPackageName ?: apiReg.cls.qualifiedName ?: ""
        val rawTemplate = this.templateClass()
        val template = rawTemplate
                .replace("@{className}"  , apiReg.name.pascalCase())
                .replace("@{packageName}", packageName)
                .replace("@{about}"      , "Client side API for " + apiReg.name.pascalCase())
                .replace("@{description}", apiReg.desc)
                .replace("@{route}"      , apiReg.area + "/" + apiReg.name)
                .replace("@{version}"    , req.data?.getStringOrElse("version", "1.0.0") ?: "1.0.0")
                .replace("@{methods}"    , methods)

        File(folder, apiReg.name.pascalCase() + templateClassSuffix + ".java").writeText(template)
    }


    fun genMethod(req: Request, apiReg: ApiReg, apiLookup: Actions, apiRegAction:ApiRegAction): String {
        val info = buildMethodInfo(apiRegAction)
        val rawTemplate = this.templateMethod()
        val finalTemplate = info.entries.fold( rawTemplate, { acc, entry ->
            acc.replace("@{${entry.key}}", entry.value)
        })
        return finalTemplate
    }


    fun genModel(req: Request, folder:File, apiReg: ApiReg, apiLookup: Actions, apiRegAction:ApiRegAction, cls: KClass<*>): String {
        val info = buildModelInfo(cls)
        val rawTemplate = this.templateModel()
        val template = rawTemplate
                .replace("@{className}"  , cls.simpleName ?: "")
                .replace("@{properties}" , info)
        val file = File(folder, cls.simpleName?.pascalCase() + ".java")
        file.writeText(template)
        return file.absolutePath
    }


    fun buildMethodInfo(reg: ApiRegAction):Map<String,String> {
        val typeInfo = buildTypeName(reg.member.returnType)
        val verb = buildVerb(reg.name)
        return mapOf(
            "route"                 to  reg.api.area + "/" + reg.api.name + "/" + reg.name,
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
    open fun buildTypeName(tpe: KType): TypeInfo {
        return when (tpe) {
            // Basic types
            KTypes.KStringType        -> TypeInfo(true, false, "String"  , "String"  , KTypes.KStringClass       , KTypes.KStringClass       , "String"  + ".class")
            KTypes.KBoolType          -> TypeInfo(true, false, "boolean" , "Boolean" , KTypes.KBoolClass         , KTypes.KBoolClass         , "Boolean" + ".class")
            KTypes.KShortType         -> TypeInfo(true, false, "short"   , "Short"   , KTypes.KShortClass        , KTypes.KShortClass        , "Short"   + ".class")
            KTypes.KIntType           -> TypeInfo(true, false, "int"     , "Integer" , KTypes.KIntClass          , KTypes.KIntClass          , "Integer" + ".class")
            KTypes.KLongType          -> TypeInfo(true, false, "long"    , "Long"    , KTypes.KLongClass         , KTypes.KLongClass         , "Long"    + ".class")
            KTypes.KFloatType         -> TypeInfo(true, false, "float"   , "Float"   , KTypes.KFloatClass        , KTypes.KFloatClass        , "Float"   + ".class")
            KTypes.KDoubleType        -> TypeInfo(true, false, "double"  , "Double"  , KTypes.KDoubleClass       , KTypes.KDoubleClass       , "Double"  + ".class")
            KTypes.KDateTimeType      -> TypeInfo(true, false, "Date"    , "Date"    , KTypes.KDateTimeClass     , KTypes.KDateTimeClass     , "Date"    + ".class")
            KTypes.KLocalDateType     -> TypeInfo(true, false, "Date"    , "Date"    , KTypes.KLocalDateClass    , KTypes.KLocalDateClass    , "Date"    + ".class")
            KTypes.KLocalTimeType     -> TypeInfo(true, false, "Date"    , "Date"    , KTypes.KLocalTimeClass    , KTypes.KLocalTimeClass    , "Date"    + ".class")
            KTypes.KLocalDateTimeType -> TypeInfo(true, false, "Date"    , "Date"    , KTypes.KLocalDateTimeClass, KTypes.KLocalDateTimeClass, "Date"    + ".class")
            KTypes.KZonedDateTimeType -> TypeInfo(true, false, "Date"    , "Date"    , KTypes.KZonedDateTimeClass, KTypes.KZonedDateTimeClass, "Date"    + ".class")
            KTypes.KDocType           -> TypeInfo(true, false, "String"  , "String"  , KTypes.KDocClass          , KTypes.KDocClass          , "String"  + ".class")
            KTypes.KVarsType          -> TypeInfo(true, false, "String"  , "String"  , KTypes.KVarsClass         , KTypes.KVarsClass         , "String"  + ".class")
            KTypes.KSmartStringType   -> TypeInfo(true, false, "String"  , "String"  , KTypes.KSmartStringClass  , KTypes.KSmartStringClass  , "String"  + ".class")
            KTypes.KContentType       -> TypeInfo(true, false, "String"  , "String"  , KTypes.KContentClass      , KTypes.KContentClass      , "String"  + ".class")
            KTypes.KDecStringType     -> TypeInfo(true, false, "String"  , "String"  , KTypes.KDecStringClass    , KTypes.KDecStringClass    , "String"  + ".class")
            KTypes.KDecIntType        -> TypeInfo(true, false, "String"  , "String"  , KTypes.KDecIntClass       , KTypes.KDecIntClass       , "String"  + ".class")
            KTypes.KDecLongType       -> TypeInfo(true, false, "String"  , "String"  , KTypes.KDecLongClass      , KTypes.KDecLongClass      , "String"  + ".class")
            KTypes.KDecDoubleType     -> TypeInfo(true, false, "String"  , "String"  , KTypes.KDecDoubleClass    , KTypes.KDecDoubleClass    , "String"  + ".class")
            KTypes.KAnyType           -> TypeInfo(false,false, "Object"  , "Object"  , KTypes.KAnyClass          , KTypes.KAnyClass          , "Object"  + ".class")
            else                      -> {
                val cls = tpe.classifier as KClass<*>
                if(cls == Result::class) {
                    val genType = tpe.arguments[0]!!.type!!
                    val finalType = buildTypeName(genType)
                    finalType
                }
                else if (cls.supertypes.contains(KTypes.KSmartStringType)){
                    TypeInfo(true, false, "String"  , "String"  , KTypes.KSmartStringClass  , KTypes.KSmartStringClass, "String.class")
                }
                else if(cls == List::class){
                    val listType = tpe.arguments[0]!!.type!!
                    val listCls = KTypes.getClassFromType(listType)
                    val listTypeInfo = buildTypeName(listType)
                    val typeSig = "List<" + listTypeInfo.targetReturnType + ">"
                    TypeInfo(false, true, typeSig, typeSig, List::class, listCls, listTypeInfo.conversionType)
                }
                else if(cls == Map::class){
                    val tpeKey = tpe.arguments[0].type!!
                    val tpeVal = tpe.arguments[1].type!!
                    val clsKey = KTypes.getClassFromType(tpeKey)
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
    }


    fun buildArgs(reg:ApiRegAction): String {
        return reg.paramsUser.foldIndexed( "", { ndx:Int, acc:String, param:KParameter ->
            acc + ( if(ndx > 0 ) "\t\t" else "" ) + buildArg(param) + "," + newline
        })
    }


    /**
     * builds a string of parameters to put into the query string.
     * e.g. queryParams.put("id", id);
     */
    open fun buildQueryParams(reg:ApiRegAction): String {
        return if(buildVerb(reg.name) == "get" ) {
            reg.paramsUser.foldIndexed("", { ndx: Int, acc: String, param: KParameter ->
                acc + (if (ndx > 0) "\t\t" else "") + "queryParams.put(\"" + param.name + "\", String.valueOf(" + param.name + "));" + newline
            })
        }
        else {
            ""
        }
    }


    /**
     * builds a string of the parameters to put into the entity/body of request
     * e..g dataParams.put('id", id);
     */
    open fun buildDataParams(reg:ApiRegAction): String {
        return if(buildVerb(reg.name) != "get") {
            reg.paramsUser.foldIndexed("", { ndx: Int, acc: String, param: KParameter ->
                acc + (if (ndx > 0) "\t\t" else "") + "postData.put(\"" + param.name + "\", " + param.name + ");" + newline
            })
        }
        else {
            ""
        }
    }


    open fun buildArg(parameter:KParameter): String {
        return buildTypeName(parameter.type).targetParameterType + " " + parameter.name
    }


    open fun buildModelInfo(cls:KClass<*>): String {
        val props = Reflector.getProperties(cls)
        val fields = props.foldIndexed( "", { ndx:Int, acc:String, prop:KProperty<*> ->
            val type = prop.returnType
            val typeInfo = buildTypeName(type)
            val field = "public " + typeInfo.targetParameterType + " " + prop.name + ";" + newline
            acc + (if (ndx > 0) "\t" else "") + field
        })
        return fields
    }


    open fun canGenerate(apiReg:ApiReg, apiRegAction:ApiRegAction, declaredMemberLookup:Map<String,Boolean>): Boolean {
        // Only include declared items
        val isDeclared = declaredMemberLookup.containsKey(apiRegAction.name)
        val isWebProtocol = ApiHelper.isWebProtocol(apiRegAction.protocol, apiReg.protocol)
        return (!generateDeclaredMethodsOnly || isDeclared ) && isWebProtocol
    }

    companion object {

        fun getContent(folderPath:String, path:String): String {
            val pathToFile = folderPath + File.separator + path
            val content = Uris.readText(pathToFile) ?: ""
            return content
        }
    }
}
