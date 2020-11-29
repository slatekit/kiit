package slatekit

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.joda.time.DateTime
import slatekit.cache.*
import slatekit.common.ids.Paired
import slatekit.common.log.LoggerConsole
import slatekit.core.common.ChannelCoordinator
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
import kotlin.system.measureTimeMillis

// Message types for counterActor
sealed class CounterMsg
object IncCounter : CounterMsg() // one-way message to increment counter
class  GetCounter(val response: CompletableDeferred<Int>) : CounterMsg() // a request with reply

class CacheApp() {

    var counter = 0
    val counter2 = AtomicInteger()
    val mutex = Mutex()
    var counter3 = 0


    fun exec() {
        counter()
        //test()
    }


    fun test() {
        runBlocking {
            val a = async {
                testAsync("a")
                println("completed a")
            }
            val b = async {
                testAsync("b")
                println("completed b")
            }
            val r = a.await()
            val s = b.await()
            println("done all")
        }
    }

    suspend fun testAsync(text:String){
        val result = testDelay(text)
        println("working on $text")
        val r = result.await()
        println(r)
    }

    suspend fun testDelay(text:String):Deferred<String> {
        val d = CompletableDeferred<String>()
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            d.complete("Done $text")
        }
        return d
    }

    fun counter() {
        runBlocking {
            val counterC = counterActor() // create the actor
            withContext(Dispatchers.Default) {
                massiveRun {
                    counter++
                    counter2.incrementAndGet()
                    mutex.withLock {
                        counter3++
                    }
                    counterC.send(IncCounter)
                }
            }
            println("Counter 1 var = $counter")
            println("Counter 2 atm = ${counter2.get()}")
            println("Counter 3 mtx = $counter3")

            // send a message to get a counter value from an actor
            val response = CompletableDeferred<Int>()
            counterC.send(GetCounter(response))
            println("Counter 4 act = ${response.await()}")
            println("Counter 4 sat = ${counterC.value()}")
            counterC.close()
        }
    }

    // This function launches a new counter actor
    fun CoroutineScope.counterActor() = actor<CounterMsg> {
        var counter = 0 // actor state
        for (msg in channel) { // iterate over incoming messages
            when (msg) {
                is IncCounter -> counter++
                is GetCounter -> msg.response.complete(counter)
            }
        }
    }

    suspend fun SendChannel<CounterMsg>.value() : Int {
        val response = CompletableDeferred<Int>()
        this.send(GetCounter(response))
        val result = response.await()
        return result
    }

    suspend fun massiveRun(action: suspend () -> Unit) {
        val n = 100  // number of coroutines to launch
        val k = 10   // times an action is repeated by each coroutine
        val time = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) { action() }
                    }
                }
            }
        }
        println("Completed ${n * k} actions in $time ms")
    }
}



class CacheTests {
    suspend fun cache() {
        val cache = CacheUtils.getCache()
        CoroutineScope(Dispatchers.IO).launch {
            cache.manage()
        }
        cache.put("a", "", 200) { delay(2000); 1 }
        cache.put("b", "", 200) { delay(2000); 2 }
        cache.put("c", "", 200) { delay(2000); 3 }
        val items = listOf("a", "b", "c")
        val scope = CoroutineScope(Dispatchers.IO)
        val actor = scope.actor<Int> {
            for(msg in this.channel){
                println(msg)
            }
        }
        //actor.send()

        for(ndx in 1..100) {
            CoroutineScope(Dispatchers.Default).launch {
                delay(1000)
                val time = DateTime.now()
                val r = Random.nextInt(0, 3)
                val key = items[r]
                val value = cache.get<Int>(key)
                val name = this.toString()
                println("$time : $name : $value")
            }
        }
        delay(12000)
        println("done")
    }
}


object CacheUtils {
    suspend fun getCache(initialize:Boolean = true, settings: CacheSettings = CacheSettings(10), listener:((CacheEvent) -> Unit)? = null): SimpleAsyncCache {
        val logger = LoggerConsole()
        val raw =  SimpleCache("async-cache", settings = settings, listener = listener, logger = logger)
        val coordinator = ChannelCoordinator<CacheCommand>(logger, Paired(), Channel(Channel.UNLIMITED))
        //val coordinator = MockCacheCoordinator(logger, Paired())
        val cache = SimpleAsyncCache(raw, coordinator)
        if(initialize) {
            cache.put("countries", "countries supported for mobile app", 60) { listOf("us", "ca") }
            runBlocking {
                cache.respond()
            }
        }
        return cache
    }
}