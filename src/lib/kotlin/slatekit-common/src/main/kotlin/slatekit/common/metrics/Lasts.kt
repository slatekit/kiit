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
package slatekit.common.metrics

import slatekit.common.ids.Identity
import slatekit.results.*
import java.util.concurrent.atomic.AtomicReference

open class Lasts<TRequest, TResponse, TFailure>(val id:Identity, val custom:List<String>? = null) {

    // Last values
    private val _lastRequest = AtomicReference<TRequest>(null)
    private val _lastPending = AtomicReference<TRequest>(null)
    private val _lastSuccess = AtomicReference<Pair<TRequest, TResponse>>(null)
    private val _lastDenied  = AtomicReference<Pair<TRequest ,TFailure?>>(null)
    private val _lastInvalid = AtomicReference<Pair<TRequest, TFailure?>>(null)
    private val _lastIgnored = AtomicReference<Pair<TRequest, TFailure?>>(null)
    private val _lastErrored = AtomicReference<Pair<TRequest, TFailure?>>(null)
    private val _lastUnexpected  = AtomicReference<Pair<TRequest, TFailure?>>(null)
    private val _customLasts = custom?.let {  c -> c.map {  it to AtomicReference<Pair<TRequest, TFailure?>>() }.toMap() } ?: mapOf()

    open fun requested(request: TRequest) = _lastRequest.set(request)
    open fun pending   (req: TRequest)                      = _lastPending.set(req)
    open fun succeeded (req:TRequest, res: TResponse)       = _lastSuccess.set(Pair(req, res))
    open fun denied    (req: TRequest, failure: TFailure?)  = _lastDenied.set(Pair(req, failure))
    open fun invalid   (req: TRequest, failure: TFailure?)  = _lastInvalid.set(Pair(req, failure))
    open fun ignored   (req: TRequest, failure: TFailure?)  = _lastIgnored.set(Pair(req, failure))
    open fun errored   (req: TRequest, failure: TFailure?)  = _lastErrored.set(Pair(req, failure))
    open fun unexpected(req: TRequest, failure: TFailure?)  = _lastUnexpected.set(Pair(req, failure))
    open fun custom    (name:String, req: TRequest, failure: TFailure?) {
        getCustom(name)?.let { c -> c.set(Pair(req, failure)) }
    }


    fun lastProcessed ():TRequest                  = _lastRequest.get()
    fun lastSuccess   ():Pair<TRequest, TResponse> = _lastSuccess.get()
    fun lastInvalid   ():Pair<TRequest, TFailure?> = _lastInvalid.get()
    fun lastIgnored   ():Pair<TRequest, TFailure?> = _lastIgnored.get()
    fun lastDenied    ():Pair<TRequest, TFailure?> = _lastDenied.get()
    fun lastErrored   ():Pair<TRequest, TFailure?> = _lastErrored.get()
    fun lastUnexpected():Pair<TRequest, TFailure?> = _lastUnexpected.get()
    fun lastCustom(name:String): Pair<TRequest, TFailure?>? = getCustom(name)?.get()


    fun track(request: TRequest, result: Result<TResponse, TFailure>) {
        this.requested(request)
        when(result) {
            is Success -> this.succeeded(request, result.value)
            is Failure -> {
                when (result.status) {
                    is Status.Denied     -> this.denied(request, result.error )
                    is Status.Invalid    -> this.invalid(request, result.error)
                    is Status.Ignored    -> this.ignored(request, result.error)
                    is Status.Errored    -> this.errored(request, result.error)
                    is Status.Unexpected -> this.unexpected(request, result.error)
                    else                 -> this.unexpected(request, result.error)
                }
            }
        }
    }


    private fun getCustom(name:String):AtomicReference<Pair<TRequest ,TFailure?>>? {
        return if(_customLasts.contains(name)) _customLasts[name] else null
    }
}
