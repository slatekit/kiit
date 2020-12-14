package test.actors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.actors.*

class Actor_Tests {

    fun build(): TestActor {
        val channel = Channel<Message<Int>>(Channel.UNLIMITED)
        val scope = CoroutineScope(Dispatchers.IO)
        val actor = TestActor(Context("basic.1", scope), channel)
        return actor
    }


    @Test
    fun can_create() {
        runBlocking {
            val actor = build()
            Assert.assertEquals("basic.1", actor.id)
            Assert.assertEquals("basic.1", actor.ctx.id)
        }
    }


    @Test
    fun can_send() {
        runBlocking {
            val actor = build()

            actor.send(1)
            actor.send(2, "abc")
            actor.send(3, "def")

            val job = actor.work()
            delay(100)
            job.cancel()
            Assert.assertEquals(3, actor.current)
        }
    }


    @Test
    fun can_track(){
        runBlocking {
            val actor = build()
            actor.send(1)
            actor.send(2)
            val job = actor.work()
            delay(100)
            job.cancel()
            Assert.assertEquals(2, actor.tracked)
        }
    }
}