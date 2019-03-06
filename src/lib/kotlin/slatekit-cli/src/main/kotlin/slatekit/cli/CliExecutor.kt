/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.cli

import slatekit.common.args.Args
import slatekit.common.info.Folders
import slatekit.results.*

/**
 * Executes a single CliRequest
 *
 * @param _appMeta : Metadata about the app used for displaying help about app
 * @param folders : Used to write output to app directories
 * @param settings : Settings for the shell functionality
 */
open class CliExecutor(
        val folders: Folders,
        val settings: CliSettings,
        val view:CliHelp
) {

    fun run(line:String): Try<CliResponse<*>> {

        // Convert line into a CliRequest
        // Run the life-cycle methods ( before, execute, after )
        val req = convert(line, true)

        // 1. Before
        val res = req.then { request ->

            before(request)
        }
        // 2. Execute
        .then { request ->

            execute(request).fold( { Success(Pair(request, it)) }, { Failure(it) } )
        }
        // 3. After
        .then { info ->

            after(info)
        }

        return res
    }

    /**
     * hook for command before it is executed
     *
     * @param request
     * @return
     */
    protected fun before(request: CliRequest): Try<CliRequest> = Success(request)


    /**
     * Hook to
     */
    protected fun after(info:Pair<CliRequest, CliResponse<*>>): Try<Pair<CliRequest, CliResponse<*>>> = Success(info)


    /**
     * Hook to
     */
    protected fun execute(request: CliRequest): Try<CliResponse<*>> = Success(CliResponse(
            true, 0, mapOf(), ""
    ))


    private fun convert(line: String, checkHelp: Boolean): Try<CliRequest> {
        // 1st step, parse the command line into arguments
        val argsResult = Args.parse(line, settings.argPrefix, settings.argSeparator, true)
        return Success(CliRequest.build((argsResult as Success<Args>).value, line))
    }
}
