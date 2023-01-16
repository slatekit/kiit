/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 * 
  *  </kiit_header>
 */

package kiit.common.utils

import kotlin.random.Random as KRandom

object Random {

    const val NUMS = "0123456789"
    const val NUMS_NON_ZERO = "123456789"
    const val ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val ALPHA_SAFE = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ"
    const val ALPHA_LCASE = "abcdefghijklmnopqrstuvwxyz"
    const val ALPHA_UCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val ALPHA_NUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    const val ALPHA_NUM_SAFE = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ0123456789"
    const val ALPHA_SYM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{}|;:,./<>?"
    @JvmField val rnd = java.util.Random()

    @JvmStatic fun digits6(): Int = KRandom.nextInt(100000, 999999)
    @JvmStatic fun string6(): String = stringN(6)
    @JvmStatic fun alpha6(): String = alphaN(6)
    @JvmStatic fun alphaNum6(): String = alphaNumN(6)
    @JvmStatic fun alphaSym6(): String = alphaSymN(6)
    @JvmStatic fun stringN(n: Int, allowed:String = ALPHA_SAFE): String = randomize(n, allowed)
    @JvmStatic fun alphaN(n: Int): String = randomize(n, ALPHA)
    @JvmStatic fun alphaNumN(n: Int): String = randomize(n, ALPHA_NUM)
    @JvmStatic fun alphaSymN(n: Int): String = randomize(n, ALPHA_SYM)

    @JvmStatic fun digitsN(n: Int, allowStartingZero:Boolean = false): Long {
        return when {
            n <= 0 -> 0
            n == 1 -> randomize(n, NUMS).toLong()
            else -> {
                // This ensures that a number starting with 0 has the first char "0" with a non-zero char
                // E..g 012345 -> {NON-ZERO-FIRST-CHAR}12345 -> 912345
                // Note: We could potentially also do random( 10^(n-1), 10^n),
                // but this will likely be replaced with Kotlin MultiPlatform Random function at some point anyway
                val text = randomize(n, NUMS)
                when(allowStartingZero) {
                    true -> text.toLong()
                    false -> safeNum(text)
                }
            }
        }
    }

    @JvmStatic fun safeNum(text:String, firstNum:Int? = null):Long {
        return when(text.length) {
            0 -> 0
            1 -> text.toLong()
            else -> {
                val startsWithZero = text.startsWith("0")
                when {
                    startsWithZero && firstNum == null -> {
                        val first = randomize(1, NUMS_NON_ZERO)
                        val rest = text.substring(1)
                        val num = (first + rest).toLong()
                        num
                    }
                    startsWithZero && firstNum != null -> {
                        val rest = text.substring(1)
                        val num = (firstNum.toString() + rest).toLong()
                        num
                    }
                    else -> text.toLong()
                }
            }
        }
    }

    @JvmStatic fun guid(): String = uuid(true)

    @JvmStatic fun uuid(includeDashes: Boolean = true, upperCase: Boolean = false): String {
        val uuid = java.util.UUID.randomUUID()
        val result = if (upperCase) uuid.toString().toUpperCase() else uuid.toString()
        return if (!includeDashes)
            result.replace("-", "")
        else
            result
    }

    @JvmStatic fun randomize(n: Int, allowedChars: String): String {
        val text = 0.until(n).fold("") { s, _ ->
            s + allowedChars[rnd.nextInt(allowedChars.length)]
        }
        return text
    }
}
