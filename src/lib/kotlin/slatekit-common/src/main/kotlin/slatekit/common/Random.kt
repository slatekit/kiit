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

package slatekit.common


object Random {

    val NUMS = "0123456789"
    val LETTERS_LCASE = "abcdefghijklmnopqrstuvwxyz"
    val LETTERS_ALL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val ALPHA = "0123456789abcdefghijklmnopqrstuvwxyz"
    val ALPHASYM = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()_+-=[]{}|;:,./<>?"
    val rnd = java.util.Random()


    fun string3(): String = stringN(3)


    fun string6(): String = stringN(6)


    fun digits3(): Int = digitsN(3)


    fun digits6(): Int = digitsN(6)


    fun alpha3(): String = alphaN(3)


    fun alpha6(): String = alphaN(6)


    fun alphaSym3(): String = alphaN(3)


    fun alphaSym6(): String = alphaN(6)


    fun digitsN(n: Int): Int = Integer.parseInt(randomize(n, NUMS))


    fun stringN(n: Int, allowUpper: Boolean = true): String {
        return if (allowUpper) randomize(n, LETTERS_ALL) else randomize(n, LETTERS_LCASE)
    }


    fun alphaN(n: Int): String = randomize(n, ALPHA)


    fun alphaSymN(n: Int): String = randomize(n, ALPHASYM)


    fun stringGuid(includeDashes: Boolean = true): String {
        val result = java.util.UUID.randomUUID().toString().toUpperCase()
        return if (!includeDashes)
            result.replace("-", "")
        else
            result
    }


    fun randomize(n: Int, allowedChars: String): String {
        val text = 0.until(n).fold("") { s, _ ->

            s + allowedChars[rnd.nextInt(allowedChars.length)]

        }
        return text
    }
}
