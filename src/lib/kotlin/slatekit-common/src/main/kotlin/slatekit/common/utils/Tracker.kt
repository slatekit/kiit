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
package slatekit.common.utils

import slatekit.common.DateTime
import slatekit.common.Result
import slatekit.common.log.Logger
import slatekit.common.onFailure
import slatekit.common.onSuccess
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

open class Tracker<TRequest, TFilter, TSuccess, TFailure>(val id: String, val name: String) {

    // Last values
    val lastRequest = AtomicReference<TRequest>(null)
    val lastFiltered = AtomicReference<TFilter>(null)
    val lastSuccess = AtomicReference<TSuccess>(null)
    val lastFailure = AtomicReference<Pair<TRequest, TFailure>>(null)

    /**
     * Keep track of total requests ( regardless of success/error )
     */
    open fun requested(request: TRequest) {
        lastRequest.set(request)
    }

    /**
     * Keep track of total filtered requests ( regardless of success/error )
     */
    open fun filtered(filteredReason: TFilter) {
        lastFiltered.set(filteredReason)
    }

    /**
     * Keep track of total successfully processed engagements
     */
    open fun succeeded(success: TSuccess) {
        lastSuccess.set(success)
    }

    /**
     * Keep track of total errors
     * @param err
     */
    open fun failed(req: TRequest, failure: TFailure) {
        lastFailure.set(Pair(req, failure))
    }
}
