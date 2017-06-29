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
 * @param area      : action represented by route e.g. app in "app.reg.activateUser"
 * @param name      : name represented by route   e.g. reg in "app.reg.activateUser"
 * @param action    : action represented by route e.g. activateUser in "app.reg.activateUser"
 * @param verb      : get / post ( similar to http verb )
 * @param opts      : options representing settings/configurations ( similar to http-headers )
 * @param args      : arguments to the command
 * @param raw       : Optional raw request ( e.g. either the HttpRequest via Spark or ShellCommmand via CLI )
 * @param tag       : Optional tag for tracking individual requests and for error logging.
 */
data class Request (
                     val path       :String              ,
                     val parts      :List<String>        ,
                     val area       :String              ,
                     val name       :String              ,
                     val action     :String              ,
                     val verb       :String              ,
                     val args       :Inputs?             ,
                     val opts       :Inputs?             ,
                     val raw        :Any?          = null,
                     val tag        :String        = ""
                   ) {

    val fullName: String get() {
        return if (name.isNullOrEmpty())
            area
        else if (action.isNullOrEmpty())
            area + "." + name
        else
            "$area.$name.$action"
    }


    fun isPath(targetArea: String, targetName: String, targetAction: String): Boolean {
        return area == targetArea && name == targetName && action == targetAction
    }


    companion object RequestCompanion {

        fun build(area: String, api: String, action: String, verb: String, opts: Map<String, Any>, args: Map<String, Any>): Request {
            val path = "$area.$api.$action"
            return Request(path, listOf(area, api, action), area, api, action, verb, InputArgs(args), opts = InputArgs(opts))
        }


        fun build(path: String, args: Args, opts: Inputs?, verb: String): Request =
                Request(path, args.actionVerbs, args.getVerb(0), args.getVerb(1), args.getVerb(2), verb, args, opts, "")


        fun build(path: String, args: Args, argsInputs: Inputs?, opts: Inputs?, verb: String): Request =
                Request(path, args.actionVerbs, args.getVerb(0), args.getVerb(1), args.getVerb(2), verb, argsInputs, opts, "")
    }
}
