package slatekit.server

import slatekit.apis.support.Authenticator
import slatekit.telemetry.Diagnostics
import slatekit.common.crypto.Encryptor
import slatekit.common.log.Logger
import slatekit.telemetry.MetricsLite
import slatekit.requests.Request
import slatekit.serialization.deserializer.Deserializer

data class ServerContext(
        val logs: Logger,
        val auth: Authenticator,
        val metrics: MetricsLite,
        val stats: Diagnostics<Request>,
        val decoder: (Request, Encryptor?) -> Deserializer)