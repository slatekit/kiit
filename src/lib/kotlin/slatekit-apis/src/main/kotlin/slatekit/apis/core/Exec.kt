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


/**
 * Executes the API action using a pipeline of steps
 */
class Exec(val ctx:Ctx, val validator:Validation, val logger:Logger) {

    /**
     * Executes the final API action after going through all the middleware first.
     */
    fun run(execute: (Ctx) -> Result<Any, Exception>): Result<Any, Exception> {

        // Compose the various middleware components into a "pipeline"
        // using the convenient Kotlin syntax for single/last parameter being a lambda
        val result =

            // Ensure protocol
            protocol {

                // Ensure auth
                auth {

                    // Ensure global middleware
                    middleware {

                        // Ensure params
                        params {

                            // BEGIN: Instance Middleware
                            track {

                                // Filter requests
                                filter {

                                    // Hooks for before/after events
                                    hook {

                                        // Custom handling
                                        handle {

                                            // Finally  execute
                                            execute(ctx)
                                        }
                                    }
                                }
                            }
                            // END: Instance Middleware
                        }
                    }
                }
            }

        // for debugging
        return result
    }


    /**
     * Ensures valid protocols before processing the call
     */
    fun protocol(call: () -> Result<Any, Exception>): Result<Any, Exception> {
        println("before protocol")

        val check = validator.validateProtocol(ctx.req, ctx.apiRef)
        val result = if(check.success) {
            call()
        } else {
            check.toResultEx()
        }

        println("after protocol")
        return result
    }


    /**
     * Ensures valid authorization before processing the call
     */
    fun auth(call: () -> Result<Any, Exception>): Result<Any, Exception> {
        println("before auth")

        val check = validator.validateAuthorization(ctx.req, ctx.apiRef)
        val result = if(check.success) {
            call()
        } else {
            check.toResultEx()
        }

        println("after auth")
        return result
    }


    /**
     * Ensures valid authorization before processing the call
     */
    fun middleware(call: () -> Result<Any, Exception>): Result<Any, Exception> {
        println("before params")

        val check = validator.validateMiddleware(ctx.req, ctx.container.filters)
        val result = if(check.success) {
            call()
        } else {
            check.toResultEx()
        }

        println("after params")
        return result
    }


    /**
     * Ensures valid authorization before processing the call
     */
    fun params(call: () -> Result<Any, Exception>): Result<Any, Exception> {
        println("before params")

        val check = validator.validateParameters(ctx.req)
        val result = if(check.success) {
            call()
        } else {
            check.toResultEx()
        }

        println("after params")
        return result
    }


    /**
     * Applies the tracking middleware to track requests, successes, failures
     */
    fun track(call: () -> Result<Any, Exception>): Result<Any, Exception> {
        println("before track")
        val instance = ctx.apiRef.instance

        // Hook: Before
        if (instance is Tracked) {
            instance.tracker.trackRequest(ctx.req)
        }

        val result = call()

        // Hook: After
        if (instance is Tracked) {
            result.onSuccess { instance.tracker.handleResponse(ctx.req, result) }
            result.onFailure { instance.tracker.handleFailure(ctx.req, it) }
        }
        println("after track")
        return result
    }


    /**
     * Applies the filter middleware to filter out requests
     */
    fun filter(call: () -> Result<Any, Exception>): Result<Any, Exception> {
        println("before filter")
        val instance = ctx.apiRef.instance

        val result = if (instance is Filter) {
            val filterResult = instance.onFilter(ctx.context, ctx.req, ctx.container, null).toResultEx()
            if (filterResult.success) {
                call()
            } else {
                filterResult
            }
        } else {
            call()
        }

        println("after filter")
        return result
    }


    /**
     * Applies the hooks middleware before/after execution of action
     */
    fun hook(call: () -> Result<Any, Exception>): Result<Any, Exception> {
        println("before hook")
        val instance = ctx.apiRef.instance

        // Hook: Before
        if (instance is Hook) {
            instance.onBefore(ctx.context, ctx.req, ctx.apiRef.action, ctx.container, null)
        }

        val result = call()

        // Hook: After
        if (instance is Hook) {
            instance.onAfter(ctx.context, ctx.req, ctx.apiRef.action, this, null)
        }

        println("after hook")

        return result
    }


    /**
     * Applies the handler middleware to either handle the request or proceed to make the call
     */
    fun handle(call: () -> Result<Any, Exception>): Result<Any, Exception> {
        println("before handle")

        val instance = ctx.apiRef.instance

        val result = if (instance is Handler) {
            val handlerResult = instance.handle(ctx.context, ctx.req, ctx.apiRef.action, ctx.container, null)
            when (handlerResult.code) {
                Requests.codeHandlerNotProcessed -> call()
                else -> handlerResult.toResultEx()
            }
        } else {
            call()
        }

        println("after handle")
        return result
    }
}
