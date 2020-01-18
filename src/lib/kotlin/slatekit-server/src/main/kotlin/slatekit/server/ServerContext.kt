package slatekit.server

import slatekit.apis.support.Authenticator
import slatekit.tracking.Diagnostics
import slatekit.common.crypto.Encryptor
import slatekit.common.log.Logger
import slatekit.tracking.MetricsLite
import slatekit.common.requests.Request
import slatekit.meta.Deserializer

data class ServerContext(
        val logs: Logger,
        val auth: Authenticator,
        val metrics: MetricsLite,
        val stats: Diagnostics<Request>,
        val decoder: (Request, Encryptor?) -> Deserializer)