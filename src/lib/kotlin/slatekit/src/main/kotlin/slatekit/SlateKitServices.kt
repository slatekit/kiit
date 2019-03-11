package slatekit

import slatekit.cloud.aws.AwsCloudFiles
import slatekit.cloud.aws.AwsCloudQueue
import slatekit.common.queues.QueueStringConverter
import slatekit.core.cloud.CloudFiles
import slatekit.core.cloud.CloudQueue
import slatekit.core.email.EmailService
import slatekit.core.email.EmailServiceSendGrid
import slatekit.core.sms.SmsService
import slatekit.core.sms.SmsServiceTwilio
import slatekit.integration.apis.ModuleApi
import slatekit.integration.common.AppEntContext
import slatekit.integration.mods.Mod
import slatekit.integration.mods.ModService
import slatekit.integration.mods.ModuleContext
import slatekit.orm.migrations.MigrationService
import slatekit.orm.migrations.MigrationSettings

interface SlateKitServices {

    val ctx: AppEntContext


    fun emails():EmailService {
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
        return AwsCloudFiles(bucket, false, apiLogin)
    }


    fun queues():CloudQueue<String> {
        val apiLogin = ctx.cfg.apiLogin("queues")
        val queue = apiLogin.tag
        return AwsCloudQueue(queue, apiLogin, QueueStringConverter(), 3)
    }


    fun migrations():MigrationService {
        // entity migration services ( to install/uninstall )
        val migrationSettings = MigrationSettings(enableLogging = true, enableOutput = true)
        val migrationService = MigrationService(ctx.ent, ctx.ent.dbs, migrationSettings, ctx.dirs)
        return migrationService
    }


    fun moduleContext():ModuleContext {
        // Services/depenencies for all modules
        val moduleService = ctx.ent.getSvc<Long, Mod>(Mod::class) as ModService
        val moduleContext = ModuleContext(moduleService, migrations())
        return moduleContext
    }
}