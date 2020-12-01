package slatekit.tracking

import slatekit.common.DateTime

data class Tracked<T>(
    @JvmField val value: T?,
    @JvmField val count: Long,
    @JvmField val created: DateTime?,
    @JvmField val timestamp: DateTime?)
