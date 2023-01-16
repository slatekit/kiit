package kiit.server

import kiit.apis.support.Authenticator
import slatekit.telemetry.Diagnostics
import kiit.common.crypto.Encryptor
import kiit.common.log.Logger
import slatekit.telemetry.MetricsLite
import kiit.requests.Request
import kiit.serialization.deserializer.Deserializer

data class ServerContext(
        val logs: Logger,
        val auth: Authenticator,
        val metrics: MetricsLite,
        val stats: Diagnostics<Request>,
        val decoder: (Request, Encryptor?) -> Deserializer)