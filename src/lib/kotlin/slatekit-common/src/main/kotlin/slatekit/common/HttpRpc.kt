package slatekit.common

import okhttp3.*
import java.io.IOException

/**
 * Very Simple API for making Http calls that uses OkHTTP internally.
 */
class HttpRpc {

    /**
     * Media types for posting data
     */
    private val jsonType = MediaType.parse("application/json charset=utf-8")


    /**
     * Performs an HTTP get operation and supplies the response back in the Callback
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param callback    : The callback to call when the response is available.
     * @param <T>         : The datatype of the expected result
     */
    fun get(route: String,
            headers: Map<String, String>,
            queryParams: Map<String, String>,
            callback: (Result<Response, IOException>) -> Unit) {
        val client = OkHttpClient()
        val url = buildUrl(route, queryParams)
        val headerbuild = buildHeaders(headers)
        val request = Request.Builder().url(url).headers(headerbuild).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                callback(Success(response))
            }

            override fun onFailure(call: Call?, ex: IOException) {
                callback(Failure(ex))
            }
        })
    }


    /**
     * Performs an HTTP post operation and supplies the response back in the Callback
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param dataParams  : Post/Entity parameters to use
     * @param callback    : The callback to call when the response is available.
     */
    fun post(route: String,
             headers: Map<String, String>,
             queryParams: Map<String, String>,
             dataParams: Map<String, Any>,
             callback: (Result<Response, IOException>) -> Unit) {
        this.send("post", route, headers, queryParams, dataParams, callback)
    }


    /**
     * Performs an HTTP patch operation and supplies the response back in the Callback
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param dataParams  : Post/Entity parameters to use
     * @param callback    : The callback to call when the response is available.
     */
    fun patch(route: String,
              headers: Map<String, String>,
              queryParams: Map<String, String>,
              dataParams: Map<String, Any>,
              callback: (Result<Response, IOException>) -> Unit) {
        this.send("patch", route, headers, queryParams, dataParams, callback)
    }


    /**
     * Performs an HTTP put operation and supplies the response back in the Callback
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param dataParams  : Post/Entity parameters to use
     * @param callback    : The callback to call when the response is available.
     */
    fun put(route: String,
            headers: Map<String, String>,
            queryParams: Map<String, String>,
            dataParams: Map<String, Any>,
            callback: (Result<Response, IOException>) -> Unit) {
        this.send("put", route, headers, queryParams, dataParams, callback)
    }


    /**
     * Performs an HTTP delete operation and supplies the response back in the Callback
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param dataParams  : Post/Entity parameters to use
     * @param callback    : The callback to call when the response is available.
     */
    fun delete(route: String,
               headers: Map<String, String>,
               queryParams: Map<String, String>,
               dataParams: Map<String, Any>,
               callback: (Result<Response, IOException>) -> Unit) {
        this.send("delete", route, headers, queryParams, dataParams, callback)
    }


    /**
     * Performs an HTTP operation and supplies the response back in the Callback
     * @param verb        : The http verb
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param callback    : The callback to call when the response is available.
     */
    fun send(verb: String,
             route: String,
             headers: Map<String, String>,
             queryParams: Map<String, String>,
             dataParams: Map<String, Any>,
             callback: (Result<Response, IOException>) -> Unit) {
        val client = OkHttpClient()
        val url = buildUrl(route, queryParams)
        val headerbuild = buildHeaders(headers)
        val builder = Request.Builder().url(url).headers(headerbuild)
        val json = "" //converter.convertObjectToJson(dataParams)
        val method = verb.toUpperCase()

        val request = when (method) {
            "POST" -> builder.post(RequestBody.create(jsonType, json)).build()
            "DELETE" -> builder.delete(RequestBody.create(jsonType, json)).build()
            "PUT" -> builder.put(RequestBody.create(jsonType, json)).build()
            "PATCH" -> builder.patch(RequestBody.create(jsonType, json)).build()
            else -> builder.post(RequestBody.create(jsonType, json)).build()
        }
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                callback(Success(response))
            }

            override fun onFailure(call: Call?, ex: IOException) {
                callback(Failure(ex))
            }
        })
    }


    private fun buildUrl(endpoint: String, queryParams: Map<String, String>): HttpUrl {

        // Url path
        val urlBuilder = HttpUrl.Builder().addPathSegments(endpoint)

        // Query params
        queryParams.forEach { urlBuilder.addQueryParameter(it.key, it.value) }

        // Final url.
        val url = urlBuilder.build()
        return url
    }


    private fun buildHeaders(headers: Map<String, String>): Headers {
        return Headers.of(headers)
    }
}