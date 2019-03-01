/**
 * <slate_header>
 * author: Kishore Reddy
 * url: https://github.com/kishorereddy/scala-slate
 * copyright: 2016 Kishore Reddy
 * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
 * desc: a scala micro-framework
 * usage: Please refer to license on github for more info.
 * </slate_header>
 */

package slatekit.tutorial


import slatekit.core.cmds.Cmd
import kotlin.reflect.KProperty
import slatekit.common.ext.*
import slatekit.results.Success
import slatekit.results.Try

/**
 * Created by kreddy on 4/4/2016.
 */
class Example_Kotlin_Misc : Cmd("types") {

    override fun executeInternal(args: Array<String>?): Try<Any> {
        testExtensions()
        testOperator()
        return Success("")
    }
}


/**
 * @param start: 1
 * @param end  : 4
 */
class MyRange(val start:Int, val end:Int ){

    // Override inc ++
    operator fun inc():MyRange = MyRange(start, end + 1)


    // Override dec --
    operator fun dec():MyRange = MyRange(start, end - 1 )


    // Override plus +
    operator fun plus(num:Int):MyRange = MyRange(start, end + num )


    // Override minus - ( get the smallest range )
    operator fun minus(num:Int):MyRange = MyRange(start, end - num )


    // Override times *
    operator fun times(num:Int):MyRange = MyRange(start, end * num )


    // Override div Point * Point
    operator fun div(num:Int):MyRange = MyRange(start, end / num )


    // Override "in" 3 in myRange
    operator fun contains(x:Int):Boolean = x in start..end


    // Override index access [x] = true | false
    operator fun get(x:Int):Boolean = contains(x)


    // Override compare to ( <, <=, >, >=, ==, != )
    operator fun compareTo(other:MyRange):Int {
        val xCompare = start.compareTo(other.start)
        if (xCompare != 0) return xCompare
        return end.compareTo(other.end)
    }


    override fun toString(): String {
        return "$start : $end"
    }
}


fun testOperator():Unit {

    // Test ++, --
    var r1 = MyRange(1,4)
    println( r1++ )
    println( r1-- )

    // Test +, -, *, /
    println( r1 + 2 )
    println( r1 - 2 )
    println( r1 * 2 )
    println( r1 / 2 )

    // Test in/contains
    println( 3 in r1 )
    println( 10 in r1 )

    // Test get
    println(r1[3])
    println(r1[10])

    // Test <, <=, >, >=
    println(MyRange(1, 3) >  MyRange(1, 2))
    println(MyRange(1, 3) >= MyRange(1, 2))
    println(MyRange(1, 3) <  MyRange(1, 4))
    println(MyRange(1, 3) <= MyRange(1, 4))
}


// The type alias here becomes the shorthand for the function signature
typealias ErrorHandler = (Int, String, Exception) -> Unit


fun onError(code:Int, msg:String, ex:Exception):Unit {
   println("$code $msg ${ex.message}")
}


// onError callback explicitly defined with function signature
fun createUser1(email:String, onError:(Int, String, Exception) -> Unit):Unit {
    try {
        // create user here
    } catch (ex:Exception) {
        onError(500, "unexpected error creating user", ex )
    }
}


// onError callback is shorter and cleaner using type alias
fun createUser2(email:String, onError:ErrorHandler):Unit {
    try {
        // create user here
    } catch (ex:Exception) {
        onError(500, "unexpected error creating user", ex )
    }
}


fun testTypeAlias():Unit {
    // Same code at the call site
    createUser1("batman@gotham", ::onError)
    createUser2("batman@gotham", ::onError)
}

// Here the TODO function from standard library returns "Nothing"
// which allows the code to compile even though the function returns
// a type of Pair<Int,String>
fun testTODO(email:String, subject:String, content:String ):Pair<Int,String> {
    return if(email.isNullOrEmpty()){
        TODO("figure out error handling/logging here")
    }
    else {
        // send email
        Pair(200, "success")
    }
}


data class Employee(val id:Int, val email:String)


fun printMember(prop: KProperty<*>):Unit = println(prop.name)

// You can reference the properties of Employee class using "::"
// This facilitates type-safe meta programming. E.g. you can use
// this approach for mapping properties to a table, or building
// queries in an ORM.
fun testMemberReference():Unit {
    printMember( Employee::id    )
    printMember( Employee::email )
}



@Target(AnnotationTarget.CLASS)
annotation class Api (val name     : String = "",
                      val desc     : String = "")


@Target(AnnotationTarget.FUNCTION)
annotation class Get(
        val path     : String = ""
)


@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Arg(val desc:String)


data class Movie(val name:String, val year:Int)


@Api("movies", desc = "api to get movie info")
class MovieApi {

    @Get("blockbusters")
    fun blockbusters(@Arg("year released")
                     year:Int): List<Movie> {
        return listOf(
                Movie("Wonder Woman", 2017),
                Movie("Spider-Man"  , 2017)
        )
    }
}


@Deprecated("deprecation warning", level = DeprecationLevel.WARNING)
fun sampleFunc1():Unit {
}


@Deprecated("deprecation hidden", level = DeprecationLevel.HIDDEN)
fun sampleFunc2():Unit {
}


@Deprecated("deprecation error", level = DeprecationLevel.ERROR)
fun sampleFunc3():Unit {
}


fun testDeprecation():Unit {
    // deprecations with level warning are shown with strike-through
    sampleFunc1()

    // deprecations with level hidden are not available / resolved
    // so the following usage is a compile error
    //sampleFunc2()

    // depracation with level error is a compile level deprecation error
    //sampleFunc3()
}


/**
 * Truncate the string to the length supplied
 */
fun String.truncate(len:Int):String =
    if(this.length > len)
        this.substring(0, len)
    else
        this


/**
 * Returns all items after the first
 */
fun <T> List<T>.tail(): List<T> = this.drop(1)


fun testExtensions():Unit {
    println("abcd".truncate(3) + "!")
    println("abcd".truncate(4) + "!")
    println("abcd".truncate(5) + "!")

    println(listOf("a", "b", "c"))
    println(listOf("a", "b", "c").tail())
    println(listOf("a", "b", "c").insertAt(0, "x"))
    println(listOf("a", "b", "c").insertAt(1, "x"))
    println(listOf("a", "b", "c").insertAt(2, "x"))
    println(listOf("a", "b", "c").removeAt(0))
    println(listOf("a", "b", "c").removeAt(1))
    println(listOf("a", "b", "c").removeAt(2))
    println(listOf("a", "b", "c").update(0, "z"))
    println(listOf("a", "b", "c").update(1, "z"))
    println(listOf("a", "b", "c").update(2, "z"))
    println(listOf("a", "b", "c").convertToMap())
}


fun rangeTest():Unit {
    val r1:IntRange = 1..2
    r1.first
    r1.last
    r1.step
}
