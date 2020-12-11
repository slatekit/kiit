package test.actors
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.Assert
import org.junit.Test
import slatekit.actors.*

class Control_Tests : ActorTestSupport {

    fun setup(op:suspend(TestController, Puller<Int>) -> Unit) {
        runBlocking {
            val puller = puller("control.1")
            val actor = puller.handler as TestController
            op(actor, puller)
        }
    }


    fun adder(callback:(Message<Int>) -> Unit, op:suspend(TestAdder, Puller<Int>) -> Unit) {
        runBlocking {
            val channel = Channel<Message<Int>>(Channel.UNLIMITED)
            val scope = CoroutineScope(Dispatchers.IO)
            val context = Context("control.1", scope)
            val actor = TestAdder(context, channel)
            val puller = Puller<Int>(channel, actor, callback)
            op(actor, puller)
        }
    }


    @Test
    fun can_start() {
        setup { actor, puller ->
            actor.start()
            puller.pull(1)
            Assert.assertEquals(Status.Started, actor.status())
        }
    }



    @Test
    fun can_pause() {
        setup { actor, puller ->
            actor.pause()
            puller.pull(1)
            Assert.assertEquals(Status.Paused, actor.status())
        }
    }



    @Test
    fun can_stop() {
        setup { actor, puller ->
            actor.stop()
            puller.pull(1)
            Assert.assertEquals(Status.Stopped, actor.status())
        }
    }



    @Test
    fun can_resume_after_pause() {
        setup { actor, puller ->
            actor.pause()
            puller.pull(1)
            Assert.assertEquals(Status.Paused, actor.status())
            actor.resume()
            puller.pull(1)
            Assert.assertEquals(Status.Running, actor.status())
        }
    }



    @Test
    fun can_resume_after_stop() {
        setup { actor, puller ->
            actor.stop()
            puller.pull(1)
            Assert.assertEquals(Status.Stopped, actor.status())
            actor.resume()
            puller.pull(1)
            Assert.assertEquals(Status.Running, actor.status())
        }
    }



    @Test
    fun can_kill() {
        setup { actor, puller ->
            actor.kill()
            puller.pull(1)
            Assert.assertEquals(Status.Killed, actor.status())
        }
    }



    @Test
    fun can_process() {
        setup { actor, puller ->
            actor.start()
            actor.send(1)
            actor.send(Content(2))
            puller.pull(3)
            Assert.assertEquals(Status.Running, actor.status())
            Assert.assertEquals(2, actor.current)
        }
    }


    @Test
    fun can_work() {

        adder( { msg ->
            when(msg){
                is Control -> println("control: " + msg.action)
                is Content -> println("content: " + msg.data)
                is Request -> println("request: " + msg.action)
            }

        }) { actor, puller ->
            actor.start()
            val scope = CoroutineScope(Dispatchers.IO)
            val actions = listOf(Action.Pause, Action.Stop)
            (1..100).forEachIndexed { ndx, number ->
                scope.launch {
                    println("index=$number - sending 1")
                    actor.send(number)
                    when {
                        number % 20 == 0 -> { println("index=$number stopping"); actor.stop()    }
                        number % 10 == 0 -> { println("index=$number pausing "); actor.pause()   }
                        number % 5  == 0 -> { println("index=$number resuming"); actor.resume()  }
                        else -> {}
                    }
                }
            }
            // To account for ending with a stop
            puller.poll()
            actor.resume()
            puller.poll()
            Assert.assertEquals(Status.Running, actor.status())
            Assert.assertEquals(5050, actor.current)
        }
    }
}