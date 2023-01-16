package kiit.cache

import kiit.common.DateTime
import kiit.common.Identity
import kiit.common.ext.toStringUtc
import kiit.common.utils.Random

/**
 * @param id     : Id of this event
 * @param origin : Name of the cache this event originated from.
 * @param action : Cache action e.g. CRUD action
 * @param key    : Affected key in the cache. Empty if action = DeleteAll
 * @param time   : Time at which this event occurred
 */
data class CacheEvent(val uuid:String, val identity:Identity, val action: CacheAction, val key: String, val time: DateTime) {

    val id:String = "$uuid.${identity.name}.${action.name}.$key"

    val name:String = "${identity.name}.${action.name}.$key"


    fun structured():List<Pair<String, Any>>{
        return listOf(
            CacheEvent::id.name     to id,
            CacheEvent::uuid.name   to uuid,
            CacheEvent::action.name to action.name,
            CacheEvent::key.name    to key,
            CacheEvent::time.name   to time.toStringUtc()
        )
    }

    companion object {

        fun of(id:Identity, action:CacheAction, key:String):CacheEvent {
            val finalKey = if(key.trim().isNullOrEmpty()) "*" else key.trim()
            return CacheEvent(Random.uuid(), id, action, finalKey, DateTime.now())
        }
    }
}
