package slatekit.common.encrypt

import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class HMAC(secret: ByteArray) {
    val provider = "HmacSHA256"
    val hasher: javax.crypto.Mac = Mac.getInstance(provider)
    val key = SecretKeySpec(secret, provider)
    val base64 = Base64.getUrlEncoder()

    init {
        hasher.init(key)
    }

    fun sign(content: String): ByteArray {
        val hash = hasher.doFinal(content.toByteArray(Charsets.UTF_8))
        return hash
    }

    fun encode(content: String): String {
        val hash = sign(content)
        val encoded = base64.encodeToString(hash)
        return encoded
    }
}
