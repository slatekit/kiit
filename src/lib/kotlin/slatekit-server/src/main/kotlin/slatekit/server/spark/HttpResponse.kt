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
import slatekit.common.*
import slatekit.meta.Serialization


object HttpResponse {

    /**
     * Returns the value of the result as an html(string)
     */
    fun result(res: Response, result: Result<Any>): Any {
        return when(result.value){
            is Content -> content(res, result as Result<Content>)
            is Doc     -> file(res, result as Result<Doc>)
            else       -> json( res, result)
        }
    }


    /**
     * Returns the value of the resulut as JSON.
     */
    fun json(res: Response, result: Result<Any>): String {
        res.status(result.code)
        res.type("application/json")
        val json = Serialization.json(true).serialize(result)
        return json
    }


    /**
     * Explicitly supplied content
     * Return the value of the result as a content with type
     */
    fun content(res: Response, result: Result<Content>): String {
        res.status(result.code)
        res.type(result.value?.tpe?.http ?: "text/plain")
        return result.value?.text ?: ""
    }


    /**
     * Returns the value of the result as a file document
     */
    fun file(res: Response, result: Result<Doc>): Any {
        res.status(result.code)
        val doc = result.value!!
        val bytes = doc.content.toByteArray()
        val raw = res.raw()

        res.header("Content-Disposition", "attachment; filename=" + doc.name)
        //res.type("application/force-download")
        res.type(result.value!!.tpe.http)
        raw.outputStream.write(bytes)
        raw.outputStream.flush()
        raw.outputStream.close()
        return res.raw()
    }
}