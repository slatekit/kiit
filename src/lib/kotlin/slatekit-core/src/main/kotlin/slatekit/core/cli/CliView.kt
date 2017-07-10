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

import slatekit.common.app.AppMeta
import slatekit.common.console.ConsoleWriter


class CliView(val _writer: ConsoleWriter,
                val _infoCallback: ((Boolean, (Int, Pair<String, Any>) -> Unit) -> Unit)?
) {

    /**
     * Shows general help info
     */
    fun showHelp() {
        _writer.title("Please type your commands")
        _writer.line()

        _writer.tab(1)
        _writer.highlight("Syntax")
        showHelpCommandSyntax()

        _writer.tab(1)
        _writer.highlight("Examples")
        showHelpCommandExample()

        _writer.tab(1)
        _writer.highlight("Available")
        showHelpExtended()

        _writer.line()
        _writer.important("type 'exit' or 'quit' to quit program")
        _writer.url("type 'info' for detailed information")
        _writer.success("type '?'                 : to list all areas")
        _writer.success("type 'area ?'            : to list all apis in an area")
        _writer.success("type 'area.api ?'        : to list all actions in an api")
        _writer.success("type 'area.api.action ?' : to list all parameters for an action")
        _writer.line()
    }


    /**
     * Shows help command structure
     */
    fun showHelpCommandSyntax() {
        _writer.tab(1)
        _writer.text("area.api.action  -key=value*")
        _writer.line()
    }


    /**
     * Shows help command example syntax
     */
    fun showHelpCommandExample() {
        _writer.tab(1)
        _writer.text("app.users.activate -email=johndoe@gmail.com -role=user")
        _writer.line()
    }


    /**
     * Shows extra help - useful for derived classes to show more help info
     */
    fun showHelpExtended() {
    }


    fun showVersion(meta: AppMeta): Unit {
        _writer.line()
        _writer.subTitle(meta.about.name)
        _writer.highlight(meta.about.version)
    }


    fun showAbout(): Unit {
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
    fun showHelpFor(cmd: CliCommand, mode: Int): Unit {
        _writer.text("help for : " + cmd.fullName())
    }


    /**
     * shows error related to arguments
     *
     * @param message
     */
    fun showArgumentsError(message: String): Unit {
        _writer.important("Unable to parse arguments")
        _writer.important("Error : " + message ?: "")
    }
}
