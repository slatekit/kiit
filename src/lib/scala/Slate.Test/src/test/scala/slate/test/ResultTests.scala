package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.{SuccessResult, Result}
import slate.common.results.{ResultCode, ResultSupportIn}

/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */


class ResultTests extends FunSuite with BeforeAndAfter with BeforeAndAfterAll with ResultSupportIn {

  test("can build result") {
    val res = new SuccessResult[String]("user01", 123, msg = Some("created"), ext = Some(20), tag = Some("code12"))
    assert( res.success == true )
    assert( res.msg == Some("created" ))
    assert( res.code == 123 )
    assert( res.get == "user01")
    assert( res.ext == Some(20))
    assert( res.tag == Some("code12"))
  }


  test("can build success") {
    ensure(success(true), true, ResultCode.SUCCESS, "success", true)
  }

  test("can build success with type") {
    ensure(success[Int](123), true, ResultCode.SUCCESS, "success", 123)
  }


  test("can build failure") {
    ensure(failure(), false, ResultCode.FAILURE, "failure", null)
  }


  test("can build unauthorized") {
    ensure(unAuthorized(), false, ResultCode.UNAUTHORIZED, "unauthorized", null)
  }


  test("can build unexpected") {
    ensure(unexpectedError(), false, ResultCode.UNEXPECTED_ERROR, "unexpected error", null)
  }


  test("can build conflict") {
    ensure(conflict(), false, ResultCode.CONFLICT, "conflict", null)
  }


  test("can build not found") {
    ensure(notFound(), false, ResultCode.NOT_FOUND, "not found", null)
  }


  private def ensure(result:Result[Any], success:Boolean, code:Int, msg:String, data:Any):Unit = {
    assert(result.success == success)
    assert(result.code == code)
    assert(result.msg == Some(msg))
    if(result.success){
      assert(result.get == data)
    }
  }
}
