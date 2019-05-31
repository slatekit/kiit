package slatekit

import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.cloud.aws.AwsCloudFiles
import slatekit.cloud.aws.AwsCloudQueue
import slatekit.common.queues.QueueStringConverter
import slatekit.core.cloud.CloudFiles
import slatekit.core.cloud.CloudQueue
import slatekit.core.email.EmailService
import slatekit.core.email.EmailServiceSendGrid
import slatekit.core.sms.SmsService
import slatekit.core.sms.SmsServiceTwilio
import slatekit.docs.DocApi
import slatekit.info.DependencyApi
import slatekit.info.DependencyModule
import slatekit.integration.apis.*
import slatekit.integration.common.AppEntContext
import slatekit.integration.mods.Mod
import slatekit.integration.mods.ModService
import slatekit.integration.mods.ModuleContext
import slatekit.orm.migrations.MigrationService
import slatekit.orm.migrations.MigrationSettings
import slatekit.generator.GeneratorApi
import slatekit.generator.GeneratorService

interface SlateKitServices {

    val ctx: AppEntContext


    fun emails(): EmailService {
        val cfg = ctx.cfg
        val apiLogin = cfg.apiLogin("email")
        return EmailServiceSendGrid(apiLogin)
    }


    fun sms(): SmsService {
        val cfg = ctx.cfg
        val apiLogin = cfg.apiLogin("sms")
        return SmsServiceTwilio(apiLogin)
    }


    fun files(): CloudFiles {
        val apiLogin = ctx.cfg.apiLogin("files")
        val bucket = apiLogin.tag
        return AwsCloudFiles("us-east-1", bucket, false, apiLogin)
    }


    fun queues(): CloudQueue<String> {
        val apiLogin = ctx.cfg.apiLogin("queues")
        val queue = apiLogin.tag
        return AwsCloudQueue("us-east-1", queue, apiLogin, QueueStringConverter(), 3)
    }


    fun migrations(): MigrationService {
        // entity migration services ( to install/uninstall )
        val migrationSettings = MigrationSettings(enableLogging = true, enableOutput = true)
        val migrationService = MigrationService(ctx.ent, ctx.ent.dbs, migrationSettings, ctx.dirs)
        return migrationService
    }


    fun moduleContext(): ModuleContext {
        // Services/depenencies for all modules
        val moduleService = ctx.ent.getSvc<Long, Mod>(Mod::class) as ModService
        val moduleContext = ModuleContext(moduleService, migrations())
        return moduleContext
    }

    fun apis(): List<Api> {
        // Module api
        val moduleApi = ModuleApi(moduleContext(), ctx)
        moduleApi.register(DependencyModule(ctx, moduleContext()))

        // APIs
        val requiredApis = listOf(
                Api(GeneratorApi(ctx, GeneratorService(ctx, SlateKit::class.java)), declaredOnly = true, setup = Annotated),
                Api(DocApi(ctx), declaredOnly = true, setup = Annotated),
                Api(InfoApi(ctx), declaredOnly = true, setup = Annotated),
                Api(VersionApi(ctx), declaredOnly = true, setup = Annotated),
                Api(moduleApi, declaredOnly = true, setup = Annotated)
        )
        val optionalApis = optionalApis()
        val allApis = requiredApis.plus(optionalApis)
        return allApis
    }


    fun optionalApis(): List<Api> {
        // @param key : "email"
        fun load(key: String, call: () -> Api): Api? {
            val enabled = ctx.cfg.getBoolOrElse(key, false)
            return if (enabled) call() else null
        }

        val apis = listOf(
                load("email") { Api(EmailApi(emails(), ctx), declaredOnly = true, setup = Annotated) },
                load("files") { Api(FilesApi(files(), ctx), declaredOnly = true, setup = Annotated) },
                load("queues") { Api(QueueApi(queues(), ctx), declaredOnly = true, setup = Annotated) },
                load("sms") { Api(SmsApi(sms(), ctx), declaredOnly = true, setup = Annotated) },
                load("db") { Api(DependencyApi(ctx), declaredOnly = false, setup = Annotated) }
        )
        return apis.filterNotNull()
    }
}