
import org.junit.Assert
import org.junit.Test
import slatekit.results.*
import slatekit.results.Status
import slatekit.results.StatusCodes
import slatekit.results.StatusGroup

class StatusTests {

    @Test
    fun can_build_basic(){
        val status = StatusGroup.Succeeded(1, "success")
        Assert.assertEquals(1, status.code)
        Assert.assertEquals("success", status.msg)
    }


    @Test
    fun can_copy_values(){
        fun check(status: Status, code:Int, msg:String){
            Assert.assertEquals(code, status.code)
            Assert.assertEquals(msg, status.msg)
        }
        val status = StatusGroup.Succeeded(1, "success")
        val statusGroup: StatusGroup = status
        check(statusGroup.copyMsg("ok"), status.code, "ok")
        check(statusGroup.copyAll("ok", 2), 2, "ok")
    }


    @Test
    fun can_convert_to_http() {
        fun checkHttp(status:Status, code:Int, msg:String) {
            val result = StatusCodes.toHttp(status)
            Assert.assertEquals(result.first, code)
            Assert.assertEquals(result.second.msg, msg)
        }
        checkHttp(StatusCodes.SUCCESS   , 200, "Success")
        checkHttp(StatusCodes.CONFIRM   , 200, "Confirm")
        checkHttp(StatusCodes.INVALID   , 400, "Invalid")
        checkHttp(StatusCodes.ERRORED   , 500, "Errored")
    }


    @Test
    fun confirm_codes_values() {
        checkCode(StatusCodes.SUCCESS   , 1001, "Success")
        checkCode(StatusCodes.PENDING   , 1008, "Pending")
        checkCode(StatusCodes.IGNORED   , 2001, "Ignored")
        checkCode(StatusCodes.INVALID   , 2003, "Invalid")
        checkCode(StatusCodes.DENIED    , 2004, "Denied" )
        checkCode(StatusCodes.ERRORED   , 3007, "Errored")
        checkCode(StatusCodes.UNEXPECTED, 3008, "Unexpected")
    }


    @Test
    fun confirm_group_types() {
        Assert.assertTrue(StatusCodes.SUCCESS    is StatusGroup.Succeeded )
        Assert.assertTrue(StatusCodes.PENDING    is StatusGroup.Pending   )
        Assert.assertTrue(StatusCodes.IGNORED    is StatusGroup.Ignored   )
        Assert.assertTrue(StatusCodes.INVALID    is StatusGroup.Invalid   )
        Assert.assertTrue(StatusCodes.DENIED     is StatusGroup.Denied    )
        Assert.assertTrue(StatusCodes.ERRORED    is StatusGroup.Errored   )
        Assert.assertTrue(StatusCodes.UNEXPECTED is StatusGroup.Unhandled )
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
        val status = StatusCodes.SUCCESS

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