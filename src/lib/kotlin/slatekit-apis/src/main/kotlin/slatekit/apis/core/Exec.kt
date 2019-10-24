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

import slatekit.apis.ApiRequest
import slatekit.apis.Filter
import slatekit.apis.Hooks
import slatekit.common.log.Logger
import slatekit.common.toResponse
import slatekit.results.Try
import slatekit.results.builders.Notices
import slatekit.results.builders.Tries

/**
 * Executes the API action using a pipeline of steps
 */
class Exec(val ctx: Ctx, val req:ApiRequest, val logger: Logger, val options: ExecOptions?) {

    /**
     * Executes the final API action after going through all the middleware first.
     */
    fun run(execute: (Ctx) -> Try<Any>): Try<Any> {
        // attempt | track | protocol | auth | middleware | params | diagnostics | filter | hook | handle | exec
        val result = execute(this.ctx)

        result.onFailure { logError("exec", it) }
        return result
    }


    inline fun log(method: String, call: () -> Try<Any>): Try<Any> {
        // Build a message
        val result = call()

        logger.debug("""{ "api-pipeline": "${method}", "path" : "${ctx.req.fullName}", """ +
                           """verb" : "${ctx.req.verb}", "success" : ${result.success}, "message" : "${result.msg}"""".trimIndent())

        return result
    }

     fun logError(method: String, ex: Exception) {
        val json = Requests.toJsonString(ctx.req, ctx.context.enc)
        logger.error("""{ "method": "$method", "path": "${ctx.req.fullName}", "request" : $json }""".trimIndent(), ex)
    }
}



