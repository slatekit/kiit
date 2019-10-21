package slatekit.cmds

import slatekit.common.DateTime
import slatekit.common.Inputs
import slatekit.common.Metadata
import slatekit.common.args.Args
import slatekit.common.requests.InputArgs
import slatekit.common.requests.Request
import slatekit.common.requests.Source

/**
 * Represents the request to the command
 * @param args  : the arguments supplied
 * @param meta : options representing settings/configurations ( similar to http-headers )
 * @param data : arguments to the command
 * @param raw : Optional raw request ( e.g. either the HttpRequest via Spark or ShellCommmand via CLI )
 * @param output : Optional output format of the result e.g. json by default json | csv | props
 * @param tag : Optional tag for tracking individual requests and for error logging.
 */
data class CommandRequest(
        val args: Args,
        override val data: Inputs = args,
        override val meta: Metadata = InputArgs(args.meta),
        override val verb: String = Source.Cmd.id,
        override val raw: Any? = null,
        override val output: String? = "",
        override val tag: String = "",
        override val version: String = "1.0",
        override val timestamp: DateTime = DateTime.now()

) : Request {

    /**
     * E.g. [ "app", "info", "version" ]
     */
    override val parts: List<String> = args.parts

    /**
     * E.g. "app.info.version"
     */
    override val path: String = parts.joinToString(".")

    /**
     * Defaulted to CLI for now
     */
    override val source: Source = Source.Cmd


    /**
     * To transform / rewrite the request
     */
    override fun clone(
            otherPath: String,
            otherParts: List<String>,
            otherSource: Source,
            otherVerb: String,
            otherData: Inputs,
            otherMeta: slatekit.common.Metadata,
            otherRaw: Any?,
            otherOutput: String?,
            otherTag: String,
            otherVersion: String,
            otherTimestamp: DateTime): Request {
        return this.copy(
                data = otherData,
                meta = otherMeta,
                raw = otherRaw,
                output = otherOutput,
                tag = otherTag,
                version = otherVersion,
                timestamp = otherTimestamp
        )
    }


    companion object {

        fun empty(): CommandRequest {
            return build(Args.default())
        }


        /**
         * Builds the CMD Request from the argum
         */
        @JvmStatic
        fun build(args: Args): CommandRequest {
            return CommandRequest(
                    args = args,
                    data = args,
                    meta = InputArgs(args.meta)
            )
        }
    }
}