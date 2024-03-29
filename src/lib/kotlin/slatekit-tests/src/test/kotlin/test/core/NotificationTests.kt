package test.core

import kotlinx.coroutines.runBlocking
import okio.Buffer
import org.junit.Assert
import org.junit.Test
import kiit.common.conf.Config
import kiit.common.info.ApiLogin
import kiit.common.types.ContentFile
import kiit.common.types.ContentTypes
import kiit.common.types.Countries
import kiit.http.HttpRPC
import kiit.comms.email.EmailMessage
import kiit.comms.email.SendGrid
import kiit.comms.sms.SmsMessage
import kiit.comms.sms.TwilioSms
import test.TestApp
import java.io.File

//import kiit.providers.metrics.dropwizard.MetricService


class NotificationTests {

    @Test
    fun can_build_sendgrid() {
        //val conf = Config.of(TestApp::class.java, "usr://.kiit/common/conf/email.conf")
        //val key = conf.apiLogin("email")
        val key = ApiLogin("support@kiit.dev", "slatekit", "pswd", "dev", "test")
        val service = SendGrid(key)
        val req = service.build(EmailMessage("jl@dc.com", "Series 123", "The totality", true))
        runBlocking {
            Assert.assertTrue(req.success)
            req.onSuccess {
                Assert.assertEquals("https://api.sendgrid.com/v3/mail/send", it.url().toString())
                Assert.assertEquals("POST", it.method())
                Assert.assertTrue(it.header("Authorization").isNotEmpty())

                val expected = """{"personalizations":[{"to":[{"name":"","email":"jl@dc.com"}]}],"subject":"Series 123","from":{"name":"slatekit","email":"support@kiit.dev"},"content":[{"type":"text\/html","value":"The totality"}]}"""
                val buffer = Buffer()
                it.body()?.writeTo(buffer)
                val content = buffer.readUtf8()
                Assert.assertEquals(expected, content)
            }
        }
    }

    @Test
    fun can_build_twilio() {
        val key = ApiLogin("9876543210", "abc123", "9876543210", "dev", "test")
        val india = Countries.find("in")!!
        val service = TwilioSms(key, countries = listOf(Countries.usa, india))
        val cases = listOf(
                Pair(Countries.usa, "1234567890"),
                Pair(india, "1234567890")
        )
        cases.forEach {info ->
            val country = info.first
            val destinationPhone = info.second
            val req = service.build(SmsMessage("hello sms from unit-test", country.iso2, destinationPhone))
            runBlocking {
                Assert.assertTrue(req.success)
                req.onSuccess {
                    Assert.assertEquals("https://api.twilio.com/2010-04-01/Accounts/abc123/Messages.json", it.url().toString())
                    Assert.assertEquals("POST", it.method())
                    Assert.assertTrue(it.header("Authorization").isNotEmpty())

                    val expected = """To=%2B${country.phoneCode}${destinationPhone}&From=9876543210&Body=hello%20sms%20from%20unit-test"""
                    val buffer = Buffer()
                    it.body()?.writeTo(buffer)
                    val content = buffer.readUtf8()
                    Assert.assertEquals(expected, content)
                }
            }
        }
    }

    //@Test
    fun can_build_twilio2() {
        val conf = Config.of(TestApp::class.java, "usr://.kiit/common/conf/sms.conf")
        val key = conf.apiLogin("sms")
        val india = Countries.find("in")!!
        val service = TwilioSms(key, countries = listOf(Countries.usa, india))
        runBlocking {
            val result = service.send("testing from unit-tests", Countries.usa.iso2, "1234567890")
            println(result)
        }
    }

    //@Test
    suspend fun can_upload_file() {
        val bytes = File("/Users/kishorereddy/git/slate/slatekit/tests/img/cat1-test.jpeg").readBytes()
        val file = ContentFile("file1.jpeg", bytes, null, ContentTypes.Jpeg)
        val http = HttpRPC()
        val result = http.post(
                url   = "http://localhost:5000/api/samples/files/upload3?id=1",
                meta  = mapOf("userId" to "user123", "postId" to "post123"),
                args  = mapOf("a" to "1", "b" to "2"),
                body  = HttpRPC.Body.MultiPart(listOf("file1" to file))
        )
    }
}