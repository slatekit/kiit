package slatekit.data.features

import slatekit.common.DateTime
import slatekit.data.features.Deletable
import slatekit.data.features.Inspectable
import slatekit.query.Op

interface Purgeable<TId, T> : Inspectable<TId, T>, Deletable<TId, T> where TId : Comparable<TId>, T: Any {
    /**
     * Purges data older than the number of days supplied
     */
    fun purge(field:String, days: Int): Int  {
        val since = DateTime.now().plusDays((days * -1).toLong())
        return purge(field, since)
    }

    /**
     * Purges data older before the timestamp
     */
    fun purge(field:String, timestamp:DateTime): Int  {
        val count = deleteByField(columnName(field), Op.Lt, timestamp)
        return count
    }
}
