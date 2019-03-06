package slatekit.cli

import slatekit.common.DateTime
import slatekit.common.Inputs

/**
 * Represents an abstraction of a Web Api Request and also a CLI ( Command Line ) request
 * path : route(endpoint) e.g. /{area}/{name}/{action} e.g. /app/reg/activateUser
 * parts : list of the parts of the action e.g. [ "app", "reg", "activateUser" ]
 * source : protocol e.g. "cli" for command line and "http"
 * verb : get / post ( similar to http verb )
 * meta : options representing settings/configurations ( similar to http-headers )
 * data : arguments to the command
 * raw : Optional raw request ( e.g. either the HttpRequest via Spark or ShellCommmand via CLI )
 * output : Optional output format of the result e.g. json by default json | csv | props
 * tag : Optional tag for tracking individual requests and for error logging.
 */
interface Request {
        val path: String
        val parts: List<String>
        val source: String
        val verb: String
        val data: Inputs
        val meta: slatekit.common.Metadata
        val raw: Any?
        val output: String?
        val tag: String
        val version: String
        val timestamp: DateTime

    /**
     * The full path of the route
     */
    val fullName: String get() {
        return if (name.isNullOrEmpty())
            area
        else if (action.isNullOrEmpty())
            "$area.$name"
        else
            "$area.$name.$action"
    }

    /**
     * The top-most, first part of the route
     * e.g. Given /app/users/activate  , the area is 'app'
     */
    val area:String get(){ return parts.getOrElse(0) { "" } }

    /**
     * The second part of the route
     * e.g. Given /app/users/activate , the name is 'users'
     */
    val name:String get(){ return parts.getOrElse(1) { "" } }

    /**
     * The third part of the route
     */
    val action:String get(){ return  parts.getOrElse(2) { "" } }

    /**
     * Whether or not this is the path
     */
    fun isAction(targetArea: String, targetName: String, targetAction: String): Boolean {
        return area == targetArea && name == targetName && action == targetAction
    }
}