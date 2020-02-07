package slatekit.cache

import slatekit.common.DateTime
import slatekit.common.ext.toStringUtc
import slatekit.common.utils.Random

/**
 * @param id     : Id of this event
 * @param origin : Name of the cache this event originated from.
 * @param action : Cache action e.g. CRUD action
 * @param key    : Affected key in the cache. Empty if action = DeleteAll
 * @param time   : Time at which this event occurred
 */
data class CacheEvent(val uuid:String, val origin:String, val action: CacheAction, val key: String, val time: DateTime) {

    val id:String = "$uuid.$origin.${action.name}.$key"

    val name:String = "$origin.${action.name}.$key"


    fun toPairs():List<Pair<String, Any>>{
        return listOf(
            CacheEvent::origin.name to origin,
            CacheEvent::uuid.name   to uuid,
            CacheEvent::action.name to action.name,
            CacheEvent::key.name    to key,
            CacheEvent::time.name   to time.toStringUtc()
        )
    }

    companion object {

        fun of(name:String, action:CacheAction, key:String):CacheEvent {
            val finalKey = if(key.trim().isNullOrEmpty()) "*" else key.trim()
            return CacheEvent(Random.uuid(), name, action, finalKey, DateTime.now())
        }
    }
}
