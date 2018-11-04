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


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis


suspend fun main(args:Array<String>) {

    val svc = EntityService<User>(EntityRepo<User>(mutableListOf()))
    svc.create(User(0, "user_1"))
    svc.create(User(0, "user_2"))
    svc.create(User(0, "user_3"))

    svc.all().forEach { println(it) }

    println("done")
}


suspend fun testMeasure(){
    val time = measureTimeMillis {
        println("before 1")
        val one = doSomethingUsefulOne()
        println("before 2")
        val two = doSomethingUsefulTwo()
        println("The answer is ${one + two}")
    }

    //test_runBlocking()
    //Thread.sleep(2000)
}



