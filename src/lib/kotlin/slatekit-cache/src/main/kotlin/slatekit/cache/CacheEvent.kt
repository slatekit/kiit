package slatekit.cache

import slatekit.common.DateTime
import slatekit.common.utils.Random

/**
 * @param id    : Id of this event
 * @param name  : Name of the cache this event originated from.
 * @param action: Cache action e.g. CRUD action
 * @param key   : Affected key in the cache. Empty if action = ClearAll
 * @param time  : Time at which this event occurred
 */
data class CacheEvent(val uuid:String, val name:String, val action: CacheAction, val key: String, val time: DateTime) {
    val id:String = "$uuid.$name.${action.name}.$key"

    companion object {

        fun of(name:String, action:CacheAction, key:String):CacheEvent {
            return CacheEvent(Random.uuid(), name, action, key, DateTime.now())
        }
    }
}
