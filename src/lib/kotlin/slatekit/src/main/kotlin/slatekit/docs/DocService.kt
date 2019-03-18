package slatekit.docs

import slatekit.common.*
import slatekit.common.console.SemanticConsole
import slatekit.common.ext.toStringYYYYMMDD
import slatekit.common.utils.StringParser
import slatekit.results.Notice
import slatekit.results.Try
import slatekit.results.Success
import java.io.File

class DocService(val _rootdir: String, val _outputDir: String, val templatePath: String) {

    private val _templatePath = File(_rootdir, templatePath).toString()
    private var _template = ""
    private val _writer = SemanticConsole()
    private val _docFiles = DocFiles()
    private val _version = "0.9.9"

    private val _docs = listOf(
              Doc("Args"         ,  "slatekit-common"  , "slatekit.common.args"               ,"slatekit.common.args.Args"                    , _version, "Example_Args"             , true  , false, true , "utils", "slatekit.common.jar"  , ""                    , "A lexical command line argument parser with optional support for allowing a route/method call in the beginning")
            , Doc("Auth"         ,  "slatekit-common"  , "slatekit.common.auth"               ,"slatekit.common.auth.Auth"                    , _version, "Example_Auth"             , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "A simple authentication component to check current user role and permissions")
            , Doc("Config"       ,  "slatekit-common"  , "slatekit.common.conf"               ,"slatekit.common.conf.Config"                  , _version, "Example_Config"           , true  , false, true , "utils", "slatekit.common.jar"  , ""                    , "Thin wrapper over typesafe config with decryption support, uri loading, and mapping of database connections and api keys")
            , Doc("Console"      ,  "slatekit-common"  , "slatekit.common.console"            ,"slatekit.common.console.Console"              , _version, "Example_Console"          , true  , false, true , "utils", "slatekit.common.jar"  , ""                    , "Enhanced printing to console with support for semantic writing like title, subtitle, url, error, etc with colors")
            , Doc("Data"         ,  "slatekit-common"  , "slatekit.common.db"                 ,"slatekit.common.db.Db"                        , _version, "Example_Database"         , true  , false, true , "utils", "slatekit.common.jar"  , ""                    , "Database access utilty to query and manage data using JDBC for MySql. Other database support coming later.")
            , Doc("DbLookup"     ,  "slatekit-common"  , "slatekit.common.db"                 ,"slatekit.common.db.DbLookup"                  , _version, "Example_DbLookup"         , true  , false, true , "utils", "slatekit.common.jar"  , ""                    , "Database access utilty to query and manage data using JDBC for MySql. Other database support coming later.")
            , Doc("DateTime"     ,  "slatekit-common"  , "slatekit.common"                    ,"slatekit.common.DateTime"                     , _version, "Example_DateTime"         , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "DataTime wrapper around Java 8 LocalDateTime providing a simplified interface, some convenience, extra features.")
            , Doc("Encrypt"      ,  "slatekit-common"  , "slatekit.common.encrypt"            ,"slatekit.common.encrypt.Encryptor"            , _version, "Example_Encryptor"        , true  , false, true , "utils", "slatekit.common.jar"  , ""                    , "Encryption using AES")
            , Doc("Env"          ,  "slatekit-common"  , "slatekit.common.envs"               ,"slatekit.common.envs.Env"                     , _version, "Example_Env"              , true  , false, true , "utils", "slatekit.common.jar"  , ""                    , "Environment selector and validator for environments such as (local, dev, qa, stg, prod) )")
            , Doc("Folders"      ,  "slatekit-common"  , "slatekit.common.info"               ,"slatekit.common.info.Folders"                 , _version, "Example_Folders"          , true  , false, true , "utils", "slatekit.common.jar"  , ""                    , "Standardized application folder setup; includes conf, cache, inputs, logs, outputs")
            , Doc("Info"         ,  "slatekit-common"  , "slatekit.common.info"               ,"slatekit.common.info.About"                   , _version, "Example_Info"             , true  , false, true , "utils", "slatekit.common.jar"  , ""                    , "Get/Set useful diagnostics about the system, language runtime, application and more")
            , Doc("Lex"          ,  "slatekit-common"  , "slatekit.common.lex"                ,"slatekit.common.lex.Lexer"                    , _version, "Example_Lexer"            , true  , false, true , "utils", "slatekit.common.jar"  , ""                    , "Lexer for parsing text into tokens")
            , Doc("Logger"       ,  "slatekit-common"  , "slatekit.common.log"                ,"slatekit.common.log.Logger"                   , _version, "Example_Logger"           , true  , false, true , "utils", "slatekit.common.jar"  , ""                    , "A simple logger with extensibility for using other 3rd party loggers")
            , Doc("Mapper"       ,  "slatekit-common"  , "slatekit.common.mapper"             ,"slatekit.common.mapper.Mapper"                , _version, "Example_Mapper"           , false , false, true , "utils", "slatekit.common.jar"  , ""                    , "A simple auth component for desktop/local and web/concurrent apps" )
            , Doc("Queue"        ,  "slatekit-common"  , "slatekit.common.queues"             ,"slatekit.common.queues.QueueSource"           , _version, "Example_Queue"            , false , false, true , "infra", "slatekit.common.jar"  , ""                    , "Queue implementation and interfaces used for Queue abstractions over Amazon SQS" )
            , Doc("Random"       ,  "slatekit-common"  , "slatekit.common"                    ,"slatekit.common.Random"                       , _version, "Example_Random"           , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "A random generator for strings, guids, numbers, alpha-numeric, and alpha-numeric-symbols for various lengths" )
            , Doc("Request"      ,  "slatekit-common"  , "slatekit.common.requests"           ,"slatekit.common.requests.Request"             , _version, "Example_Request"          , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "Models and abstracts a request that can represent either an HTTP request or a request from the CLI ( Command line )." )
            , Doc("Results"      ,  "slatekit-common"  , "slatekit.results"                   ,"slatekit.common.Result"                       , _version, "Example_Results"          , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "Models success / failures with status codes, message, and other fields. Compatible with http status codes." )
            , Doc("SmartValues"  ,  "slatekit-common"  , "slatekit.common.smartvalues"        ,"slatekit.common.smartvalues.Email"            , _version, "Example_SmartValues"      , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "A way to store, validate and describe strongly typed and formatted strings")
            , Doc("Todo"         ,  "slatekit-common"  , "slatekit.common"                    ,"slatekit.common.Todo"                         , _version, "Example_Todo"             , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "A programmatic approach to marking and tagging code that is strongly typed and consistent")
            , Doc("Serialization",  "slatekit-common"  , "slatekit.common.serialization"      ,"slatekit.common.serialization.Serializer"     , _version, "Example_Serialization"    , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "Serializers for data classes to generate CSV, Props, HOCON, JSON files")
            , Doc("Templates"    ,  "slatekit-common"  , "slatekit.common.templates"          ,"slatekit.common.templates.Templates"          , _version, "Example_Templates"        , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "A micro template system for processing text with variables, useful for generating dynamic emails/messages.")
            , Doc("Validations"  ,  "slatekit-common"  , "slatekit.common.validation"         ,"slatekit.common.validation.ValidationFuncs"   , _version, "Example_Validation"       , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "A set of validation related components, simple validation checks, RegEx checks, error collection and custom validators")
            , Doc("Utils"        ,  "slatekit-common"  , "slatekit.common.utils"              ,"slatekit.common.console.ConsoleWriter"        , _version, "Example_Utils"            , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "Various utilities available in the Slate library")

            , Doc("Query"        ,  "slatekit-common"  , "slatekit.query.Query"                         ,"slatekit.query.Query"                         , _version, "Example_Query"            , false , false, true , "utils", "slatekit.common.jar"  , ""                    , "Query pattern used for specifying search and selection criteria" )
            , Doc("Model"        ,  "slatekit-meta"    , "slatekit.common.Model"                        ,"slatekit.common.Model"                        , _version, "Example_Model"            , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "Allows construction of model schema with fields for code-generation. Also used in the ORM mapper" )
            , Doc("Reflect"      ,  "slatekit-meta"    , "slatekit.common.Reflector"                    ,"slatekit.common.Reflector"                    , _version, "Example_Reflect"          , true  , false, false, "utils", "slatekit.common.jar"  , ""                    , "Reflection helper to create instances, get methods, fields, annotations and more" )
            , Doc("Orm-Model"    ,  "slatekit-entities", "slatekit.common.Model"                        ,"slatekit.common.Model"                        , _version, "Example_Model"            , true  , false, false, "orm"  , "slatekit.common.jar"  , "com"                 , "A model schema builder")
            , Doc("Orm-Entity"   ,  "slatekit-entities", "slatekit.common.entities.Entity"              ,"slatekit.common.entities.Entity"              , _version, "Example_Entities"         , true  , false, false, "orm"  , "slatekit.entities.jar", "com"                 , "A base class for persistent domain entities")
            , Doc("Orm-Mapper"   ,  "slatekit-entities", "slatekit.common.entities.EntityMapper"        ,"slatekit.common.entities.EntityMapper"        , _version, "Example_Mapper"           , true  , false, false, "orm"  , "slatekit.entities.jar", "com"                 , "A mapper that converts a entity to a sql create/updates")
            , Doc("Orm-Repo"     ,  "slatekit-entities", "slatekit.common.entities.EntityRepo"          ,"slatekit.common.entities.EntityRepo"          , _version, "Example_Entities_Repo"    , true  , false, false, "orm"  , "slatekit.entities.jar", "com"                 , "A repository pattern for entity/model CRUD operations")
            , Doc("Orm-Service"  ,  "slatekit-entities", "slatekit.common.entities.EntityService"       ,"slatekit.common.entities.EntityService"       , _version, "Example_Entities_Service" , true  , false, false, "orm"  , "slatekit.entities.jar", "com"                 , "A service pattern for entity/model CRUD + business operations")
            , Doc("Orm-Setup"    ,  "slatekit-entities", "slatekit.common.entities.Entities"            ,"slatekit.common.entities.Entities"            , _version, "Example_Entities_Reg"     , true  , false, false, "orm"  , "slate.entities.jar"   , "com"                 , "A registration system for entities and their corresponding repository/service impelementations")
            , Doc("Notifications",  "slatekit-core"    , "slatekit.core."                               ,"slatekit.core."                               , _version, "Example_Notifications"    , false , false, false, "infra", "slatekit.common.jar"  , ""                    , "Push notifications for Mobile" )
            , Doc("Api"          ,  "slatekit-apis"    , "slatekit.apis.ApiContainer"                   ,"slatekit.apis.ApiContainer"                   , _version, "Example_Api"              , false , false, true , "infra", "slatekit.apis.jar"    , "com"                 , "An API Container to host protocol agnostic apis to run on the command line or web")
            , Doc("App"          ,  "slatekit-core"    , "slatekit.core.app.App"                        ,"slatekit.core.app.App"                        , _version, "Example_App"              , true  , false, true , "infra", "slatekit.core.jar"    , "com"                 , "A base application with support for command line args, environment selection, configs, encryption, logging, diagnostics and more")
            , Doc("Cache"        ,  "slatekit-core"    , "slatekit.core.cache.Cache"                    ,"slatekit.core.cache.Cache"                    , _version, "Example_Cache"            , true  , false, false, "infra", "slatekit.core.jar"    , "com"                 , "Light-weight cache to loadAnnotated, store, and refresh data, with support for metrics and time-stamps. Default in-memory implementation available")
            , Doc("Ctx"          ,  "slatekit-core"    , "slatekit.core.common.AppContext"              ,"slatekit.core.common.AppContext"              , _version, "Example_Context"          , true  , false, false, "infra", "slatekit.core.jar"    , "com"                 , "An application context to contain common dependencies such as configs, logger, encryptor, etc, to be accessible to other components")
            , Doc("Cmd"          ,  "slatekit-core"    , "slatekit.core.cmds.Cmd"                       ,"slatekit.core.cmds.Cmd"                       , _version, "Example_Command"          , true  , false, false, "infra", "slatekit.core.jar"    , "com"                 , "A variation to the command pattern to support ad-hoc execution of code, with support for metrics and time-stamps")
            , Doc("Email"        ,  "slatekit-core"    , "slatekit.core.sms.EmailService"               ,"slatekit.core.sms.EmailService"               , _version, "Example_Email"            , true  , false, false, "infra", "slatekit.core.jar"    , "com"                 , "An Email service to send emails with support for templates using SendGrid as the default implementation")
            , Doc("Shell"        ,  "slatekit-core"    , "slatekit.core.cli.CliService"                 ,"slatekit.core.cli.CliService"                 , _version, "Example_CLI"              , true  , false, false, "infra", "slatekit.core.jar"    , "com"                 , "A CLI ( Command Line Interface ) you can extend / hook into to run handle user. Can also be used to execute your APIs")
            , Doc("Sms"          ,  "slatekit-core"    , "slatekit.core.sms.SmsService"                 ,"slatekit.core.sms.SmsService"                 , _version, "Example_Sms"              , true  , false, false, "infra", "slatekit.core.jar"    , "com"                 , "An Sms ( Text message ) service to send text messages to mobile phones for confirmation codes and invites.")
            , Doc("Tasks"        ,  "slatekit-core"    , "slatekit.core.tasks.Task"                     ,"slatekit.core.tasks.Task"                     , _version, "Example_Task"             , false , false, false, "infra", "slatekit.core.jar"    , "com"                 , "A robust Task/Job implementation that can be hooked up with a Queue")
            , Doc("Workers"      ,  "slatekit-core"    , "slatekit.core.workers.Worker"                 ,"slatekit.core.workers.Worker"                 , _version, "Example_Workers"          , true  , false, false, "infra", "slatekit.core.jar"    , "com"                 , "Background workers that can be paused, resumed, stopped, with support for worker groups, metrics, and queue and more")
            , Doc("AWS-S3"       ,  "slatekit-cloud"   , "slatekit.cloud.aws.AwsCloudFiles"             ,"slatekit.cloud.aws.AwsCloudFiles"             , _version, "Example_Aws_S3"           , true  , false, false, "infra", "slatekit.cloud.jar"   , "com,core"            , "Abstraction layer on cloud file storage to Amazon S3"             )
            , Doc("AWS-SQS"      ,  "slatekit-cloud"   , "slatekit.cloud.aws.AwsCloudQueue"             ,"slatekit.cloud.aws.AwsCloudQueue"             , _version, "Example_Aws_Sqs"          , true  , true , false, "infra", "slatekit.cloud.jar"   , "com,core"            , "Abstraction layer on message queues using Amazon SQS"              )
            , Doc("ext-Users"    ,  "slatekit-ext"     , "slatekit.ext.users.User"                      ,"slatekit.ext.users.User"                      , _version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Devices"  ,  "slatekit-ext"     , "slatekit.ext.devices.Device"                  ,"slatekit.ext.devices.Device"                  , _version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Reg"      ,  "slatekit-ext"     , "slatekit.ext.reg.RegService"                  ,"slatekit.ext.reg.RegService"                  , _version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Settings" ,  "slatekit-ext"     , "slatekit.ext.settings.Settings"               ,"slatekit.ext.settings.Settings"               , _version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Tasks"    ,  "slatekit-ext"     , "slatekit.ext.tasks.Tasks"                     ,"slatekit.ext.tasks.Tasks"                     , _version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Invites"  ,  "slatekit-ext"     , "slatekit.ext.invites.Invite"                  ,"slatekit.ext.invites.Invite"                  , _version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Logs"     ,  "slatekit-ext"     , "slatekit.ext.logs.Log"                        ,"slatekit.ext.logs.Log"                        , _version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Status"   ,  "slatekit-ext"     , "slatekit.ext.status.Status"                   ,"slatekit.ext.status.Status"                   , _version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
            , Doc("ext-Audits"   ,  "slatekit-ext"     , "slatekit.ext.audits.Audits"                   ,"slatekit.ext.audits.Audits"                   , _version, "Example_Ext_Users"        , false , true , false, "feat" , "slatekit.ext.jar"     , "com,ent,core,cloud"  , "Feature to create and manage users"              )
    )


    fun process(): Try<String> {

        val keys = _docs.map { d -> d.name }.toList()
        val maxLength = (keys.maxBy { it.length }?.length ?: 0) + 3
        var pos = 1
        _docs.forEach { doc ->
            process(doc, pos, maxLength)
            pos += 1
        }
        return Success(_outputDir, msg = "generated docs to " + _outputDir)
    }


    fun processProject(project:String): Try<String> {

        val docs = _docs.filter{ it.proj == project }
        val keys = docs.map { d -> d.name }.toList()
        val maxLength = (keys.maxBy { it.length }?.length ?: 0) + 3
        var pos = 1
        docs.forEach { doc ->
            process(doc, pos, maxLength)
            pos += 1
        }
        return Success(_outputDir, msg = "generated docs to " + _outputDir)
    }


    fun processComponent(name: String): Try<String> {
        val doc = _docs.first { it.name == name }
        process(doc, 0, doc.name.length)
        return Success(_outputDir, msg = "generated docs to " + _outputDir)
    }


    private fun process(doc: Doc, pos: Int, maxLength: Int) {
        if (doc.available) {
            val data = mutableMapOf<String, String>()
            val number = pos.toString().padEnd(2)
            val displayName = doc.name.padEnd(maxLength)
            _writer.highlight("$number. $displayName :", false)
            init(doc, data)
            val parseResult = if (doc.available) parse(doc, data) else fillComingSoon(doc, data)
            val formatResult = fill(doc, data)
            val filePath = generate(doc, data, formatResult)
            if (doc.available && doc.readme) {
                generateReadMe(doc, data, formatResult)
            }
            _writer.url(filePath as String, true)
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
        var template = _template
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

        //https://github.com/kishorereddy/blend-server/blob/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Args.scala

        template = replace(template, DocConstants.header, data, "header")
        template = replace(template, DocConstants.import_required, data, "import_required")
        template = replace(template, DocConstants.import_optional, data, "import_optional")
        template = replace(template, DocConstants.import_examples, data, "import_examples")
        template = replace(template, DocConstants.depends, data, "depends")
        template = replace(template, DocConstants.setup, data, "setup", true)
        template = replace(template, DocConstants.examples, data, "examples")
        template = replace(template, DocConstants.notes, data, "notes")
        template = replaceWithSection("Output", template, DocConstants.output, data, "output")
        return slatekit.results.Success(template)
    }


    private fun init(doc: Doc, data: MutableMap<String, String>) {
        _template = File(_templatePath).readText()
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
        data.put("sourceFolder", doc.sourceFolder(_docFiles))
        data.put("example", doc.example)
        data.put("setup", "")
        data.put("lang", _docFiles.lang)
        data.put("lang-ext", _docFiles.ext)
        data.put("examplefile", _docFiles.buildComponentExamplePathLink(doc))
    }


    private fun parse(doc: Doc, data: MutableMap<String, String>) {
        val filePath = _docFiles.buildComponentExamplePath(_rootdir, doc)
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
        val outputPath = File(_outputDir)
        val file = File(outputPath, fileName)
        val content = result.map { content ->
            file.writeText(content)
        }
        return content.toString()
    }


    private fun generateReadMe(doc: Doc, data: Any, result: Notice<String>): Unit {
        val fileName = if (doc.multi) "Readme_" + doc.name + ".md" else "Readme.md"
        val outputPath = _docFiles.buildComponentFolder(_rootdir, doc)
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
