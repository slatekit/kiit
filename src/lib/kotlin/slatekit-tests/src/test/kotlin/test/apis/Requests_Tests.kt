package test.apis

import org.junit.Assert
import org.junit.Test
import org.threeten.bp.ZoneId
import slatekit.apis.core.Requests
import slatekit.common.CommonRequest
import slatekit.common.DateTime
import slatekit.common.requests.InputArgs
import slatekit.common.requests.Source

class Requests_Tests {

    @Test
    fun can_convert_to_json() {
        val timestamp = DateTime.of(2018, 1, 27, 9, 30, 45, 0, ZoneId.of("UTC"))
        val sampleDate = timestamp
        val req = CommonRequest(
                path = "samples.types2.loadBasicTypes",
                parts = listOf("samples", "types2", "loadBasicTypes"),
                source = Source.Web,
                verb = "post",
                data = InputArgs(mapOf(
                        "s" to "user1@abc.com",
                        "b" to true,
                        "i" to 123,
                        "d" to sampleDate
                )),
                meta = InputArgs(mapOf(
                        "api-key" to "2DFAD90A0F624D55B9F95A4648D7619A",
                        "token" to "mmxZr5tkfMUV5/duU2rhHg"
                )),
                raw = null,
                output = null,
                tag = "tag123",
                version = "1.1",
                timestamp = timestamp
        )
        val json = Requests.toJsonObject(req, null, "aws", "queue")
        Assert.assertEquals("1.1"           , json.get("version"))
        Assert.assertEquals("aws"           , json.get("source"))
        Assert.assertEquals("queue"         , json.get("verb"))
        Assert.assertEquals("tag123"        , json.get("tag"))
        Assert.assertEquals("2018-01-27T09:30:45Z", json.get("timestamp"))
        Assert.assertEquals("samples.types2.loadBasicTypes", json.get("path"))

        val jsonStr = json.toString()
        println(jsonStr)
    }

    @Test
    fun can_convert_from_json() {
        val json = """
            {
                "path": "samples.types2.loadBasicTypes",
                "data": "{\"s\" : \"user1@abc.com\", \"b\" : true, \"i\" : 123, \"d\" : \"2018-01-27T09:30:45Z\"}",
                "meta": "{\"api-key\" : \"2DFAD90A0F624D55B9F95A4648D7619A\", \"token\" : \"mmxZr5tkfMUV5\/duU2rhHg\"}",
                "verb": "queue",
                "source": "aws",
                "tag": "tag123",
                "version": "1.1",
                "timestamp": "2018-01-27T09:30:45Z"
            }
        """.trimIndent()
        val timestamp = DateTime.of(2018, 1, 27, 9, 30, 45, 0, ZoneId.of("Z"))
        val request = Requests.fromJson(json, "cloud", "post", null, null, null)
        Assert.assertEquals("1.1"           , request.version)
        Assert.assertEquals("other"         , request.source.id)
        Assert.assertEquals("cloud"         , (request.source as Source.Other).name)
        Assert.assertEquals("post"          , request.verb)
        Assert.assertEquals("tag123"        , request.tag)
        Assert.assertEquals(timestamp                , request.timestamp)
        Assert.assertEquals("samples.types2.loadBasicTypes", request.path)
        Assert.assertEquals("2DFAD90A0F624D55B9F95A4648D7619A", request.meta.getString("api-key"))
        Assert.assertEquals("mmxZr5tkfMUV5/duU2rhHg", request.meta.getString("token"))
        Assert.assertEquals("user1@abc.com", request.data.getString("s"))
    }
}