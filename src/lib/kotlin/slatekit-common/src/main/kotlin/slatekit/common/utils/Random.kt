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

    @JvmStatic fun digitsN(n: Int): Long = randomize(n, NUMS).toLong()

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
