package slatekit.cli

import slatekit.common.console.ConsoleWriter
import slatekit.common.console.TextType
import slatekit.common.console.Writer
import slatekit.common.io.IO

/**
 * Default writer to console ( unless overridden by the writer from constructor )
 * @param io: Function to handle the writint ( for abstracting IO and testability )
 */
class CliWriter(val io: ((CliOutput) -> Unit)? = null) : IO<CliOutput, Unit>, Writer {

    private val consoleWriter = ConsoleWriter()

    override fun perform(i: CliOutput) = when (io) {
        null -> consoleWriter.write(i.type, i.text ?: "", i.newline)
        else -> io.invoke(i)
    }

    override fun write(mode: TextType, text: String, endLine: Boolean) {
        this.perform(CliOutput(mode, text, endLine))
    }
}
