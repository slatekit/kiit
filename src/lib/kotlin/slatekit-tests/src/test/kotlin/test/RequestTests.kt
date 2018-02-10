package test

import org.junit.Test
import slatekit.apis.ApiConstants
import slatekit.apis.core.Reqs
import slatekit.common.Success
import test.common.MyEncryptor

class RequestTests {

    @Test
    fun can_build_from_json() {
        val json = """
        {
             "version"  : "1.0",
             "path"     : "app.users.activate"
             "tag"      : "abcd",
             "meta"     : {
                 "api-key" : "2DFAD90A0F624D55B9F95A4648D7619A",
                 "token"   : "mmxZr5tkfMUV5/duU2rhHg"
             },
             "data"      : {
                 "email" : "user1@abc.com",
                 "phone" : "123-456-7890"
             }
        }
        """
        val req = Reqs.fromJson(null,"json", ApiConstants.SourceFile, json, MyEncryptor)
        assert( req.path == "app.users.activate")
        assert( req.parts == listOf("app", "users", "activate"))
        assert( req.source == ApiConstants.SourceFile)
        assert( req.area == "app")
        assert( req.name == "users")
        assert( req.action == "activate")
        assert( req.verb == ApiConstants.SourceFile)
        assert( req.fullName == "app.users.activate")
        assert( req.tag == "abcd")

        assert( req.meta?.containsKey("api-key") ?: false )
        assert( req.meta?.containsKey("token") ?: false )
        assert( req.meta!!.getString("api-key") == "2DFAD90A0F624D55B9F95A4648D7619A")
        assert( req.meta!!.getString("token") == "mmxZr5tkfMUV5/duU2rhHg")
    }
}