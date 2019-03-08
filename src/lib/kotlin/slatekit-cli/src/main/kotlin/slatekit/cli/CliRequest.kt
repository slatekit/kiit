package slatekit.cli

import slatekit.common.DateTime
import slatekit.common.Inputs
import slatekit.common.Metadata
import slatekit.common.Uris
import slatekit.common.args.Args
import slatekit.common.content.Doc
import slatekit.common.requests.InputArgs
import slatekit.common.requests.RequestSupport
import slatekit.common.requests.Request
import slatekit.common.requests.Source
import java.io.InputStream

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
    override val parts: List<String> = args.actionParts

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
    override fun getDoc(name: String): Doc {
        return Uris.readDoc(this.args.getString(name))!!
    }

    /**
     * Get a file referenced by name in the arguments
     */
    override fun getFile(name: String, callback: (InputStream) -> Doc): Doc {
        return Uris.readDoc(this.args.getString(name))!!
    }

    /**
     * Get a file referenced by name in the arguments as a stream
     */
    override fun getFileStream(name: String, callback: (InputStream) -> Unit) {
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