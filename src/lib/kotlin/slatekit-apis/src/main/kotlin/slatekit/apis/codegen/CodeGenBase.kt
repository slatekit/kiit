package slatekit.apis.codegen

import slatekit.apis.ApiContainer
import slatekit.apis.ApiReg
import slatekit.apis.ApiRegAction
import slatekit.apis.helpers.ApiHelper
import slatekit.apis.helpers.ApiLookup
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

        // Collection of all custom types
        this.container.lookup().visitApis({ apiReg, apiLookup ->

            // Get only the declared members in the api/class
            val declaredMembers = apiReg.cls.declaredMemberFunctions
            val declaredMemberLookup = declaredMembers.map { func -> ( func.name to true ) }.toMap()

            // Get all the actions on the api
            val buff = StringBuilder()

            // Iterate over all the api actions
            apiLookup.actions().values().forEach { apiRegAction ->

                // Ok to generate ?
                if(canGenerate(apiReg, apiRegAction, declaredMemberLookup)) {

                    // Generate code here.
                    val methodInfo = genMethod(req, apiReg, apiLookup, apiRegAction)
                    this.container.ctx.log.info("generating method for: " + apiReg.area + "/" + apiReg.name + "/" + apiRegAction.name)
                    buff.append(methodInfo)
                    buff.append(newline)
                }
            }

            // Get unique types
            // Iterate over all the api actions
            val uniqueTypes = apiLookup.actions().values().map{ apiRegAction ->
                val customTypes = apiRegAction.paramList.map { p -> buildTypeName(p.type) }.filter{ !it.first }
                customTypes.forEach { typeInfo ->
                    val cls = typeInfo.third
                    genModel(req, modelFolder, apiReg, apiLookup, apiRegAction, cls)
                }
            }

            // Generate file.
            genClientApi(req, apiReg, apiLookup, apiFolder, buff.toString())
        })
    }


    fun genClientApi(req: Request, apiReg: ApiReg, apiLookup:ApiLookup, folder: File, methods:String):Unit {
        val rawPackageName = req.args?.getStringOpt("package")
        val packageName = rawPackageName ?: apiReg.cls.qualifiedName ?: ""
        val rawTemplate = this.templateClass()
        val template = rawTemplate
                .replace("@{className}"  , apiReg.name.pascalCase())
                .replace("@{packageName}", packageName)
                .replace("@{about}"      , "Client side API for " + apiReg.name.pascalCase())
                .replace("@{description}", apiReg.desc)
                .replace("@{route}"      , apiReg.area + "/" + apiReg.name)
                .replace("@{version}"    , req.args?.getStringOrElse("version", "1.0.0") ?: "1.0.0")
                .replace("@{methods}"    , methods)

        File(folder, apiReg.name.pascalCase() + templateClassSuffix + ".java").writeText(template)
    }


    fun genMethod(req: Request, apiReg: ApiReg, apiLookup:ApiLookup, apiRegAction:ApiRegAction): String {
        val info = buildMethodInfo(apiRegAction)
        val rawTemplate = this.templateMethod()
        val template = rawTemplate
                .replace("@{methodName}"   , info["method"]  ?: "")
                .replace("@{methodDesc}"   , info["desc"]    ?: "")
                .replace("@{methodParams}" , info["args"]    ?: "")
                .replace("@{queryParams}"  , info["query"]   ?: "")
                .replace("@{dataParams}"   , info["data"]    ?: "")
                .replace("@{returnType}"   , info["returns"] ?: "")
                .replace("@{route}"        , info["route"]   ?: "")
                .replace("@{verb}"         , info["verb"]    ?: "")
        return template
    }


    fun genModel(req: Request, folder:File, apiReg: ApiReg, apiLookup:ApiLookup, apiRegAction:ApiRegAction, cls: KClass<*>): String {
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
        return mapOf(
            "route"   to  reg.api.area + "/" + reg.api.name + "/" + reg.name,
            "verb"    to  buildVerb(reg.name),
            "method"  to  reg.name,
            "desc"    to  reg.desc,
            "returns" to  buildTypeName(reg.member.returnType).second,
            "args"    to  buildArgs(reg),
            "query"   to  buildQueryParams(reg),
            "data"    to  buildDataParams(reg)
        )
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
    open fun buildTypeName(tpe: KType):Triple<Boolean, String, KClass<*>> {
        return when (tpe) {
        // Basic types
            KTypes.KStringType        -> Triple(true, "String"  , KTypes.KStringClass       )
            KTypes.KBoolType          -> Triple(true, "boolean" , KTypes.KBoolClass         )
            KTypes.KShortType         -> Triple(true, "short"   , KTypes.KShortClass        )
            KTypes.KIntType           -> Triple(true, "int"     , KTypes.KIntClass          )
            KTypes.KLongType          -> Triple(true, "long"    , KTypes.KLongClass         )
            KTypes.KFloatType         -> Triple(true, "float"   , KTypes.KFloatClass        )
            KTypes.KDoubleType        -> Triple(true, "double"  , KTypes.KDoubleClass       )
            KTypes.KDateTimeType      -> Triple(true, "DateTime", KTypes.KDateTimeClass     )
            KTypes.KLocalDateType     -> Triple(true, "DateTime", KTypes.KLocalDateClass    )
            KTypes.KLocalTimeType     -> Triple(true, "DateTime", KTypes.KLocalTimeClass    )
            KTypes.KLocalDateTimeType -> Triple(true, "DateTime", KTypes.KLocalDateTimeClass)
            KTypes.KZonedDateTimeType -> Triple(true, "DateTime", KTypes.KZonedDateTimeClass)
            KTypes.KDocType           -> Triple(true, "String"  , KTypes.KDocClass          )
            KTypes.KVarsType          -> Triple(true, "String"  , KTypes.KVarsClass         )
            KTypes.KSmartStringType   -> Triple(true, "String"  , KTypes.KSmartStringClass  )
            KTypes.KDecStringType     -> Triple(true, "String"  , KTypes.KDecStringClass    )
            KTypes.KDecIntType        -> Triple(true, "String"  , KTypes.KDecIntClass       )
            KTypes.KDecLongType       -> Triple(true, "String"  , KTypes.KDecLongClass      )
            KTypes.KDecDoubleType     -> Triple(true, "String"  , KTypes.KDecDoubleClass    )
            else                      -> {
                val cls = tpe.classifier as KClass<*>
                if(cls == List::class){
                    val listType = tpe.arguments[0]!!.type!!
                    val listCls = KTypes.getClassFromType(listType)
                    Triple(false, "List<" + listCls.simpleName + ">", cls)
                }
                else {
                    Triple(false, (tpe.classifier as KClass<*>).simpleName ?: "", cls)
                }
            }
        }
    }


    fun buildArgs(reg:ApiRegAction): String {
        return reg.paramList.foldIndexed( "", { ndx:Int, acc:String, param:KParameter ->
            acc + ( if(ndx > 0 ) "\t\t" else "" ) + buildArg(param) + "," + newline
        })
    }


    /**
     * builds a string of parameters to put into the query string.
     * e.g. queryParams.put("id", id);
     */
    open fun buildQueryParams(reg:ApiRegAction): String {
        return if(buildVerb(reg.name) == "get" ) {
            reg.paramList.foldIndexed("", { ndx: Int, acc: String, param: KParameter ->
                acc + (if (ndx > 0) "\t\t" else "") + "queryParams.put(\"" + param.name + "\", " + param.name + ");" + newline
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
            reg.paramList.foldIndexed("", { ndx: Int, acc: String, param: KParameter ->
                acc + (if (ndx > 0) "\t\t" else "") + "dataParams.put(\"" + param.name + "\", " + param.name + ");" + newline
            })
        }
        else {
            ""
        }
    }


    open fun buildArg(parameter:KParameter): String {
        return buildTypeName(parameter.type).second + " " + parameter.name
    }


    open fun buildModelInfo(cls:KClass<*>): String {
        val props = Reflector.getProperties(cls)
        val fields = props.foldIndexed( "", { ndx:Int, acc:String, prop:KProperty<*> ->
            val type = prop.returnType
            val typeInfo = buildTypeName(type)
            val field = "public " + typeInfo.second + " " + prop.name + ";" + newline
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