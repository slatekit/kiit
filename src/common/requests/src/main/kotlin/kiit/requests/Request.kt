/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.requests

import kiit.common.DateTime
import kiit.common.values.Inputs
import kiit.common.Source
import kiit.common.ext.toStringUtc
import kiit.common.values.Metadata

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
    val source: Source
    val verb: String
    val data: Inputs
    val meta: Metadata
    val raw: Any?
    val output: String?
    val tag: String
    val version: String
    val timestamp: DateTime

    /**
     * The full path of the route
     */
    val fullName: String
        get() {
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
    val area: String
        get() {
            return parts.getOrElse(0) { "" }
        }

    /**
     * The second part of the route
     * e.g. Given /app/users/activate , the name is 'users'
     */
    val name: String
        get() {
            return parts.getOrElse(1) { "" }
        }

    /**
     * The third part of the route
     */
    val action: String
        get() {
            return parts.getOrElse(2) { "" }
        }

    /**
     * Whether or not this is the path
     */
    fun isAction(targetArea: String, targetName: String, targetAction: String): Boolean {
        return area == targetArea && name == targetName && action == targetAction
    }

    /**
     * Descturctured into key/value pairs for structured logging
     */
    fun structured(): List<Pair<String, Any?>> {
        return listOf(
            Request::area.name to area,
            Request::name.name to name,
            Request::action.name to action,
            Request::source.name to source.id,
            Request::verb.name to verb,
            Request::tag.name to tag,
            Request::timestamp.name to timestamp.toStringUtc()
        )
    }

    /**
     * To transform / rewrite the request
     */
    fun clone(
        otherPath: String,
        otherParts: List<String>,
        otherSource: Source,
        otherVerb: String,
        otherData: Inputs,
        otherMeta: Metadata,
        otherRaw: Any?,
        otherOutput: String?,
        otherTag: String,
        otherVersion: String,
        otherTimestamp: DateTime
    ): Request
}
