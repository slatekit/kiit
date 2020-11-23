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

package slatekit.integration.apis

import slatekit.apis.ApiServer
import slatekit.apis.core.Api
import slatekit.apis.core.HelpType
import slatekit.cli.*
import slatekit.common.Source
import slatekit.common.types.Content
import slatekit.common.types.ContentType
import slatekit.common.requests.InputArgs
import slatekit.common.info.Info
import slatekit.context.Context
import slatekit.results.Codes
import slatekit.results.Success
import slatekit.results.Try

/**
 * Layer on top of the core CliService to provide support for handling command line requests
 * to your APIs using the Protocol independent APIs in the api module.
 *
 * @param creds : credentials for authentication/authorization purposes.
 * @param ctx : the app context hosting the selected environment, logger, configs and more
 * @param auth : the auth provider
 * @param settings : Settings for the shell functionality
 *
 *
 * @sample
 *
 *  area.api.action -param1=value -param2=value
 *
 * META
 *  1. sample   : generates a sample file        : $sample="sample-request.json"
 *  2. file     : loads the request from a file  : $file="create-users.json"
 *  3. code gen : generates client code for apis : $codegen=true -lang="kotlin"
 */
open class CliApi(
        val ctx: Context,
        val auth: slatekit.apis.core.Auth,
        settings: CliSettings = CliSettings(),
        apiItems: List<Api> = listOf(),
        serializer:(Any?, ContentType) -> Content,
        val metaTransform: ((Map<String,Any>) -> List<Pair<String,String>>)? = null
)
    : CLI(settings, Info(ctx.info.about, ctx.info.build, ctx.info.system), ctx.dirs, serializer = serializer)
{

    // api container holding all the apis.
    val apis = ApiServer.of(ctx, apiItems, auth, Source.CLI)

    /**
     * executes a line of text by handing it off to the executor
     * This can be overridden in derived class
     */
    override suspend fun executeRequest(request:CliRequest) : Try<CliResponse<*>> {
        val args = request.args
        context.writer.highlight("Executing ${info.about.name} api command " + request.fullName)

        // Check for help
        val helpCheck = checkForHelp(request)
        return if(helpCheck.first) {
            showHelpFor(request, helpCheck.second)
            Success(CliResponse(request, true, Codes.HELP.name, Codes.HELP.code, mapOf(), args.line), Codes.HELP)
        }
        else {
            // Supply the api-key into each command.
            val existingMeta = request.meta.toMap()
            val transformedMeta = metaTransform?.invoke(existingMeta)?.toMap() ?: existingMeta
            val metaUpdated = InputArgs(transformedMeta)
            val requestWithMeta = request.copy(meta = metaUpdated)
            val response = apis.respond(requestWithMeta)
            val cliResponse = CliResponse(
                    requestWithMeta,
                    response.success,
                    response.name,
                    response.code,
                    response.meta,
                    response.value,
                    response.desc,
                    response.err,
                    response.tag
            )
            Success(cliResponse)
        }
    }


    /**
     * Handles help request on any part of the api request. Api requests are typically in
     * the format "area.api.action" so you can type help on each part / region.
     * e.g.
     * 1. area ?
     * 2. area.api ?
     * 3. area.api.action ?
     * @param req
     * @param mode
     */
    fun showHelpFor(req: CliRequest, mode: HelpType) {
        when (mode) {
            // 1: {area} ? = help on area
            HelpType.All -> {
                apis.help.areas()
            }
            // 2: {area} ? = help on area
            HelpType.Area -> {
                apis.help.area(req.args.getVerb(0))
            }
            // 3. {area}.{api} = help on api
            HelpType.Api -> {
                apis.help.api(req.args.getVerb(0), req.args.getVerb(1))
            }
            // 4. {area}.{api}.{action} = help on api action
            HelpType.Action-> {
                apis.help.action(req.args.getVerb(0), req.args.getVerb(1), req.args.getVerb(2))
            }
            else -> {
                context.writer.failure("Unexpected command")
            }
        }
    }


    fun checkForHelp(req:CliRequest):Pair<Boolean, HelpType> {
        val args = req.args
        val hasQuestion = args.parts.isNotEmpty() && args.parts.last() == "?"
        return if( hasQuestion ) {
            when(args.parts.size ) {
                1    -> Pair(true , HelpType.All)
                2    -> Pair(true , HelpType.Area)
                3    -> Pair(true , HelpType.Api)
                4    -> Pair(true , HelpType.Action)
                else -> Pair(false, HelpType.All)
            }
        } else {
            if(args.parts.isNotEmpty() && args.parts[0] == "?" ){
                Pair(true, HelpType.All)
            } else {
                Pair(false, HelpType.All)
            }
        }
    }
}
