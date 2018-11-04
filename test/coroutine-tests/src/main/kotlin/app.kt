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


import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import slatekit.async.futures.AsyncContextFuture
import slatekit.entities.repo.EntityRepo
import slatekit.entities.services.EntityService
import slatekit.async.futures.Future
import slatekit.async.futures.await
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import kotlin.system.measureTimeMillis


suspend fun main(args:Array<String>) {

    testCompose_Via_Futures().await()
    println("\n====================")
    testCompose_Via_Coroutines().await()
}


suspend fun testCoRoutines() {
    val asyncScope = AsyncContextFuture()
    val svc = EntityService<User>(EntityRepo<User>(mutableListOf(), asyncScope), asyncScope)

    val futures = listOf(
        svc.create(User(0, "user_1")),
        svc.create(User(0, "user_2")),
        svc.create(User(0, "user_3"))
    )

    futures.forEach{ it.await() }


    val member = EntityService::class.members.first { it.name == "all" }
    val result = member.call(svc)
    when(result) {
        is Future<*> -> {
            val v = result.await()
            println(v)
        }
        else -> println(result)
    }
    svc.all().await().forEach { println(it) }
    //svc.all().forEach { println(it) }



    println("done with suspend")
}



fun testCompose_Via_Futures(): Future<String> {
    val f0 = CompletableFuture.supplyAsync {
        println("JAVA FUTURES ================\n")
        val f1 = CompletableFuture.supplyAsync {
            println("f1: loading user : begin : " + LocalDateTime.now().toString())
            Thread.sleep(2000L)
            println("f1: loaded  user : end   : " + LocalDateTime.now().toString())
            "user_01"
        }

        val f2 = f1.thenApply { it ->
            println("\n")
            println("f2: updating user : begin : " + LocalDateTime.now().toString())
            Thread.sleep(3000L)
            println("f2: updated  user : end   : " + LocalDateTime.now().toString())
            "f2: updated user: $it"
        }

        val f3 = f2.thenCompose { it ->
            println("\n")
            println("f3: logging user : begin : " + LocalDateTime.now().toString())
            Thread.sleep(2000L)
            println("f3: logged  user : end   : " + LocalDateTime.now().toString())
            CompletableFuture.completedFuture("f3:logged: $it")
        }
        val result = f3.get()
        println()
        println(result)
        result
    }
    return f0
}


suspend fun testCompose_Via_Coroutines(): Deferred<String> {
    val f0 = GlobalScope.async {
        println("KOTLIN COROUTINES ================\n")
        val f1 = GlobalScope.async {
            println("f1: loading user : begin : " + LocalDateTime.now().toString())
            delay(2000L)
            println("f1: loaded  user : end   : " + LocalDateTime.now().toString())
            "user_01"
        }
        val f1Result = f1.await()
        val f2 = GlobalScope.async {
            val it = f1Result
            println("\n")
            println("f2: updating user : begin : " + LocalDateTime.now().toString())
            delay(3000L)
            println("f2: updated  user : end   : " + LocalDateTime.now().toString())
            "f2: updated user: $it"
        }
        val f2Result = f2.await()
        val f3 = GlobalScope.async {
            val it = f2Result
            println("\n")
            println("f3: logging user : begin : " + LocalDateTime.now().toString())
            delay(2000L)
            println("f3: logged  user : end   : " + LocalDateTime.now().toString())
            "f3:logged: $it"
        }
        val result = f3.await()
        println()
        println(result)
        result
    }
    return f0
}




fun testFutures(){

    val f1 = CompletableFuture.completedFuture(123)
    val f2 = f1.thenApply { it -> it + 1  }
    val f3 = f1.thenCompose { v -> CompletableFuture.completedFuture(v + 2) }
    val f4 = f1.thenAccept { it -> it + 3  }
    f3.handle { t, x ->
        println(t)
        println(x)
    }

    val f1Val = f1.get()
    val f2Val = f2.get()
    val f3Val = f3.get()
    val f4Val = f4.get()
    println(f1Val)

    println(f2Val)
    println(f3Val)
    println(f4Val)
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



