/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.apis.core

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.apis.ApiConstants
import slatekit.apis.support.JsonSupport
import slatekit.common.DateTime
import slatekit.common.Inputs
import slatekit.common.Metadata
import slatekit.common.requests.Request
import slatekit.common.Uris
import slatekit.common.encrypt.Encryptor
import slatekit.meta.Serialization
import java.io.File

object Requests {

    /**
     * Loads a file from a file with the existing meta data supplied.
     */
    fun fromFileWithMeta(route: String, rawPath: String, keys: Map<String, String>? = null, enc: Encryptor? = null): Request {

        // Interpret the path as it could have slatekit supported URIS:
        // e.g. user:// | file:// | temp://
        val filePath = Uris.interpret(rawPath)

        // Parse json
        val content = File(filePath).readText()
        val req = fromJson(content, ApiConstants.SourceFile, ApiConstants.SourceFile, route, rawPath, enc)
        val jsonObj = req.meta.raw as JSONObject
        keys?.forEach { pair ->
            jsonObj.put(pair.key, pair.value)
        }
        return req
    }

    /**
     * Loads The json structure for the request will match 1 to 1.
     * NOTE: The following fields can be omitted:
     *
     * 1. path     : the 3 part route structure, as it could be specified on command line
     * 2. parts    : populated based on path. "area.api.action". e.g. [ "area", "api"  , "action" ]
     * 3. source   : defaulted to "file"
     * 4. verb     : defaulted to "file"
     * 5. timestamp: current time
     * 6. version  : defaulted to 1.0
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
    fun fromJson(jsonContent: String, sourceOverride: String? = null, verbOverride: String? = null, route: String? = null, rawSource: Any? = null, enc: Encryptor? = null): Request {

        val parser = JSONParser()
        val doc = parser.parse(jsonContent)
        val jsonRoot = doc as JSONObject

        // Core fields
        val hasVersion = jsonRoot.containsKey("version")
        val version = if (hasVersion)jsonRoot.get("version") as String else ApiConstants.Version
        val path = route ?: jsonRoot.get("path") as String
        val tag = jsonRoot.get("tag") as String
        val source = jsonRoot.get("source") as String
        val verb = jsonRoot.get("verb") as String

        // Meta / Data
        val jsonData = jsonRoot.get("data") as JSONObject
        val jsonMeta = jsonRoot.get("meta") as JSONObject
        val sep = if (path.contains("/")) "/" else "."

        return Request(
                version = version,
                path = path,
                parts = path.split(sep),
                source = sourceOverride ?: source,
                verb = verbOverride ?: verb,
                meta = Meta(rawSource ?: "json", jsonMeta, enc),
                data = Params(rawSource ?: "json", ApiConstants.SourceFile, true, enc, jsonData),
                raw = rawSource,
                tag = tag,
                timestamp = DateTime.now()
        )
    }

    /**
     * Converts the request to JSON and encrypts the content if the encryptor is supplied
     */
    fun toJson(req: Request, enc: Encryptor? = null, source: String? = null, verb: String? = null): String {
        // Convert the meta data to JSON
        val meta = convertMetaToJson(req.meta, req.meta.raw)
        val data = convertDataToJson(req.data, req.data.raw)
        val finalMeta = enc?.encrypt(meta) ?: meta
        val finalData = enc?.encrypt(data) ?: data
        val finalSource = source ?: req.source
        val finalVerb = verb ?: req.verb
        val finalPath = req.parts.joinToString(".")
        val json = """
            {
                 "version"  : "${req.version}",
                 "path"     : "$finalPath",
                 "source"   : "$finalSource",
                 "verb"     : "$finalVerb",
                 "tag"      : "${req.tag}",
                 "timestamp": "${req.timestamp}",
                 "meta"     : $finalMeta,
                 "data"     : $finalData
            }
            """
        return json
    }

    /**
     * Converts the request to JSON designated as a request from a Queue ( source = queue )
     */
    fun toJsonAsQueued(req: Request): String {
        return toJson(req, null, ApiConstants.SourceQueue, ApiConstants.SourceQueue)
    }

    private fun convertMetaToJson(source: Inputs, rawData: Any): String {
        // Convert the data to JSON
        // NOTE: It may already be in json
        val serializer = Serialization.json(true)
        val json = when (source) {
            is JsonSupport -> source.toJson().toString()
            is Metadata -> serializer.serialize(source.toMap())
            else -> serializer.serialize(rawData)
        }
        return json
    }

    private fun convertDataToJson(source: Inputs, rawData: Any): String {
        // Convert the data to JSON
        // NOTE: It may already be in json
        val serializer = Serialization.json(true)
        val json = when (source) {
            is JsonSupport -> source.toJson().toString()
            else -> serializer.serialize(rawData)
        }
        return json
    }
}
