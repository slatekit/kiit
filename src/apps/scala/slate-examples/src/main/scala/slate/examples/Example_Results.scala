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

package slate.examples

//<doc:import_required>

import slate.common.{SuccessResult, Result}
import slate.common.results.{ResultSupportIn, ResultCode}

//</doc:import_required>

//<doc:import_examples>
import slate.core.cmds.Cmd
//</doc:import_examples>


class Example_Results extends Cmd("types") with ResultSupportIn {

  override protected def executeInternal(args: Any) : AnyRef = {

    //<doc:examples>
    // CASE 1: The Result class is a glorified wrapper around a status code
    // and contains the following
    // 1. success / failure  - flag
    // 2. message / error    - informative message
    // 3. data               - data being returned
    // 4. code               - matches http status codes
    // 5. ref                - tag for external id references
    // 6. exception          - error info
    val result = new SuccessResult[String](
      value  = "userId:1234567890",
      code  = ResultCode.SUCCESS,
      msg   = Some("user created"),
      tag   = Some("tag001")
    )

    // NOTES: ResultSupport trait builds results that simulate Http Status codes
    // This allows easy construction of results/status from a server layer
    // instead of a controller/api layer

    // CASE 2: Success ( 200 )
    val res1 = success(123456, msg = Some("user created"), tag=Some("promoCode:ny001"))
    printResult(res1)


    // CASE 3: Failure ( 400 ) with ref tag
    val res2 = failure(Some("invalid email"), tag = Some("23SKASDF23"))
    printResult(res2)


    // CASE 4: Unauthorized ( 401 )
    val res3 = unAuthorized(Some("invalid email"))
    printResult(res3)


    // CASE 5: Unexpected ( 500 )
    val res4 = unexpectedError(Some("invalid email"))
    printResult(res4)


    // CASE 6: Conflict ( 409 )
    val res5 = conflict(Some("item already exists"))
    printResult(res5)


    // CASE 7: Failure
    val res6 = notFound(Some("action not found"))
    printResult(res6)


    // CASE 8: Failure
    val res7 = notAvailable(Some("operation currently unavailable"))
    printResult(res7)
    //</doc:examples>

    ok()
  }


  def printResult(result:Result[Any]):Unit = {
    println("success: " + result.success)
    println("message: " + result.msg)
    println("code   : " + result.code)
    println("data   : " + result.get)
    println("ref    : " + result.tag)
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
