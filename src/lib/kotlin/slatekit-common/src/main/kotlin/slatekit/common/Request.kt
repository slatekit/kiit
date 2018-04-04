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
 * @param source    : protocol e.g. "cli" for command line and "http"
 * @param verb      : get / post ( similar to http verb )
 * @param meta      : options representing settings/configurations ( similar to http-headers )
 * @param data      : arguments to the command
 * @param raw       : Optional raw request ( e.g. either the HttpRequest via Spark or ShellCommmand via CLI )
 * @param output    : Optional output format of the result e.g. json by default json | csv | props
 * @param tag       : Optional tag for tracking individual requests and for error logging.
 */
data class Request (
        val path       :String,
        val parts      :List<String>,
        val source     :String,
        val verb       :String,
        val data       :Inputs?,
        val meta       :Meta?,
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


        fun path(path:String, verb: String, opts: Map<String, Any>, args: Map<String, Any>): Request {
            val parts = path.split(".")
            val area = parts[0]
            val api = parts[1]
            val action = parts[2]
            val path = if(area.isNullOrEmpty()) "$api.$action" else "$area.$api.$action"
            return Request(path, listOf(area, api, action), "cli", verb, InputArgs(args), meta = InputArgs(opts))
        }


        /**
         * builds the request
         */
        fun cli(path: String,
                            headers: List<Pair<String, Any>>?,
                            inputs: List<Pair<String, Any>>?): Request {

            fun buildArgs(inputs: List<Pair<String, Any>>?): InputArgs {

                // fill args
                val rawArgs = inputs?.let { all -> all.toMap() } ?: mapOf()
                val args = InputArgs(rawArgs)
                return args
            }

            val tokens = path.split('.').toList()
            val args = buildArgs(inputs)
            val opts = buildArgs(headers)
            val req = Request(path, tokens, "cli", "get", args, opts,
                    null, "", "", "1.0", DateTime.now())
            return req
        }


        fun cli(path: String, args: Args, opts: Meta?, verb: String, raw:Any?): Request =
                Request(path, args.actionParts, "cli", verb, args, opts, raw, "")
    }
}
