package test.actors

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.actors.*

class Puller_Tests : ActorTestSupport{


    @Test
    fun can_handle(){
        runBlocking {
            val actor = controller()
            actor.handle(Content(1))
            Assert.assertEquals(1, actor.current)
        }
    }


    @Test
    fun can_pull(){
        runBlocking {
            val puller = puller()
            val actor = puller.handler as TestController
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
            val puller = puller()
            val actor = puller.handler as TestController
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
            val puller = puller()
            val actor = puller.handler as TestController
            actor.send(1)
            actor.send(2)
            actor.send(3)
            puller.wipe()
            Assert.assertEquals(0, actor.current)
        }
    }
}