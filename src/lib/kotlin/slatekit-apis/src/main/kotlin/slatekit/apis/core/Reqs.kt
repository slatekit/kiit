package slatekit.apis.core

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.apis.ApiConstants
import slatekit.common.DateTime
import slatekit.common.Request
import slatekit.common.Uris
import slatekit.common.encrypt.Encryptor
import java.io.File

/**
 * Helper utilities around requests
 */
object Reqs {

    fun fromFile(rawPath:String, enc: Encryptor?): Request {

        // Interpret the path as it could have slatekit supported URIS:
        // e.g. user:// | file:// | temp://
        val filePath = Uris.interpret(rawPath)

        // Parse json
        val content = File(filePath).readText()
        return fromJson(rawPath, ApiConstants.SourceFile, content, enc)
    }


    fun fromFileWithMeta(rawPath:String, keys: Map<String,String>, enc: Encryptor?): Request {

        // Interpret the path as it could have slatekit supported URIS:
        // e.g. user:// | file:// | temp://
        val filePath = Uris.interpret(rawPath)

        // Parse json
        val content = File(filePath).readText()
        val req = fromJson(rawPath, ApiConstants.SourceFile, content, enc)
        req.meta?.let { meta ->
            val jsonObj = meta.raw as JSONObject
            keys.forEach { pair ->
                jsonObj.put( pair.key, pair.value )
            }
        }
        return req
    }


    /**
     * The json structure for the request will match 1 to 1.
     * NOTE: The following fields can be omitted:
     *
     * 1. parts    : populated based on path. "area.api.action". e.g. [ "area", "api"  , "action" ]
     * 2. source   : defaulted to "file"
     * 3. verb     : defaulted to "file"
     * 4. timestamp: current time
     * 5. version  : defaulted to 1.0
     *
     * EXAMPLE:
     * {
     *      "path"     : "app.users.activate"
     *      "tag"      : "abcd",
     *      "meta"     : {
     *          "api-key" : "2DFAD90A0F624D55B9F95A4648D7619A"
     *      },
     *      "data"      : {
     *          "email" : "user1@abc.com",
     *          "phone" : "123-456-7890"
     *      }
     * }
     */
    fun fromJson(rawSource:Any, source:String, content:String, enc: Encryptor?): Request {

        val parser = JSONParser()
        val doc = parser.parse(content)
        val jsonRoot = doc as JSONObject

        // Meta
        val hasVersion = jsonRoot.containsKey("version")
        val version = if(hasVersion)jsonRoot.get("version") as String else ApiConstants.Version
        val path = jsonRoot.get("path") as String
        val tag = jsonRoot.get("tag") as String

        val jsonData = jsonRoot.get("data") as JSONObject
        val jsonMeta = jsonRoot.get("meta") as JSONObject

        return Request(
                version = version,
                path    = path,
                parts   = path.split("."),
                source  = source,
                verb    = source,
                meta    = Meta(rawSource, jsonMeta, enc),
                data    = Params(rawSource, ApiConstants.SourceFile, true, enc, jsonData),
                raw     = rawSource,
                tag     = tag,
                timestamp = DateTime.now()
        )
    }
}