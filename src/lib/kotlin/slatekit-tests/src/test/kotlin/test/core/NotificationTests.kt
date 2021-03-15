package test.core

import kotlinx.coroutines.runBlocking
import okio.Buffer
import org.junit.Assert
import org.junit.Test
import slatekit.common.conf.Config
import slatekit.common.info.ApiLogin
import slatekit.notifications.email.EmailMessage
import slatekit.notifications.email.SendGrid
import slatekit.notifications.sms.TwilioSms
import test.TestApp

//import slatekit.providers.metrics.dropwizard.MetricService


class NotificationTests {

    @Test
    fun can_build_sendgrid() {
        //val conf = Config.of(TestApp::class.java, "usr://.slatekit/common/conf/email.conf")
        //val key = conf.apiLogin("email")
        val key = ApiLogin("support@slatekit.com", "slatekit", "pswd", "dev", "test")
        val service = SendGrid(key)
        val req = service.build(EmailMessage("jl@dc.com", "Series 123", "The totality", true))
        runBlocking {
            req.onSuccess {
                Assert.assertEquals("https://api.sendgrid.com/v3/mail/send", it.url().toString())
                Assert.assertEquals("POST", it.method())
                Assert.assertTrue(it.header("Authorization").isNotEmpty())

                val expected = """{"personalizations":[{"to":[{"name":"","email":"jl@dc.com"}]}],"subject":"Series 123","from":{"name":"slatekit","email":"support@slatekit.com"},"content":[{"type":"text\/html","value":"The totality"}]}"""
                val buffer = Buffer()
                it.body()?.writeTo(buffer)
                val content = buffer.readUtf8()
                Assert.assertEquals(expected, content)
            }
        }
    }

    //@Test
    fun can_build_twilio() {
        val conf = Config.of(TestApp::class.java, "usr://.slatekit/common/conf/sms.conf")
        val key = conf.apiLogin("sms")
        val service = TwilioSms(key)
        runBlocking {
            val req = service.send("Testing from kotlin", "us", "123456789")
            println(req)
        }
    }
}