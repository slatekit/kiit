package slatekit.core.workers

import slatekit.common.info.About
import slatekit.common.info.Host
import slatekit.common.info.Lang

/**
 * Metadata about the worker which include info, the host, and lang.
 * Since workers could be running on different machines, its good
 * to know the host and language runtime that the workers are in
 */
data class WorkerMetadata(val about: About = About.none,
                          val host: Host = Host.local(),
                          val lang: Lang = Lang.kotlin()
)
