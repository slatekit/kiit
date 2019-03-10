package slatekit

import slatekit.cloud.aws.AwsCloudFiles
import slatekit.cloud.aws.AwsCloudQueue
import slatekit.common.Context
import slatekit.core.cloud.CloudFiles
import slatekit.core.cloud.CloudQueue
import slatekit.core.email.EmailService
import slatekit.core.email.EmailServiceSendGrid
import slatekit.core.sms.SmsService
import slatekit.core.sms.SmsServiceTwilio

interface SlateKitServices {

    val ctx:Context


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
        return AwsCloudQueue(queue, apiLogin, 3)
    }
}