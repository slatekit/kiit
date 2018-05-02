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


import slatekit.common.Result
import slatekit.common.ResultEx
import slatekit.common.Success
import slatekit.core.cmds.Cmd

/**
 * Created by kreddy on 4/4/2016.
 */
class Example_Kotlin_Functions : Cmd("types") {

    override fun executeInternal(args: Array<String>?): ResultEx<Any> {
        testFuncs()
        hof_pass_func()
        hof_return_func()
        composition()
        return Success("")
    }


    // Test the functions
    fun testFuncs(): ResultEx<Any> {

        // function with parameters
        val r1 = salary(10.50, 8)
        println(r1)

        // function with parameters with default values
        val r2 = salary(rate = 10.50, hours = 8 )
        println(r2)

        // function with parameters with default values
        createUser("john doe")

        // function with function parameter
        createUser("john doe", true, { id -> println("created with id : $id") } )

        return Success("")
    }


    fun testLambda():Unit {

        TODO.REFACTOR("ACCOUNTS", "Improve error handling", {
            // Some code to cleanup here.
        })


        // With parameters inside (...) first followed by code block { }
        TODO.REFACTOR("ACCOUNTS", "Improve error handling") {
            // Some code to cleanup here
        }

        // With default params e.g. "" empty string and code block { }
        TODO.REFACTOR {
            // Some code to cleanup here
        }

        // With defaults and no code block
        TODO.REFACTOR()
    }


    // Sample function to calculate a salary.
    // NOTE: Single line function body can be written using an "=" sign
    fun salary(rate:Double, hours:Int):Double = rate * hours


    // Sample function to create a user.
    // NOTES:
    // 1. Parameters can have default values
    // 2. Unit is a type that does not represent any value
    fun createUser(name: String, isActive: Boolean = true): Unit {
        println(name)
    }


    // Sample function to simulate creating a new user with a callback
    // function supplied. This function takes 1 parameter of type Int
    // and returns nothing ( Unit ).
    fun createUser( name:String, isActive:Boolean, onComplete:(Int) -> Unit) : Unit {

        println(name + ", " + isActive)

        // simulate saving to database....
        val id = 2

        // notify caller
        onComplete( id )
    }

    // 4. function with functions as parameters,
    // Last expression is assumed to be the return value
    fun test4(name: String,
              helloCallBack1: () -> Unit,
              helloCallBack2: (String) -> Unit,
              helloCallBack3: (String, String) -> String): Unit {

        // 1. first function takes 0 parameters, and returns nothing
        helloCallBack1()

        // 2. second function takes 1 paramter, and returns nothing
        helloCallBack2("hello")

        // 3. third function takes 2 parameter and returns a value
        val result = helloCallBack3("hey", "hello")
        println(result)
    }
}


fun hof_pass_func(): Unit {

    // Creates a user, using the logger
    // supplied for handling any errors
    fun createUser(email: String, logger: (String, Exception) -> Unit): Unit {

        try {
            // try creating the user...
            throw Exception("test")
        }
        catch(ex: Exception) {
            logger("Error sending invite", ex)
        }
    }

    // Function to handle errors
    // Just printing to console for sample purposes
    val onError = { msg: String, ex: Exception -> println(msg + ":" + ex.message) }

    // Pass the error handler
    createUser("john@google.com", onError)
}


fun hof_return_func(): Unit {

    // Return a function that uses the rate per hour, and
    // overtime rate per hour to determine the salary.
    // This function only needs 1 parameter representing the hours worked
    fun getSalaryCalculator(rate: Double, overtimeRate: Double): (Int) -> Double {

        // NOTE: As an analogy, you have basically "dependency injected"
        // the salary and overtimePay into the function you are returning.
        val f = { hours: Int ->
            if (hours <= 8)
                rate * hours
            else
                rate * 8 + (overtimeRate * (hours - 8))
        }
        return f
    }

    // Supply the initial inputs ( rate per hour, rate overtime per hour )
    val calc = getSalaryCalculator(10.0, 15.0)

    // Now you have a function that only requires the hours worked
    // e.g. 140.00 = 8hours * 10.0 + 4hours * 15.0
    val pay = calc(12)
    println(pay)
}


// Simple utility function to "compose" 2 functions
// into a new function that calls both.
fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C {
    return { x -> f(g(x)) }
}


fun composition(): Unit {

    // 1st function to generate html for h1
    val h1 = { text: String -> "<h1>" + text + "</h1>" }

    // 2nd function to generate html section
    val section = { content: String -> "<div class='section'>" + content + "</div>" }

    // Now lets combine section and h1
    val heading = compose( section, h1 )

    // Generate the heading html
    val html = heading("Welcome")

    // e.g. <div class="section"><h1>Welcome</h1></div>"
    println(html)
}
