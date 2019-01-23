/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */

package slatekit.examples

//<doc:import_required>
import slatekit.common.*
import slatekit.common.results.Err
import slatekit.common.results.ErrInfo
import slatekit.common.results.ResultCode.BAD_REQUEST
import slatekit.common.results.ResultCode.SUCCESS
import slatekit.common.results.ResultFuncs.conflict
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.notAvailable
import slatekit.common.results.ResultFuncs.notFound
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.unAuthorized
import slatekit.common.results.ResultFuncs.unexpectedError

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd

//</doc:import_examples>


class Example_Results : Cmd("results") {

    override fun executeInternal(args: Array<String>?): ResultEx<Any> {
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

        // Explicitly build result using the Success "branch" of Result
        val result1:Result<String,Exception> = Success(
                data = "userId:1234567890",
                code = SUCCESS,
                msg = "user created"
        )

        // Explicitly build a result using the Failure "branch" of Result
        val result2:Result<String,Exception> = Failure<Exception>(
                err = IllegalArgumentException("user id"),
                code = BAD_REQUEST,
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
        // 2. There is ResultEx ( also a type alias ) for Result<S, Exception>
        //    representing the error type as an Exception
        val res1:ResultMsg<Int> = success(123456, msg = "user created")
        printResult(res1)


        // CASE 2: Failure ( 400 ) with message and ref tag
        val res2a = failure<String>(msg = "invalid email")
        printResult(res2a)


        // CASE 2: Failure ( 400 ) with data ( user ), message, and ref tag
        val res2b = failure<String>(msg = "invalid email")
        printResult(res2b)


        // CASE 4: Unauthorized ( 401 )
        val res3 = unAuthorized<String>(msg = "invalid email")
        printResult(res3)


        // CASE 5: Unexpected ( 500 )
        val res4 = unexpectedError<String>(Exception("Invalid email"), msg = "invalid email")
        printResult(res4)


        // CASE 6: Conflict ( 409 )
        val res5 = conflict<String>(msg = "item already exists")
        printResult(res5)


        // CASE 7: Not found
        val res6 = notFound<String>(msg = "action not found")
        printResult(res6)


        // CASE 8: Not available
        val res7 = notAvailable<String>(msg = "operation currently unavailable")
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
```bat
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
