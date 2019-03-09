package slatekit.cli

import slatekit.common.console.SemanticConsole
import slatekit.common.io.IO

/**
 * Default writer to console ( unless overridden by the writer from constructor )
 * @param io: Function to handle the writint ( for abstracting IO and testability )
 */
class CliWriter(val io: ((CliOutput) -> Unit)? = null) : IO<CliOutput, Unit> {

    private val consoleWriter = SemanticConsole()


    override fun run(i: CliOutput) = when (io) {
        null -> consoleWriter.write(i.type, i.text ?: "", i.newline)
        else -> io.invoke(i)
    }
}