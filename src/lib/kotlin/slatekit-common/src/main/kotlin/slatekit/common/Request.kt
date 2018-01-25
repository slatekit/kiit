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

package slatekit.common

import slatekit.common.args.Args


/**
 * Represents an abstraction of a Web Api Request and also a CLI ( Command Line ) request
 * @param path      : route(endpoint) e.g. /{area}/{name}/{action} e.g. /app/reg/activateUser
 * @param parts     : list of the parts of the action e.g. [ "app", "reg", "activateUser" ]
 * @param source  : protocol e.g. "cli" for command line and "http"
 * @param verb      : get / post ( similar to http verb )
 * @param meta      : options representing settings/configurations ( similar to http-headers )
 * @param data      : arguments to the command
 * @param raw       : Optional raw request ( e.g. either the HttpRequest via Spark or ShellCommmand via CLI )
 * @param output
 * : Optional output format of the result e.g. json by default json | csv | props
 * @param tag       : Optional tag for tracking individual requests and for error logging.
 */
data class Request (
        val path       :String,
        val parts      :List<String>,
        val source     :String,
        val verb       :String,
        val data       :Inputs?,
        val meta       :Inputs?,
        val raw        :Any?          = null,
        val output     :String?       = "",
        val tag        :String        = "",
        val version    :String        = "1.0",
        val timestamp  :DateTime      = DateTime.now()
                   ) {

    /**
     * The full path of the route
     */
    val fullName: String get() {
        return if (name.isNullOrEmpty())
            area
        else if (action.isNullOrEmpty())
            area + "." + name
        else
            "$area.$name.$action"
    }


    /**
     * The top-most, first part of the route
     * e.g. Given /app/users/activate  , the area is 'app'
     */
    val area = parts.getOrElse(0, { _ -> "" })


    /**
     * The second part of the route
     * e.g. Given /app/users/activate , the name is 'users'
     */
    val name = parts.getOrElse(1, { _ -> "" })


    /**
     * The third part of the route
     */
    val action = parts.getOrElse(2, { _ -> "" })


    fun isPath(targetArea: String, targetName: String, targetAction: String): Boolean {
        return area == targetArea && name == targetName && action == targetAction
    }


    companion object {

        fun raw(area: String, api: String, action: String, verb: String, opts: Map<String, Any>, args: Map<String, Any>): Request {
            val path = if(area.isNullOrEmpty()) "$api.$action" else "$area.$api.$action"
            return Request(path, listOf(area, api, action), "cli", verb, InputArgs(args), meta = InputArgs(opts))
        }


        fun cli(path: String, args: Args, opts: Inputs?, verb: String, raw:Any?): Request =
                Request(path, args.actionParts, "cli", verb, args, opts, raw, "")
    }
}
