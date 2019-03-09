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

import slatekit.common.io.IO
import slatekit.common.newline

interface SemanticWrites {

    val TAB: String get() = "    "

    val NEWLINE: String get() = newline

    /**
     * Map the text type to functions that can implement it.
     */
    val lookup: Map<SemanticType, (String, Boolean) -> Unit> get() = mapOf(
            Title to this::title,
            Subtitle to this::subTitle,
            Url to this::url,
            Important to this::important,
            Highlight to this::highlight,
            Success to this::success,
            Failure to this::failure,
            Text to this::text
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
    fun writeItem(mode: SemanticType, msg: String, endLine: Boolean) {
        lookup[mode]?.invoke(msg, endLine)
    }

    /**
     * Writes the text using the TextType
     *
     * @param mode
     * @param text
     * @param endLine
     */
    fun write(mode: SemanticType, text: String, endLine: Boolean = true)

    /**
     * prints a empty line
     */
    fun line() = write(NoFormat, NEWLINE, false)

    /**
     * prints a empty line
     */
    fun lines(count: Int) = (0..count).forEach { line() }

    /**
     * prints a tab count times
     *
     * @param count
     */
    fun tab(count: Int = 1) = (0..count).forEach { write(Text, TAB, false) }

    /**
     * prints text in title format ( UPPERCASE and BLUE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun title(text: String, endLine: Boolean = true): Unit = write(Title, text, endLine)

    /**
     * prints text in subtitle format ( CYAN )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun subTitle(text: String, endLine: Boolean = true): Unit = write(Subtitle, text, endLine)

    /**
     * prints text in url format ( BLUE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun url(text: String, endLine: Boolean = true): Unit = write(Url, text, endLine)

    /**
     * prints text in important format ( RED )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun important(text: String, endLine: Boolean = true): Unit = write(Important, text, endLine)

    /**
     * prints text in highlight format ( YELLOW )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun highlight(text: String, endLine: Boolean = true): Unit = write(Highlight, text, endLine)

    /**
     * prints text in title format ( UPPERCASE and BLUE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun success(text: String, endLine: Boolean = true): Unit = write(Success, text, endLine)

    /**
     * prints text in error format ( RED )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun failure(text: String, endLine: Boolean = true): Unit = write(Failure, text, endLine)

    /**
     * prints text in normal format ( WHITE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun text(text: String, endLine: Boolean = true): Unit = write(Text, text, endLine)

    /**
     * prints text in normal format ( WHITE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun label(text: String, endLine: Boolean = true):Unit = write(Label, text, endLine)

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
