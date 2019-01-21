package slatekit.common

import slatekit.common.Result.Companion.attempt
import java.util.*


/**
 * Interface for generating, validation, parsing unique ids
 */
interface Ids {

    fun create(): String


    fun parse(id: String): String


    fun isValid(id:String):Boolean


    fun split(id:String):Array<String>
}



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
        return attempt { UUID.fromString(id) }.success
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


/**
 * Default implementation of the Ids interface with integration with a UUID v4
 */
object UUIDLCase : UUIDs(false)


