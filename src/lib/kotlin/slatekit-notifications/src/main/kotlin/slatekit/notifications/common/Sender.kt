package slatekit.notifications.common

import okhttp3.Request
import okhttp3.Response
import slatekit.http.HttpRPC
import slatekit.http.awaitHttpOutcome
import slatekit.results.*
import slatekit.results.builders.Outcomes

interface Sender<T> {

    /**
     * Whether or not sending is enabled
     */
    fun isEnabled(model: T): Boolean = true

    /**
     * Validates the model supplied
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    fun validate(model: T): Outcome<T>

    /**
     * Builds the HttpRequest for the model
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    fun build(model: T): Outcome<Request>

    /**
     * Sends the model asynchronously using OkHttp
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    suspend fun send(model: T): Outcome<String> {
        return if (!isEnabled(model)) {
            Outcomes.ignored("Not enabled")
        } else {
            validate(model)
                .then { build(it)   }
                .then { send (it)   }
                .then { convert(it) }
        }
    }

    /**
     * Sends the model synchronously
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    fun sendSync(model: T): Outcome<String> {
        return if (!isEnabled(model)) {
            Outcomes.ignored("Not enabled")
        } else {
            validate(model)
                .then { build(it)     }
                .then { sendSync (it) }
                .then { convert(it)   }
        }
    }

    /**
     * Sends the model asynchronously using OkHttp
     * @param request: A prebuilt http request representing the send
     */
    suspend fun send(request: Request): Outcome<Response> {
        return awaitHttpOutcome { HttpRPC().sendAsync(request, it) }
    }

    /**
     * Sends the model synchronously
     * @param request: A prebuilt http request representing the send
     */
    fun sendSync(request: Request): Outcome<Response> {
        val response = HttpRPC().call(request)
        return response.toOutcome()
    }


    fun convert(response:Response):Outcome<String> {
        return if(response.isSuccessful) {
            Outcomes.success(response.body()?.string() ?: "")
        } else {
            val code = response.code()
            when {
                code.isFilteredOut()       -> Outcomes.ignored(response.message())
                code.isInBadRequestRange() -> Outcomes.invalid(response.message())
                code.isInFailureRange()    -> Outcomes.errored(response.message())
                else                       -> Outcomes.unexpected(response.message())
            }
        }
    }
}
