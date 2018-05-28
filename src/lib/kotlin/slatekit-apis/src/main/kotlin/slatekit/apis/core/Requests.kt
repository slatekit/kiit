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

import slatekit.apis.ApiConstants
import slatekit.common.DateTime
import slatekit.common.Request
import slatekit.meta.Serialization

object Requests {

    val codeHandlerProcessed = 1000
    val codeHandlerNotProcessed = 1001

    fun convertToQueueRequest(original: Request): String {
        val serializer = Serialization.json(true)
        val json = serializer.serialize(original.data.raw)
        val meta = original.meta.raw
        val req = """
            {
                 "version"  : "${original.version}",
                 "path"     : "${original.path}",
                 "source"   : "${ApiConstants.SourceQueue}",
                 "verb"     : "${ApiConstants.SourceQueue}",
                 "tag"      : "${original.tag}",
                 "timestamp": "${DateTime.now().toStringNumeric()}",
                 "meta"     : ${meta},
                 "data"      : ${json}
            }
            """
        return req
    }
}
