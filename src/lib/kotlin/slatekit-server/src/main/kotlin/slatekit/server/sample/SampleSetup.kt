package slatekit.server.sample

import io.ktor.application.ApplicationCall
import slatekit.apis.ApiHost
import slatekit.apis.doc.DocWeb
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.security.WebProtocol
import slatekit.apis.svcs.Authenticator
import slatekit.common.DateTime
import slatekit.common.args.Args
import slatekit.common.auth.Roles
import slatekit.common.conf.Config
import slatekit.common.envs.Env
import slatekit.common.info.*
import slatekit.common.log.LogsDefault
import slatekit.common.metrics.MetricsLite
import slatekit.common.toResponse
import slatekit.core.common.AppContext
import slatekit.meta.Deserializer
import slatekit.results.Success
import slatekit.server.ServerConfig
import slatekit.server.common.Diagnostics
import slatekit.server.ktor.KtorResponse

object SampleSetup {

    // Configuration for port / and API documentation
    val config by lazy {
        ServerConfig(
                port = 5000,
                prefix = "/api/",
                docs = true,
                docKey = "abc123"
        )
    }


    // Context for sample purposes.
    val context by lazy {
        AppContext(
                arg = Args.default(),
                env = Env.defaults().select("dev").current!!,
                cfg = Config(),
                logs = LogsDefault,
                app = About.none,
                build = Build.empty,
                start = StartInfo.none,
                sys = Sys.build(),
                dirs = Folders.userDir("sample-server", "slatekit-samples", "sample-server")
        )
    }


    // APIs
    val apis by lazy {
        listOf(
                slatekit.apis.core.Api(
                        cls = SampleApi::class,
                        area = "samples",
                        name = "SampleTypes1",
                        desc = "Sample to show APIs with basic data-types",
                        declaredOnly = false,
                        auth = AuthModes.apiKey,
                        roles = Roles.all,
                        verb = Verbs.auto,
                        protocol = Protocols.all,
                        singleton = SampleApi(context)
                )
        )
    }


    // Diagnostics
    val log by lazy { context.logs.getLogger(SampleApi::javaClass.name) }
    val metrics by lazy { MetricsLite.build() }
    val diagnostics by lazy { Diagnostics(metrics, log) }


    // Slate Kit API Container
    fun container(auth: Authenticator) = ApiHost(
            context,
            false,
            auth,
            WebProtocol,
            apis = apis,
            deserializer = { req, enc -> Deserializer(req, enc) },
            docKey = config.docKey,
            docBuilder = { DocWeb() }
    )
}