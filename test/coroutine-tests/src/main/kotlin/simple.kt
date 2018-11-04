import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */



fun basic1(){
    GlobalScope.launch {
        delay(100)
        println("kotlin 1.3 with co-routines")
    }
    println("hello")
    runBlocking {
        delay(1000)
    }
}

fun test_runBlocking() = runBlocking<Unit>{
    launch {
        delay(100)
        println("kotlin 1.3 with co-routines")
    }
    println("hello")
    delay(1000)
}

suspend fun test_joining() {
    val job = GlobalScope.launch {
        delay(1000L)
        println("world!")
    }
    println("hello")
    job.join()
}

suspend fun doSomethingUsefulOne(): Int {
    //GlobalScope.launch {
    println("one: before delay")
    delay(1000L) // pretend we are doing something useful here
    println("one: after delay")
    //}
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    //GlobalScope.launch {
    println("two: before delay")
    delay(1000L) // pretend we are doing something useful here
    println("two: after delay")
    //}
    return 29
}
