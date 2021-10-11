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

package slatekit.cli

import slatekit.utils.writer.TextType
import slatekit.utils.writer.Writer
import slatekit.common.info.Info
import slatekit.common.io.IO

open class CliHelp(private val info: Info, private val io: IO<CliOutput, Unit>) : Writer {

    /**
     * Writes the text using the TextType
     *
     * @param mode
     * @param text
     * @param endLine
     */
    override fun write(mode: TextType, text: String, endLine: Boolean) {
        io.perform(CliOutput(mode, text, endLine))
    }

    /**
     * Shows general help info
     */
    fun showHelp() {
        title("Please type your commands")
        line()

        showHelpInfo("1. Examples", listOf(
                "app.users.activate -email=johndoe@gmail.com @token=abc",
                "To show examples for any action, type 'area.api.action ?' e.g. app.users.activate?"
        ))

        showHelpInfo("2. Parameters", listOf(
                "There are 3 types of parameters that you can pass",
                "1. regular   : begin with prefix '-' e.g. -name=john    : action parameters",
                "2. meta      : begin with prefix '@' e.g. @token=abc    : action meta data",
                "3. system    : begin with prefix '$' e.g. \$format=json  : command options"
        ))

        showHelpInfo("3. Batch", listOf(
                "You can run commands in a batch, from a file in the inputs folder:",
                "sys.cli.batch \$file='setup.txt'"
        ))

        highlight("4. Options")
        showSystemOptions()

        highlight("5. Available")
        showHelpExtended()

        line()
        important("type 'exit' or 'quit' to quit program")
        url("type 'info' for detailed information")
    }

    fun showVersion() {
        line()
        subTitle(info.about.name)
        highlight(info.build.version)
    }

    fun showAbout() {
        line()
        info.each({ first, second ->
            text("$first : $second", true)
        })

        line()
    }

    /**
     * Shows help for the command
     *
     * @param cmd
     * @param mode
     */
    fun showHelpFor(req: CliRequest, mode: Int) {
        text("help for : " + req.fullName)
    }

    /**
     * shows error related to arguments
     *
     * @param message
     */
    fun showArgumentsError(message: String) {
        important("Unable to parse arguments")
        important("Error : " + message ?: "")
    }

    private fun showHelpInfo(section: String, lines: List<String>) {

        highlight(section)
        lines.forEach { line ->
            // _tab(1)
            text(line)
        }
        line()
    }

    /**
     * Shows help command for sys parameters
     */
    private fun showSystemOptions() {
        fun logSysOption(name: String, desc: String, required: Boolean, options: String) {
            val reqChar = if (required) "! required" else "? optional"
            // _tab(1)
            text("\$$name  = $desc")
            // _tab(1)
            text("           $reqChar $options")
        }
        // -env       = the environment to run in
        //              ? optional  [String]  e.g. dev
        text("These are the options available on every command")
        logSysOption(
                "file  ",
                "loads inputs from the file path",
                false,
                "\"user://myapp/scripts/setup.txt\"")

        logSysOption(
                "sample",
                "generates a sample request file in the output directory",
                false,
                "\"sample1.json\"")

        logSysOption(
                "format",
                "the format of the output",
                false,
                "[ json | csv | prop ]")

        logSysOption(
                "log   ",
                "whether to log result to output directory",
                false,
                "true | false")

        line()
    }

    /**
     * Shows extra help - useful for derived classes to show more help info
     */
    protected fun showHelpExtended() {
    }
}
