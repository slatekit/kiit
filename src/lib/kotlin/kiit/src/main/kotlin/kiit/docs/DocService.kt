package kiit.docs

import kiit.common.*
import kiit.utils.writer.ConsoleWriter
import kiit.common.ext.toStringYYYYMMDD
import kiit.common.utils.StringParser
import kiit.results.Notice
import kiit.results.Try
import kiit.results.Success
import java.io.File

class DocService(val rootdir: String, val outputDir: String, val templatePathRaw: String) {

    private val templatePath = File(rootdir, templatePathRaw).toString()
    private var template = ""
    private val writer = ConsoleWriter()
    private val docFiles = DocFiles()
    private val version = "0.9.35"

    private val docs = listOf(
              Doc("Args"         ,  "kiit-common"  , "kiit.common.args"               ,"kiit.common.args.Args"                    , version, "Example_Args"             , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "A lexical command line argument parser with optional support for allowing a route/method call in the beginning")
            , Doc("Auth"         ,  "kiit-common"  , "kiit.common.auth"               ,"kiit.common.auth.Auth"                    , version, "Example_Auth"             , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "A simple authentication component to check current user role and permissions")
            , Doc("Config"       ,  "kiit-common"  , "kiit.common.conf"               ,"kiit.common.conf.Config"                  , version, "Example_Config"           , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "Thin wrapper on java properties based config with decryption support, uri loading, and mapping of database connections and api keys")
            , Doc("Console"      ,  "kiit-common"  , "kiit.common.console"            ,"kiit.common.console.Console"              , version, "Example_Console"          , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "Enhanced printing to console with support for semantic writing like title, subtitle, url, error, etc with colors")
            , Doc("DateTime"     ,  "kiit-common"  , "kiit.common"                    ,"kiit.common.DateTime"                     , version, "Example_DateTime"         , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "DataTime wrapper around Java 8 LocalDateTime providing a simplified interface, some convenience, extra features.")
            , Doc("Encrypt"      ,  "kiit-common"  , "kiit.common.encrypt"            ,"kiit.common.encrypt.Encryptor"            , version, "Example_Encryptor"        , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "Encryption using AES")
            , Doc("Env"          ,  "kiit-common"  , "kiit.common.envs"               ,"kiit.common.envs.Env"                     , version, "Example_Env"              , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "Environment selector and validator for environments such as (local, dev, qa, stg, prod) )")
            , Doc("Folders"      ,  "kiit-common"  , "kiit.common.info"               ,"kiit.common.info.Folders"                 , version, "Example_Folders"          , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "Standardized application folder setup; includes conf, cache, inputs, logs, outputs")
            , Doc("Info"         ,  "kiit-common"  , "kiit.common.info"               ,"kiit.common.info.About"                   , version, "Example_Info"             , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "Get/Set useful diagnostics about the system, language runtime, application and more")
            , Doc("Lex"          ,  "kiit-common"  , "kiit.common.lex"                ,"kiit.common.lex.Lexer"                    , version, "Example_Lexer"            , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "Lexer for parsing text into tokens")
            , Doc("Logger"       ,  "kiit-common"  , "kiit.common.log"                ,"kiit.common.log.Logger"                   , version, "Example_Logger"           , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "A simple logger with extensibility for using other 3rd party loggers")
            , Doc("Random"       ,  "kiit-common"  , "kiit.common"                    ,"kiit.common.utils.Random"                       , version, "Example_Random"           , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "A random generator for strings, guids, numbers, alpha-numeric, and alpha-numeric-symbols for various lengths" )
            , Doc("Request"      ,  "kiit-common"  , "kiit.common.requests"           ,"kiit.common.requests.Request"             , version, "Example_Request"          , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "Models and abstracts a send that can represent either an HTTP send or a send from the CLI ( Command line )." )
            , Doc("SmartValues"  ,  "kiit-common"  , "kiit.common.smartvalues"        ,"kiit.common.smartvalues.Email"            , version, "Example_SmartValues"      , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "A way to store, validate and describe strongly typed and formatted strings")
            , Doc("Todo"         ,  "kiit-common"  , "kiit.common"                    ,"kiit.common.Todo"                         , version, "Example_NOTE"             , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "A programmatic approach to marking and tagging code that is strongly typed and consistent")
            , Doc("Serialization",  "kiit-common"  , "kiit.serialization"             ,"kiit.common.serialization.Serializer"     , version, "Example_Serialization"    , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "Serializers for data classes to generate CSV, Props, HOCON, JSON files")
            , Doc("Templates"    ,  "kiit-common"  , "kiit.common.templates"          ,"kiit.common.templates.Templates"          , version, "Example_Templates"        , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "A micro template system for processing text with variables, useful for generating dynamic emails/messages.")
            , Doc("Validations"  ,  "kiit-common"  , "kiit.common.validation"         ,"kiit.common.validation.ValidationFuncs"   , version, "Example_Validation"       , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "A set of validation related components, simple validation checks, RegEx checks, error collection and custom validators")
            , Doc("Utils"        ,  "kiit-common"  , "kiit.common.utils"              ,"kiit.common.console.ConsoleWriter"        , version, "Example_Utils"            , true  , false, false, "utils", "kiit.common.jar"  , "res"                    , "Various utilities available in the Slate library")

            , Doc("Query"        ,  "kiit-common"  , "kiit.query.Query"                         ,"kiit.query.Query"                         , version, "Example_Query"            , false , false, true , "utils", "kiit.common.jar"  , ""                    , "Query pattern used for specifying search and selection criteria" )
            , Doc("Model"        ,  "kiit-meta"    , "kiit.common.Model"                        ,"kiit.common.Model"                        , version, "Example_Model"            , true  , false, false, "utils", "kiit.common.jar"  , ""                    , "Allows construction of model schema with fields for code-generation. Also used in the ORM mapper" )
            , Doc("Reflect"      ,  "kiit-meta"    , "kiit.common.Reflector"                    ,"kiit.common.Reflector"                    , version, "Example_Reflect"          , true  , false, false, "utils", "kiit.common.jar"  , ""                    , "Reflection helper to create instances, get methods, fields, annotations and more" )

            , Doc("ext-Users"    ,  "slatekit-ext"     , "kiit.ext.users.User"                      ,"kiit.ext.users.User"                      , version, "Example_Ext_Users"        , false , true , false, "feat" , "kiit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Devices"  ,  "slatekit-ext"     , "kiit.ext.devices.Device"                  ,"kiit.ext.devices.Device"                  , version, "Example_Ext_Users"        , false , true , false, "feat" , "kiit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Reg"      ,  "slatekit-ext"     , "kiit.ext.reg.RegService"                  ,"kiit.ext.reg.RegService"                  , version, "Example_Ext_Users"        , false , true , false, "feat" , "kiit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Settings" ,  "slatekit-ext"     , "kiit.ext.settings.Settings"               ,"kiit.ext.settings.Settings"               , version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Tasks"    ,  "slatekit-ext"     , "slatekit.ext.tasks.Tasks"                     ,"slatekit.ext.tasks.Tasks"                     , version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Invites"  ,  "slatekit-ext"     , "slatekit.ext.invites.Invite"                  ,"slatekit.ext.invites.Invite"                  , version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Logs"     ,  "slatekit-ext"     , "slatekit.ext.logs.Log"                        ,"slatekit.ext.logs.Log"                        , version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Status"   ,  "slatekit-ext"     , "slatekit.ext.status.Status"                   ,"slatekit.ext.status.Status"                   , version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Audits"   ,  "slatekit-ext"     , "slatekit.ext.audits.Audits"                   ,"slatekit.ext.audits.Audits"                   , version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
    )


    fun process(): Try<String> {

        val keys = docs.map { d -> d.name }.toList()
        val maxLength = (keys.maxByOrNull { it.length }?.length ?: 0) + 3
        var pos = 1
        docs.forEach { doc ->
            process(doc, pos, maxLength)
            pos += 1
        }
        return Success(outputDir, msg = "generated docs to " + outputDir)
    }


    fun processItems(names:List<String>): Try<String> {

        val docs = docs.filter{ names.contains(it.name) }
        val keys = docs.map { d -> d.name }.toList()
        val maxLength = (keys.maxByOrNull { it.length }?.length ?: 0) + 3
        var pos = 1
        docs.forEach { doc ->
            process(doc, pos, maxLength)
            pos += 1
        }
        return Success(outputDir, msg = "generated docs to " + outputDir)
    }


    fun processProject(project:String): Try<String> {

        val docs = docs.filter{ it.proj == project }
        val keys = docs.map { d -> d.name }.toList()
        val maxLength = (keys.maxByOrNull { it.length }?.length ?: 0) + 3
        var pos = 1
        docs.forEach { doc ->
            process(doc, pos, maxLength)
            pos += 1
        }
        return Success(outputDir, msg = "generated docs to " + outputDir)
    }


    fun processComponent(name: String): Try<String> {
        val doc = docs.first { it.name == name }
        process(doc, 0, doc.name.length)
        return Success(outputDir, msg = "generated docs to " + outputDir)
    }


    private fun process(doc: Doc, pos: Int, maxLength: Int) {
        if (doc.available) {
            val data = mutableMapOf<String, String>()
            val number = pos.toString().padEnd(2)
            val displayName = doc.name.padEnd(maxLength)
            writer.highlight("$number. $displayName :", false)
            init(doc, data)
            val parseResult = if (doc.available) parse(doc, data) else fillComingSoon(doc, data)
            val formatResult = fill(doc, data)
            val filePath = generate(doc, data, formatResult)
            if (doc.available && doc.readme) {
                generateReadMe(doc, data, formatResult)
            }
            writer.url(filePath as String, true)
        }
    }


    private fun fillComingSoon(doc: Doc, data: MutableMap<String, String>) {
        data.put("import_required", newline + "coming soon")
        data.put("import_examples", newline + "coming soon")
        data.put("setup", "-")
        data.put("examples", "coming soon")
        data.put("output", "")
    }


    private fun fill(doc: Doc, data: Map<String, String>): Notice<String> {
        var template = template
        template = replace(template, "layout", data, "layout")
        template = replace(template, "name", data, "name")
        template = replace(template, "namelower", data, "namelower")
        template = replace(template, "desc", data, "desc")
        template = replace(template, "date", data, "date")
        template = replace(template, "version", data, "version")
        template = replace(template, "jar", data, "jar")
        template = replace(template, "namespace", data, "namespace")
        template = replace(template, "source", data, "source")
        template = replace(template, "sourceFolder", data, "sourceFolder")
        template = replace(template, "example", data, "example")
        template = replace(template, "dependencies", data, "dependencies")
        template = replace(template, "examplefile", data, "examplefile")
        template = replace(template, "lang", data, "lang")
        template = replace(template, "lang-ext", data, "lang-ext")
        template = replace(template, "artifact", data, "artifact")

        //https://github.com/kishorereddy/myapp-server/blob/master/src/apps/kotlin/slate-examples/src/main/kotlin/slate/examples/Example_Args.kotlin

        template = replace(template, DocConstants.header, data, "header")
        template = replace(template, DocConstants.import_required, data, "import_required")
        template = replace(template, DocConstants.import_optional, data, "import_optional")
        template = replace(template, DocConstants.import_examples, data, "import_examples")
        template = replace(template, DocConstants.depends, data, "depends")
        template = replace(template, DocConstants.setup, data, "setup", true)
        template = replace(template, DocConstants.examples, data, "examples")
        template = replace(template, DocConstants.notes, data, "notes")
        template = replaceWithSection("Output", template, DocConstants.output, data, "output")
        return kiit.results.Success(template)
    }


    private fun init(doc: Doc, data: MutableMap<String, String>) {
        template = File(templatePath).readText()
        data.put("output", "")
        data.put("layout", doc.layout())
        data.put("name", doc.name)
        data.put("namelower", doc.name.toLowerCase())
        data.put("desc", doc.desc)
        data.put("date", DateTime.now().toStringYYYYMMDD())
        data.put("version", doc.version)
        data.put("jar", doc.jar)
        data.put("dependencies", doc.dependsOn())
        data.put("namespace", doc.namespace)
        data.put("source", doc.source)
        data.put("artifact", doc.artifact())
        data.put("sourceFolder", doc.sourceFolder(docFiles))
        data.put("example", doc.example)
        data.put("setup", "")
        data.put("lang", docFiles.lang)
        data.put("lang-ext", docFiles.ext)
        data.put("examplefile", docFiles.buildComponentExamplePathLink(doc))
    }


    private fun parse(doc: Doc, data: MutableMap<String, String>) {
        val filePath = docFiles.buildComponentExamplePath(rootdir, doc)
        val content = File(filePath).readText()
        val parser = StringParser(content)

        parser.moveTo("<doc:import_required>", ensure = true)
                .saveUntil("//</doc:import_required>", name = "import_required", ensure = true)

                .moveTo("<doc:import_examples>", ensure = true)
                .saveUntil("//</doc:import_examples>", name = "import_examples", ensure = true)

                .moveTo("<doc:setup>", ensure = false)
                .saveUntil("//</doc:setup>", name = "setup", ensure = false)

                .moveTo("<doc:examples>", ensure = true)
                .saveUntil("//</doc:examples>", name = "examples", ensure = true)

                .moveTo("<doc:output>", ensure = false)
                .saveUntil("//</doc:output>", name = "output", ensure = false)

        val extractedData = parser.extracts()
        extractedData.forEach { entry ->
            data.put(entry.key, entry.value)
        }
    }


    private fun generate(doc: Doc, data: Any, result: Notice<String>): String {
        val fileName =  doc.name.toLowerCase() + ".md"
        val outputPath = File(rootdir, outputDir)
        val file = File(outputPath, fileName)
        val content = result.map { content ->
            file.writeText(content)
        }
        return content.toString()
    }


    private fun generateReadMe(doc: Doc, data: Any, result: Notice<String>): Unit {
        val fileName = if (doc.multi) "Readme_" + doc.name + ".md" else "Readme.md"
        val outputPath = docFiles.buildComponentFolder(rootdir, doc)
        val file = File(outputPath, fileName)
        result.map { content ->
            file.writeText(content)
        }
    }


    private fun replace(template: String, name: String, data: Map<String, String>, key: String,
                        enableNotApplicableIfEmptyData: Boolean = false): String {
        if (!data.contains(key)) {
            return template.replace("@{" + name + "}", "n/a")
        }

        val replacement = data[key]
        if (replacement.isNullOrEmpty() && enableNotApplicableIfEmptyData) {
            return template.replace("@{" + name + "}", "n/a")
        }
        val result = template.replace("@{" + name + "}", replacement ?: "")
        return result
    }


    private fun replaceWithSection(sectionName: String, template: String,
                                   name: String, data: Map<String, String>, key: String): String {
        if (!data.contains(key)) return replaceItem(template, name)

        val replacement = data[key]
        if (replacement.isNullOrEmpty()) return replaceItem(template, name)

        val section = "$newline## $sectionName$newline$replacement"
        val result = template.replace("@{$name}", section)
        return result
    }


    private fun replaceItem(template: String, name: String): String {
        return template.replace("@{$name}", "")
    }
}
