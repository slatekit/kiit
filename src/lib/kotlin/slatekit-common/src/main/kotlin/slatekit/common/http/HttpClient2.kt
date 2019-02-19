package slatekit.common.http

import okhttp3.*
import java.io.IOException

/**
 * Very Simple API for making Http calls that uses OkHTTP internally.
 */
class HttpClient2 {

    /**
     * Media types for posting data
     */
    private val JSON = MediaType.parse("application/json charset=utf-8")


    /**
     * Performs an HTTP get operation and supplies the response back in the Callback
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param tag         : Tag to use as as "correlation id"
     * @param converter   : Converter to deserialize the response
     * @param callback    : The callback to call when the response is available.
     * @param <T>         : The datatype of the expected result
     */
    fun <T>get(route:String,
    headers:Map<String,String>,
    queryParams:Map<String,String>,
    tag:String,
    callback:(T) -> Unit)
    {
        val client =  OkHttpClient()
        val url = buildUrl(route, queryParams)
        val headerbuild = buildHeaders(headers, tag)
        val request =  Request.Builder().url(url).headers(headerbuild).build()

        client.newCall(request).enqueue( object: Callback {
            override fun onResponse(call: Call?, response: Response) {

            }

            override fun onFailure(call: Call?, ex: IOException) {

            }
        })
    }


    /**
     * Performs an HTTP post operation and supplies the response back in the Callback
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param dataParams  : Post/Entity parameters to use
     * @param tag         : Tag to use as as "correlation id"
     * @param converter   : Converter to deserialize the response
     * @param callback    : The callback to call when the response is available.
     * @param <T>         : The datatype of the expected result
     */
    fun <T>post(route:String,
                headers:Map<String,String>,
                queryParams:Map<String,String>,
                dataParams:Map<String, Any>,
                tag:String,
                callback:(T) -> Unit)
    {
        this.send("post", route, headers, queryParams, dataParams, tag, callback)
    }


    /**
     * Performs an HTTP patch operation and supplies the response back in the Callback
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param dataParams  : Post/Entity parameters to use
     * @param tag         : Tag to use as as "correlation id"
     * @param converter   : Converter to deserialize the response
     * @param callback    : The callback to call when the response is available.
     * @param <T>         : The datatype of the expected result
     */
    fun <T>patch(route:String,
                 headers:Map<String,String>,
                 queryParams:Map<String,String>,
                 dataParams:Map<String, Any>,
                 tag:String,
                 callback:(T) -> Unit)
    {
        this.send("patch", route, headers, queryParams, dataParams, tag, callback)
    }


    /**
     * Performs an HTTP put operation and supplies the response back in the Callback
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param dataParams  : Post/Entity parameters to use
     * @param tag         : Tag to use as as "correlation id"
     * @param converter   : Converter to deserialize the response
     * @param callback    : The callback to call when the response is available.
     * @param <T>         : The datatype of the expected result
     */
    fun <T>put(route:String,
               headers:Map<String,String>,
               queryParams:Map<String,String>,
               dataParams:Map<String, Any>,
               tag:String,
               callback:(T) -> Unit)
    {
        this.send("put", route, headers, queryParams, dataParams, tag, callback)
    }


    /**
     * Performs an HTTP delete operation and supplies the response back in the Callback
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param dataParams  : Post/Entity parameters to use
     * @param tag         : Tag to use as as "correlation id"
     * @param converter   : Converter to deserialize the response
     * @param callback    : The callback to call when the response is available.
     * @param <T>         : The datatype of the expected result
     */
    fun <T>delete(route:String,
                  headers:Map<String,String>,
                  queryParams:Map<String,String>,
                  dataParams:Map<String, Any>,
                  tag:String,
                  callback:(T) -> Unit)
    {
        this.send("delete", route, headers, queryParams, dataParams, tag, callback)
    }


    /**
     * Performs an HTTP operation and supplies the response back in the Callback
     * @param verb        : The http verb
     * @param route       : The route endpoint ( without the base route )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param tag         : Tag to use as as "correlation id"
     * @param converter   : Converter to deserialize the response
     * @param callback    : The callback to call when the response is available.
     * @param <T>         : The datatype of the expected result
     */
    fun <T>send(verb:String,
                route:String,
                headers:Map<String,String>,
                queryParams:Map<String,String>,
                dataParams:Map<String, Any>,
                tag:String,
                callback:(T) -> Unit)
    {
        val client =  OkHttpClient()
        val url = buildUrl(route, queryParams)
        val headerbuild = buildHeaders(headers, tag)
        val builder =  Request.Builder().url(url).headers(headerbuild)
        val json = "" //converter.convertObjectToJson(dataParams)

        if(verb.equals("post")){
            builder.post(RequestBody.create(JSON, json))
        }
        else if(verb.equals("put")){
            builder.put(RequestBody.create(JSON, json))
        }
        else if(verb.equals("delete")){
            builder.delete(RequestBody.create(JSON, json))
        }
        else if(verb.equals("patch")){
            builder.patch(RequestBody.create(JSON, json))
        }

        val request = builder.build()
        client.newCall(request).enqueue( object: Callback {
            override fun onResponse(call: Call?, response: Response) {

            }

            override fun onFailure(call: Call?, ex: IOException) {

            }
        })
    }


    private fun buildUrl(endpoint:String, queryParams:Map<String, String>): HttpUrl {

        // Url path
        val urlBuilder =  HttpUrl.Builder().addPathSegments(endpoint)

        // Query params
        queryParams.forEach{ urlBuilder.addQueryParameter(it.key, it.value) }

        // Final url.
        val url = urlBuilder.build()
        return url
    }


    private fun buildHeaders(headers:Map<String, String>, tag:String):Headers {
        val httpHeaders = Headers.of(headers)
        return httpHeaders
    }
}