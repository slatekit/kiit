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

import slatekit.common.ext.escapeHtml

/**
 * Generates help docs on the console.
 */
class SemanticWeb : SemanticWrites {

    val template = """
    <html>
        <body style="width:800px; font-size:16px">
            <div style="border:1px solid #cccccc; background-color:#ecf0f1; padding:10px 10px 10px 10px;">
            {{content_here}}
            </div>
        </body>
    </html>
    """

    private val buffer = StringBuilder()
    val settings = SemanticConsoleSettings()

    override val SPACE: String get() = " "
    override val TAB: String get() = "&nbsp;&nbsp;&nbsp;&nbsp;"

    override val NEWLINE: String get() = "<br/>"

    /**
     * Write a single item based on the semantic textType
     *
     * @param mode
     * @param msg
     * @param endLine
     */
    override fun writeItem(mode: SemanticText, msg: String, endLine: Boolean) {
        when (mode) {
            SemanticText.Title     -> writeTag("H1", mode.format(msg), endLine, "color:Black ")
            SemanticText.Subtitle  -> writeTag("H2", mode.format(msg), endLine, "color:Black ")
            SemanticText.Url       -> writeLink(msg, mode.format(msg), endLine, "color:Blue ")
            SemanticText.Important -> writeTag(tag("span", endLine,"h4"), mode.format(msg), endLine, "color:Black ")
            SemanticText.Highlight -> writeTag(tag("span", endLine,"p" ), mode.format(msg), endLine, "color:Orange")
            SemanticText.Success   -> writeTag(tag("span", endLine,"p" ), mode.format(msg), endLine, "color:Green ")
            SemanticText.Failure   -> writeTag(tag("span", endLine,"p" ), mode.format(msg), endLine, "color:Red   ")
            SemanticText.Text      -> writeTag("span", mode.format(msg), endLine, "color:Black ")
            SemanticText.NoFormat  -> writeTag("", mode.format(msg), endLine, "")
            SemanticText.NewLine   -> buffer.append(NEWLINE)
            SemanticText.Tab       -> buffer.append(TAB)
        }
    }

    /**
     * Writes the text using the TextType
     *
     * @param mode
     * @param text
     * @param endLine
     */
    override fun write(mode: SemanticText, text: String, endLine: Boolean) {
        writeItem(mode, text, endLine)
    }

    /**
     * prints text in normal format ( WHITE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    override fun label(text: String, endLine: Boolean) {
        writeItem(SemanticText.Text, text, endLine)
    }

    /**
     * Prints a list of items with indentation
     *
     * @param items
     * @param isOrdered
     */
    override fun list(items: List<Any>, isOrdered: Boolean) {
        val tag = if (isOrdered) "ol" else "ul"
        buffer.append("<$tag>")
        items.map { it.toString() }.forEach { writeTag("li", it, endLine = true) }
        buffer.append("</$tag>")
        line()
    }

    /**
     * prints text using a label : value format
     *
     * @param key: 
     * @param value : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    override fun keyValue(key: String, value: String, endLine: Boolean) {
        text(key + " = " + value, endLine)
    }

    fun writeTag(tag: String, text: String, endLine: Boolean, cssCls: String = "") {
        val html = text.escapeHtml()
        buffer.append("""<$tag style="$cssCls">$html</$tag>""")
        if (endLine) {
            buffer.append("<br/>")
        }
    }

    fun writeLink(href: String, text: String, endLine: Boolean, cssCls: String = "") {
        val html = text.escapeHtml()
        buffer.append("""<a href="$href" style="$cssCls">$html</a>""")
        if (endLine) {
            buffer.append("<br/>")
        }
    }

    override fun toString(): String {
        val content = buffer.toString()
        return template.replace("{{content_here}}", content)
    }


    private fun tag(tagName:String, newLine:Boolean, tagIfNewLine:String) :String = if(newLine) tagIfNewLine else tagName
}
