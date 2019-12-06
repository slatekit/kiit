package slatekit

import slatekit.apis.Setup
import slatekit.apis.core.Api
import slatekit.cloud.aws.AwsCloudFiles
import slatekit.cloud.aws.AwsCloudQueue
import slatekit.common.Context
import slatekit.core.queues.QueueStringConverter
import slatekit.core.files.CloudFiles
import slatekit.core.queues.CloudQueue
import slatekit.notifications.email.EmailService
import slatekit.notifications.email.EmailServiceSendGrid
import slatekit.notifications.sms.SmsService
import slatekit.notifications.sms.SmsServiceTwilio
import slatekit.docs.DocApi
import slatekit.info.DependencyApi
import slatekit.integration.apis.*
import slatekit.integration.common.AppEntContext
import slatekit.integration.mods.Mod
import slatekit.integration.mods.ModService
import slatekit.integration.mods.ModuleContext
import slatekit.orm.migrations.MigrationService
import slatekit.orm.migrations.MigrationSettings
import slatekit.generator.GeneratorApi
import slatekit.generator.GeneratorService
import slatekit.generator.GeneratorSettings
import slatekit.generator.ToolSettings

interface SlateKitServices {

    val ctx: Context


    fun emails(): EmailService {
        val cfg = ctx.conf
        val apiLogin = cfg.apiLogin("email")
        return EmailServiceSendGrid(apiLogin)
    }


    fun sms(): SmsService {
        val cfg = ctx.conf
        val apiLogin = cfg.apiLogin("sms")
        return SmsServiceTwilio(apiLogin)
    }


    fun files(): CloudFiles {
        val apiLogin = ctx.conf.apiLogin("files")
        val bucket = apiLogin.tag
        return AwsCloudFiles("us-east-1", bucket, false, apiLogin)
    }


    fun queues(): CloudQueue<String> {
        val apiLogin = ctx.conf.apiLogin("queues")
        val queue = apiLogin.tag
        return AwsCloudQueue("us-east-1", queue, apiLogin, QueueStringConverter(), 3)
    }

    fun apis(): List<Api> {
//        // Module api
//        val moduleApi = ModuleApi(moduleContext(), ctx)
//        moduleApi.register(DependencyModule(ctx, moduleContext()))

        // APIs
        val genSettings = ToolSettings(this.ctx.conf.getString("slatekit.version"))
        val requiredApis = listOf(
                Api(GeneratorApi(ctx, GeneratorService(ctx, SlateKit::class.java, GeneratorSettings(genSettings))), declaredOnly = true, setup = Setup.Annotated),
                Api(DocApi(ctx), declaredOnly = true, setup = Setup.Annotated),
                Api(InfoApi(ctx), declaredOnly = true, setup = Setup.Annotated),
                Api(VersionApi(ctx), declaredOnly = true, setup = Setup.Annotated)
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
                load("email") { Api(EmailApi(emails(), ctx), declaredOnly = true, setup = Setup.Annotated) },
                load("files") { Api(FilesApi(files(), ctx), declaredOnly = true, setup = Setup.Annotated) },
                load("queues") { Api(QueueApi(queues(), ctx), declaredOnly = true, setup = Setup.Annotated) },
                load("sms") { Api(SmsApi(sms(), ctx), declaredOnly = true, setup = Setup.Annotated) }
                //load("db") { Api(DependencyApi(ctx), declaredOnly = false, setup = Setup.Annotated) }
        )
        return apis.filterNotNull()
    }
}