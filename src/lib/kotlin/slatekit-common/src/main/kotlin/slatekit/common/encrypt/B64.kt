package slatekit.common.encrypt

import java.util.*

/**
 * Not really encryption, but leaving in this package for now.
 */
interface B64 {
    fun encode(bytes:ByteArray):String
    fun decode(text:String):ByteArray
}


/**
 * Not really encryption, but leaving in this package for now.
 */
object B64Java8 : B64 {

    override fun decode(text: String): ByteArray {
        return Base64.getDecoder().decode(text.toByteArray()).toTypedArray().toByteArray()
    }


    override fun encode(bytes: ByteArray): String {
        return Base64.getEncoder().withoutPadding().encodeToString(bytes)
    }
}