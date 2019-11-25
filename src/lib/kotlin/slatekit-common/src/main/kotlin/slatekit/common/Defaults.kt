package slatekit.common

import slatekit.common.args.Args
import slatekit.common.conf.Conf
import slatekit.common.conf.Config
import slatekit.common.utils.B64Java8
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Envs
import slatekit.common.ext.toIdent
import slatekit.common.info.*
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.common.requests.InputArgs
import slatekit.common.requests.Request
import slatekit.common.requests.Response

/**
 * =================================================================================
 * ABOUT: This file contains default implementations of multiple components
 * 1. Context  : Default implementation of container for storing args, env, config, logs
 * 2. Request  : Default implementation of an abstracted response for web/cli
 * 3. Response : Default implementation of an abstracted request  for web/cli
 *
 * NOTES:
 * 1. These 3 components are used throughout Slate Kit
 * 2. The Context is used to pass core application dependencies to other components
 * 3. The Request/Response are used for the purpose of building "Universal APIs" in Slate Kit
 * 4. The Request is used by both the Slate Kit APIs and CLI component
 * 5. The Response is used by both the Slate Kit APIs and CLI component
 * =================================================================================
 */
/**
 * General purpose class to model a Response at an application boundary ( such as http response )
 * NOTE: This is used for the APIs in Slate Kit
 * @param success : Whether or not the response is successful
 * @param code : A status code ( can be the http status code )
 * @param meta : Meta data for the response ( can be used for headers for http )
 * @param value : The actual value returned by the response
 * @param msg : Message in the case of an failure
 * @param err : Exception in event of failure
 * @param tag : Tag used as a correlation field
 */
data class CommonResponse<out T>(
        override val success: Boolean,
        override val code: Int,
        override val meta: Map<String, String>?,
        override val value: T?,
        override val msg: String? = null,
        override val err: Exception? = null,
        override val tag: String? = null
) : Response<T> {

    override fun withMeta(meta: List<Pair<String, String>>): Response<T> {
        return this.meta?.let {
            copy(meta = it.plus(meta))
        } ?: copy(meta = meta.toMap())
    }
}



/**
 *
 * @param args   : command line arguments
 * @param envs   : environment selection ( dev, qa, staging, prod )
 * @param conf   : config settings
 * @param logs  : factory to create logs
 * @param about   : info about the running application
 * @param sys   : info about system ( host / language )
 * @param build : info about the build
 * @param enc   : encryption/decryption service
 * @param dirs  : directories used for the app
 */
data class CommonContext(
        override val args: Args,
        override val envs: Envs,
        override val conf: Conf,
        override val logs: Logs,
        override val info: Info,
        override val enc: Encryptor? = null,
        override val dirs: Folders? = null
) : Context {

    companion object {

        @JvmStatic
        fun err(code: Int, msg: String? = null): CommonContext {
            val args = Args.default()
            val envs = Envs.defaults()
            val conf = Config()
            return CommonContext(
                    args = args,
                    envs = envs,
                    conf = conf,
                    logs = LogsDefault,
                    info = Info.none
            )
        }

        @JvmStatic
        fun simple(name: String): CommonContext {
            val args = Args.default()
            val envs = Envs.defaults()
            val conf = Config()
            return CommonContext(
                    args = args,
                    envs = envs,
                    conf = conf,
                    logs = LogsDefault,
                    info = Info.none,
                    dirs = Folders.userDir("slatekit", name.toIdent(), name.toIdent())
            )
        }

        @JvmStatic
        fun sample(id: String, name: String, about: String, company: String): CommonContext {
            val args = Args.default()
            val envs = Envs.defaults()
            val conf = Config()
            return CommonContext(
                    args = args,
                    envs = envs,
                    conf = conf,
                    logs = LogsDefault,
                    info = Info(
                            About(id, name, about, company),
                            Build.empty,
                            Sys.build()
                    ),
                    enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                    dirs = Folders.userDir("slatekit", "samples", "sample1")
            )
        }
    }
}



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
            otherMeta: slatekit.common.Metadata,
            otherRaw: Any?,
            otherOutput: String?,
            otherTag: String,
            otherVersion: String,
            otherTimestamp:DateTime) : Request {
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


