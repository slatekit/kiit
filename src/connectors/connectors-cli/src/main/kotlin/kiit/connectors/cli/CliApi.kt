/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 *
 *  </kiit_header>
 */

package kiit.connectors.cli

import kiit.apis.ApiServer
import kiit.apis.routes.Api
import kiit.apis.core.Part
import kiit.apis.routes.VersionAreas
import kiit.cli.CLI
import kiit.cli.CliRequest
import kiit.cli.CliResponse
import kiit.cli.CliSettings
import kiit.common.Source
import kiit.common.types.Content
import kiit.common.types.ContentType
import kiit.requests.InputArgs
import kiit.context.Context
import kiit.results.Codes
import kiit.results.Status
import kiit.results.Success
import kiit.results.Try
import kiit.utils.writer.ConsoleWriter

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
    val auth: kiit.apis.core.Auth,
    settings: CliSettings = CliSettings(),
    routes: List<VersionAreas> = listOf(),
    serializer: (Any?, ContentType) -> Content,
    val metaTransform: ((Map<String, Any>) -> List<Pair<String, String>>)? = null
) : CLI(settings, ctx.info, ctx.dirs, serializer = serializer) {
    // api container holding all the apis.
    val apis = ApiServer.of(ctx, routes, auth, Source.CLI)

    /**
     * executes a line of text by handing it off to the executor
     * This can be overridden in derived class
     */
    override suspend fun executeRequest(request: CliRequest): Try<CliResponse<*>> {
        val args = request.args
        context.writer.highlight("Executing ${info.about.name} api command " + request.fullName)

        // Check for help
        val helpCheck = checkForHelp(request)
        return if (helpCheck.first) {
            showHelpFor(request, helpCheck.second)
            Success(
                CliResponse(
                    request,
                    true,
                    Codes.HELP.name,
                    Status.toType(Codes.HELP),
                    Codes.HELP.code,
                    mapOf(),
                    args.line
                ),
                Codes.HELP
            )
        } else {
            // Supply the api-key into each command.
            val existingMeta = request.meta.toMap()
            val transformedMeta = metaTransform?.invoke(existingMeta)?.toMap() ?: existingMeta
            val metaUpdated = InputArgs(transformedMeta)
            val requestWithMeta = request.copy(meta = metaUpdated)
            val response = apis.executeResponse(requestWithMeta)
            val cliResponse = CliResponse(
                requestWithMeta,
                response.success,
                response.name,
                response.type,
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
    fun showHelpFor(req: CliRequest, mode: Part) {
        when (mode) {
            // 1: {area} ? = help on area
            Part.All -> {
                apis.help.areas()
            }
            // 2: {area} ? = help on area
            Part.Area -> {
                apis.help.area(req.args.getVerb(0))
            }
            // 3. {area}.{api} = help on api
            Part.Api -> {
                apis.help.api(req.args.getVerb(0), req.args.getVerb(1))
            }
            // 4. {area}.{api}.{action} = help on api action
            Part.Action -> {
                apis.help.action(req.args.getVerb(0), req.args.getVerb(1), req.args.getVerb(2))
            }

            else -> {
                context.writer.failure("Unexpected command")
            }
        }
    }

    fun checkForHelp(req: CliRequest): Pair<Boolean, Part> {
        val args = req.args
        val hasQuestion = args.parts.isNotEmpty() && args.parts.last() == "?"
        return if (hasQuestion) {
            when (args.parts.size) {
                1 -> Pair(true, Part.All)
                2 -> Pair(true, Part.Area)
                3 -> Pair(true, Part.Api)
                4 -> Pair(true, Part.Action)
                else -> Pair(false, Part.All)
            }
        } else {
            if (args.parts.isNotEmpty() && args.parts[0] == "?") {
                Pair(true, Part.All)
            } else {
                Pair(false, Part.All)
            }
        }
    }

    open fun showOverview(name: String) {
        val writer = ConsoleWriter()

        writer.text("**********************************************")
        writer.title("Welcome to $name")
        writer.text("You can organize, discover, execute actions")
        writer.text("**********************************************")
        writer.text("")

        // Routing
        writer.highlight("1) ROUTING:   Actions are organized into 3 part routes ( AREAS, APIS, ACTIONS )")
        writer.subTitle("{area}.{api}.{action}")
        writer.text("e.g. in the example, the area = netflix, api = search, action = movies")
        writer.url("1. samples.all.greet ")
        writer.text("")

        // Discovery
        writer.text("")
        writer.highlight("2) DISCOVERY: Actions are easily discovered by using \"?\"")
        writer.subTitle("?                ", false); writer.text("- to show all areas")
        writer.subTitle("area ?           ", false); writer.text("- to show all apis in an area")
        writer.subTitle("area.api ?       ", false); writer.text("- to show all actions in an api.area")
        writer.subTitle("area.api.action ?", false); writer.text("- to show all inputs to api.area.action")
        writer.text("e.g. you can run the following commands to discover areas, apis, actions, inputs to actions: ")
        writer.url("1. ?")
        writer.url("2. samples ?")
        writer.url("3. samples.all ?")
        writer.url("4. samples.all.add ?")
        writer.text("")

        // Universal
        writer.text("")
        writer.highlight("3) EXECUTION: Actions can be executed by their name and passing inputs")
        writer.subTitle("{area}.{api}.{action} -key=value")
        writer.url("1. samples.all.add  -a=1 -b=2")
        writer.url("2. samples.all.movies  -category=\"drama\"")
        writer.text("")
        writer.text("type \"?\" to discover areas!")
        writer.text("")
    }
}
