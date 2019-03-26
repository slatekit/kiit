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

import slatekit.apis.ApiHost
import slatekit.apis.security.CliProtocol
import slatekit.apis.core.Api
import slatekit.cli.*
import slatekit.common.args.Args
import slatekit.common.requests.InputArgs
import slatekit.common.info.Credentials
import slatekit.common.info.Info
import slatekit.results.StatusCodes
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
class CliApi(
        val ctx: slatekit.common.Context,
        val auth: slatekit.apis.core.Auth,
        settings: CliSettings = CliSettings(),
        apiItems: List<Api> = listOf(),
        val metaTransform: ((Map<String,Any>) -> List<Pair<String,String>>)? = null
        //val cliMeta: CliMeta? = null
)
    : CLI(settings, Info(ctx.app, ctx.build, ctx.start, ctx.sys), ctx.dirs)
{

    val metaNameForApiKey = "api-key"

    // api container holding all the apis.
    val apis = ApiHost(ctx, true, auth, apis = apiItems, protocol = CliProtocol)

    enum class ApiHelpType {
        Listing,
        Area,
        Api,
        Action,
        NA
    }

    /**
     * executes a line of text by handing it off to the executor
     * This can be overridden in derived class
     */
    override fun executeRequest(request:CliRequest) : Try<CliResponse<*>> {
        val args = request.args
        context.writer.highlight("Executing ${info.about.name} api command " + request.fullName)

        // Check for help
        val helpCheck = checkForHelp(request)
        return if(helpCheck.first) {
            showHelpFor(request, helpCheck.second)
            Success(CliResponse(request, true, StatusCodes.HELP.code, mapOf(), args.line), StatusCodes.HELP)
        }
        else {
            // Supply the api-key into each command.
            val existingMeta = request.meta.toMap()
            val transformedMeta = metaTransform?.invoke(existingMeta)?.toMap() ?: existingMeta
            val metaUpdated = InputArgs(transformedMeta)
            val requestWithMeta = request.copy(meta = metaUpdated)
            val response = apis.call(requestWithMeta)
            val cliResponse = CliResponse(
                    requestWithMeta,
                    response.success,
                    response.code,
                    response.meta,
                    response.value,
                    response.msg,
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
    fun showHelpFor(req: CliRequest, mode: ApiHelpType) {
        when (mode) {
            // 1: {area} ? = help on area
            ApiHelpType.Listing -> {
                apis.help.help()
            }
            // 2: {area} ? = help on area
            ApiHelpType.Area -> {
                apis.help.area(req.args.getVerb(0))
            }
            // 3. {area}.{api} = help on api
            ApiHelpType.Api -> {
                apis.help.api(req.args.getVerb(0), req.args.getVerb(1))
            }
            // 4. {area}.{api}.{action} = help on api action
            ApiHelpType.Action-> {
                apis.help.action(req.args.getVerb(0), req.args.getVerb(1), req.args.getVerb(2))
            }
            else -> {
                context.writer.failure("Unexpected command")
            }
        }
    }


    fun checkForHelp(req:CliRequest):Pair<Boolean, ApiHelpType> {
        val args = req.args
        val hasQuestion = args.actionParts.isNotEmpty() && args.actionParts.last() == "?"
        return if( hasQuestion ) {
            when(args.actionParts.size ) {
                1    -> Pair(true, ApiHelpType.Listing)
                2    -> Pair(true, ApiHelpType.Area)
                3    -> Pair(true, ApiHelpType.Api)
                4    -> Pair(true, ApiHelpType.Action)
                else -> Pair(false, ApiHelpType.NA)
            }
        } else {
            if(args.actionParts.isNotEmpty() && args.actionParts[0] == "?" ){
                Pair(true, ApiHelpType.Listing)
            } else {
                Pair(false, ApiHelpType.NA)
            }
        }
    }

//    override fun collectSummaryExtra(): List<Pair<String, String>>? {
//        return listOf(
//                Pair("db.conn", ctx.cfg.dbCon().url ),
//                Pair("db.user", ctx.cfg.dbCon().user),
//                Pair("dirs.app", ctx.dirs?.pathToApp ?: "")
//        )
//    }

//    private fun buildRequestSample(cmd: CliRequest): Notice<String> {
//        val opts = InputArgs(mapOf<String, Any>(metaNameForApiKey to creds.key))
//        val apiCmd = SimpleRequest.cli(cmd.args.line, ApiConstants.SourceCLI, opts, cmd.args, cmd)
//
//        // Generate sample json
//        val fileName = cmd.args.getSysString(SysParam.Sample.id)
//        val filePath = File(ctx.dirs?.pathToOutputs, fileName).absolutePath
//        val file = File(filePath)
//        return apis.sample(apiCmd, file)
//    }
//
//    private fun buildRequestFromFile(cmd: CliRequest): Request {
//        // The file path
//        val rawPath = cmd.args.getSysString(SysParam.File.id)
//        val route = cmd.fullName
//        val req = Requests.fromFileWithMeta(
//                route,
//                rawPath ?: "",
//                mapOf(metaNameForApiKey to creds.key),
//                ctx.enc)
//        return req
//    }
//
//
//    private fun containsRequestLevelSystemCommand(cmd: CliRequest): Boolean {
//        return cmd.args.sys.isNotEmpty() &&
//                (cmd.args.sys.containsKey(SysParam.File.id) ||
//                  cmd.args.sys.containsKey(SysParam.Sample.id)
//                )
//    }
}
