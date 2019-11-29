package slatekit.tracking

import slatekit.common.DateTime

interface Timed {
    val created: DateTime?
    val updated: DateTime?
    val applied: Long
}
