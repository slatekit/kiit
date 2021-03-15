package test.core

import kotlinx.coroutines.runBlocking
import okio.Buffer
import org.junit.Assert
import org.junit.Test
import slatekit.common.conf.Config
import slatekit.notifications.email.EmailMessage
import slatekit.notifications.email.SendGrid
import test.TestApp

//import slatekit.providers.metrics.dropwizard.MetricService


class NotificationTests {

    @Test
    fun can_build_sendgrid() {
        val conf = Config.of(TestApp::class.java, "usr://.slatekit/common/conf/email.conf")
        val key = conf.apiLogin("email")
        val email = SendGrid(key)
        val req = email.build(EmailMessage("jl@dc.com", "Series 123", "The totality", true))
        runBlocking {
            req.onSuccess {
                Assert.assertEquals("https://api.sendgrid.com/v3/mail/send", it.url().toString())
                Assert.assertEquals("POST", it.method())
                Assert.assertTrue(it.header("Authorization").isNotEmpty())

                val expected = """{"personalizations":[{"to":[{"name":"","email":"jl@dc.com"}]}],"subject":"Series 123","from":{"name":"slatekit","email":"support@slatekit.com"},"content":[{"type":"text\/html","value":"The totality"}]}"""
                val buffer = Buffer()
                it.body()?.writeTo(buffer)
                val content = buffer.readUtf8()
                //println(content)
                Assert.assertEquals(expected, content)
            }
        }
    }
}