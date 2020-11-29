package slatekit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ChannelTests {
    suspend fun run(scope: CoroutineScope) {
        val actor = Actor<String>(scope) {
            delay(1000)
            println("processed $it")
        }
        actor.send("a")
        actor.send("b")
        actor.poll()
        scope.launch {
            actor.work()
        }
        delay(10000)
        //actor.done()
        println("done")
    }


    suspend fun run2() {
        val scope = CoroutineScope(Dispatchers.IO)
        val channel = Channel<String>(Channel.UNLIMITED)
        channel.send("a")
        scope.launch {
            for (item in channel) {
                println(item)
            }
        }
        println("done")
    }
}


class Actor<T>(val scope:CoroutineScope = CoroutineScope(Dispatchers.IO),
               val channel: Channel<T> = Channel<T>(Channel.UNLIMITED),
               val op:suspend(T) -> Unit) {

    suspend fun send(item:T) {
        channel.send(item)
    }

    suspend fun work() {
        for(item in channel) {
            process(item)
        }
    }

    suspend fun poll(){
        val item = channel.poll()
        item?.let { process(it) }
    }

    fun done() {
        channel.close()
    }

    private suspend fun process(item:T) {
        item?.let { op(it) }
    }
}