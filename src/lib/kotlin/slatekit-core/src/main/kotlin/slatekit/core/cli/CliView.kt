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

package slatekit.core.cli

import slatekit.common.info.AppMeta
import slatekit.common.console.ConsoleWriter

class CliView(
    val _writer: ConsoleWriter,
    val _infoCallback: ((Boolean, (Int, Pair<String, Any>) -> Unit) -> Unit)?,
    val _extendedInfo: ((ConsoleWriter) -> Unit)?
) {

    /**
     * Shows general help info
     */
    fun showHelp() {
        _writer.title("Please type your commands")
        _writer.line()

        showHelpInfo("1. Overview", listOf(
                "This component exposes your applicable APIs on the CLI"
        ))

        showHelpInfo("2. Routing", listOf(
                "Actions are in a 3 part hierarchical structure of {area}.{api}.{action}"
        ))

        showHelpInfo("3. Syntax", listOf(
                "area.api.action  -param1=value* @param2='abc' @meta=value*"
        ))

        showHelpInfo("4. Examples", listOf(
                "app.users.activate -email=johndoe@gmail.com @token=abc",
                "To show examples for any action, type 'area.api.action ?' e.g. app.users.activate?"
        ))

        showHelpInfo("5. Parameters", listOf(
                "There are 3 types of parameters that you can pass",
                "1. regular   : begin with prefix '-' e.g. -name=john    : action parameters",
                "2. meta      : begin with prefix '@' e.g. @token=abc    : action meta data",
                "3. system    : begin with prefix '$' e.g. \$format=json  : command options"
        ))

        showHelpInfo("6. Batch", listOf(
                "You can run commands in a batch, from a file in the inputs folder:",
                "sys.cli.batch \$file='setup.txt'"
        ))

        showHelpInfo("7. Discovery", listOf(
                "You can use a question mark '?' to display areas, apis, actions and action inputs",
                "type ?                 : to list all areas",
                "type area ?            : to list all apis in an area",
                "type area.api ?        : to list all actions in an api",
                "type area.api.action ? : to list all inputs for an action"
        ))

        _writer.highlight("8. Options")
        showHelpCommandOptions()

        _writer.highlight("9. Available")
        showHelpExtended()

        _writer.line()
        _writer.important("type 'exit' or 'quit' to quit program")
        _writer.url("type 'info' for detailed information")
    }

    fun showHelpInfo(section: String, lines: List<String>) {

        _writer.highlight(section)
        lines.forEach { line ->
            // _writer.tab(1)
            _writer.text(line)
        }
        _writer.line()
    }

    /**
     * Shows help command for sys parameters
     */
    fun showHelpCommandOptions() {
        fun logSysOption(name: String, desc: String, required: Boolean, options: String) {
            val reqChar = if (required) "! required" else "? optional"
            // _writer.tab(1)
            _writer.text("\$$name  = $desc")
            // _writer.tab(1)
            _writer.text("           $reqChar $options")
        }
        // -env       = the environment to run in
        //              ? optional  [String]  e.g. dev
        _writer.text("These are the options available on every command")
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

        _writer.line()
    }

    /**
     * Shows extra help - useful for derived classes to show more help info
     */
    fun showHelpExtended() {
        _extendedInfo?.invoke(_writer)
    }

    fun showVersion(meta: AppMeta) {
        _writer.line()
        _writer.subTitle(meta.about.name)
        _writer.highlight(meta.about.version)
    }

    fun showAbout() {
        _writer.line()

        _infoCallback?.let { callback ->
            callback(false, { maxLength, item ->
                _writer.text(item.first.padEnd(maxLength) + " : " + item.second)
            })
        }

        _writer.line()
    }

    /**
     * Shows help for the command
     *
     * @param cmd
     * @param mode
     */
    fun showHelpFor(cmd: CliCommand, mode: Int) {
        _writer.text("help for : " + cmd.fullName())
    }

    /**
     * shows error related to arguments
     *
     * @param message
     */
    fun showArgumentsError(message: String) {
        _writer.important("Unable to parse arguments")
        _writer.important("Error : " + message ?: "")
    }
}
