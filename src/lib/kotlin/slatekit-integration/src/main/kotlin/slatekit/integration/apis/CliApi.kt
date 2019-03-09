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

import slatekit.apis.ApiConstants
import slatekit.apis.ApiContainer
import slatekit.apis.security.CliProtocol
import slatekit.apis.core.Api
import slatekit.apis.core.Requests
import slatekit.cli.*
import slatekit.common.*
import slatekit.common.console.ConsoleWriter
import slatekit.common.requests.InputArgs
import slatekit.common.requests.Request
import slatekit.common.info.Credentials
import slatekit.common.info.Info
import slatekit.common.requests.SimpleRequest
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Tries
import java.io.File

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
        private val creds: Credentials,
        val ctx: slatekit.common.Context,
        val auth: slatekit.apis.core.Auth,
        settings: CliSettings = CliSettings(),
        apiItems: List<Api> = listOf()
        //val cliMeta: CliMeta? = null
)
   // : CLI(ctx.dirs!!, settings, Info(ctx.app, ctx.build, ctx.start, ctx.sys))
{

    fun process(line:String):Try<CliResponse<*>> = slatekit.results.Success(CliResponse.empty)
    /*
    val metaNameForApiKey = "api-key"

    // api container holding all the apis.
    val apis = ApiContainer(ctx, true, auth, apis = apiItems, protocol = CliProtocol)

    /**
     * Exposed life-cycle hook for when the shell is ending/shutting down.
     */
    override fun end(status:Status):Try<Boolean> {
        _writer.highlight("Shutting down ${info.about.name} command line")
        return Tries.success(true)
    }

    /**
     * Converts the raw CliRequest the ApiCmd for passing along the API container
     * which will ultimately delegate the call to the respective api action.
     *
     * @param cmd : The raw user entered command.
     * @return
     */
    override fun onCommandExecuteInternal(cmd: CliRequest): CliRequest {
        _writer.highlight("Executing ${info.about.name} api command " + cmd.fullName)

        // Supplying params from file ?
        return if (containsRequestLevelSystemCommand(cmd)) {
            val metaCmd = cmd.args.sys.keys.first()
            val cmdResult = when (metaCmd) {

                // Case 1: Generate a sample command to output/file
                CliConstants.SysSample -> cmd.copy(result = buildRequestSample(cmd).toTry().toResponse())

                // Case 2: Get command from params file and execute
                CliConstants.SysFile -> cmd.copy(result = apis.call(buildRequestFromFile(cmd)))

                // Case 3: Unknown
                else -> cmd
            }
            cmdResult
        } else {
            // Supply the api-key into each command.
            val meta = cliMeta?.let { cliMeta.getMetaData(ctx, cmd, creds) } ?: cmd.args.meta.plus(Pair(metaNameForApiKey, creds.key))
            val metaInputs = InputArgs(meta)
            val apiCmd = Request.cli(cmd.line, ApiConstants.SourceCLI, metaInputs, cmd.args, cmd)
            cmd.copy(result = apis.call(apiCmd))
        }
    }

    override fun showExtendedHelp(writer: ConsoleWriter) {
        apis.help.help()
    }

    override fun showHelp() {
        _view.showHelp()
    }

    /**
     * Handles help request on any part of the api request. Api requests are typically in
     * the format "area.api.action" so you can type help on each part / region.
     * e.g.
     * 1. area ?
     * 2. area.api ?
     * 3. area.api.action ?
     * @param cmd
     * @param mode
     */
    override fun showHelpFor(cmd: CliRequest, mode: Int) {
        when (mode) {
            // 1: {area} ? = help on area
            CliConstants.VerbPartArea -> {
                apis.help.area(cmd.args.getVerb(0))
            }
            // 2. {area}.{api} = help on api
            CliConstants.VerbPartApi -> {
                apis.help.api(cmd.args.getVerb(0), cmd.args.getVerb(1))
            }
            // 3. {area}.{api}.{action} = help on api action
            else -> {
                apis.help.action(cmd.args.getVerb(0), cmd.args.getVerb(1), cmd.args.getVerb(2))
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

    private fun buildRequestSample(cmd: CliRequest): Notice<String> {
        val opts = InputArgs(mapOf<String, Any>(metaNameForApiKey to creds.key))
        val apiCmd = SimpleRequest.cli(cmd.args.line, ApiConstants.SourceCLI, opts, cmd.args, cmd)

        // Generate sample json
        val fileName = cmd.args.getSysString(SysParam.Sample.id)
        val filePath = File(ctx.dirs?.pathToOutputs, fileName).absolutePath
        val file = File(filePath)
        return apis.sample(apiCmd, file)
    }

    private fun buildRequestFromFile(cmd: CliRequest): Request {
        // The file path
        val rawPath = cmd.args.getSysString(SysParam.File.id)
        val route = cmd.fullName
        val req = Requests.fromFileWithMeta(
                route,
                rawPath ?: "",
                mapOf(metaNameForApiKey to creds.key),
                ctx.enc)
        return req
    }

    private fun containsRequestLevelSystemCommand(cmd: CliRequest): Boolean {
        return cmd.args.sys.isNotEmpty() &&
                (cmd.args.sys.containsKey(SysParam.File.id) ||
                  cmd.args.sys.containsKey(SysParam.Sample.id)
                )
    }
    */
}
