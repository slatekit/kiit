package slate.test

import slatekit.common.Result
import slatekit.common.results.ResultFuncs.no
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.unAuthorized
import slatekit.common.results.ResultFuncs.unexpectedError
import slatekit.common.results.ResultFuncs.conflict
import slatekit.common.results.ResultFuncs.notFound
import org.junit.Test
import slatekit.common.Success
import slatekit.common.results.*
import slatekit.common.results.ResultFuncs.yes

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


class ResultTests {

  @Test fun can_build_result() {
    val res =  Success(123,"user01",  msg = "created")
    assert( res.success  )
    assert( res.msg == "created" )
    assert( res.code == 123 )
    assert( res.value == "user01")
  }


  @Test fun can_build_success() {
    ensure(success(true), true, SUCCESS, "success", true)
  }


  @Test fun can_build_success_with_type() {
    ensure(success(123), true, SUCCESS, "success", 123)
  }


  @Test fun can_build_failure() {
    ensure(failure(), false, FAILURE, "failure", null)
  }


  @Test fun can_build_unauthorized() {
    ensure(unAuthorized(), false, UNAUTHORIZED, "unauthorized", null)
  }


  @Test fun can_build_unexpected() {
    ensure(unexpectedError(), false, UNEXPECTED_ERROR, "unexpected error", null)
  }


  @Test fun can_build_conflict() {
    ensure(conflict(), false, CONFLICT, "conflict", null)
  }


  @Test fun can_build_not_found() {
    ensure(notFound(), false, NOT_FOUND, "not found", null)
  }


  @Test fun can_build_okFailure_with_error() {

    val res = ResultFuncs.okOrFailure( {
      val msg = "fail"
      if(msg == "fail" ) {
        throw  IllegalArgumentException("test")
      }
      msg
    })
    ensure(res, false, FAILURE, "test", null)
  }


  @Test fun can_map_to_another_value() {
    val yes = yes(msg = "m1")
    val good  = yes.map( { v -> if(v) 1 else 0 })

    assert( good.success )
    assert( good.code == yes.code )
    assert( good.msg == yes.msg )
    assert( good.value == 1 )
  }


  @Test fun can_map_failure() {
    val fail = no(msg = "m1")
    val fail2  = fail.map( { v -> if(v) 1 else 0 } )

    assert( !fail2.success )
    assert( fail2.code == fail.code )
    assert( fail2.msg  == "m1")
    assert( fail2.value == null)
  }


 @Test fun can_flat_map_to_another_value() {
    val yes = yes(msg = "m1")
    val good  = yes.flatMap( { v ->
      if(v)
        success(1, msg = "f_msg1")
      else
        failure<Int>(msg = "f_msg2")
    } )

    assert( good.success )
    assert( good.code == yes.code )
    assert( good.msg  == "f_msg1")
  }


  private fun ensure(result:Result<Any>, success:Boolean, code:Int, msg:String, data:Any?) {
    assert(result.success == success)
    assert(result.code == code)
    assert(result.msg == msg)
    if(result.success){
      assert(result.value == data)
    }
  }
}
