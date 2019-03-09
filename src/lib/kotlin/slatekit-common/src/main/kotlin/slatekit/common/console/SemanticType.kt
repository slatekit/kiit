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

sealed class SemanticType(val name:String, val color: String, private val upperCase: Boolean) {

    object Title     : SemanticType("title"    , Console.BLUE, true)
    object Subtitle  : SemanticType("subtitle" , Console.CYAN, false)
    object Url       : SemanticType("url"      , Console.BLUE, false)
    object Important : SemanticType("important", Console.RED, false)
    object Highlight : SemanticType("highlight", Console.YELLOW, false)
    object Success   : SemanticType("success"  , Console.GREEN, false)
    object Failure   : SemanticType("failure"  , Console.RED, false)
    object Text      : SemanticType("text"     , Console.WHITE, false)
    object Label     : SemanticType("label"    , Console.WHITE, false)
    object NoFormat  : SemanticType("none"     , "",false)

    fun format(text: String): String {
        return if (upperCase) text.toUpperCase() else text
    }


    companion object {
        /**
         * Converts the string representation of a semantic text to the strongly typed object
         *
         * @param mode
         */
        fun parse(mode: String): SemanticType {
            return when (mode.toLowerCase()) {
                Title    .name -> Title
                Subtitle .name -> Subtitle
                Url      .name -> Url
                Important.name -> Important
                Highlight.name -> Highlight
                Success  .name -> Success
                Failure  .name -> Failure
                Text     .name -> Text
                NoFormat .name -> NoFormat
                else -> Text
            }
        }
    }
}
