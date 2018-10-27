/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.core.workers

import slatekit.common.Failure
import slatekit.common.Result

open class Middleware {

    open fun <T> run(worker: Worker<T>, job: Job, call: () -> Result<T, Exception>): Result<T, Exception> {

        // Track all requests
        worker.metrics.request(job)

        // Let handler do any logging or other behaviour here
        worker.handler.onRequest(job, worker)

        // Attempt
        val result = try {
            call()
        } catch (ex: Exception) {
            Failure(ex, msg = "Unexpected error : " + ex.message)
        }

        // Track successes/failures
        if (result.success) {
            worker.metrics.success(job, result)
            worker.handler.onSuccess(job, worker, result)
        } else {
            worker.metrics.errored(job, result)
            worker.handler.onErrored(job, worker, result)
        }
        return result
    }
}
