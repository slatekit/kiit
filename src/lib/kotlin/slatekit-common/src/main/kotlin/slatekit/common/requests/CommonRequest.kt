package slatekit.common.requests

import slatekit.common.DateTime
import slatekit.common.Inputs
import slatekit.common.Metadata
import slatekit.common.Source
import slatekit.common.args.Args


/**
 * Represents an abstraction of a Web Api Request and also a CLI ( Command Line ) request
 * @param path : route(endpoint) e.g. /{area}/{name}/{action} e.g. /app/reg/activateUser
 * @param parts : list of the parts of the action e.g. [ "app", "reg", "activateUser" ]
 * @param source : protocol e.g. "cli" for command line and "http"
 * @param verb : get / post ( similar to http verb )
 * @param meta : options representing settings/configurations ( similar to http-headers )
 * @param data : arguments to the command
 * @param raw : Optional raw request ( e.g. either the HttpRequest via Spark or ShellCommmand via CLI )
 * @param output : Optional output format of the result e.g. json by default json | csv | props
 * @param tag : Optional tag for tracking individual requests and for error logging.
 */
data class CommonRequest(
        override val path: String,
        override val parts: List<String>,
        override val source: Source,
        override val verb: String,
        override val data: Inputs,
        override val meta: Metadata,
        override val raw: Any? = null,
        override val output: String? = "",
        override val tag: String = "",
        override val version: String = "1.0",
        override val timestamp: DateTime = DateTime.now()
) : Request {

    /**
     * To transform / rewrite the request
     */
    override fun clone(
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
            otherTimestamp: DateTime) : Request {
        return this.copy(
                path      = otherPath,
                parts     = otherParts,
                source    = otherSource,
                verb      = otherVerb,
                data      = otherData,
                meta      = otherMeta,
                raw       = otherRaw,
                output    = otherOutput,
                tag       = otherTag,
                version   = otherVersion,
                timestamp = otherTimestamp
        )
    }


    companion object {

        /**
         * Builds a request that is designated as a web request with empty data and meta objects.
         */
        @JvmStatic
        fun web(area: String, api: String, action: String, verb: String, opts: Map<String, Any>, args: Map<String, Any>, raw: Any? = null): Request {
            val path = if (area.isNullOrEmpty()) "$api.$action" else "$area.$api.$action"
            return CommonRequest(path, listOf(area, api, action), Source.Web, verb, InputArgs(args), meta = InputArgs(opts), raw = raw)
        }

        /**
         * Builds a request that is designated as a cli request using the raw data/meta supplied
         */
        @JvmStatic
        fun cli(area: String, api: String, action: String, verb: String, opts: Map<String, Any>, args: Map<String, Any>, raw: Any? = null): Request {
            val path = if (area.isNullOrEmpty()) "$api.$action" else "$area.$api.$action"
            return CommonRequest(path, listOf(area, api, action), Source.CLI, verb, InputArgs(args), meta = InputArgs(opts), raw = raw)
        }

        /**
         * Builds a cli based request using pre-build data/meta
         */
        @JvmStatic
        fun cli(path: String, verb: String, meta: Metadata?, args: Args, raw: Any?): Request {
            return CommonRequest(path, args.parts, Source.CLI, verb, args, meta
                    ?: InputArgs(mapOf()), raw, "")
        }

        /**
         * builds the request using the path and raw meta/data supplied.
         * NOTE: This is used mostly for testing purposes
         */
        @JvmStatic
        fun cli(
                path: String,
                headers: List<Pair<String, Any>>?,
                inputs: List<Pair<String, Any>>?
        ): Request {

            fun buildArgs(inputs: List<Pair<String, Any>>?): InputArgs {

                // fill args
                val rawArgs = inputs?.let { all -> all.toMap() } ?: mapOf()
                val args = InputArgs(rawArgs)
                return args
            }

            val tokens = path.split('.').toList()
            val args = buildArgs(inputs)
            val opts = buildArgs(headers)
            val req = CommonRequest(path, tokens, Source.CLI, "get", args, opts,
                    null, "", "", "1.0", DateTime.now())
            return req
        }

        @JvmStatic
        fun path(path: String, verb: String, opts: Map<String, Any>, args: Map<String, Any>, raw: Any? = null): Request {
            val parts = path.split(".")
            val area = parts[0]
            val api = parts[1]
            val action = parts[2]
            return cli(area, api, action, verb, opts, args, raw)
        }
    }
}


