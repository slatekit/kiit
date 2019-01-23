package test.common

import org.junit.Assert
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.unAuthorized
import slatekit.common.results.ResultFuncs.unexpectedError
import slatekit.common.results.ResultFuncs.conflict
import slatekit.common.results.ResultFuncs.notFound
import org.junit.Test
import slatekit.common.*
import slatekit.common.Success
import slatekit.common.results.*
import slatekit.common.results.ResultCode.CONFLICT
import slatekit.common.results.ResultCode.FAILURE
import slatekit.common.results.ResultCode.NOT_FOUND
import slatekit.common.results.ResultCode.SUCCESS
import slatekit.common.results.ResultCode.UNAUTHORIZED
import slatekit.common.results.ResultCode.UNEXPECTED_ERROR

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


//object MyCodes {
//
//  val UserCreated = slatekit.common.results.Success(200, "Created user")
//  val UserUpdated = slatekit.common.results.Success(201, "Updated user")
//  val UserDeleted = slatekit.common.results.Success(200, "Deleted user")
//}


class ResultTests {


  data class Person(val name:String)


  @Test fun can_build_result() {
    val res =  Success("user01", 123, msg = "created")
    assert( res.success  )
    assert( res.msg == "created" )
    assert( res.code == 123 )
    assert( res.getOrElse { "" } == "user01")
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


  @Test fun can_build_unauthorized2() {
    val result1 = unAuthorized<Int>("1")
    val result2 = unAuthorized<Int, Err>(Invalid(2, "Unauthorized"))

//    val result4 = success2<Person, Err>(Person("kishore"))
//    val result5 = success2<Person, Err>(Person("kishore"), MyCodes.UserCreated)
//    val result6 = failure2<Person, Err>(Invalid(300, "Invalid user"))
  }


  @Test fun can_build_unexpected() {
      val result = unexpectedError<Person>(Exception("Test error"))
      assert(!result.success)
      assert(result.code == UNEXPECTED_ERROR)
      assert(result.msg == "unexpected error")
  }


  @Test fun can_build_conflict() {
    ensure(conflict(), false, CONFLICT, "conflict", null)
  }


  @Test fun can_build_not_found() {
    ensure(notFound(), false, NOT_FOUND, "not found", null)
  }


  @Test
  fun test_success(){
    val res = success(Person("peter"), msg="created")
    Assert.assertTrue(res.success)
    Assert.assertEquals("created", res.msg)
    Assert.assertEquals(SUCCESS, res.code)
  }


  @Test
  fun test_map_success(){
    val res1 = success(Person("peter"), msg="created")
    val res = res1.map { it -> Pair(it, "spider-man") }

    Assert.assertTrue(res.success)
    Assert.assertEquals("created", res.msg)
    Assert.assertEquals(SUCCESS, res.code)

    val user = res.getOrElse( { Pair(Person("kishore"), "reddy") })
    Assert.assertTrue(user.first.name == "peter")
    Assert.assertTrue(user.second == "spider-man")
  }


  @Test
  fun test_map_failure(){
    val res1 = failure<Person>()
    val res = res1.map { it -> Pair(it, "spider-man") }

    Assert.assertFalse(res.success)
    Assert.assertEquals("failure", res.msg)
    Assert.assertEquals(FAILURE, res.code)

    val user = res.getOrElse( { Pair(Person("kishore"), "reddy") })
    Assert.assertTrue(user.first.name == "kishore")
    Assert.assertTrue(user.second == "reddy")
  }


  @Test
  fun test_flat_map_success(){
    val res1 = success(Person("peter"), msg="created")
    val res = res1.flatMap { it -> success(Pair(it, "spider-man"), SUCCESS, "alias") }

    Assert.assertTrue(res.success)
    Assert.assertEquals("alias", res.msg)
    Assert.assertEquals(SUCCESS, res.code)

    val user = res.getOrElse( { Pair(Person("kishore"), "reddy") })
    Assert.assertTrue(user.first.name == "peter")
    Assert.assertTrue(user.second == "spider-man")
  }


  @Test
  fun test_flat_map_failure(){
    val res1 = failure<Person>("setup")
    val res = res1.flatMap { it -> failure<Pair<Person, String>>() }

    Assert.assertFalse(res.success)
    Assert.assertEquals("setup", res.msg)
    Assert.assertEquals(FAILURE, res.code)

    val user = res.getOrElse( { Pair(Person("kishore"), "reddy") })
    Assert.assertTrue(user.first.name == "kishore")
    Assert.assertTrue(user.second == "reddy")
  }


  @Test
  fun test_of_success(){
    val res = Result.of { Person("peter") }

    Assert.assertTrue(res.success)
    Assert.assertEquals("success", res.msg)
    Assert.assertEquals(SUCCESS, res.code)
  }


  @Test
  fun test_of_failure(){
    val res = Result.of { throw Exception("Test error") }

    Assert.assertFalse(res.success)
    Assert.assertEquals("Test error", res.msg)
    Assert.assertEquals(FAILURE, res.code)
  }


  @Test
  fun test_attempt_failure(){
    val res = Result.attempt { throw Exception("Test error") }

    Assert.assertFalse(res.success)
    Assert.assertEquals("Test error", res.msg)
    Assert.assertEquals(UNEXPECTED_ERROR, res.code)
  }


  @Test
  fun test_fold(){
    val res = Result.of { Person("peter") }
    val name = res.fold({ it -> "Success Peter" },
            { it -> "Error Peter"   })

    Assert.assertTrue(res.success)
    Assert.assertEquals("success", res.msg)
    Assert.assertEquals(SUCCESS, res.code)
    Assert.assertEquals("Success Peter", name)
  }


  private fun ensure(result:ResultMsg<Any>, success:Boolean, code:Int, msg:String, data:Any?) {
    assert(result.success == success)
    assert(result.code == code)
    assert(result.msg == msg)
    if(result.success){
      assert(result.getOrElse { "" } == data)
    }
  }
}
