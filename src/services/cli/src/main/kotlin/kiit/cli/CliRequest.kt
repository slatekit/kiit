package kiit.cli

import java.io.InputStream
import kiit.common.DateTime
import kiit.common.values.Inputs
import kiit.common.values.Metadata
import kiit.common.io.Uris
import kiit.common.args.Args
import kiit.common.types.ContentFile
import kiit.requests.InputArgs
import kiit.requests.Request
import kiit.requests.RequestSupport
import kiit.common.Source

/**
 * Represents an abstraction of a Web Api Request and also a CLI ( Command Line ) request
 * @param path : route(endpoint) e.g. /{area}/{name}/{action} e.g. /app/reg/activateUser
 * @param meta : options representing settings/configurations ( similar to http-headers )
 * @param data : arguments to the command
 * @param raw : Optional raw request ( e.g. either the HttpRequest via Spark or ShellCommmand via CLI )
 * @param output : Optional output format of the result e.g. json by default json | csv | props
 * @param tag : Optional tag for tracking individual requests and for error logging.
 */
data class CliRequest(
    val args: Args,
    override val path: String,
    override val data: Inputs,
    override val meta: Metadata,
    override val raw: Any? = null,
    override val output: String? = "",
    override val tag: String = "",
    override val version: String = "1.0",
    override val timestamp: DateTime = DateTime.now()

) : Request, RequestSupport {

    /**
     * E.g. [ "app", "info", "version" ]
     */
    override val parts: List<String> = args.parts

    /**
     * Defaulted to CLI for now
     */
    override val source: Source = Source.CLI

    /**
     * Defaulted to CLI for now ( mimics an Http method )
     */
    override val verb: String = Source.CLI.id

    /**
     * Access to any underlying raw request
     * In this case the request comes from parsing the args so use this now.
     */
    override fun raw(): Any? = args

    /**
     * Get a document referenced by name in the arguments as a Doc of string content
     */
    override fun getDoc(name: String?): ContentFile? {
        return name?.let { n -> this.args.getStringOrNull(name)?.let { Uris.readDoc(it) } }
    }

    /**
     * Get a file referenced by name in the arguments
     */
    override fun getDoc(name: String?, callback: (InputStream) -> ContentFile): ContentFile? {
        return name?.let { n -> this.args.getStringOrNull(n)?.let { Uris.readDoc(it) } }
    }

    /**
     * Get a file referenced by name in the arguments as a stream
     */
    override fun getFileStream(name: String?):InputStream? {
        return null
    }

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
        otherTimestamp: DateTime
    ): Request {
        return this.copy(
                path = otherPath,
                data = otherData,
                meta = otherMeta,
                raw = otherRaw,
                output = otherOutput,
                tag = otherTag,
                version = otherVersion,
                timestamp = otherTimestamp
        )
    }

    /**
     * To transform / rewrite the request
     */
    fun clone(
        otherArgs: Args,
        otherPath: String,
        otherData: Inputs,
        otherMeta: Metadata,
        otherRaw: Any?,
        otherOutput: String?,
        otherTag: String,
        otherVersion: String,
        otherTimestamp: DateTime
    ): Request {
        return this.copy(
                args = otherArgs,
                path = otherPath,
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

        /**
         * Builds the CLI Request from the argum
         */
        @JvmStatic
        fun build(args: Args, line: String): CliRequest {
            return CliRequest(
                    args = args,
                    path = line,
                    data = args,
                    meta = InputArgs(args.meta)
            )
        }
    }
}
