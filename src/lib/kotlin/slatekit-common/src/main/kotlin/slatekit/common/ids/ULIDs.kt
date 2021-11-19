package slatekit.common.ids

import slatekit.common.DateTime
import slatekit.common.utils.Random

/**
 * Custom implementation of the ULID
 * https://github.com/ulid/spec
 *
 * GOALS
 * 1. Unique by time + node + random
 * 1. Lexicographically sortable!
 * 2. Canonically encoded as a 32 char string
 * 3. Case insensitive
 * 4. No special chars ( url safe )
 *
 *
 * NOTES:
 * 1. 32 chars : 11 char (time), 8 char (node), 10 char (random), 1 char (version)
 * 2. {TIME-HEX}{NODE}{RANDOM}{VERSION}
 * 3. "17587643cb8 23juuycq mbav3u9uez a"
 *
 *
 */
data class ULID internal constructor(val instant:String, val node:String, val random:String, val version:String) : UID {
    override val name: String = "ulid"
    override val value: String = "$instant$node$random$version"
    override fun toString(): String = value
}


/**
 * Default implementation of the Ids interface with integration UPID ( using UUID V4 )
 */
object ULIDs : UIDGen<ULID> {
    val VERSION = "a"
    val RANDOM_CHARS = "abcdefghjkmnpqrstuvwxyz23456789"
    val NODE_ID = Random.randomize(8, RANDOM_CHARS)
    private var lastTime = System.currentTimeMillis()

    override fun create(): ULID {
        return create(null)
    }

    override fun create(context:String?): ULID {
        val node = when {
            context == null     -> NODE_ID
            context.length < 8  -> NODE_ID
            context.length > 8  -> context.substring(0, 8)
            context.length == 8 -> context
            else                -> NODE_ID
        }
        val timestamp = getTime()
        val instant = java.lang.Long.toHexString(timestamp).toLowerCase()
        val random = Random.randomize(10, RANDOM_CHARS)
        return ULID(instant, node, random, VERSION)
    }

    override fun parse(id: String): ULID {
        val parts = split(id)
        return when(parts.size){
            4 -> ULID(parts[0], parts[1], parts[2], parts[3])
            else -> throw Exception("ULID has invalid value of $id")
        }
    }

    override fun split(id: String): Array<String> {
        val hex = id.substring(0, 11)
        val node = id.substring(11,19)
        val rand = id.substring(19, 29)
        val ver = id[id.length -1]
        return arrayOf(hex, node, rand, ver.toString())
    }


    @Synchronized
    private fun getTime():Long {
        // Monotonic : Too Simplistic?
        val now = System.currentTimeMillis()
        if(now > lastTime ){
            lastTime = now
        }
        else {
            lastTime++
        }
        return lastTime
    }
}

