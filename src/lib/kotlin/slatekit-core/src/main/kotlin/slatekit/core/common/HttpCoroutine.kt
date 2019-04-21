package slatekit.core.common

import okhttp3.Response
import slatekit.common.HttpRPC
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