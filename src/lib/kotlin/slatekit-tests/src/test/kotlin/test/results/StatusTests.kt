package test.results
import org.junit.Assert
import org.junit.Test
import kiit.results.*

class StatusTests {

    @Test
    fun can_build_basic_passed_category(){
        val status = Passed.Succeeded("SUCCESS", 1, "success")
        Assert.assertEquals("SUCCESS", status.name)
        Assert.assertEquals(1, status.code)
        Assert.assertEquals("success", status.desc)
        Assert.assertEquals(true, status.success)
    }


    @Test
    fun can_build_basic_failed_category(){
        fun validate(status:Failed, name:String, code:Int, desc:String) {
            Assert.assertEquals(name, status.name)
            Assert.assertEquals(code, status.code)
            Assert.assertEquals(desc, status.desc)
            Assert.assertEquals(false, status.success)
        }
        validate(Failed.Ignored("IGNORED", 2, "ignored"), "IGNORED", 2, "ignored")
        validate(Failed.Invalid("INVALID", 3, "invalid"), "INVALID", 3, "invalid")
        validate(Failed.Denied ("DENIED" , 4, "denied" ), "DENIED" , 4, "denied" )
        validate(Failed.Errored("ERRORED", 5, "errored"), "ERRORED", 5, "errored")
        validate(Failed.Unknown("UNKNOWN", 6, "unknown"), "UNKNOWN", 6, "unknown")
    }


    @Test
    fun can_copy_values(){
        fun check(status: Status, code:Int, msg:String){
            Assert.assertEquals(code, status.code)
            Assert.assertEquals(msg, status.desc)
        }
        val status = Passed.Succeeded("SUCCESS",1, "success")
        val statusGroup: Status = status
        check(statusGroup.copyAll("ok", 2), 2, "ok")
    }


    @Test
    fun can_convert_to_http() {
        fun checkHttp(status:Status, code:Int, msg:String) {
            val result = Codes.toHttp(status)
            Assert.assertEquals(result.first, code)
            Assert.assertEquals(result.second.desc, msg)
        }
        checkHttp(Codes.SUCCESS   , 200, "Success")
        checkHttp(Codes.CONFIRM   , 200, "Confirm")
        checkHttp(Codes.INVALID   , 400, "Invalid")
        checkHttp(Codes.ERRORED   , 500, "Errored")
    }


    @Test
    fun confirm_codes_values() {
        checkCode(Codes.SUCCESS   , 200001, "Success")
        checkCode(Codes.PENDING   , 200008, "Pending")
        checkCode(Codes.IGNORED   , 400001, "Ignored")
        checkCode(Codes.INVALID   , 400003, "Invalid")
        checkCode(Codes.DENIED    , 400004, "Denied" )
        checkCode(Codes.ERRORED   , 500007, "Errored")
        checkCode(Codes.UNEXPECTED, 500008, "Unexpected")
    }


    @Test
    fun confirm_group_types() {
        Assert.assertTrue(Codes.SUCCESS    is Passed.Succeeded )
        Assert.assertTrue(Codes.PENDING    is Passed.Pending   )
        Assert.assertTrue(Codes.IGNORED    is Failed.Ignored   )
        Assert.assertTrue(Codes.INVALID    is Failed.Invalid   )
        Assert.assertTrue(Codes.DENIED     is Failed.Denied    )
        Assert.assertTrue(Codes.ERRORED    is Failed.Errored   )
        Assert.assertTrue(Codes.UNEXPECTED is Failed.Unknown )
    }

    @Test
    fun can_build_with_options(){

        // This checks the Results.status method that builds a status
        // from optional msg and code. It is somewhat optimized to
        // avoid creating new instances of Status unless the information
        // is different than the default status supplied to the method.
        fun check(built: Status, code:Int, msg:String, original: Status, isSameInstance:Boolean) {
            if(isSameInstance) {
                Assert.assertEquals(built, original)
            }
            Assert.assertEquals(code, built.code)
            Assert.assertEquals(msg , built.desc )
        }
        val status = Codes.SUCCESS

        // Empty values
        check(Status.ofCode(null, null  , status), status.code, status.desc, status,true)
        check(Status.ofCode(""  , null  , status), status.code, status.desc, status, true)

        // Empty msg or code
        check(Status.ofCode(status.desc, null  , status), status.code, status.desc, status, true)
        check(Status.ofCode(null, status.code , status), status.code, status.desc, status, true)

        // Both values supplied but same
        check(Status.ofCode(status.desc, status.code , status), status.code, status.desc, status, true)

        // Diff values supplied
        check(Status.ofCode("abc", 2  , status), 2, "abc", status, false)
        check(Status.ofCode("abc", null  , status), status.code, "abc", status, false)
        check(Status.ofCode(null , 2  , status), 2, status.desc, status, false)

    }


    @Test
    fun can_build_from_custom_error(){
        val err:Result<Int, RegistrationError> = Failure(RegistrationError.InvalidEmail("abc@somewhere"))
        Assert.assertTrue(err is Failure)
        err.onFailure {
            Assert.assertEquals(it.field, "email")
            Assert.assertEquals(it.value, "abc@somewhere")
        }
    }


    private fun checkCode(Statuses: Status, expectedCode: Int, expectedMsg: String) {
        Assert.assertEquals(Statuses.code, expectedCode)
        Assert.assertEquals(Statuses.desc, expectedMsg)
    }
}


sealed class RegistrationError(val field:String) {
    abstract val value:String

    data class InvalidEmail  (override val value:String): RegistrationError("email")
    data class InvalidPhone  (override val value:String): RegistrationError("phone")
    data class DuplicateUser (override val value:String): RegistrationError("name")
    data class ReservedName  (override val value:String): RegistrationError("name")
}