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
package slatekit.apis.core

import slatekit.apis.middleware.Filter
import slatekit.apis.middleware.Handler
import slatekit.apis.middleware.Hook
import slatekit.apis.middleware.Tracked
import slatekit.common.*
import slatekit.common.log.Logger
import kotlin.reflect.KCallable


/**
 * Executes the API action using a pipeline of steps
 */
class Exec(val ctx:Ctx, val validator:Validation, val logger:Logger) {
    private val BEFORE = "Before"
    private val AFTER  = "After "


    /**
     * Executes the final API action after going through all the middleware first.
     */
    fun run(execute: (Ctx) -> Result<Any, Exception>): Result<Any, Exception> {

        // Compose the various middleware components into a "pipeline"
        // using the convenient Kotlin syntax for single/last parameter being a lambda
        val result =

            // Top level error handler
            attempt {

                // Middleware: Track all requests
                track {

                    // Ensure protocol
                    protocol {

                        // Ensure auth
                        auth {

                            // Ensure global middleware
                            middleware {

                                // Ensure params
                                params {

                                    // Middleware: Track usage of api
                                    diagnostics {

                                        // Middleware: Filter requests
                                        filter {

                                            // Middleware: Hooks for before/after events
                                            hook {

                                                // Middleware: Custom handling
                                                handle {

                                                    // Finally  execute
                                                    execute(ctx)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        // for debugging
        return result
    }


    /**
     * Ensures valid protocols before processing
     */
    fun protocol(proceed: () -> Result<Any, Exception>): Result<Any, Exception> {
        return log(::protocol) {

            val check = validator.validateProtocol(ctx.req, ctx.apiRef)
            val result = if (check.success) {
                proceed()
            } else {
                check.toResultEx()
            }
            result
        }
    }


    /**
     * Ensures valid authorization before processing
     */
    fun auth(proceed: () -> Result<Any, Exception>): Result<Any, Exception> {
        return log(::auth) {

            val check = validator.validateAuthorization(ctx.req, ctx.apiRef)
            val result = if (check.success) {
                proceed()
            } else {
                check.toResultEx()
            }
            result
        }
    }


    /**
     * Ensures valid authorization before processing
     */
    fun middleware(proceed: () -> Result<Any, Exception>): Result<Any, Exception> {
        return log(::middleware) {

            val check = validator.validateMiddleware(ctx.req, ctx.container.filters)
            val result = if(check.success) {
                proceed()
            } else {
                check.toResultEx()
            }
            result
        }
    }


    /**
     * Ensures valid authorization before processing
     */
    fun params(proceed: () -> Result<Any, Exception>): Result<Any, Exception> {
        return log(::params) {

            val check = validator.validateParameters(ctx.req)
            val result = if (check.success) {
                proceed()
            } else {
                check.toResultEx()
            }
            result
        }
    }


    /**
     * Applies the tracking middleware to track requests, successes, failures
     */
    fun track(proceed: () -> Result<Any, Exception>): Result<Any, Exception> {
        return log(::track) {
            val instance = ctx.container.tracker

            // Hook: Before
            if (instance is Tracked) {
                instance.tracker.trackRequest(ctx.req)
            }

            val result = proceed()

            // Hook: After
            if (instance is Tracked) {
                result.onSuccess { instance.tracker.handleResponse(ctx.req, result) }
                result.onFailure { instance.tracker.handleFailure(ctx.req, it) }
            }
            result
        }
    }


    /**
     * Applies the tracking middleware to track requests, successes, failures
     */
    fun diagnostics(proceed: () -> Result<Any, Exception>): Result<Any, Exception> {
        return log(::track) {
            val instance = ctx.apiRef.instance

            // Hook: Before
            if (instance is Tracked) {
                instance.tracker.trackRequest(ctx.req)
            }

            val result = proceed()

            // Hook: After
            if (instance is Tracked) {
                result.onSuccess { instance.tracker.handleResponse(ctx.req, result) }
                result.onFailure { instance.tracker.handleFailure(ctx.req, it) }
            }
            result
        }
    }


    /**
     * Applies the filter middleware to filter out requests
     */
    fun filter(proceed: () -> Result<Any, Exception>): Result<Any, Exception> {
        return log(::filter) {
            val instance = ctx.apiRef.instance

            val result = if (instance is Filter) {
                val filterResult = instance.onFilter(ctx.context, ctx.req, ctx.container, null).toResultEx()
                if (filterResult.success) {
                    proceed()
                } else {
                    logger.warn("API pipeline: filter has filtered out this request : ${filterResult.msg}")
                    filterResult
                }
            } else {
                proceed()
            }
            result
        }
    }


    /**
     * Applies the hooks middleware before/after execution of action
     */
    fun hook(proceed: () -> Result<Any, Exception>): Result<Any, Exception> {
        return log(::hook) {

            val instance = ctx.apiRef.instance

            // Hook: Before
            if (instance is Hook) {
                instance.onBefore(ctx.context, ctx.req, ctx.apiRef.action, ctx.container, null)
            }

            val result = proceed()

            // Hook: After
            if (instance is Hook) {
                instance.onAfter(ctx.context, ctx.req, ctx.apiRef.action, this, null)
            }
            result
        }
    }


    /**
     * Applies the handler middleware to either handle the request or proceed
     */
    fun handle(proceed: () -> Result<Any, Exception>): Result<Any, Exception> {
        return log(::handle) {
            val instance = ctx.apiRef.instance
            val result = if (instance is Handler) {
                val handlerResult = instance.handle(ctx.context, ctx.req, ctx.apiRef.action, ctx.container, null)
                when (handlerResult.code) {
                    Requests.codeHandlerNotProcessed -> proceed()
                    else -> handlerResult.toResultEx()
                }
            } else {
                proceed()
            }
            result
        }
    }


    private fun attempt(call: () -> Result<Any, Exception>): Result<Any, Exception> {
        // Build a message
        val result = try {
            call()
        } catch ( ex:Exception ) {
            logError("attempt", ex)
            ctx.container.errorHandler.handleError(ctx.context, ctx.container.errs, ctx.apiRef.api, ctx.apiRef, ctx.req, ex)
        }

        logger.debug("API pipeline: attempting to process: ${ctx.req.fullName} : $AFTER")

        return result
    }


    private fun log(method:KCallable<*>, call: () -> Result<Any, Exception>): Result<Any, Exception> {
        // Build a message
        val result = call()

        logger.debug("""{ "api-pipeline": "${method.name}", "path" : "${ctx.req.fullName}", """ +
                           """verb" : "${ctx.req.verb}", "success" : ${result.success}, "message" : "${result.msg}"""".trimIndent())

        result.onFailure { logError(method.name, it) }
        return result
    }


    private fun logError(method:String, ex:Exception) {
        val json = Requests.convertToJson(ctx.req, ctx.context.enc)
        logger.error("""{ "method": "$method", "path": "${ctx.req.fullName}", "request" : $json }""".trimIndent(), ex)

    }
}
