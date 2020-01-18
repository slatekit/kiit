package slatekit

import slatekit.apis.SetupType
import slatekit.apis.core.Api
import slatekit.apis.tools.code.CodeGenApi
import slatekit.cloud.aws.S3
import slatekit.cloud.aws.SQS
import slatekit.context.Context
import slatekit.core.queues.QueueStringConverter
import slatekit.core.files.CloudFiles
import slatekit.core.queues.CloudQueue
import slatekit.notifications.email.EmailService
import slatekit.notifications.email.SendGrid
import slatekit.notifications.sms.SmsService
import slatekit.notifications.sms.TwilioSms
import slatekit.docs.DocApi
import slatekit.generator.*
import slatekit.integration.apis.*
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.samples.common.apis.SampleApi

interface SlateKitServices {

    val ctx: Context


    fun emails(): EmailService {
        val cfg = ctx.conf
        val apiLogin = cfg.apiLogin("email")
        return SendGrid(apiLogin)
    }


    fun sms(): SmsService {
        val cfg = ctx.conf
        val apiLogin = cfg.apiLogin("sms")
        return TwilioSms(apiLogin)
    }


    fun files(): CloudFiles {
        val apiLogin = ctx.conf.apiLogin("files")
        val bucket = apiLogin.tag
        val files = S3.of("us-east-1", bucket, false, apiLogin)
        return when(files){
            is Success -> files.value
            is Failure -> throw files.error
        }
    }


    fun queues(): CloudQueue<String> {
        val apiLogin = ctx.conf.apiLogin("queues")
        val name = apiLogin.tag
        val queue = SQS.of("us-east-1", name, apiLogin, QueueStringConverter(), 3)
        return when(queue){
            is Success -> queue.value
            is Failure -> throw queue.error
        }
    }

    fun apis(): List<Api> {
//        // Module api
//        val moduleApi = ModuleApi(moduleContext(), ctx)
//        moduleApi.register(DependencyModule(ctx, moduleContext()))

        // APIs
        val toolSettings = ToolSettings(
                this.ctx.conf.getString("slatekit.version"),
                this.ctx.conf.getString("slatekit.version.beta"))
        val buildSettings = BuildSettings(this.ctx.conf.getString("kotlin.version"))
        val requiredApis = listOf(
                Api(GeneratorApi(ctx, GeneratorService(ctx, SlateKit::class.java, GeneratorSettings(toolSettings, buildSettings))), declaredOnly = true, setup = SetupType.Annotated),
                Api(DocApi(ctx), declaredOnly = true, setup = SetupType.Annotated),
                Api(CodeGenApi(), declaredOnly = true, setup = SetupType.Annotated),
                Api(SampleApi(ctx), declaredOnly = true, setup = SetupType.Annotated)
//                Api(InfoApi(ctx), declaredOnly = true, setup = Setup.Annotated),
//                Api(VersionApi(ctx), declaredOnly = true, setup = Setup.Annotated)
                //Api(moduleApi, declaredOnly = true, setup = Setup.Annotated)
        )
        val optionalApis = listOf<Api>() //optionalApis()
        val allApis = requiredApis.plus(optionalApis)
        return allApis
    }


    fun optionalApis(): List<Api> {
        // @param key : "email"
        fun load(key: String, call: () -> Api): Api? {
            val enabled = ctx.conf.getBoolOrElse(key, false)
            return if (enabled) call() else null
        }

        val apis = listOf(
                load("email") { Api(EmailApi(emails(), ctx), declaredOnly = true, setup = SetupType.Annotated) },
                load("files") { Api(FilesApi(files(), ctx), declaredOnly = true, setup = SetupType.Annotated) },
                load("queues") { Api(QueueApi(queues(), ctx), declaredOnly = true, setup = SetupType.Annotated) },
                load("sms") { Api(SmsApi(sms(), ctx), declaredOnly = true, setup = SetupType.Annotated) },
                Api(CodeGenApi(), declaredOnly = true, setup = SetupType.Annotated)
                //load("db") { Api(DependencyApi(ctx), declaredOnly = false, setup = Setup.Annotated) }
        )
        return apis.filterNotNull()
    }
}