/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.cli

import kiit.results.*

/**
 * Executes a single CliRequest with hooks ( before, after )
 * This can be used by derived to incorporate hooks / middle into the
 * request processing pipeline
 */
open class CliExecutor {

    /**
     * Executes the line and returns a CliResponse
     */
    open fun excecute(cli: CLI, request: CliRequest): Try<CliResponse<*>> {

        // Convert line into a CliRequest
        // Run the life-cycle methods ( before, execute, after )
        // 1. Before
        return before(cli, request)
        .then {

            // 2. Process
            process(cli, request)
        }
        .then { response ->

            // 3. After
            after(cli, response)
        }
    }

    /**
     * hook for command before it is executed
     *
     * @param request
     * @return
     */
    protected fun before(cli: CLI, request: CliRequest): Try<CliRequest> = Success(request)

    /**
     * Hook to
     */
    protected fun after(cli: CLI, response: CliResponse<*>): Try<CliResponse<*>> = Success(response)

    /**
     * Hook to
     */
    protected fun process(cli: CLI, request: CliRequest): Try<CliResponse<*>> = Success(
            CliResponse(
                    request,
                    true,
                    Codes.SUCCESS.name,
                    Status.toType(Codes.SUCCESS),
                    0,
                    mapOf(),
                    ""
            )
    )
}
