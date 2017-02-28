package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.{SuccessResult, Result}
import slate.common.results.{ResultFuncs, ResultCode, ResultSupportIn}

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
    val res = new SuccessResult[String]("user01", 123, msg = Some("created"), ref = Some(20), tag = Some("code12"))
    assert( res.success == true )
    assert( res.msg == Some("created" ))
    assert( res.code == 123 )
    assert( res.get == "user01")
    assert( res.ref == Some(20))
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


  test("can build okFailure with error") {

    val res = ResultFuncs.okOrFailure( {
      val msg = "fail"
      if(msg == "fail" ) {
        throw new IllegalArgumentException("test")
      }
      msg
    })
    ensure(res, false, ResultCode.FAILURE, "test", null)
  }


  test("can map to another value"){
    val yes = ok(msg = Some("m1"), tag = Some("t1"), ref = Some("GUID_123"))
    val good  = yes.map( v => if(v) 1 else 0 )

    assert( good.success )
    assert( good.code == yes.code )
    assert( good.msg == yes.msg )
    assert( good.tag == yes.tag )
    assert( good.ref == yes.ref )
    assert( good.get == 1 )
  }


  test("can map failure"){
    val fail = no(msg = Some("m1"), tag = Some("t1"), ref = Some("GUID_123"))
    val fail2  = fail.map( v => if(v) 1 else 0 )

    assert( !fail2.success )
    assert( fail2.code == fail.code )
    assert( fail2.msg  == Some("m1"))
    assert( fail2.tag  == Some("t1"))
    assert( fail2.ref  == Some("GUID_123"))
    assert( fail2.isEmpty )
  }


  test("can flat map to another value"){
    val yes = ok(msg = Some("m1"), tag = Some("t1"), ref = Some("GUID_123"))
    val good  = yes.flatMap( v => {
      if(v)
        success(1, msg = Some("f_msg1"), tag = Some("f_tag1"), ref = Some("GUID_XYZ"))
      else
        failure[Int](msg = Some("f_msg2"), tag = Some("f_tag2"))
    } )

    assert( good.success )
    assert( good.code == yes.code )
    assert( good.msg  == Some("f_msg1") )
    assert( good.tag  == Some("f_tag1") )
    assert( good.ref  == Some("GUID_XYZ") )
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
