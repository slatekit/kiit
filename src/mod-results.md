---
layout: start_page_mods_utils
title: module Results
permalink: /kotlin-mod-results
---

# Results

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Models success / failures with status codes, message, and other fields. Compatible with http status codes. | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common  |
| **source core** | slatekit.common.Result.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Results.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Results.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.Failure
import slatekit.common.Result
import slatekit.common.Success
import slatekit.common.results.BAD_REQUEST
import slatekit.common.results.ResultFuncs.conflict
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.notAvailable
import slatekit.common.results.ResultFuncs.notFound
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.unAuthorized
import slatekit.common.results.ResultFuncs.unexpectedError
import slatekit.common.results.SUCCESS



// optional 
import slatekit.core.cmds.Cmd



```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


        // CASE 1: The Result class is a container for the following
        // 1. success / failure  - flag
        // 2. message / error    - informative message
        // 3. data               - data being returned
        // 4. code               - matches http status codes
        // 5. ref                - tag for external id references
        // 6. exception          - error info
        // 7. tag                - for referencing/tracking purposes

        // NOTES:
        // - The result is inspired by Scala's Option[T] and Try/Success/Failure
        // - It provides a status code as an integer
        // - The result has 2 branches ( Success and Failure )
        // - You can supply a type parameter for the data
        // - Convenience functions are available to mimick HTTP Status Codes( see samples below ).

        // Explicitly build result using the Success "branch" of Result
        val result1 = Success(
                data = "userId:1234567890",
                code = SUCCESS,
                msg = "user created",
                tag = "tag001",
                ref = "XY123"
        )

        // Explicitly build a result using the Failure "branch" of Result
        val result2 = Failure<String>(
                code = BAD_REQUEST,
                msg = "user id not supplied",
                tag = "tag001",
                err = IllegalArgumentException("user id"),
                ref = null
        )

        // NOTES: ResultFuncs object contain methods to easily build up either
        // success or failure results that align with Http Status codes.
        // HTTP status codes are very general purpose with meaningful intents
        // ( bad-request, unauthorized, unexpected, etc ), and since the
        // Result class models success / failures, its useful to build up
        // results from from a server layer and pass them back up to the top
        // level controller / api layer.

        // CASE 1: Success ( 200 )
        val res1 = success(123456, msg = "user created", tag = "promoCode:ny001")
        printResult(res1)


        // CASE 2: Failure ( 400 ) with message and ref tag
        val res2a = failure<String>(msg = "invalid email", tag = "23SKASDF23")
        printResult(res2a)


        // CASE 2: Failure ( 400 ) with data ( user ), message, and ref tag
        val res2b = failure<String>(msg = "invalid email", tag = "23SKASDF23")
        printResult(res2b)


        // CASE 4: Unauthorized ( 401 )
        val res3 = unAuthorized<String>(msg = "invalid email")
        printResult(res3)


        // CASE 5: Unexpected ( 500 )
        val res4 = unexpectedError<String>(msg = "invalid email")
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
        

```


## Output

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
