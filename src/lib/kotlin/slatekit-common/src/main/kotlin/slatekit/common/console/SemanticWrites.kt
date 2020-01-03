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

import slatekit.common.newline

interface SemanticWrites {

    val SPACE: String get() = " "
    val TAB: String get() = "    "

    val NEWLINE: String get() = newline

    /**
     * Map the text type to functions that can implement it.
     */
    val lookup: Map<SemanticText, (String, Boolean) -> Unit> get() = mapOf(
            SemanticText.Title to this::title,
            SemanticText.Subtitle to this::subTitle,
            SemanticText.Url to this::url,
            SemanticText.Important to this::important,
            SemanticText.Highlight to this::highlight,
            SemanticText.Success to this::success,
            SemanticText.Failure to this::failure,
            SemanticText.Text to this::text
    )

    /**
     * Write many items based on the semantic modes
     *
     * @param items
     */
    fun writeItems(items: List<SemanticOutput>) {
        items.forEach { item -> writeItem(item.textType, item.msg, item.endLine) }
    }

    /**
     * Write a single item based on the semantic textType
     *
     * @param mode
     * @param msg
     * @param endLine
     */
    fun writeItem(mode: SemanticText, msg: String, endLine: Boolean) {
        lookup[mode]?.invoke(msg, endLine)
    }

    /**
     * Writes the text using the TextType
     *
     * @param mode
     * @param text
     * @param endLine
     */
    fun write(mode: SemanticText, text: String, endLine: Boolean = true)

    /**
     * prints a empty line
     */
    fun line() = write(SemanticText.NewLine, NEWLINE, false)

    /**
     * prints a empty line
     */
    fun lines(count: Int) = (0..count).forEach { line() }

    /**
     * prints a tab count times
     *
     * @param count
     */
    fun tab(count: Int = 1) = (0..count).forEach { write(SemanticText.Tab, TAB, false) }

    /**
     * prints text in title format ( UPPERCASE and BLUE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun title(text: String, endLine: Boolean = true): Unit = write(SemanticText.Title, text, endLine)

    /**
     * prints text in subtitle format ( CYAN )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun subTitle(text: String, endLine: Boolean = true): Unit = write(SemanticText.Subtitle, text, endLine)

    /**
     * prints text in url format ( BLUE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun url(text: String, endLine: Boolean = true): Unit = write(SemanticText.Url, text, endLine)

    /**
     * prints text in important format ( RED )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun important(text: String, endLine: Boolean = true): Unit = write(SemanticText.Important, text, endLine)

    /**
     * prints text in highlight format ( YELLOW )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun highlight(text: String, endLine: Boolean = true): Unit = write(SemanticText.Highlight, text, endLine)

    /**
     * prints text in title format ( UPPERCASE and BLUE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun success(text: String, endLine: Boolean = true): Unit = write(SemanticText.Success, text, endLine)

    /**
     * prints text in error format ( RED )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun failure(text: String, endLine: Boolean = true): Unit = write(SemanticText.Failure, text, endLine)

    /**
     * prints text in normal format ( WHITE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun text(text: String, endLine: Boolean = true): Unit = write(SemanticText.Text, text, endLine)

    /**
     * prints text in normal format ( WHITE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun label(text: String, endLine: Boolean = true):Unit = write(SemanticText.Label, text, endLine)

    /**
     * Prints a list of items with indentation
     *
     * @param items
     * @param isOrdered
     */
    fun list(items: List<Any>, isOrdered: Boolean = false) {

        for (ndx in 0..items.size) {
            val item = items[ndx]
            val value = item.toString()
            val prefix = if (isOrdered) (ndx + 1).toString() + ". " else "- "
            text(TAB + prefix + value, endLine = true)
        }
        line()
    }

    /**
     * prints text using a label : value format
     *
     * @param key: 
     * @param value : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun keyValue(key: String, value: String, endLine: Boolean = true) {
        label("$key = ", false)
        text(value, endLine)
    }
}
