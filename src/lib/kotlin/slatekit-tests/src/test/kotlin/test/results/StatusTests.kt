package test.results
import org.junit.Assert
import org.junit.Test
import slatekit.results.*
import slatekit.results.Status
import slatekit.results.Codes

class StatusTests {

    @Test
    fun can_build_basic(){
        val status = Status.Succeeded(1, "success")
        Assert.assertEquals(1, status.code)
        Assert.assertEquals("success", status.msg)
    }


    @Test
    fun can_copy_values(){
        fun check(status: Status, code:Int, msg:String){
            Assert.assertEquals(code, status.code)
            Assert.assertEquals(msg, status.msg)
        }
        val status = Status.Succeeded(1, "success")
        val statusGroup: Status = status
        check(statusGroup.copyMsg("ok"), status.code, "ok")
        check(statusGroup.copyAll("ok", 2), 2, "ok")
    }


    @Test
    fun can_convert_to_http() {
        fun checkHttp(status:Status, code:Int, msg:String) {
            val result = Codes.toHttp(status)
            Assert.assertEquals(result.first, code)
            Assert.assertEquals(result.second.msg, msg)
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
        Assert.assertTrue(Codes.SUCCESS    is Status.Succeeded )
        Assert.assertTrue(Codes.PENDING    is Status.Pending   )
        Assert.assertTrue(Codes.IGNORED    is Status.Ignored   )
        Assert.assertTrue(Codes.INVALID    is Status.Invalid   )
        Assert.assertTrue(Codes.DENIED     is Status.Denied    )
        Assert.assertTrue(Codes.ERRORED    is Status.Errored   )
        Assert.assertTrue(Codes.UNEXPECTED is Status.Unexpected )
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
            Assert.assertEquals(msg , built.msg )
        }
        val status = Codes.SUCCESS

        // Empty values
        check(Result.status(null, null  , status), status.code, status.msg, status,true)
        check(Result.status(""  , null  , status), status.code, status.msg, status, true)

        // Empty msg or code
        check(Result.status(status.msg, null  , status), status.code, status.msg, status, true)
        check(Result.status(null, status.code , status), status.code, status.msg, status, true)

        // Both values supplied but same
        check(Result.status(status.msg, status.code , status), status.code, status.msg, status, true)

        // Diff values supplied
        check(Result.status("abc", 2  , status), 2, "abc", status, false)
        check(Result.status("abc", null  , status), status.code, "abc", status, false)
        check(Result.status(null , 2  , status), 2, status.msg, status, false)

    }


    private fun checkCode(Statuses: Status, expectedCode: Int, expectedMsg: String) {
        Assert.assertEquals(Statuses.code, expectedCode)
        Assert.assertEquals(Statuses.msg, expectedMsg)
    }
}