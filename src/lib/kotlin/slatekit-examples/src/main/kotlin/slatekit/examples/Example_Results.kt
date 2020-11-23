/**
<slate_header>
author: Kishore Reddy
url: www.github.com/code-helix/slatekit
copyright: 2015 Kishore Reddy
license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
desc: A tool-kit, utility library and server-backend
usage: Please refer to license on github for more info.
</slate_header>
 */

package slatekit.examples

//<doc:import_required>

//</doc:import_required>

//<doc:import_examples>
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.common.validations.Validations
import slatekit.examples.common.User
import slatekit.results.*
import slatekit.results.Try
import slatekit.results.Success
import slatekit.results.Failure
import slatekit.results.builders.Notices
import slatekit.results.builders.OutcomeBuilder
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries
import java.util.*

//</doc:import_examples>


class Example_Results : Command("results"), OutcomeBuilder {

    //<doc:examples>
    fun usage() {

        // Create success explicitly
        val start: Result<Int, Err> = Success(10)

        // Properties
        println(start.success)     // true
        println(start.status.code) // Codes.SUCCESS.code
        println(start.status.desc)  // Codes.SUCCESS.msg

        // Safely operate on values with map/flatMap
        val addResult = start.map { it + 1 }
        val subResult = start.flatMap { Success(it - 1) }

        // Check values
        println(addResult.contains(11))
        println(addResult.exists { it == 11 })

        // Get values
        println(addResult.getOrNull())
        println(addResult.getOrElse { 0 })

        // On conditions
        subResult.onSuccess { println(it) } // 9
        subResult.onFailure { println(it) } // N/A

        // Pattern match on branches ( Success / Failure )
        when (addResult) {
            is Success -> println("Value is : ${addResult.value}") // 11
            is Failure -> println("Error is : ${addResult.error}") // N/A
        }

        // Pattern match on status
        when (addResult.status) {
            is Passed.Succeeded  -> println(addResult.msg)
            is Passed.Pending    -> println(addResult.msg)
            is Failed.Denied     -> println(addResult.msg)
            is Failed.Invalid    -> println(addResult.msg)
            is Failed.Ignored    -> println(addResult.msg)
            is Failed.Errored    -> println(addResult.msg)
            is Failed.Unknown -> println(addResult.msg)
        }
    }


    fun creation() {
        // Success: Straight-forward
        val result = Success(42)

        // Success referenced as base type Result<Int, Err>
        val result1a: Result<Int, Err> = Success(42)

        // Success created with status codes / messages
        val result1b = Success(42, status = Codes.SUCCESS)
        val result1c = Success(42, msg = "Successfully processed")
        val result1d = Success(42, msg = "Successfully processed", code = 200)

        // Failure
        val result1e = Failure(Err.of("Invalid email"))

        // Failure referenced as base type Result<Int, Err>
        val result1f: Result<Int, Err> = Failure(Err.of("Invalid email"))

        // Failure created with status codes / messages
        val result1g = Failure(Err.of("Invalid email"), Codes.INVALID)
        val result1h = Failure(Err.of("Invalid email"), msg = "Invalid inputs")
        val result1i = Failure(Err.of("Invalid email"), msg = "Invalid inputs", code = Codes.INVALID.code)
    }


    fun get() {
        // Create
        val result:Result<Int, Err> = Success(42)

        // Get value or default to null
        val value1:Int? = result.getOrNull()

        // Get value or default with value provided
        val value2:Int = result.getOrElse { 0 }

        // Map over the value
        val op1 = result.map { it + 1 }

        // Flat Map over the value
        val op2 = result.flatMap { Success(it + 1 ) }

        // Fold to transform both the success / failure into something else ( e.g. string here )
        val value3:String = result.fold({ "Succeeded : $it" }, {err -> "Failed : ${err.msg}" })

        // Get value if success
        result.onSuccess { println("Number = $it") }

        // Get error if failure
        result.onFailure { println("Error is ${it.msg}") }

        // Pattern match
        when(result) {
            is Success -> println(result.value)  // 42
            is Failure -> println(result.error)  // Err
        }
    }


