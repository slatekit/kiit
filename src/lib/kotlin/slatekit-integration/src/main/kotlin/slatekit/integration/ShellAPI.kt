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

package slatekit.integration

import TODO
import slatekit.apis.ApiConstants
import slatekit.apis.ApiReg
import slatekit.apis.containers.ApiContainerCLI
import slatekit.apis.core.Auth
import slatekit.common.Credentials
import slatekit.common.InputArgs
import slatekit.common.Request
import slatekit.common.Result
import slatekit.core.common.AppContext
import slatekit.core.shell.ShellCommand
import slatekit.core.shell.ShellConstants
import slatekit.core.shell.ShellService
import slatekit.core.shell.ShellSettings

/**
 * Layer on top of the core ShellService to provide support for handling command line requests
 * to your APIs using the Protocol independent APIs in the api module.
 * @param creds  : credentials for authentication/authorization purposes.
 * @param ctx    : the app context hosting the selected environment, logger, configs and more
 * @param auth   : the auth provider
 * @param appDir : deprecated
 * @param settings : Settings for the shell functionality
 */
class ShellAPI(private val creds: Credentials,
               val ctx: AppContext,
               val auth: Auth,
               val appDir: String,
               settings: ShellSettings = ShellSettings(),
               apiItems: List<ApiReg>? = null
)
    : ShellService(ctx.dirs!!, settings, ctx.app) {

    // api container holding all the apis.
    val apis = ApiContainerCLI(ctx, auth, apiItems)

    // configure the root directory in user directory for this app.
    //configure(appDir, s".${appDir}", true)


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
    override fun onCommandBeforeExecute(cmd: ShellCommand): ShellCommand {
        _writer.highlight("\t")
        return cmd
    }


    /**
     * Converts the raw ShellCommand the ApiCmd for passing along the API container
     * which will ultimately delegate the call to the respective api action.
     *
     * @param cmd : The raw user entered command.
     * @return
     */
    override fun onCommandExecuteInternal(cmd: ShellCommand): ShellCommand {
        _writer.highlight("Executing ${_appMeta.about.name} api command " + cmd.fullName())

        // Supply the api-key into each command.
        val opts = InputArgs(mapOf<String, Any>("api-key" to creds.key))
        val apiCmd = Request.build(cmd.line, cmd.args, opts, ApiConstants.ProtocolCLI)
        return cmd.copy(result = apis.call(apiCmd))
    }


    /**
     * Use case 3d: ( OPTIONAL ) do some stuff after the command execution
     *
     * @param cmd
     * @return
     */
    override fun onCommandAfterExecute(cmd: ShellCommand): ShellCommand {
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
    override fun showHelpFor(cmd: ShellCommand, mode: Int): Unit {
        when (mode) {
        // 1: {area} ? = help on area
            ShellConstants.VerbPartArea -> {
                apis.help.helpForArea(cmd.args.getVerb(0))
            }
        // 2. {area}.{api} = help on api
            ShellConstants.VerbPartApi  -> {
                apis.help.helpForApi(cmd.args.getVerb(0), cmd.args.getVerb(1))
            }
        // 3. {area}.{api}.{action} = help on api action
            else                        -> {
                apis.help.helpForAction(cmd.args.getVerb(0), cmd.args.getVerb(1), cmd.args.getVerb(2))
            }
        }
    }


    override fun showResult(result: Result<Any>): Unit {
        TODO.BUG("entities")
        //_printer.setEntities(apis.ctx.ent)
        _printer.printResult(result)
    }
}
