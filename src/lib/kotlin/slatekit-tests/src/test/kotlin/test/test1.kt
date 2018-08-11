
package test

import slatekit.common.Random
import slatekit.common.queues.QueueSourceDefault
import slatekit.core.common.AppContext
import slatekit.core.workers.System
import slatekit.core.workers.WorkerSample
import slatekit.core.workers.core.Priority
import slatekit.core.workers.core.QueueInfo

/*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType
import slatekit.utility.*
import slate.common.Random
*/

/**
 * Created by kishorereddy on 5/18/17.
 */


fun main(args: Array<String>) {
    println("slatekit.tests 1.1")
    testWorkers()
}


fun testWorkers():Unit {

    // 1. Queues
    val queues = (1..4).mapIndexed{ index, ndx -> QueueSourceDefault("q" + index.toString()) }

    // Populate each queue
    queues.forEachIndexed { index, queue ->

        val task = if(index > 0 ) "task2" else "task1"
        // Add 100 messages to each queue
        (1..100).forEach { count ->

            // Add with tags needed to convert to a Job
            queue.send(count.toString(), mapOf(
                "id" to Random.guid().toString(),
                "task" to task)
            )
        }
    }

    // 2. Work system.
    val queueInfos = queues.map { QueueInfo(it.name, Priority.Low, it) }
    val sys = System(
        AppContext.simple("test"),
        queueInfos
    )

    // 3. Register workers
    sys.register(WorkerSample("w1","g1",  ""))
    sys.register(WorkerSample("w2","g1",  ""))
    sys.register(WorkerSample("w3","g1",  ""))

    // 4. Test
    sys.exec()
    Thread.sleep(60000)
}

/*
fun testMods() {
    println(Consts.version)
    println("Hello, world 2.0!")

    test.testRandom()
    testOption()

    test.strings()
    println(test.controlFlow1())
    println(test.controlFlow2(2))
    test.lists()
    test.maps()
    test.clsData()
    test.cls()
    test.reflect()
}


fun testRandom(){

    println(Random.alpha3())
}


fun nulls():Unit {
    val s:String = ""
    val u: test.User = test.User(2, "a", "a,b", true)
}


fun strings():Unit {
    val name1:String? = "kishore"
    println(name1)

    val name2:String = name1 ?: ""
    println(name2)

    val id1:Int? = null
    println(id1)

    val id2:Int = id1 ?: 321
    println(id2)
}


fun controlFlow1():String {
    val v = 1
    return if (v == 1)
        "1"
    else
        "2"
}


fun controlFlow2(a:Int):String = if (a == 1) "1" else "2"


fun lists():Unit {
    // 1. declare
    val items = listOf(1, 2, 3)

    // 2. print
    println(items)

    // 3. loop
    items.forEach { println( " - " + it) }

    // 4. index
    println(items[1])

    // 5. count
    println(items.size)

    // 6. map
    val nums = items.map { it -> it.toString() }
    println(nums)

    // 7. copy
    val items2 = listOf(items.map{ it * 2 })
    println(items2)
}


fun maps():Unit {
    // 1. declare
    val items = mapOf( "a" to 1, "b" to 2)

    // 2. print
    println(items)

    // 3. loop
    items.forEach { println( " - " + it) }

    // 4. index
    println(items["a"])

    // 5. count
    println(items.size)

    // 6. map
    val nums = items.map { it -> it.toString() }
    println(nums)

    // 7. copy
    val items2 = items.entries.associate{ it -> it.key to it.value * 2 }
    println(items2)
}



value class User(val id:Int, val api:String, val email:String, val active:Boolean)


class Invite(val api:String, val email:String, val code:String) {

    val validCode = code == "abc"

    init {
        println("init : $api")
    }


    fun send():Unit {
        println("sending invite: $api, $email, $code")
    }
}

annotation class Api(val area: String, val api:String, val mode:String = "all", val roles:String = "")
annotation class ApiAction(val api:String="", val roles:String = "@parent")


@test.Api(area="app", api="invites")
class InviteApi {

    @test.ApiAction(roles= "dev")
    fun invite():Unit {
    }
}


fun clsData(): Unit {
    val u1 = test.User(1, "kreddy", "kreddy@abc.com", true)
    val u2 = u1.copy( 0 )
    println(u2)
}


fun cls(): Unit {
    val u1 = test.Invite("kreddy", "kreddy@abc.com", "xyz")
    println(u1.validCode)
    u1.send()
}


fun reflect():Unit {
    val u1 = test.Invite("kreddy", "kreddy@abc.com", "xyz")
    val uapi = test.InviteApi()
    test.reflector(uapi.kClass)
}

val<T: Any> T.kClass: KClass<T>
    get() = javaClass.kotlin


fun reflector(c:KClass<Any>):Unit {

    println("\napi: " + c.simpleName)
    println("\nsize: " + c.constructors.size)

    println("\nc parms 1: ")
    println(c.constructors.first().parameters)
    println("\nc parms 2: ")
    c.constructors.first().parameters.forEach { it -> println(it.api + ", " + it.type.javaType) }

    println("\nmem props: ")
    println(c.memberProperties)

    println("\nmembers: ")
    c.members.forEach { it -> println( it.api ) }

    println("annotations")
    c.annotations.forEach { it ->
        if (it is test.Api){
            val api = it as test.Api

            println("api ${api.area}, ${api.api}, ${api.mode}, ${api.roles}")
        }
    }
}



fun testOption(){
    testValue(Some("kishore"))
    testValue(None)

    testValue(Some(2))
    testValue(None)
    testValueInt(Some(3))
    testValueInt(None)
}


fun testValue(item: Optional2<Any>){
   println("empty: " + item.empty())
   println("exist: " + item.exists())
}


fun testValueInt(item: Optional2<Int>){
    println("value: " + item.getOrElse(2))
    println("empty: " + item.empty())
    println("exist: " + item.exists())
}


sealed class Optional2<out T> {

    abstract fun empty(): Boolean

    abstract fun get(): T

    fun exists(): Boolean = !empty()
}


class Some<out T>( val v:T? ) : Optional2<T>() {

        override fun get():T = v!!

        override fun empty():Boolean = v == null
}


object None : Optional2<Nothing>() {
    override fun empty():Boolean = true
    override fun get() = throw NoSuchElementException("None.get")
}


fun <T> Optional2<T>.getOrElse(default:  T): T = if (empty()) {
    default
} else {
    get()
}



*/
