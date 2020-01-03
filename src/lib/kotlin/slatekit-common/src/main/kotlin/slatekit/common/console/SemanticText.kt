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

sealed class SemanticText(val name:String, val color: String, private val upperCase: Boolean, val format:Boolean = true) {

    object Title     : SemanticText("title"    , Console.BLUE, true)
    object Subtitle  : SemanticText("subtitle" , Console.CYAN, false)
    object Url       : SemanticText("url"      , Console.BLUE, false)
    object Important : SemanticText("important", Console.RED, false)
    object Highlight : SemanticText("highlight", Console.YELLOW, false)
    object Success   : SemanticText("success"  , Console.GREEN, false)
    object Failure   : SemanticText("failure"  , Console.RED, false)
    object Text      : SemanticText("text"     , Console.WHITE, false)
    object Label     : SemanticText("label"    , Console.WHITE, false)
    object NoFormat  : SemanticText("none"     , "",false)
    object NewLine   : SemanticText("newline"  , "",false)
    object Tab       : SemanticText("tab"      , "",false)
    object Raw       : SemanticText("raw"      , "",false, false)

    fun format(text: String): String {
        return if (upperCase) text.toUpperCase() else text
    }


    companion object {
        /**
         * Converts the string representation of a semantic text to the strongly typed object
         *
         * @param mode
         */
        fun parse(mode: String): SemanticText {
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
                NewLine  .name -> NewLine
                Tab      .name -> Tab
                else           -> Text
            }
        }
    }
}
