package test.actors

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.actors.*

class Issuer_Tests : ActorTestSupport{


    @Test
    fun can_issue(){
        runBlocking {
            val actor = controller()
            actor.issue(Content(0, 1))
            Assert.assertEquals(1, actor.current)
        }
    }


    @Test
    fun can_pull(){
        runBlocking {
            val puller = issuer()
            val actor = puller.issuable as TestController
            actor.start()
            puller.pull()
            actor.send(1)
            actor.send(2)
            actor.send(3)
            puller.pull(2)
            Assert.assertEquals(2, actor.current)
        }
    }


    @Test
    fun can_poll(){
        runBlocking {
            val puller = issuer()
            val actor = puller.issuable as TestController
            actor.start()
            puller.pull()
            actor.send(1)
            actor.send(2)
            actor.send(3)
            puller.poll()
            Assert.assertEquals(3, actor.current)
        }
    }


    @Test
    fun can_wipe(){
        runBlocking {
            val puller = issuer()
            val actor = puller.issuable as TestController
            actor.send(1)
            actor.send(2)
            actor.send(3)
            puller.wipe()
            Assert.assertEquals(0, actor.current)
        }
    }
}