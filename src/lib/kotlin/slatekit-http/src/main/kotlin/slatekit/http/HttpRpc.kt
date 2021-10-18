package slatekit.http

import okhttp3.*
import slatekit.common.ext.toId
import slatekit.common.types.Content
import slatekit.common.types.ContentFile
import slatekit.common.types.ContentText
import slatekit.results.Failure
import slatekit.results.Result
import slatekit.results.Success
import java.io.IOException


/**
 * Minimal Http Client for making RPC like calls over Http
 * Uses OkHttp under the hood.
 *
 * DESIGN
 * 1. This has very simplified interface
 * 2. It is designed to be declarative
 *
 * @sample
 * post( url   = "https://myserver.com/api/post/comment",
 *       meta  = mapOf("userId" to "user123", "postId" to "post123"),
 *       args  = mapOf("a" to "1", "b" to "2"),
 *       auth  = HttpRPC.Auth.Bearer("token123"),
 *       body  = HttpRPC.Body.JsonContent("""{ "comment": "hello" }"""),
 *       call  = { res -> println("") }
 * )
 *
 * @notes
 * 1. Only get/post/put/patch/delete are supported for CRUD operations
 * 2. Coroutines/Suspend are not currently supported
 * 3. Response is modeled as a Slate Kit Result<T,E>
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
        data class MultiPart(val values:List<Pair<String,Content>>): Body()
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
     * @param url  : The url endpoint ( without the base url )
     * @param meta : Http headers to use
     * @param args : Query parameters to use
     * @param call : The callback to call when the response is available.
     * @param <T>  : The datatype of the expected result
     */
    fun get(url: String,
            meta: Map<String, String>? = null,
            args: Map<String, String>? = null,
            auth: Auth? = null,
            call: (Result<Response, IOException>) -> Unit) {
        val client = httpClient()
        val finalUrl = buildUrl(url, args)
        val finalMeta = buildHeaders(meta)
        val builder = Request.Builder().url(finalUrl).headers(finalMeta)

        // AUTHORIZATION
        buildAuth(auth, builder)

        // REQUEST
        val request = builder.build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                call(Success(response))
            }

            override fun onFailure(call: Call?, ex: IOException) {
                call(Failure(ex))
            }
        })
    }


    /**
     * Performs an HTTP post operation and supplies the response back in the Callback
     * @param url   : The url endpoint ( without the base url )
     * @param meta  : Http headers to use
     * @param args  : Query parameters to use
     * @param body  : Body data
     * @param call  : The callback to call when the response is available.
     */
    fun post(url: String,
             meta: Map<String, String>? = null,
             args: Map<String, String>? = null,
             auth: Auth? = null,
             body: Body? = null,
             call: (Result<Response, IOException>) -> Unit) {
        this.sendAsync(Method.Post, url, meta, args, auth, body, call)
    }


    /**
     * Performs an HTTP patch operation and supplies the response back in the Callback
     * @param url   : The url endpoint ( without the base url )
     * @param meta  : Http headers to use
     * @param args  : Query parameters to use
     * @param body  : Body data
     * @param call  : The callback to call when the response is available.
     */
    fun patch(url: String,
              meta: Map<String, String>? = null,
              args: Map<String, String>? = null,
              auth: Auth? = null,
              body: Body? = null,
              call: (Result<Response, IOException>) -> Unit) {
        this.sendAsync(Method.Patch, url, meta, args, auth, body, call)
    }


    /**
     * Performs an HTTP put operation and supplies the response back in the Callback
     * @param url   : The url endpoint ( without the base url )
     * @param meta  : Http headers to use
     * @param args  : Query parameters to use
     * @param body  : Body data
     * @param call  : The callback to call when the response is available.
     */
    fun put(url: String,
            meta: Map<String, String>? = null,
            args: Map<String, String>? = null,
            auth: Auth? = null,
            body: Body? = null,
            call: (Result<Response, IOException>) -> Unit) {
        this.sendAsync(Method.Put, url, meta, args, auth, body, call)
    }


    /**
     * Performs an HTTP delete operation and supplies the response back in the Callback
     * @param url   : The url endpoint ( without the base url )
     * @param meta  : Http headers to use
     * @param args  : Query parameters to use
     * @param body  : Body data
     * @param call  : The callback to call when the response is available.
     */
    fun delete(url: String,
               meta: Map<String, String>? = null,
               args: Map<String, String>? = null,
               auth: Auth? = null,
               body: Body? = null,
               call: (Result<Response, IOException>) -> Unit) {
        this.sendAsync(Method.Delete, url, meta, args, auth, body, call)
    }


    /**
     * Performs an HTTP operation and supplies the response back in the Callback
     * @param verb  : The http verb
     * @param url   : The url endpoint ( without the base url )
     * @param meta  : Http headers to use
     * @param args  : Query parameters to use
     * @param body  : Body data
     * @param call  : The callback to call when the response is available.
     */
    fun sendAsync(verb: Method,
                  url : String,
                  meta: Map<String, String>? = null,
                  args: Map<String, String>? = null,
                  auth: Auth? = null,
                  body: Body? = null,
                  call: (Result<Response, IOException>) -> Unit) {
        val request = build(verb, url, meta, args, auth, body)
        sendAsync(request, call)
    }


    fun sendAsync(request: Request, callback: (Result<Response, IOException>) -> Unit) {
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


    fun sendAsync(request: Request, callback: HttpRPCResult) {
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
     * @param url   : The url endpoint ( without the base url )
     * @param meta  : Http headers to use
     * @param args  : Query parameters to use
     * @param body  : Body data
     * @param call  : The callback to call when the response is available.
     */
    fun sendSync(method: Method,
                 url: String,
                 meta: Map<String, String>? = null,
                 args: Map<String, String>? = null,
                 auth: Auth? = null,
                 body: Body? = null): Result<Response, Exception> {
        val request = build(method, url, meta, args, auth, body)
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


    fun build(verb: Method,
              url : String,
              meta: Map<String, String>? = null,
              args: Map<String, String>? = null,
              auth: Auth? = null,
              body: Body? = null):Request {
        // URL
        val finalUrl = buildUrl(url, args)

        // HEADERS
        val headers = buildHeaders(meta)
        val builder = Request.Builder().url(finalUrl).headers(headers)

        // BODY
        val finalBody = when(body) {
            is Body.MultiPart   -> buildMulti(body)
            is Body.FormData    -> buildFormEncoded(body.values)
            is Body.RawContent  -> RequestBody.create(textType, body.content )
            is Body.JsonContent -> RequestBody.create(jsonType, body.content )
            is Body.JsonObject  -> RequestBody.create(jsonType, serializer?.let{ it(body.toString()) } ?: "" )
            else                -> RequestBody.create(jsonType, serializer?.let{ it(body.toString()) } ?: "" )
        }

        // AUTHORIZATION
        buildAuth(auth, builder)

        // SEND ( post / put / etc )
        val request = when (verb) {
            Method.Get -> builder.get().build()
            Method.Post -> builder.post(finalBody).build()
            Method.Delete -> builder.delete(finalBody).build()
            Method.Put -> builder.put(finalBody).build()
            Method.Patch -> builder.patch(finalBody).build()
            else          -> builder.post(finalBody).build()
        }
        return request
    }


    private fun buildUrl(url: String, args: Map<String, String>? = null): HttpUrl {
        val urlBuilder = HttpUrl.parse(url)?.newBuilder() ?: throw Exception("Invalid url")
        args?.forEach { urlBuilder.addQueryParameter(it.key, it.value) }
        return urlBuilder.build()
    }


    private fun buildFormEncoded(dataParams: List<Pair<String, String>>): FormBody {
        val fe = FormBody.Builder()
        dataParams.forEach { fe.add(it.first, it.second) }
        return fe.build()
    }


    private fun buildMulti(body:Body.MultiPart): MultipartBody {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        body.values.forEachIndexed { ndx, part ->
            when(part.second) {
                is ContentFile -> {
                    val content = part.second as ContentFile
                    val file = RequestBody.create(MediaType.parse(content.tpe.http), content.data)
                    val name = content.name.toId()
                    builder.addFormDataPart(name, content.name, file)
                }
                is ContentText -> builder.addFormDataPart("some-field", "some-value")
                else           -> {}
            }
        }
        val requestBody = builder.build()
        return requestBody
    }


    private fun buildHeaders(meta: Map<String, String>? = null): Headers {
        return meta?.let { Headers.of(it) } ?: Headers.of(mapOf())
    }


    private fun buildAuth(auth:Auth?, builder:Request.Builder) {
        when(auth) {
            is Auth.Basic -> builder.addHeader("Authorization", Credentials.basic(auth.name, auth.pswd))
            is Auth.Bearer -> builder.addHeader("Authorization", "Bearer " + auth.token)
            else           -> {}
        }
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