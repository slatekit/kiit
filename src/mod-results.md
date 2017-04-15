---
layout: start_page_mods_utils
title: module Results
permalink: /mod-results
---

# Results

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A monad that wraps a value with status codes, message, and other fields. Support failure, success branchs and supports http status codes. | 
| **date**| 2017-04-12T22:59:15.190 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common  |
| **source core** | slate.common.Result.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/common](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/common)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Results.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Results.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.{SuccessResult, Result}
import slate.common.results.{ResultCode}
import slate.common.results.ResultFuncs._


// optional 
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala


    // CASE 1: The Result class is a container for the following
    // 1. success / failure  - flag
    // 2. message / error    - informative message
    // 3. data               - data being returned
    // 4. code               - matches http status codes
    // 5. ref                - tag for external id references
    // 6. exception          - error info
    // 7. tag                - for referencing/tracking purposes

    // NOTES: The result is in an alternative to Scala's Option[T]
    // and also the Try/Success/Failure. It provides a status code
    // as an integer that can be set to Http Status codes.
    // Also, the ResultSupportIn trait supports convenience functions
    // that can be used to easily build up Success or Failure Results
    // that match Http Status Codes ( see samples below ).
    val result = new SuccessResult[String](
      value  = "userId:1234567890",
      code  = ResultCode.SUCCESS,
      msg   = Some("user created"),
      tag   = Some("tag001")
    )

    // NOTES: ResultSupport trait builds results that simulate Http Status codes
    // This allows easy construction of results/status from a server layer
    // instead of a controller/api layer

    // CASE 1: Success ( 200 )
    val res1 = success(123456, msg = Some("user created"), tag=Some("promoCode:ny001"))
    printResult(res1)


    // CASE 2: Failure ( 400 ) with message and ref tag
    val res2a = failure(msg = Some("invalid email"), tag = Some("23SKASDF23"))
    printResult(res2a)


    // CASE 2: Failure ( 400 ) with data ( user ), message, and ref tag
    val res2b = failure(msg = Some("invalid email"), tag = Some("23SKASDF23"))
    printResult(res2b)


    // CASE 4: Unauthorized ( 401 )
    val res3 = unAuthorized(msg = Some("invalid email"))
    printResult(res3)


    // CASE 5: Unexpected ( 500 )
    val res4 = unexpectedError(msg = Some("invalid email"))
    printResult(res4)


    // CASE 6: Conflict ( 409 )
    val res5 = conflict(msg = Some("item already exists"))
    printResult(res5)


    // CASE 7: Not found
    val res6 = notFound(msg = Some("action not found"))
    printResult(res6)


    // CASE 8: Not available
    val res7 = notAvailable(msg = Some("operation currently unavailable"))
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
  