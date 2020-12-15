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
package slatekit.apis.services

import java.io.File
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.apis.ApiConstants
import slatekit.apis.core.Data
import slatekit.apis.core.Meta
import slatekit.apis.support.JsonSupport
import slatekit.common.*
import slatekit.common.crypto.Encryptor
import slatekit.common.ext.toStringUtc
import slatekit.common.io.Uris
import slatekit.common.requests.CommonRequest
import slatekit.common.requests.Request
import slatekit.meta.Serialization

object Reqs {

    /**
     * Loads a file from a file with the existing meta data supplied.
     */
    fun fromFileWithMeta(route: String, rawPath: String, keys: Map<String, String>? = null, enc: Encryptor? = null): Request {

        // Interpret the path as it could have slatekit supported URIS:
        // e.g. user:// | file:// | temp://
        val filePath = Uris.interpret(rawPath)

        // Parse json
        val content = File(filePath).readText()
        val req = fromJson(content, Source.File.id, Source.File.id, route, rawPath, enc)
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
        val version = if (hasVersion)jsonRoot.get("version") as String else ApiConstants.version
        val path = route ?: jsonRoot.get("path") as String
        val tag = jsonRoot.get("tag") as String
        val source = jsonRoot.get("source") as String
        val verb = jsonRoot.get("verb") as String
        val timestamp = jsonRoot.get("timestamp") as String
        val time = DateTime.parse(timestamp)

        // Meta / Data
        val data = jsonRoot.get("data") as String
        val meta = jsonRoot.get("meta") as String
        val jsonData = JSONParser().parse(data) as JSONObject
        val jsonMeta = JSONParser().parse(meta) as JSONObject
        val sep = if (path.contains("/")) "/" else "."

        return CommonRequest(
            version = version,
            path = path,
            parts = path.split(sep),
            source = Source.parse(sourceOverride ?: source),
            verb = verbOverride ?: verb,
            meta = Meta(rawSource ?: "json", jsonMeta, enc),
            data = Data(rawSource ?: "json", Source.File.id, true, enc, jsonData),
            raw = rawSource,
            tag = tag,
            timestamp = time
        )
    }

    /**
     * Convert the request to a JSON string with optionally encrypted metadata and data
     * @param req : Request
     * @param enc : Encryption service to encrypt the meta and data
     * @param source : Overridable source to set on request ( e.g. queue if converted from web -> queue )
     * @param verb : Overridable verb to set on request ( e.g. queue if converted from web -> queue )
     */
    fun toJsonString(req: Request, enc: Encryptor? = null, source: String? = null, verb: String? = null): String {
        // Convert the meta data to JSON
        val meta = convertMetaToJson(req.meta, req.meta.raw)
        val data = convertDataToJson(req.data, req.data.raw)
        val root = toJsonObject(req, meta, data, enc, source, verb)
        val json = root.toJSONString()
        return json
    }

    /**
     * Convert the request to a JSON object with optionally encrypted metadata and data
     * @param req : Request
     * @param meta : Meta converted to a JSON string ( to be encrypted if encryptor is supplied )
     * @param data : Data converted to a JSON string ( to be encrypted if encryptor is supplied )
     * @param enc : Encryption service to encrypt the meta and data
     * @param source : Overridable source to set on request ( e.g. queue if converted from web -> queue )
     * @param verb : Overridable verb to set on request ( e.g. queue if converted from web -> queue )
     */
    fun toJsonObject(req: Request, enc: Encryptor? = null, source: String? = null, verb: String? = null): JSONObject {
        // Convert the meta data to JSON
        val meta = convertMetaToJson(req.meta, req.meta.raw)
        val data = convertDataToJson(req.data, req.data.raw)
        val root = toJsonObject(req, meta, data, enc, source, verb)
        return root
    }

    /**
     * Convert the request to a JSON object with optionally encrypted metadata and data
     * @param req : Request
     * @param meta : Meta converted to a JSON string ( to be encrypted if encryptor is supplied )
     * @param data : Data converted to a JSON string ( to be encrypted if encryptor is supplied )
     * @param enc : Encryption service to encrypt the meta and data
     * @param source : Overridable source to set on request ( e.g. queue if converted from web -> queue )
     * @param verb : Overridable verb to set on request ( e.g. queue if converted from web -> queue )
     */
    private fun toJsonObject(req: Request, meta: String, data: String, enc: Encryptor? = null, source: String? = null, verb: String? = null): JSONObject {
        val finalSource = source ?: req.source.id
        val finalVerb = verb ?: req.verb
        val finalPath = req.parts.joinToString(".")

        // Basics
        val root = JSONObject()
        root["version"] = req.version
        root["path"] = finalPath
        root["source"] = finalSource
        root["verb"] = finalVerb
        root["tag"] = req.tag
        root["timestamp"] = req.timestamp.toStringUtc()

        // Encrypt
        val finalMeta = enc?.encrypt(meta) ?: meta
        val finalData = enc?.encrypt(data) ?: data
        root["meta"] = finalMeta
        root["data"] = finalData
        return root
    }

    /**
     * Converts the request to JSON designated as a request from a Queue ( source = queue )
     */
    fun toJsonAsQueued(req: Request): String {
        return toJsonString(req, null, Source.Queue.id, Source.Queue.id)
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
