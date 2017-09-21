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

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.apis.ApiConstants
import slatekit.apis.core.Headers
import slatekit.apis.core.Params
import slatekit.common.Random
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.Uris
import slatekit.core.cli.CliCommand
import java.io.File

/**
 * Layer on top of the core CliService to provide support for handling command line requests
 * to your APIs using the Protocol independent APIs in the api module.
 * @param creds  : credentials for authentication/authorization purposes.
 * @param ctx    : the app context hosting the selected environment, logger, configs and more
 * @param auth   : the auth provider
 * @param appDir : deprecated
 * @param settings : Settings for the shell functionality
 */
class CliApi(private val creds: slatekit.common.Credentials,
             val ctx: slatekit.common.Context,
             val auth: slatekit.apis.core.Auth,
             val appDir: String,
             settings: slatekit.core.cli.CliSettings = slatekit.core.cli.CliSettings(),
             apiItems: List<slatekit.apis.ApiReg>? = null
)
    : slatekit.core.cli.CliService(ctx.dirs!!, settings, ctx.app) {

    // api container holding all the apis.
    val apis = slatekit.apis.containers.ApiContainerCLI(ctx, auth, apiItems)


    /**
     * Exposed life-cycle hook for when the shell is starting up.
     */
    override fun onShellStart(): Unit {
        // You don't need to override this as the base method displays help info
        _view.showHelp()
        _writer.highlight("\tStarting up ${_appMeta.about.name} command line")
    }


    /**
     * Exposed life-cycle hook for when the shell is ending/shutting down.
     */
    override fun onShellEnd(): Unit {
        _writer.highlight("\tShutting down ${_appMeta.about.name} command line")
    }


    /**
     * Exposed life-cycle hook to do some work before executing the command
     * @param cmd : The raw user entered command
     * @return
     */
    override fun onCommandBeforeExecute(cmd: slatekit.core.cli.CliCommand): slatekit.core.cli.CliCommand {
        _writer.highlight("\t")
        return cmd
    }


    /**
     * Converts the raw CliCommand the ApiCmd for passing along the API container
     * which will ultimately delegate the call to the respective api action.
     *
     * @param cmd : The raw user entered command.
     * @return
     */
    override fun onCommandExecuteInternal(cmd: slatekit.core.cli.CliCommand): slatekit.core.cli.CliCommand {
        _writer.highlight("Executing ${_appMeta.about.name} api command " + cmd.fullName())

        // Create request from cmd
        val apiCmd = createRequest(cmd)

        return cmd.copy(result = apis.call(apiCmd))
    }


    /**
     * Use case 3d: ( OPTIONAL ) do some stuff after the command execution
     *
     * @param cmd
     * @return
     */
    override fun onCommandAfterExecute(cmd: slatekit.core.cli.CliCommand): slatekit.core.cli.CliCommand {
        return super.onCommandAfterExecute(cmd)
        // Do anything app specific else here.
    }


    override fun showHelp(): Unit {
        _view.showHelp()
        apis.help.help()
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
    override fun showHelpFor(cmd: slatekit.core.cli.CliCommand, mode: Int): Unit {
        when (mode) {
            // 1: {area} ? = help on area
            slatekit.core.cli.CliConstants.VerbPartArea -> {
                apis.help.helpForArea(cmd.args.getVerb(0))
            }
            // 2. {area}.{api} = help on api
            slatekit.core.cli.CliConstants.VerbPartApi  -> {
                apis.help.helpForApi(cmd.args.getVerb(0), cmd.args.getVerb(1))
            }
            // 3. {area}.{api}.{action} = help on api action
            else                                        -> {
                apis.help.helpForAction(cmd.args.getVerb(0), cmd.args.getVerb(1), cmd.args.getVerb(2))
            }
        }
    }


    override fun showResult(cmd: CliCommand, result: slatekit.common.Result<Any>): Unit {
        _printer.printResult(cmd, result)
    }


    private fun createRequest(cmd:CliCommand): Request {

        // Supplying params from file ?
        return if(cmd.args.containsMetaKey("params")) {

            // The file path
            val rawPath = cmd.args.getMetaString("params")

            // Get the json data
            val json = rawPath?.let { raw ->
                val path = Uris.interpret(raw)
                val content = File(path).readText()
                val parser = JSONParser()
                val root = parser.parse(content)
                val json = root as JSONObject
                json

            } ?: JSONObject()

            // Create request
            slatekit.common.Request(
                    path = cmd.line,
                    parts = cmd.args.actionVerbs,
                    protocol =  ApiConstants.ProtocolCLI,
                    verb =  ApiConstants.ProtocolCLI,
                    opts = Headers(cmd.args.meta, ctx.enc),
                    args = Params(cmd, "cli", true, ctx.enc, json),
                    raw = cmd,
                    tag = Random.stringGuid()
            )
        }
        else {
            // Supply the api-key into each command.
            val opts = slatekit.common.InputArgs(mapOf<String, Any>("api-key" to creds.key))
            val apiCmd = slatekit.common.Request.cli(cmd.line, cmd.args, opts, ApiConstants.ProtocolCLI, cmd)
            apiCmd
        }
    }

    override fun collectSummaryExtra(): List<Pair<String, String>>? {
        return listOf(
                Pair("db.conn", ctx.dbs?.default()?.url ?: ""),
                Pair("db.user", ctx.dbs?.default()?.user ?: ""),
                Pair("dirs.app", ctx.dirs?.pathToApp ?: "" )
        )
    }
}
