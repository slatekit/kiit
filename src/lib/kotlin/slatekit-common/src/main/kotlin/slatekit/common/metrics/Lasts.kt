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

/**
 * Used for diagnostics / metrics to track the last results of calling some function/target identified by @param id
 * NOTE: This is particularly helpful for functions/operations such as scheduled tasks, syncs, one-off commands.
 * This serves to track the following:
 *
 * 1. total requests  ( processed )
 * 2. total successes  e.g. passed
 * 3. total denied     e.g. security/auth failure
 * 4. total invalid    e.g. invalid / bad request
 * 5. total ignored    e.g. ineligible request
 * 6. total errored    e.g. failed validation
 * 7. total unexpected e.g. unexpected error
 *
 * @param TRequest : Type of the request  / input to the operation
 * @param TResponse: Type of the response / output of the operation
 * @param TFailure : Type of the error of the operation
 */
open class Lasts<TRequest, TResponse, TFailure>(val id:Identity,
                                                tags:List<Tag> = listOf(),
                                                val custom:List<String>? = null) : Tagged, Events<TRequest, TResponse, TFailure>(tags) {

    // Last values
    private val _lastRequest     = AtomicReference<TRequest>(null)
    private val _lastPending     = AtomicReference<TRequest>(null)
    private val _lastSuccess     = AtomicReference<Triple<Any, TRequest, TResponse>>(null)
    private val _lastDenied      = AtomicReference<Triple<Any, TRequest ,TFailure?>>(null)
    private val _lastInvalid     = AtomicReference<Triple<Any, TRequest, TFailure?>>(null)
    private val _lastIgnored     = AtomicReference<Triple<Any, TRequest, TFailure?>>(null)
    private val _lastErrored     = AtomicReference<Triple<Any, TRequest, TFailure?>>(null)
    private val _lastUnexpected  = AtomicReference<Triple<Any, TRequest, TFailure?>>(null)
    private val _customLasts = custom?.let {  c -> c.map {  it to AtomicReference<Triple<Any, TRequest, TFailure?>>() }.toMap() } ?: mapOf()

    override fun requested (sender:Any, req: TRequest)                      = _lastRequest.set(req)
    override fun succeeded (sender:Any, req: TRequest, res: TResponse)      = _lastSuccess.set(Triple(sender, req, res))
    override fun denied    (sender:Any, req: TRequest, failure: TFailure?)  = _lastDenied.set (Triple(sender, req, failure))
    override fun invalid   (sender:Any, req: TRequest, failure: TFailure?)  = _lastInvalid.set(Triple(sender, req, failure))
    override fun ignored   (sender:Any, req: TRequest, failure: TFailure?)  = _lastIgnored.set(Triple(sender, req, failure))
    override fun errored   (sender:Any, req: TRequest, failure: TFailure?)  = _lastErrored.set(Triple(sender, req, failure))
    override fun unexpected(sender:Any, req: TRequest, failure: TFailure?)  = _lastUnexpected.set(Triple(sender, req, failure))
    override fun custom    (sender:Any, name:String, req: TRequest, failure: TFailure?) {
        getCustom(name)?.let { c -> c.set(Triple(sender, req, failure)) }
    }


    fun lastProcessed ():TRequest                  = _lastRequest.get()
    fun lastSuccess   ():Triple<Any, TRequest, TResponse> = _lastSuccess.get()
    fun lastInvalid   ():Triple<Any, TRequest, TFailure?> = _lastInvalid.get()
    fun lastIgnored   ():Triple<Any, TRequest, TFailure?> = _lastIgnored.get()
    fun lastDenied    ():Triple<Any, TRequest, TFailure?> = _lastDenied.get()
    fun lastErrored   ():Triple<Any, TRequest, TFailure?> = _lastErrored.get()
    fun lastUnexpected():Triple<Any, TRequest, TFailure?> = _lastUnexpected.get()
    fun lastCustom(name:String):Triple<Any, TRequest, TFailure?>? = getCustom(name)?.get()


    private fun getCustom(name:String):AtomicReference<Triple<Any, TRequest ,TFailure?>>? {
        return if(_customLasts.contains(name)) _customLasts[name] else null
    }
}
