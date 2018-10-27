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

package slatekit.common.console

sealed class TextType(val color: String, val upperCase: Boolean) {

    fun format(text: String): String {
        val checkedText = text
        val res = if (upperCase) checkedText.toUpperCase() else checkedText
        return res
    }
}

object Title : TextType(Console.BLUE, true)
object Subtitle : TextType(Console.CYAN, false)
object Url : TextType(Console.BLUE, false)
object Important : TextType(Console.RED, false)
object Highlight : TextType(Console.YELLOW, false)
object Success : TextType(Console.GREEN, false)
object Error : TextType(Console.RED, false)
object Text : TextType(Console.WHITE, false)
