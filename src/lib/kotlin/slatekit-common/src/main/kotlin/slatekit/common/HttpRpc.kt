package slatekit.common

import okhttp3.*
import java.io.IOException

/**
 * Minimal Http Client for making RPC like calls over Http
 * Uses OkHttp under the hood.
 */
class HttpRPC(val serializer:((Any?) -> String)? = null) {

    enum class Method(val value:Int) {
        Get(0),
        Post(1),
        Put(2),
        Patch(3),
        Delete(4)
    }


    enum class Encoding(val value:Int) {
        None(0),
        Form(1),
        Url (2),
        Raw (3),
        Json(4)
    }


    sealed class AuthInfo {
        data class Basic(val name:String, val pswd:String): AuthInfo()
        data class Bearer(val token:String): AuthInfo()
    }


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
            headerParams: Map<String, String>? = null,
            queryParams: Map<String, String>? = null,
            creds: AuthInfo? = null,
            callback: (Result<Response, IOException>) -> Unit) {
        val client = OkHttpClient()
        val url = buildUrl(route, queryParams)
        val headers = buildHeaders(headerParams)
        val builder = Request.Builder().url(url).headers(headers)

        // AUTHORIZATION
        when(creds) {
            is AuthInfo.Basic  -> builder.addHeader("Authorization", Credentials.basic(creds.name, creds.pswd))
            is AuthInfo.Bearer -> builder.addHeader("Authorization", "Bearer " + creds.token)
            else               -> {}
        }

        // REQUEST
        val request = builder.build()

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
             headers: Map<String, String>? = null,
             queryParams: Map<String, String>? = null,
             dataParams: Map<String, Any>? = null,
             credentials: AuthInfo? = null,
             encoding: Encoding = Encoding.Json,
             callback: (Result<Response, IOException>) -> Unit) {
        this.send(Method.Post, route, headers, queryParams, dataParams, credentials, encoding, callback)
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
              headers: Map<String, String>? = null,
              queryParams: Map<String, String>? = null,
              dataParams: Map<String, Any>? = null,
              credentials: AuthInfo? = null,
              encoding: Encoding = Encoding.Json,
              callback: (Result<Response, IOException>) -> Unit) {
        this.send(Method.Patch, route, headers, queryParams, dataParams, credentials, encoding, callback)
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
            headers: Map<String, String>? = null,
            queryParams: Map<String, String>? = null,
            dataParams: Map<String, Any>? = null,
            credentials: AuthInfo? = null,
            encoding: Encoding = Encoding.Json,
            callback: (Result<Response, IOException>) -> Unit) {
        this.send(Method.Put, route, headers, queryParams, dataParams, credentials, encoding, callback)
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
               headers: Map<String, String>? = null,
               queryParams: Map<String, String>? = null,
               dataParams: Map<String, Any>? = null,
               credentials: AuthInfo? = null,
               encoding: Encoding = Encoding.Json,
               callback: (Result<Response, IOException>) -> Unit) {
        this.send(Method.Delete, route, headers, queryParams, dataParams, credentials, encoding, callback)
    }


    /**
     * Performs an HTTP operation and supplies the response back in the Callback
     * @param verb        : The http verb
     * @param route       : The route endpoint ( without the base route )
     * @param headerParams: Http headers to use
     * @param queryParams : Query parameters to use
     * @param callback    : The callback to call when the response is available.
     */
    fun send(method: Method,
             route: String,
             headerParams: Map<String, String>? = null,
             queryParams: Map<String, String>? = null,
             dataParams: Map<String, Any>? = null,
             creds: AuthInfo? = null,
             encoding: Encoding = Encoding.Json,
             callback: (Result<Response, IOException>) -> Unit) {
        val client = OkHttpClient()

        // URL
        val url = buildUrl(route, queryParams)

        // HEADERS
        val headers = buildHeaders(headerParams)
        val builder = Request.Builder().url(url).headers(headers)

        // FORM Body
        val body = when(encoding) {
            Encoding.Form -> buildForm(dataParams)
            else -> RequestBody.create(jsonType, serializer?.let{ it(dataParams) } ?: "" )
        }

        // AUTHORIZATION
        when(creds) {
            is AuthInfo.Basic  -> builder.addHeader("Authorization", Credentials.basic(creds.name, creds.pswd))
            is AuthInfo.Bearer -> builder.addHeader("Authorization", "Bearer " + creds.token)
            else               -> {}
        }

        // SEND ( post / put / etc )
        val request = when (method) {
            Method.Post   -> builder.post(body).build()
            Method.Delete -> builder.delete(body).build()
            Method.Put    -> builder.put(body).build()
            Method.Patch  -> builder.patch(body).build()
            else          -> builder.post(body).build()
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


    private fun buildUrl(url: String, queryParams: Map<String, String>? = null): HttpUrl {
        val urlBuilder = HttpUrl.parse(url)?.newBuilder() ?: throw Exception("Invalid url")
        queryParams?.forEach { urlBuilder.addQueryParameter(it.key, it.value) }
        return urlBuilder.build()
    }


    private fun buildForm(dataParams: Map<String, Any>?): FormBody {
        val fe = FormBody.Builder()
        dataParams?.let { data ->
            data.forEach { fe.add(it.key, it.value.toString()) }
        }
        return fe.build()
    }

    private fun buildJson(dataParams: Map<String, Any>?): FormBody {
        val fe = FormBody.Builder()
        dataParams?.let { data ->
            data.forEach { fe.add(it.key, it.value.toString()) }
        }
        return fe.build()
    }


    private fun buildHeaders(headers: Map<String, String>? = null): Headers {
        return headers?.let { Headers.of(it) } ?: Headers.of(mapOf())
    }
}