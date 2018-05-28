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


object Exec {

    fun run(ctx: Ctx, execute: (Ctx) -> Result<Any, Exception>): Result<Any, Exception> {

        // Compose the various middleware components into a "pipeline"
        // using the convenient Kotlin syntax for last parameter being a lambda
        val result =
            track(ctx) {
                filter(ctx) {
                    hook(ctx) {
                        handle(ctx) {
                            execute(ctx)
                        }
                    }
                }
            }

        // for debugging
        return result
    }


    /**
     * Applies the tracking middleware to track requests, successes, failures
     */
    fun track(ctx: Ctx, call: (Ctx) -> Result<Any, Exception>): Result<Any, Exception> {
        println("before track")
        val instance = ctx.apiRef.instance

        // Hook: Before
        if (instance is Tracked) {
            instance.tracker.trackRequest(ctx.req)
        }

        val result = call(ctx)

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
    fun filter(ctx: Ctx, call: (Ctx) -> Result<Any, Exception>): Result<Any, Exception> {
        println("before filter")
        val instance = ctx.apiRef.instance

        val result = if (instance is Filter) {
            val filterResult = instance.onFilter(ctx.context, ctx.req, ctx.container, null).toResultEx()
            if (filterResult.success) {
                call(ctx)
            } else {
                filterResult
            }
        } else {
            call(ctx)
        }

        println("after filter")
        return result
    }


    /**
     * Applies the hooks middleware before/after execution of action
     */
    fun hook(ctx: Ctx, call: (Ctx) -> Result<Any, Exception>): Result<Any, Exception> {
        println("before hook")
        val instance = ctx.apiRef.instance

        // Hook: Before
        if (instance is Hook) {
            instance.onBefore(ctx.context, ctx.req, ctx.apiRef.action, ctx.container, null)
        }

        val result = call(ctx)

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
    fun handle(ctx: Ctx, call: (Ctx) -> Result<Any, Exception>): Result<Any, Exception> {
        println("before handle")

        val instance = ctx.apiRef.instance

        val result = if (instance is Handler) {
            val handlerResult = instance.handle(ctx.context, ctx.req, ctx.apiRef.action, ctx.container, null)
            when (handlerResult.code) {
                Requests.codeHandlerNotProcessed -> call(ctx)
                else -> handlerResult.toResultEx()
            }
        } else {
            call(ctx)
        }

        println("after handle")
        return result
    }
}
