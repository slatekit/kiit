/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.server.spark

import slatekit.common.Result
import slatekit.common.serialization.SerializerJson
import spark.Response

object HttpResponse {

    /**
     * Returns the value of the resulut as JSON.
     */
    fun json(res: Response, result: Result<Any>): String {
        res.status(result.code)
        res.type("application/json")
        val json = SerializerJson().serialize(result)
        return json
    }


    /**
     * Returns the value of the result as an html(string)
     */
    fun html(res: Response, result: Result<Any>): String {
        res.status(result.code)
        res.type("text/html")
        return result.value?.toString() ?: ""
    }
}