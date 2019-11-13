package slatekit.common.ids

import slatekit.common.utils.Random
import java.util.*



/**
 * Default implementation of the Ids interface with integration with a UUID v4
 */
open class UUIDs(val upperCase:Boolean) : Ids {
    override fun create(): String {
        return Random.uuid(true, upperCase)
    }

    override fun parse(id: String):String {
        return UUID.fromString(id).toString()
    }

    override fun isValid(id: String): Boolean {
        return try { UUID.fromString(id); true } catch (ex:Exception) { false }
    }

    override fun split(id: String): Array<String> {
        val uuid = UUID.fromString(id)
        return arrayOf(
                uuid.timestamp().toString(),
                uuid.version().toString(),
                uuid.variant().toString(),
                uuid.clockSequence().toString(),
                uuid.node().toString())
    }
}