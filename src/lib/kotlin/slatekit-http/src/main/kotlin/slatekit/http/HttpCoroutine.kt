package slatekit.http

import okhttp3.Response
import slatekit.results.*
import slatekit.results.builders.Notices
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine



suspend fun awaitHttp(callback: (HttpRPC.HttpRPCResult) -> Unit ) : Response {
    return suspendCoroutine { cont ->
        callback(object : HttpRPC.HttpRPCResult {
            override fun onSuccess(result: Response) = cont.resume(result)
            override fun onFailure(e: Exception?) {
                e?.let { cont.resumeWithException(it) }
            }
        })
    }
}


suspend fun awaitHttpTry(callback: (HttpRPC.HttpRPCResult) -> Unit ) : Result<Response,Exception> {
    return suspendCoroutine { cont ->
        callback(object : HttpRPC.HttpRPCResult {
            override fun onSuccess(result: Response) = cont.resume(Success(result))
            override fun onFailure(e: Exception?) {
                e?.let { cont.resume(Failure(e, Codes.ERRORED)) }
            }
        })
    }
}


suspend fun awaitHttpOutcome(callback: (HttpRPC.HttpRPCResult) -> Unit ) : Outcome<Response> {
    return suspendCoroutine { cont ->
        callback(object : HttpRPC.HttpRPCResult {
            override fun onSuccess(result: Response) {
                cont.resume(Outcomes.success(result))
            }
            override fun onFailure(e: Exception?) {
                e?.let { cont.resume(Outcomes.errored(e)) }
            }
        })
    }
}


suspend fun awaitHttpNotice(callback: (HttpRPC.HttpRPCResult) -> Unit ) : Notice<Response> {
    return suspendCoroutine { cont ->
        callback(object : HttpRPC.HttpRPCResult {
            override fun onSuccess(result: Response) = cont.resume(Notices.success(result))
            override fun onFailure(e: Exception?) {
                e?.let { cont.resume(Notices.errored(e)) }
            }
        })
    }
}