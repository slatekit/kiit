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

sealed class TextType(val name:String, val color: String, private val upperCase: Boolean) {

    fun format(text: String): String {
        return if (upperCase) text.toUpperCase() else text
    }
}

object Title     : TextType("title", Console.BLUE, true)
object Subtitle  : TextType("subtitle", Console.CYAN, false)
object Url       : TextType("url", Console.BLUE, false)
object Important : TextType("important", Console.RED, false)
object Highlight : TextType("highlight", Console.YELLOW, false)
object Success   : TextType("success", Console.GREEN, false)
object Failure   : TextType("failure", Console.RED, false)
object Text      : TextType("text", Console.WHITE, false)
object NoFormat  : TextType("none", "",false)