    fun check(){
        val result:Result<Int,Err> = Success(42)

        // Check if the value matches the criteria
        result.exists { it == 42 } // true

        // Check if the value matches the one provided
        result.contains(2)        // false

        // Pattern match 1: "Top-Level" on Success/Failure (Binary true / false )
        when(result) {
            is Success -> println(result.value)  // 42
            is Failure -> println(result.error)  // Err
        }

        // Pattern match 2: "Mid-level" on Status ( 7 logical groups )
        // NOTE: The status property is available on both the Success/Failure branches
        when(result.status) {
            is Passed.Succeeded  -> println(result.msg) // Success!
            is Passed.Pending    -> println(result.msg) // Success, but in progress
            is Failed.Denied     -> println(result.msg) // Security related
            is Failed.Invalid    -> println(result.msg) // Bad inputs / data
            is Failed.Ignored    -> println(result.msg) // Ignored for processing
            is Failed.Errored    -> println(result.msg) // Expected errors
            is Failed.Unknown -> println(result.msg) // Unexpected errors
        }

        // Pattern match 3: "Low-Level" on numeric code
        when(result.status.code) {
            Codes.SUCCESS.code    -> println("OK")
            Codes.QUEUED.code     -> println("Pending")
            Codes.UPDATED.code    -> println("User updated")
            Codes.DENIED.code     -> println("Log in again")
            Codes.DEPRECATED.code -> println("No longer supported")
            Codes.CONFLICT.code   -> println("Email already exists")
            else                  -> println("Other!!")
        }
    }


    fun errors() {

        // Build Err from various sources using convenience methods
        // From simple string
        val err1 = Err.of("Invalid email")

        // From Exception
        val err2 = Err.ex(Exception("Invalid email"))

        // From field name / value
        val err3 = Err.on("email", "abc123@", "Invalid email")

        // From status code
        val err4 = Err.code(Codes.INVALID)

        // From list of error strings
        val err5 = Err.list(listOf(
                "username must be at least 8 chars",
                "username must have 1 UPPERCASE letter"),
                "Username is invalid")

        // From list of Err types
        val err6 = Err.ErrorList(listOf(
                Err.on("email", "abc123 is not a valid email", "Invalid email"),
                Err.on("phone", "123-456-789 is not a valid U.S. phone", "Invalid phone")
        ), "Please correct the errors")

        // Create the Failure branch from the errors
        val result:Result<UUID, Err> = Failure(err6)
    }


    fun aliases1(){
        val result:Try<Int> = Success( "1".toInt() )
        println(result)
    }


    fun aliases(){

        // Build results ( imagine this is some user registration flow )
        // Try<T> = Result<T, Exception>
        val tried1 = Tries.success( User() )
        val tried2 = Tries.denied<User>("Phone exists")
        val tried3 = Tries.invalid<User>("Email required")

        // Outcome<T> = Result<T, Err>
        val outcome1 = Outcomes.success( User() )
        val outcome2 = Outcomes.denied<User>("Phone exists")
        val outcome3 = Outcomes.invalid<User>("Email required" )

        // Notice<T> = Result<T, String>
        val notice1 = Notices.success( User() )
        val notice2 = Notices.denied<User>("Phone exists")
        val notice3 = Notices.invalid<User>("Email required" )

        // Validated<T> = Result<T, ErrorList>
        val res4:Validated<String> = Failure(Err.ErrorList(listOf(
                Err.on("email", "abc123 is not a valid email", "Invalid email"),
                Err.on("phone", "123-456-789 is not a valid U.S. phone", "Invalid phone")
        ), "Please correct the errors"))

    }


    fun builders(){
        // Outcome<Int> = Result<Int, Err>
        val res1 = Outcomes.success(1, "Created User with id 1")
        val res2 = Outcomes.denied<Int>("Not authorized to send alerts")
        val res3 = Outcomes.ignored<Int>("Not a beta tester")
        val res4 = Outcomes.invalid<Int>("Email is invalid")
        val res5 = Outcomes.conflict<Int>("Duplicate email found")
        val res6 = Outcomes.errored<Int>("Phone is invalid")
        val res7 = Outcomes.unexpected<Int>("Unable to send confirmation code")
    }


