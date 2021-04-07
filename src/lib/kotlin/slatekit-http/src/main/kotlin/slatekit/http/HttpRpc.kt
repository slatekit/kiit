package slatekit.http

import okhttp3.*
import slatekit.results.Failure
import slatekit.results.Result
import slatekit.results.Success
import java.io.IOException

/**
 * Minimal Http Client for making RPC like calls over Http
 * Uses OkHttp under the hood.
 */
class HttpRPC(private val serializer:((Any?) -> String)? = null,
              private val clientBuilder: (() -> OkHttpClient)? = null,
              private val singletonClient:Boolean = true) {

    /**
     * Singleton recommended but configurable based on flag
     * See: https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/
     * NOTE: This is only called if singletonClient = true
     */
    private val client:OkHttpClient by lazy { build(clientBuilder) }


    enum class Method(val value:Int) {
        Get(0),
        Post(1),
        Put(2),
        Patch(3),
        Delete(4)
    }


    interface HttpRPCResult {
        fun onSuccess(result: Response)
        fun onFailure(e: Exception?)
    }


    sealed class Auth {
        data class Basic(val name:String, val pswd:String): Auth()
        data class Bearer(val token:String): Auth()
    }


    sealed class Body {
        data class FormData(val values:List<Pair<String,String>>): Body()
        data class RawContent(val content:String) :  Body()
        data class JsonContent(val content:String) :  Body()
        data class JsonObject(val content:String) :  Body()
    }


    /**
     * Media types for posting data
     */
    private val jsonType = MediaType.parse("application/json charset=utf-8")
    private val textType = MediaType.parse("application/text charset=utf-8")


    /**
     * Performs an HTTP get operation and supplies the response back in the Callback
     * @param url       : The url endpoint ( without the base url )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param callback    : The callback to call when the response is available.
     * @param <T>         : The datatype of the expected result
     */
    fun get(url: String,
            headerParams: Map<String, String>? = null,
            queryParams: Map<String, String>? = null,
            creds: Auth? = null,
            callback: (Result<Response, IOException>) -> Unit) {
        val client = httpClient()
        val url = buildUrl(url, queryParams)
        val headers = buildHeaders(headerParams)
        val builder = Request.Builder().url(url).headers(headers)

        // AUTHORIZATION
        when(creds) {
            is Auth.Basic -> builder.addHeader("Authorization", Credentials.basic(creds.name, creds.pswd))
            is Auth.Bearer -> builder.addHeader("Authorization", "Bearer " + creds.token)
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
     * @param url       : The url endpoint ( without the base url )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param body        : Body data
     * @param callback    : The callback to call when the response is available.
     */
    fun post(url: String,
             headers: Map<String, String>? = null,
             queryParams: Map<String, String>? = null,
             credentials: Auth? = null,
             body: Body? = null,
             callback: (Result<Response, IOException>) -> Unit) {
        this.sendAsync(Method.Post, url, headers, queryParams, credentials, body, callback)
    }


    /**
     * Performs an HTTP patch operation and supplies the response back in the Callback
     * @param url       : The url endpoint ( without the base url )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param body        : Body data
     * @param callback    : The callback to call when the response is available.
     */
    fun patch(url: String,
              headers: Map<String, String>? = null,
              queryParams: Map<String, String>? = null,
              credentials: Auth? = null,
              body: Body? = null,
              callback: (Result<Response, IOException>) -> Unit) {
        this.sendAsync(Method.Patch, url, headers, queryParams, credentials, body, callback)
    }


    /**
     * Performs an HTTP put operation and supplies the response back in the Callback
     * @param url       : The url endpoint ( without the base url )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param body        : Body data
     * @param callback    : The callback to call when the response is available.
     */
    fun put(url: String,
            headers: Map<String, String>? = null,
            queryParams: Map<String, String>? = null,
            credentials: Auth? = null,
            body: Body? = null,
            callback: (Result<Response, IOException>) -> Unit) {
        this.sendAsync(Method.Put, url, headers, queryParams, credentials, body, callback)
    }


    /**
     * Performs an HTTP delete operation and supplies the response back in the Callback
     * @param url       : The url endpoint ( without the base url )
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param body        : Body data
     * @param callback    : The callback to call when the response is available.
     */
    fun delete(url: String,
               headers: Map<String, String>? = null,
               queryParams: Map<String, String>? = null,
               credentials: Auth? = null,
               body: Body? = null,
               callback: (Result<Response, IOException>) -> Unit) {
        this.sendAsync(Method.Delete, url, headers, queryParams, credentials, body, callback)
    }


    /**
     * Performs an HTTP operation and supplies the response back in the Callback
     * @param verb        : The http verb
     * @param url         : The url endpoint ( without the base url )
     * @param headerParams: Http headers to use
     * @param queryParams : Query parameters to use
     * @param callback    : The callback to call when the response is available.
     */
    fun sendAsync(method: Method,
                  urlRaw: String,
                  headerParams: Map<String, String>? = null,
                  queryParams: Map<String, String>? = null,
                  creds: Auth? = null,
                  body: Body? = null,
                  callback: (Result<Response, IOException>) -> Unit) {
        val request = build(method, urlRaw, headerParams, queryParams, creds, body)
        sendAsync(request, callback)
    }


    fun sendAsync(request: Request,
                  callback: (Result<Response, IOException>) -> Unit) {
        val client = httpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                callback(Success(response))
            }

            override fun onFailure(call: Call?, ex: IOException) {
                callback(Failure(ex))
            }
        })
    }

    fun sendAsync(request: Request,
                  callback: HttpRPCResult) {
        val client = httpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                callback.onSuccess(response)
            }

            override fun onFailure(call: Call?, ex: IOException) {
                callback.onFailure(ex)
            }
        })
    }

    /**
     * Performs an HTTP operation and supplies synchronously
     * @param method      : The http method
     * @param url         : The url to send to
     * @param headers     : Http headers to use
     * @param queryParams : Query parameters to use
     * @param callback    : The callback to call when the response is available.
     */
    fun sendSync(method: Method,
                 url: String,
                 headers: Map<String, String>? = null,
                 queryParams: Map<String, String>? = null,
                 creds: Auth? = null,
                 body: Body? = null): Result<Response, Exception> {
        val request = build(method, url, headers, queryParams, creds, body)
        return call(request)
    }

    /**
     * Performs an HTTP operation and supplies synchronously
     * @param request     : A pre-built okhttp request
     */
    fun call(request:Request): Result<Response, Exception> {
        val client = httpClient()
        val response = client.newCall(request).execute()
        return if(response.isSuccessful) {
            Success(response)
        } else {
            Failure(Exception(response.message()))
        }
    }


    fun build(method: Method,
              urlRaw: String,
              headerParams: Map<String, String>? = null,
              queryParams: Map<String, String>? = null,
              creds: Auth? = null,
              body: Body? = null):Request {
        // URL
        val url = buildUrl(urlRaw, queryParams)

        // HEADERS
        val headers = buildHeaders(headerParams)
        val builder = Request.Builder().url(url).headers(headers)

        // BODY
        val finalBody = when(body) {
            is Body.FormData -> buildForm(body.values)
            is Body.RawContent -> RequestBody.create(textType, body.content )
            is Body.JsonContent -> RequestBody.create(jsonType, body.content )
            is Body.JsonObject -> RequestBody.create(jsonType, serializer?.let{ it(body.toString()) } ?: "" )
            else                -> RequestBody.create(jsonType, serializer?.let{ it(body.toString()) } ?: "" )
        }

        // AUTHORIZATION
        when(creds) {
            is Auth.Basic -> builder.addHeader("Authorization", Credentials.basic(creds.name, creds.pswd))
            is Auth.Bearer -> builder.addHeader("Authorization", "Bearer " + creds.token)
            else           -> {}
        }

        // SEND ( post / put / etc )
        val request = when (method) {
            Method.Get -> builder.get().build()
            Method.Post -> builder.post(finalBody).build()
            Method.Delete -> builder.delete(finalBody).build()
            Method.Put -> builder.put(finalBody).build()
            Method.Patch -> builder.patch(finalBody).build()
            else          -> builder.post(finalBody).build()
        }
        return request
    }


    private fun buildUrl(url: String, queryParams: Map<String, String>? = null): HttpUrl {
        val urlBuilder = HttpUrl.parse(url)?.newBuilder() ?: throw Exception("Invalid url")
        queryParams?.forEach { urlBuilder.addQueryParameter(it.key, it.value) }
        return urlBuilder.build()
    }


    private fun buildForm(dataParams: List<Pair<String, String>>): FormBody {
        val fe = FormBody.Builder()
        dataParams.forEach { fe.add(it.first, it.second) }
        return fe.build()
    }


    private fun buildHeaders(headers: Map<String, String>? = null): Headers {
        return headers?.let { Headers.of(it) } ?: Headers.of(mapOf())
    }


    private fun httpClient():OkHttpClient {
        return when(singletonClient) {
            true  -> client
            false -> build(clientBuilder)
        }
    }

    companion object {
        private fun build(builder: (() -> OkHttpClient)? = null): OkHttpClient {
            return builder?.invoke() ?: OkHttpClient()
        }
    }
}