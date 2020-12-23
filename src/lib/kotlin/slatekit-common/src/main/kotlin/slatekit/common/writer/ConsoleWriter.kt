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

package slatekit.common.writer

import slatekit.common.io.IO
import slatekit.common.io.Print
import slatekit.common.newline

/**
 * Semantic console writer to print text in different colors and in Uppercase/lowercase for
 * things like title, subtitle, url etc.
 */
class ConsoleWriter(
        val settings: TextSettings = Colors.defaults(),
        writer  : IO<Any?, Unit>? = null ) : Writer {

    /**
     * IO abstraction for system.println.
     * Assists with testing and making code a bit more "functional"
     * This is a simple, custom alternative to the IO Monad.
     * Refer to IO.kt for details.
     */
    private val io: IO<Any?, Unit> = writer ?: Print()


    /**
     * Writes the text using the TextType
     *
     * @param mode
     * @param text
     * @param endLine
     */
    override fun write(mode: TextType, text: String, endLine: Boolean) {
        write(mode.color, mode.format(text), endLine)
    }


    /**
     * prints text in the color supplied.
     *
     * @param text : the text to print
     * @param endLine : whether or not to include a newline at the end
     */
    private fun write(color: String?, text: String, endLine: Boolean) {
        val finalColor = if(color == null || color == "") "" else "$color "
        val finalText = if (endLine)
            finalColor + text + newline
        else
            finalColor + text
        io.perform(finalText)
    }
}