    fun tries(){
        // Try<Long> = Result<Long, Exception>
        val converted1:Try<Long> = Tries.of { "1".toLong() }

        // DeniedException will checked and converted to Status.Denied
        val converted2:Try<Long> = Tries.of<Long> {
            throw DeniedException("Token invalid")
        }
    }


    fun validated(){

        // Model to validate
        val user = User(0, "batman_gotham", "batman", "", true, 34)

        // Validated<User> = Result<User, Err.ErrorList>
        val validated = Validations.collect<User,String, Err>(user) {
            listOf(
                    isNotEmpty(user.firstName),
                    isNotEmpty(user.lastName),
                    isEmail(user.email)
            )
        }

        // Print first error
        when(validated) {
            is Success -> println("User model is valid")
            is Failure -> println("User model failed with : " + validated.error.errors.first().msg)
        }
    }


    fun http(){
        // Simulate a denied exception ( Security related )
        val denied:Outcome<Long> = Outcomes.denied("Access token has expired")

        // Convert it to HTTP
        // This returns back the HTTP code + original Status
        val code:Pair<Int, Status> = Codes.toHttp(denied.status)
        println(code.first) // 401
    }

    //</doc:examples>


    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:examples>
        // The Result<S,F> class is a way to model successes and failures.
        // Design: This is essentially a specialized Either[L,R] with optional integer code/string message.
        //
        // FIELDS:
        // 1. data    : [required] - data being returned in a success case
        // 2. success : [derived ] - success/ failure flag based on Success/Failure branch
        // 3. code    : [optional] - integer code to describe error
        // 4. message : [optional] - string to represent message/error for Success/Failure

        // NOTES:
        // This is essentially a specialized Either[L,R] with optional integer code/string message.
        // - The result is inspired by Scala's Option[T] and Try/Success/Failure
        // - It provides a status code as an integer
        // - The result has 2 branches ( Success and Failure )
        // - You can supply a type parameter for the data
        // - Convenience functions are available to mimick HTTP Status Codes( see samples below ).
        // - HTTP status are fairly general purpose and can be used outside of an http context.
        //   However, you can supply and use your own status codes if needed.

        // Create success explicity
        val result: Result<Int, Err> = Success(1)

        // Properties
        println(result.success)     // true
        println(result.status.code) // Codes.SUCCESS.code
        println(result.status.desc)  // Codes.SUCCESS.msg

        // Get value or default to null
        val value1: Int? = result.getOrNull()

        // Get value or default with value provided
        val value2: Int = result.getOrElse { 0 }

        // Map over the value
        val op1: Result<Int, Err> = result.map { it + 1 }

        // Flat Map over the value
        val op2: Result<Int, Err> = result.flatMap { Success(it + 1) }

        // Fold to transform both the success / failure into something else ( e.g. string here )
        val value3: String = result.fold({ "Succeeded : $it" }, { err -> "Failed : ${err.msg}" })

        // Check if the value matches the criteria
        result.exists { it == 1 } // true

        // Check if the value matches the one provided
        result.contains(2)        // false

        // Get value if success
        result.onSuccess { println("Number = $it") }

        // Get error if failure
        result.onFailure { println("Error is ${it.msg}") }

        // Pattern match
        when (result) {
            is Success -> println(result.value)  // 1
            is Failure -> println(result.error)  // Err
        }

        val result1b = Success(42, msg = "Successfully processed")
        val result1c = Success(42, msg = "Successfully processed", code = 200)
        val result1d = Outcomes.success(42, Codes.SUCCESS)

        // Create failure explicitly
        val result1e: Result<Int, Err> = Failure(Err.of("Invalid email"))
        val result1f = Failure(Err.of("Invalid email"), msg = "Invalid inputs")
        val result1g = Failure(Err.of("Invalid email"), msg = "Invalid inputs", code = Codes.INVALID.code)
        val result1h = Outcomes.invalid<Int>(Err.of("Invalid email"), Codes.INVALID)

        // PATTERN MATCH 1: Success / Failure
        when (result) {
            is Success -> println(result.value)  // 1
            is Failure -> println(result.error)  // Err
        }

