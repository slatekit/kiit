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

package kiit.utils.writer

import kiit.common.newline

interface Writer {

    val SPACE: String get() = " "
    val TAB: String get() = "    "

    val NEWLINE: String get() = newline

    /**
     * Map the text type to functions that can implement it.
     */
    val lookup: Map<TextType, (String, Boolean) -> Unit> get() = mapOf(
            TextType.Title to this::title,
            TextType.Subtitle to this::subTitle,
            TextType.Url to this::url,
            TextType.Important to this::important,
            TextType.Highlight to this::highlight,
            TextType.Success to this::success,
            TextType.Failure to this::failure,
            TextType.Text to this::text
    )

    /**
     * Write many items based on the semantic modes
     *
     * @param items
     */
    fun writeItems(items: List<TextOutput>) {
        items.forEach { item -> writeItem(item.textType, item.msg, item.endLine) }
    }

    /**
     * Write a single item based on the semantic textType
     *
     * @param mode
     * @param msg
     * @param endLine
     */
    fun writeItem(mode: TextType, msg: String, endLine: Boolean) {
        lookup[mode]?.invoke(msg, endLine)
    }

    /**
     * Writes the text using the TextType
     *
     * @param mode
     * @param text
     * @param endLine
     */
    fun write(mode: TextType, text: String, endLine: Boolean = true)

    /**
     * prints a empty line
     */
    fun line() = write(TextType.NewLine, NEWLINE, false)

    /**
     * prints a empty line
     */
    fun lines(count: Int) = (0..count).forEach { line() }

    /**
     * prints a tab count times
     *
     * @param count
     */
    fun tab(count: Int = 1) = (0..count).forEach { write(TextType.Tab, TAB, false) }

    /**
     * prints text in title format ( UPPERCASE and BLUE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun title(text: String, endLine: Boolean = true): Unit = write(TextType.Title, text, endLine)

    /**
     * prints text in subtitle format ( CYAN )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun subTitle(text: String, endLine: Boolean = true): Unit = write(TextType.Subtitle, text, endLine)

    /**
     * prints text in url format ( BLUE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun url(text: String, endLine: Boolean = true): Unit = write(TextType.Url, text, endLine)

    /**
     * prints text in important format ( RED )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun important(text: String, endLine: Boolean = true): Unit = write(TextType.Important, text, endLine)

    /**
     * prints text in highlight format ( YELLOW )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun highlight(text: String, endLine: Boolean = true): Unit = write(TextType.Highlight, text, endLine)

    /**
     * prints text in title format ( UPPERCASE and BLUE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun success(text: String, endLine: Boolean = true): Unit = write(TextType.Success, text, endLine)

    /**
     * prints text in error format ( RED )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun failure(text: String, endLine: Boolean = true): Unit = write(TextType.Failure, text, endLine)

    /**
     * prints text in normal format ( WHITE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun text(text: String, endLine: Boolean = true): Unit = write(TextType.Text, text, endLine)

    /**
     * prints text in normal format ( WHITE )
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    fun label(text: String, endLine: Boolean = true):Unit = write(TextType.Label, text, endLine)

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
