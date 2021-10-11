package slatekit.common.convert

import java.util.*

interface B64 {
    fun encode(bytes:ByteArray):String
    fun decode(text:String):ByteArray
}


object B64Java8 : B64 {

    override fun decode(text: String): ByteArray {
        return Base64.getDecoder().decode(text.toByteArray()).toTypedArray().toByteArray()
    }


    override fun encode(bytes: ByteArray): String {
        return Base64.getEncoder().withoutPadding().encodeToString(bytes)
    }
}