        // PATTERN MATCH 2: On status ( logical categories of statuses )
        // NOTE: The status property is available on both the Success/Failure branches
        when(result) {
            is Success -> when(result.status) {
                is Passed.Succeeded  -> println(result.msg)
                is Passed.Pending    -> println(result.msg)
            }
            is Failure -> when(result.status) {
                is Failed.Denied     -> println(result.msg)
                is Failed.Invalid    -> println(result.msg)
                is Failed.Ignored    -> println(result.msg)
                is Failed.Errored    -> println(result.msg)
                is Failed.Unknown -> println(result.msg)
            }
        }

        // PATTERN MATCH 3: On code
        when (result.status.code) {
            Codes.SUCCESS.code -> "OK"
            Codes.QUEUED.code -> "Pending"
            Codes.UPDATED.code -> "User updated"
            Codes.DENIED.code -> "Log in again"
            Codes.DEPRECATED.code -> "No longer supported"
            Codes.CONFLICT.code -> "Email already exists"
            else -> "Other!!"
        }

        // Explicitly build a result using the Failure "branch" of Result
        val result2: Result<String, Exception> = Failure<Exception>(
                error = IllegalArgumentException("user id"),
                code = Codes.BAD_REQUEST.code,
                msg = "user id not supplied"
        )

        // NOTES: ResultFuncs object contain methods to easily build up either
        // success or failure results that align with Http Status codes.
        // HTTP status codes are very general purpose with meaningful intents
        // ( bad-request, unauthorized, unexpected, etc ), and since the
        // Result class models success / failures, its useful to build up
        // results from from a server layer and pass them back up to the top
        // level controller / api layer.

        // CASE 1: Success ( 200 )
        // NOTE:
        // 1. The ResultMsg is just a type alias for Result<S, String>
        //    representing the error type as a simple string.
        // 2. There is Try ( also a type alias ) for Result<S, Exception>
        //    representing the error type as an Exception
        val res1: Notice<Int> = Success(123456, msg = "user created")
        printResult(res1)


        // CASE 2: Failure ( 400 ) with message and ref tag
        val res2a = errored<String>(msg = "invalid email")
        printResult(res2a)


        // CASE 2: Failure ( 400 ) with data ( user ), message, and ref tag
        val res2b = errored<String>(msg = "invalid email")
        printResult(res2b)


        // CASE 4: Unauthorized ( 401 )
        val res3 = denied<String>(msg = "invalid email")
        printResult(res3)


        // CASE 5: Unexpected ( 500 )
        val res4 = Tries.unexpected<String>(Err.of("Invalid email"))
        printResult(res4)


        // CASE 6: Conflict ( 409 )
        val res5 = errored<String>(Codes.CONFLICT)
        printResult(res5)


        // CASE 7: Not found
        val res6 = errored<String>(Codes.NOT_FOUND)
        printResult(res6)


        // CASE 8: Not available
        val res7 = errored<String>(Codes.TIMEOUT)
        printResult(res7)


        // CASE 9: Build based on the Err model
        // This allows you to have pre-defined list of error infos to refer to
        //val failure:Result<Int, Err> = failure( ErrInfo(400, "Invalid user", null) )
        //</doc:examples>

        return Success("")
    }


    fun printResult(result: Result<*, *>): Unit {
        println("success: " + result.success)
        println("message: " + result.msg)
        println("code   : " + result.code)
        println()
        println()
    }

    /*
//<doc:output>
{{< highlight bat >}}
    success: true
    message: user created
    code   : 200
    data   : 123456
    ref    : promoCode:ny001


    success: false
    message: invalid email
    code   : 400
    data   : null
    ref    : 23SKASDF23


    success: false
    message: invalid email
    code   : 401
    data   : null
    ref    :


    success: false
    message: invalid email
    code   : 500
    data   : null
    ref    :


    success: false
    message: item already exists
    code   : 409
    data   : null
    ref    :


    success: false
    message: action not found
    code   : 404
    data   : null
    ref    :


    success: false
    message: operation currently unavailable
    code   : 503
    data   : null
    ref    :
```
//</doc:output>
    */
}
