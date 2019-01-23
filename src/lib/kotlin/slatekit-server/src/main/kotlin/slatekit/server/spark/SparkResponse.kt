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

import spark.Response
import slatekit.common.content.Content
import slatekit.common.content.Doc
import slatekit.meta.Serialization

object SparkResponse {

    /**
     * Returns the value of the result as an html(string)
     */
    fun result(res: Response, result: slatekit.common.requests.Response<Any>): Any {
        return when (result.value) {
            is Content -> content(res, result, result.value as Content)
            is Doc -> file(res, result, result.value as Doc)
            else -> json(res, result)
        }
    }

    /**
     * Returns the value of the resulut as JSON.
     */
    fun json(res: Response, result: slatekit.common.requests.Response<Any>): String {
        res.status(result.code)
        res.type("application/json")
        val json = Serialization.json(true).serialize(result)
        return json
    }

    /**
     * Explicitly supplied content
     * Return the value of the result as a content with type
     */
    fun content(res: Response, result: slatekit.common.requests.Response<Any>, content: Content?): String {
        res.status(result.code)
        res.type(content?.tpe?.http ?: "text/plain")
        return content?.text ?: ""
    }

    /**
     * Returns the value of the result as a file document
     */
    fun file(res: Response, result: slatekit.common.requests.Response<Any>, doc: Doc): Any {
        res.status(result.code)
        val bytes = doc.content.toByteArray()
        val raw = res.raw()

        res.header("Content-Disposition", "attachment; filename=" + doc.name)
        // res.type("application/force-download")
        res.type(doc.tpe.http)
        raw.outputStream.write(bytes)
        raw.outputStream.flush()
        raw.outputStream.close()
        return res.raw()
    }
}
