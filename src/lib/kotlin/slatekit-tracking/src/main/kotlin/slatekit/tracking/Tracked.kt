package slatekit.tracking

import slatekit.common.DateTime

/**
 * @param value   : Current value tracked
 * @param count   : Number of times accessed
 * @param created : Time this value originally created
 * @param updated : Time this value was accessed/updated
 */
data class Tracked<T>(
    @JvmField val value: T?,
    @JvmField val count: Long,
    @JvmField val created: DateTime?,
    @JvmField val updated: DateTime?)
