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

sealed class TextType(val name:String, val color: String, private val upperCase: Boolean, val format:Boolean = true) {

    object Title     : TextType("title"    , Colors.BLUE, true)
    object Subtitle  : TextType("subtitle" , Colors.CYAN, false)
    object Url       : TextType("url"      , Colors.BLUE, false)
    object Important : TextType("important", Colors.RED, false)
    object Highlight : TextType("highlight", Colors.YELLOW, false)
    object Success   : TextType("success"  , Colors.GREEN, false)
    object Failure   : TextType("failure"  , Colors.RED, false)
    object Text      : TextType("text"     , Colors.WHITE, false)
    object Label     : TextType("label"    , Colors.WHITE, false)
    object NoFormat  : TextType("none"     , "",false)
    object NewLine   : TextType("newline"  , "",false)
    object Tab       : TextType("tab"      , "",false)
    object Raw       : TextType("raw"      , "",false, false)

    fun format(text: String): String {
        return if (upperCase) text.toUpperCase() else text
    }


    companion object {
        /**
         * Converts the string representation of a semantic text to the strongly typed object
         *
         * @param mode
         */
        fun parse(mode: String): TextType {
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
