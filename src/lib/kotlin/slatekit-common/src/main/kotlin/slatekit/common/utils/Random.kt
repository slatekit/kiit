/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.utils

object Random {

    @JvmField val NUMS = "0123456789"
    @JvmField val NUMS_NON_ZERO = "123456789"
    @JvmField val LETTERS_LCASE = "abcdefghijklmnopqrstuvwxyz"
    @JvmField val LETTERS_ALL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    @JvmField val ALPHA = "0123456789abcdefghijklmnopqrstuvwxyz"
    @JvmField val ALPHANUM = "0123456789abcdefghijklmnopqrstuvwxyz0123456789"
    @JvmField val ALPHASYM = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()_+-=[]{}|;:,./<>?"
    @JvmField val rnd = java.util.Random()

    @JvmStatic fun string3(): String = stringN(3)

    @JvmStatic fun string6(): String = stringN(6)

    @JvmStatic fun digits3(): Int = digitsN(3).toInt()

    @JvmStatic fun digits6(): Int = digitsN(6).toInt()

    @JvmStatic fun alpha3(): String = alphaN(3)

    @JvmStatic fun alpha6(): String = alphaN(6)

    @JvmStatic fun alphaNum3(): String = alphaNumN(3)

    @JvmStatic fun alphaNum6(): String = alphaNumN(6)

    @JvmStatic fun alphaSym3(): String = alphaSymN(3)

    @JvmStatic fun alphaSym6(): String = alphaSymN(6)

    @JvmStatic fun digitsN(n: Int): Int {
        return when {
            n <= 0 -> 0
            n == 1 -> randomize(n, NUMS).toInt()
            else -> {
                // This ensures that a number starting with 0 has the first char "0" with a non-zero char
                // E..g 012345 -> {NON-ZERO-FIRST-CHAR}12345 -> 912345
                // Note: We could potentially also do random( 10^(n-1), 10^n),
                // but this will likely be replaced with Kotlin MultiPlatform Random function at some point anyway
                val text = randomize(n, NUMS)
                val num = safeNum(text)
                num
            }
        }
    }

    @JvmStatic fun safeNum(text:String, firstNum:Int? = null):Int {
        return when(text.length) {
            0 -> 0
            1 -> text.toInt()
            else -> {
                val startsWithZero = text.startsWith("0")
                when {
                    startsWithZero && firstNum == null -> {
                        val first = randomize(1, NUMS_NON_ZERO)
                        val rest = text.substring(1)
                        val num = (first + rest).toInt()
                        num
                    }
                    startsWithZero && firstNum != null -> {
                        val rest = text.substring(1)
                        val num = (firstNum.toString() + rest).toInt()
                        num
                    }
                    else -> text.toInt()
                }
            }
        }
    }

    @JvmStatic fun stringN(n: Int, allowUpper: Boolean = true): String {
        return if (allowUpper) randomize(n, LETTERS_ALL) else randomize(n, LETTERS_LCASE)
    }

    @JvmStatic fun alphaN(n: Int): String = randomize(n, ALPHA)

    @JvmStatic fun alphaSymN(n: Int): String = randomize(n, ALPHASYM)

    @JvmStatic fun alphaNumN(n: Int): String = randomize(n, ALPHANUM)

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
