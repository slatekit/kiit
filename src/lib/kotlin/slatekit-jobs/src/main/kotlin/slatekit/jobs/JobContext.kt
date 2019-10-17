package slatekit.jobs

import slatekit.common.Identity
import slatekit.jobs.Queue
import slatekit.jobs.Workers

data class JobContext(val id: Identity, val job:Job, val queue: Queue?, val workers: Workers)