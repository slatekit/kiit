
# Results

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Models success / failures with status codes, message, and other fields. Compatible with http status codes.</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2019-03-22</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>0.9.17</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>slatekit.common.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.results</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-common</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/results" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/results</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Results.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Results.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td> slatekit-results</td>
    </tr>
  </tbody>
</table>
{{% break %}}

## Gradle
{{< highlight gradle >}}
    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other libraries

        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-common:0.9.17'
    }

{{< /highlight >}}
{{% break %}}

## Import
{{< highlight kotlin >}}


// required 
import slatekit.common.*



// optional 
import slatekit.core.cmds.Cmd
import slatekit.results.*
import slatekit.results.Try
import slatekit.results.Success
import slatekit.results.Failure
import slatekit.results.builders.OutcomeBuilder
import slatekit.results.builders.Tries




{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}


n/a


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}


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
                value = "userId:1234567890",
                code = StatusCodes.SUCCESS.code,
                msg = "user created"
        )

        // Explicitly build a result using the Failure "branch" of Result
        val result2:Result<String,Exception> = Failure<Exception>(
                error = IllegalArgumentException("user id"),
                code = StatusCodes.BAD_REQUEST.code,
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
        val res1:Notice<Int> = Success(123456, msg = "user created")
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
        val res5 = errored<String>(StatusCodes.CONFLICT)
        printResult(res5)


        // CASE 7: Not found
        val res6 = errored<String>(StatusCodes.NOT_FOUND)
        printResult(res6)


        // CASE 8: Not available
        val res7 = errored<String>(StatusCodes.UNAVAILABLE)
        printResult(res7)


        // CASE 9: Build based on the Err model
        // This allows you to have pre-defined list of error infos to refer to
        //val failure:Result<Int, Err> = failure( ErrInfo(400, "Invalid user", null) )
        

{{< /highlight >}}
{{% break %}}


## Output

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
