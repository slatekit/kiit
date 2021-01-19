package test.core

import org.junit.Before
import org.junit.Test


class MessagingTests {



    @Before
    fun setup() {
    }

//    @Test fun can_send_notification() {
//        val io = IOTest()
//        val cfg = Config()
//        val logs = LogsDefault
//        val svc = MessageServiceGoogle("abc", cfg, logs, call = io)
//        val result = svc.sendAlert("deviceId:1234", Notification("title1", "desc1", "icon1", "LAUNCH"))
//        Assert.assertEquals(io.lastRequest?.url,  MessageServiceGoogle.fcmUrl)
//        Assert.assertEquals(io.lastRequest?.method,  HttpMethod.POST)
//        Assert.assertEquals(io.lastRequest?.headers?.get(0),  Pair("Content-Type", "application/json"))
//        Assert.assertEquals(io.lastRequest?.headers?.get(1), Pair("Authorization", "key=abc"))
//        Assert.assertEquals(io.lastRequest?.entity, """{"to":"deviceId:1234", "notification":{
//            "click_action" : "LAUNCH",
//            "title" : "title1",
//            "text": "desc1",
//            "icon": "icon1"
//        }}""")
//    }
//
//
//    @Test fun can_send_share() {
//        val io = IOTest()
//        val cfg = Config()
//        val logs = LogsDefault
//        val svc = MessageServiceGoogle("abc", cfg, logs, call = io)
//        val result = svc.sendData("deviceId:1234", "payload:data")
//        Assert.assertEquals(io.lastRequest?.url,  MessageServiceGoogle.fcmUrl)
//        Assert.assertEquals(io.lastRequest?.method,  HttpMethod.POST)
//        Assert.assertEquals(io.lastRequest?.headers?.get(0),  Pair("Content-Type", "application/json"))
//        Assert.assertEquals(io.lastRequest?.headers?.get(1),  Pair("Authorization", "key=abc"))
//        Assert.assertEquals(io.lastRequest?.entity, """{"to":"deviceId:1234", "data":payload:data}""")
//    }


    @Test fun can_send_reg() {
    }


//    class IOTest : IO<HttpRequest, Notice<Boolean>> {
//
//        var lastRequest:HttpRequest ? = null
//
//
//        override fun run(input: HttpRequest): Notice<Boolean> {
//            lastRequest = input
//            return Success(true)
//        }
//
//    }
}
