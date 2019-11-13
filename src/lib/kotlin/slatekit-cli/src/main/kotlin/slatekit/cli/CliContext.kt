package slatekit.cli

import slatekit.common.types.Content
import slatekit.common.types.ContentType
import slatekit.common.info.Info
import slatekit.common.io.IO
import slatekit.common.io.Readln

class CliContext(
    val info: Info,
    val commands: List<String?>? = listOf(),
    ioReader: ((Unit) -> String?)? = null,
    ioWriter: ((CliOutput) -> Unit)? = null,
    serializer:(Any?, ContentType) -> Content
) {

    /**
     * Actual writer to either write to console using [CliWriter] or the provided writer
     * This is to abstract out IO to any function and facilitate unit-testing
     */
    val writer = CliWriter(ioWriter)

    /**
     * Actual reader to either read from console using the [Readln] IO or the provided reader
     * This is to abstract out IO to any function and facilitate unit-testing
     */
    val reader: IO<Unit, String?> = Readln(ioReader)

    /**
     * Handles display of help, about, version, etc
     */
    val help = CliHelp(info, writer)

    /**
     * Handles output of command results
     */
    val output = CliIO(writer, serializer)
}
