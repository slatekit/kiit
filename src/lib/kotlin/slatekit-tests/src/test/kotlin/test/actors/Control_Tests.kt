package test.actors
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.Assert
import org.junit.Test
import kiit.actors.*
import kiit.actors.Status

class Control_Tests : ActorTestSupport {

    fun setup(op:suspend(TestController, Issuer<Int>) -> Unit) {
        runBlocking {
            val issuer = issuer("control.1")
            val actor = issuer.issuable as TestController
            op(actor, issuer)
        }
    }


    fun adder(callback:(Message<Int>) -> Unit, op:suspend(TestAdder, Issuer<Int>) -> Unit) {
        runBlocking {
            val channel = Channel<Message<Int>>(Channel.UNLIMITED)
            val scope = CoroutineScope(Dispatchers.IO)
            val context = Context("control.1", scope)
            val actor = TestAdder(context, channel)
            val issuer = Issuer<Int>(channel, actor, callback)
            op(actor, issuer)
        }
    }


    fun loader(callback:(Message<Int>) -> Unit, op:suspend(TestLoader, Issuer<Int>) -> Unit) {
        runBlocking {
            val channel = Channel<Message<Int>>(Channel.UNLIMITED)
            val scope = CoroutineScope(Dispatchers.IO)
            val context = Context("control.1", scope)
            val actor = TestLoader(context, channel)
            val issuer = Issuer<Int>(channel, actor, callback)
            op(actor, issuer)
        }
    }


    @Test
    fun can_start() {
        setup { actor, issuer ->
            actor.start()
            issuer.pull(1)
            Assert.assertEquals(Status.Started, actor.status())
        }
    }


    @Test
    fun can_pause() {
        setup { actor, issuer ->
            actor.pause()
            issuer.pull(1)
            Assert.assertEquals(Status.Paused, actor.status())
        }
    }


    @Test
    fun can_stop() {
        setup { actor, issuer ->
            actor.stop()
            issuer.pull(1)
            Assert.assertEquals(Status.Stopped, actor.status())
        }
    }


    @Test
    fun can_resume_after_pause() {
        setup { actor, issuer ->
            actor.pause()
            issuer.pull(1)
            Assert.assertEquals(Status.Paused, actor.status())
            actor.resume()
            issuer.pull(1)
            Assert.assertEquals(Status.Running, actor.status())
        }
    }


    @Test
    fun can_resume_after_stop() {
        setup { actor, issuer ->
            actor.stop()
            issuer.pull(1)
            Assert.assertEquals(Status.Stopped, actor.status())
            actor.resume()
            issuer.pull(1)
            Assert.assertEquals(Status.Running, actor.status())
        }
    }


    @Test
    fun can_kill() {
        setup { actor, issuer ->
            actor.kill()
            issuer.pull(1)
            Assert.assertEquals(Status.Killed, actor.status())
        }
    }


    @Test
    fun can_process() {
        setup { actor, issuer ->
            actor.start()
            issuer.pull(1)
            actor.send(1)
            actor.send(2)
            issuer.pull(2)
            Assert.assertEquals(Status.Running, actor.status())
            Assert.assertEquals(2, actor.current)
        }
    }


    @Test
    fun can_load() {
        loader({ }) { actor, issuer ->
            actor.start()
            issuer.pull(1)
            actor.load()
            actor.load()
            issuer.poll()
            Assert.assertEquals(Status.Running, actor.status())
            Assert.assertEquals(2, actor.current)
        }
    }


    @Test
    fun can_work() {

        adder(this::printMsg) { actor, issuer ->
            actor.start()
            val scope = CoroutineScope(Dispatchers.IO)
            issuer.poll()
            actor.send(1)
            actor.send(1)
            issuer.poll()
            Assert.assertEquals(Status.Running, actor.status())
            Assert.assertEquals(2, actor.current)

            // Paused, send prevented
            actor.pause()
            issuer.poll()
            actor.send(1)
            issuer.poll()
            Assert.assertEquals(Status.Paused, actor.status())
            Assert.assertEquals(2, actor.current)

            // Resume
            actor.resume()
            issuer.poll()
            actor.send(1)
            issuer.poll()
            Assert.assertEquals(Status.Running, actor.status())
            Assert.assertEquals(3, actor.current)

            // Stop
            actor.stop()
            issuer.poll()
            actor.send(1)
            issuer.poll()
            Assert.assertEquals(Status.Stopped, actor.status())
            Assert.assertEquals(3, actor.current)

            // Resume
            actor.resume()
            issuer.poll()
            actor.send(1)
            issuer.poll()
            Assert.assertEquals(Status.Running, actor.status())
            Assert.assertEquals(4, actor.current)
        }
    }


    private fun printMsg(msg:Message<Int>){
        when(msg){
            is Control -> println("control: " + msg.action)
            is Content -> println("content: " + msg.data)
            is Request -> println("request: ")
        }
    }
}