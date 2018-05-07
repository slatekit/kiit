package test.core

import org.junit.Before
import org.junit.Test
import slatekit.common.*
import slatekit.common.http.HttpRequest
import slatekit.core.push.MessageServiceGoogle


class MessagingTests {



    @Before
    fun setup() {
    }


    @Test fun can_send_notification() {
        val io = IOTest()
        val svc = MessageServiceGoogle("abc", call = io)
        //val svc.sendNotification(to = "deviceId:1234", "payload:notification")
        //assert(io.lastRequest != null)
    }


    @Test fun can_send_share() {
        val io = IOTest()
        val svc = MessageServiceGoogle("abc", call = io)
        //val svc.sendData(to = "deviceId:1234", "payload:data")
        //assert(io.lastRequest != null)
    }


    @Test fun can_send_reg() {
    }


    class IOTest : IO<HttpRequest, ResultMsg<Boolean>> {

        var lastRequest:HttpRequest ? = null


        override fun run(input: HttpRequest): ResultMsg<Boolean> {
            lastRequest = input
            return Success(true)
        }

    }
}
