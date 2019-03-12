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
package slatekit.workers.utils

import slatekit.results.Failure
import slatekit.results.Try
import slatekit.workers.Job
import slatekit.workers.Worker

open class Middleware {

    open fun <T> run(worker: Worker<T>, job: Job, call: () -> Try<T>): Try<T> {

        // Track all requests
        //worker.metrics.request(job)

        // Let handler do any logging or other behaviour here
        //worker.handler.onRequest(job, worker)

        // Attempt
        val result = try {
            call()
        } catch (ex: Exception) {
            Failure(ex, msg = "Unexpected error : " + ex.message)
        }


        // Track successes/failures
        if (result.success) {
            //worker.metrics.(job, result)
            //worker.handler.onSuccess(job, worker, result)
        } else {
            //worker.metrics.errored(job, result)
            //worker.handler.onErrored(job, worker, result)
        }
        return result
    }
}
