package slatekit.samples

import slatekit.providers.aws.S3
import slatekit.providers.aws.SQS
import slatekit.context.AppContext
import slatekit.context.Context
import slatekit.core.files.CloudFiles
import slatekit.core.queues.CloudQueue
import slatekit.core.queues.QueueStringConverter
import slatekit.notifications.email.EmailService
import slatekit.notifications.email.SendGrid
import slatekit.notifications.sms.SmsService
import slatekit.notifications.sms.TwilioSms
import slatekit.results.Failure
import slatekit.results.Success

object Builder {

    fun emails(ctx: Context): EmailService {
        val cfg = ctx.conf
        val apiLogin = cfg.apiLogin("email")
        return SendGrid(apiLogin)
    }


    fun sms(ctx:Context): SmsService {
        val cfg = ctx.conf
        val apiLogin = cfg.apiLogin("sms")
        return TwilioSms(apiLogin)
    }


    fun files(ctx:Context): CloudFiles {
        val apiLogin = ctx.conf.apiLogin("files")
        val bucket = apiLogin.tag
        val files = S3.of("us-east-1", bucket, false, apiLogin)
        return when(files){
            is Success -> files.value
            is Failure -> throw files.error
        }
    }


    fun queues(ctx:Context): CloudQueue<String> {
        val apiLogin = ctx.conf.apiLogin("queues")
        val name = apiLogin.tag
        val queue = SQS.of(Samples::class.java,"us-east-1", name, apiLogin, QueueStringConverter(), 3)
        return when(queue){
            is Success -> queue.value
            is Failure -> throw queue.error
        }
    }
}