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
import slatekit.apis.ApiConstants
import slatekit.apis.support.JsonSupport
import slatekit.common.DateTime
import slatekit.common.Inputs
import slatekit.common.Request
import slatekit.meta.Serialization

object Requests {

    const val codeHandlerProcessed = 1000
    const val codeHandlerNotProcessed = 1001


    fun convertToQueueRequest(original: Request): String {
        // Convert the meta data to JSON
        val meta = convertMetaToJson(original.meta, original.meta.raw)
        val data = convertDataToJson(original.data, original.data.raw)
        val req = """
            {
                 "version"  : "${original.version}",
                 "path"     : "${original.path}",
                 "source"   : "${ApiConstants.SourceQueue}",
                 "verb"     : "${ApiConstants.SourceQueue}",
                 "tag"      : "${original.tag}",
                 "timestamp": "${DateTime.now().toStringNumeric()}",
                 "meta"     : ${meta},
                 "data"     : ${data}
            }
            """
        return req
    }


    private fun convertMetaToJson(source: Inputs, rawData:Any): String {
        // Convert the data to JSON
        // NOTE: It may already be in json
        val serializer = Serialization.json(true)
        val json = when(source) {
            is JsonSupport  -> source.toJson().toString()
            is Meta         -> serializer.serialize(source.toMap())
            else            -> serializer.serialize(rawData)
        }
        return json
    }


    private fun convertDataToJson(source: Inputs, rawData:Any): String {
        // Convert the data to JSON
        // NOTE: It may already be in json
        val serializer = Serialization.json(true)
        val json = when(source) {
            is JsonSupport  -> source.toJson().toString()
            else            -> serializer.serialize(rawData)
        }
        return json
    }
}
