package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.apis.ApiConstants
import slatekit.apis.core.Requests
import slatekit.common.DateTime
import slatekit.common.requests.InputArgs
import slatekit.common.requests.Request

class RequestTests {

    val sampleRequest = Request(
            "app.users.activate",
            listOf("app", "users", "activate"),
            ApiConstants.SourceWeb,
            "get",
            InputArgs(mapOf("a" to true, "b" to 2, "c" to 3.0, "email" to "user1@abc.com")),
            InputArgs(mapOf("token" to "mmxZr5tkf\\MUV5duU2rhHg", "api-key" to "2DFAD90A0F624D55B9F95A4648D7619A")),
            null,
            "csv",
            "abc123",
            "1.1",
            DateTime.of(2018, 7, 1, 10, 30, 45)
    )


    @Test
    fun can_serialize_to_json(){
        val actual = Requests.toJson(sampleRequest)
        val expected = """
            {
                 "version"  : "1.1",
                 "path"     : "app.users.activate",
                 "source"   : "web",
                 "verb"     : "get",
                 "tag"      : "abc123",
                 "timestamp": "2018-07-01T10:30:45-04:00[America/New_York]",
                 "meta"     : {"token" : "mmxZr5tkf\\MUV5duU2rhHg", "api-key" : "2DFAD90A0F624D55B9F95A4648D7619A"},
                 "data"     : {"a" : true, "b" : 2, "c" : 3.0, "email" : "user1@abc.com"}
            }
            """
        Assert.assertEquals(expected, actual)
    }



    @Test
    fun can_serialize_to_json_queued(){
        val actual = Requests.toJsonAsQueued(sampleRequest)
        val expected = """
            {
                 "version"  : "1.1",
                 "path"     : "app.users.activate",
                 "source"   : "queue",
                 "verb"     : "queue",
                 "tag"      : "abc123",
                 "timestamp": "2018-07-01T10:30:45-04:00[America/New_York]",
                 "meta"     : {"token" : "mmxZr5tkf\\MUV5duU2rhHg", "api-key" : "2DFAD90A0F624D55B9F95A4648D7619A"},
                 "data"     : {"a" : true, "b" : 2, "c" : 3.0, "email" : "user1@abc.com"}
            }
            """
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun can_serialize_from_json() {
        val json = """
            {
                 "version"  : "1.1",
                 "path"     : "app.users.activate",
                 "source"   : "web",
                 "verb"     : "get",
                 "tag"      : "abc123",
                 "timestamp": "2018-07-01T10:30:45-04:00[America/New_York]",
                 "meta"     : {"token" : "mmxZr5tkf\\MUV5duU2rhHg", "api-key" : "2DFAD90A0F624D55B9F95A4648D7619A"},
                 "data"     : {"a" : true, "b" : 2, "c" : 3.0, "email" : "user1@abc.com"}
            }
            """
        val req = Requests.fromJson(json)
        assert( req.path == "app.users.activate")
        assert( req.parts == listOf("app", "users", "activate"))
        assert( req.area == "app")
        assert( req.name == "users")
        assert( req.action == "activate")
        assert( req.source == ApiConstants.SourceWeb)
        assert( req.verb == "get")
        assert( req.fullName == "app.users.activate")
        assert( req.tag == "abc123")

        assert( req.meta.containsKey("api-key")  )
        assert( req.meta.containsKey("token"))
        assert( req.meta.getString("api-key") == "2DFAD90A0F624D55B9F95A4648D7619A")
        assert( req.meta.getString("token") == "mmxZr5tkf\\MUV5duU2rhHg")
    }


    @Test
    fun can_serialize_from_json_with_override() {
        val json = """
            {
                 "version"  : "1.1",
                 "path"     : "app.users.activate",
                 "source"   : "web",
                 "verb"     : "get",
                 "tag"      : "abc123",
                 "timestamp": "2018-07-01T10:30:45-04:00[America/New_York]",
                 "meta"     : {"token" : "mmxZr5tkf\\MUV5duU2rhHg", "api-key" : "2DFAD90A0F624D55B9F95A4648D7619A"},
                 "data"     : {"a" : true, "b" : 2, "c" : 3.0, "email" : "user1@abc.com"}
            }
            """
        val req = Requests.fromJson(json, ApiConstants.SourceQueue, ApiConstants.SourceQueue)
        assert( req.path == "app.users.activate")
        assert( req.parts == listOf("app", "users", "activate"))
        assert( req.area == "app")
        assert( req.name == "users")
        assert( req.action == "activate")
        assert( req.source == ApiConstants.SourceQueue)
        assert( req.verb == ApiConstants.SourceQueue)
        assert( req.fullName == "app.users.activate")
        assert( req.tag == "abc123")

        assert( req.meta.containsKey("api-key")  )
        assert( req.meta.containsKey("token"))
        assert( req.meta.getString("api-key") == "2DFAD90A0F624D55B9F95A4648D7619A")
        assert( req.meta.getString("token") == "mmxZr5tkf\\MUV5duU2rhHg")
    }
}