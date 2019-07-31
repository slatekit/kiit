package slatekit.core.common

import okhttp3.Request
import okhttp3.Response
import slatekit.common.HttpRPC
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.results.then

interface Sender<T> {

    /**
     * Whether or not sending is enabled
     */
    fun isEnabled(model:T):Boolean = true

    /**
     * Validates the model supplied
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    fun validate(model:T):Outcome<T>

    /**
     * Builds the HttpRequest for the model
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    fun build(model:T):Outcome<Request>

    /**
     * Sends the model asynchronously using OkHttp
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    suspend fun send(model: T): Outcome<String> {
        return if(!isEnabled(model)) {
            Outcomes.ignored("Not enabled")
        } else {
            build(model).then { send(it) }.map { it.body()?.string() ?: "" }
        }
    }

    /**
     * Sends the model asynchronously using OkHttp
     * @param request: A prebuilt http request representing the send
     */
    suspend fun send(request: Request): Outcome<Response> {
        return awaitHttpOutcome {  HttpRPC().sendAsync(request, it) }
    }

    /**
     * Sends the model synchronously
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    fun sendSync(model: T): Outcome<String> {
        return if(!isEnabled(model)) {
            Outcomes.ignored("Not enabled")
        } else {
            build(model).then { sendSync(it) }.map { it.body()?.string() ?: "" }
        }
    }

    /**
     * Sends the model synchronously
     * @param request: A prebuilt http request representing the send
     */
    fun sendSync(request: Request): Outcome<Response> {
        val response = HttpRPC().call(request)
        return response.toOutcome()
    }
}